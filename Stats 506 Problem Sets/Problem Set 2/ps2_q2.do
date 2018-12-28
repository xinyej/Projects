* ---------------------------------------------------------------------------- *
* Stata Script for Question 2 in Problem Set 2
* 
* This script uses logistic regression to learn about the important predictors
* of whether people lose their primary upper right 2nd bicuspid (ur2b).
*
* Data: OHX_D.XPT, DEMO_D.XPT
*   imported from the working directory
* 
* Author: Xinye Jiang
* Updated: October 15, 2018
* ---------------------------------------------------------------------------- *

*--------------*  
* Script Setup *
*--------------*
version 15.0                        //Stata version used
log using ps2_q2.log, text replace  // Generate a log


*-------------------------------------------*
* a) Import and merge the data sets on SEQN *
*-------------------------------------------*

// Import and save OHX_D data
fdause OHX_D.XPT, clear
gsort +seqn
save OHX_D.dta

// Import DEMO_D and merge OHX_D
fdause DEMO_D.XPT, clear
gsort +seqn
merge 1:1 seqn using OHX_D.dta

// Reduce to matched data
keep if _merge==3
save ohx_demo_merge.dta

// Reduce to the variables of interest
keep ohx04htc ridagemn riagendr ridreth1 indfmpir sdmvpsu wtmec2yr sdmvstra


*---------------------------------------------------*
* b) Estimate the relationship between age and ur2b *
*---------------------------------------------------*

// Drop observations with missing ur2b or age values
drop if ohx04htc == .
drop if ohx04htc == 9
drop if ridagemn == .

// Create primary upper right 2nd bicuspid (ur2b) status groups
generate byte ur2b = 1
replace ur2b = 0 if ohx04htc == 1

// Fit a logistic regression between age and ur2b
logit ur2b ridagemn
estat ic
** BIC=1533.407

// Compute the ages at which 25, 50 and 75% of individuals lose primary ur2b
matrix coef = e(b)
local age25 = round(((ln(0.25/0.75))-coef[1,2])/coef[1,1])
local age50 = round(((ln(0.5/0.5))-coef[1,2])/coef[1,1])
local age75 = round(((ln(0.75/0.25))-coef[1,2])/coef[1,1])
display `age25', `age50', `age75'

// Compute the range of representative age values
display floor(`age25' / 12), ceil(`age75' / 12) 
** Choose representative age values: 8, 9, 10, 11, 12 (in years)
** i.e. 96, 108, 120, 132, 144 (in months)
matrix age_rep = (8,9,10,11,12)
matrix list age_rep


*----------------------------------------------------*
* c) Add demographic variables and improve the model *
*----------------------------------------------------*

// Add 'gender' to the model
logit ur2b ridagemn i.riagendr
estat ic
** BIC=1542.055 (does not improve) Do not retain it.


// Generate indicators for each race category
generate byte race_mexican = 0
replace race_mexican = 1 if ridreth1 == 1
generate byte race_black = 0
replace race_black = 1 if ridreth1 == 4
generate byte race_other = 0
replace race_other = 1 if ridreth1 == 2 | ridreth1 == 5

// Add each race category to the model
logit ur2b ridagemn i.race_mexican
estat ic
** BIC=1542.285 (does not improve) Do not retain it.
logit ur2b ridagemn i.race_black
estat ic
** BIC=1529.281 (improves) Retain it.
logit ur2b ridagemn i.race_black i.race_other
estat ic
** BIC=1536.103 (does not improve) Do not retain it.


// Drop observations with missing poverty income ratio values
drop if indfmpir == .

// Add 'poverty income ratio' (pir) to the model
logit ur2b ridagemn i.race_black indfmpir
estat ic
** BIC=1462.895 (improves) Retain it.
** This is the final model: ur2b ~ ridagemn + i.race_black + indfmpir
** (logit ur2b ridagemn i.race_black indfmpir)


*-----------------------------------------*
* d) Use the 'margins' command to compute *
*-----------------------------------------*

// d.1 Adjusted predictions at the mean at each representative age (rep. age)
margins, at(ridagemn = (96 108 120 132 144)) atmeans

// d.2 Marginal effects of retained categorical variable at rep. ages
margins, dydx(race_black) at(ridagemn = (96 108 120 132 144)) atmeans

// d.3 Average marginal effects of retained categorical variable at rep. ages
margins, dydx(race_black) at(ridagemn = (96 108 120 132 144))


*------------------------------------*
* e) Refit the final model using svy *
*------------------------------------*

svyset sdmvpsu [pweight=wtmec2yr], strata(sdmvstra) vce(linearized)
svy: logit ur2b ridagemn i.race_black indfmpir

** After refitting the final model from part c using svy, the variable 'pir'
** becomes not statistically significant any more as compared to before, 
** which can be seen from the change of its corresponding p-value. 
** Before refitting, p-value of 'pir' equals to 0.009 and is smaller 
** than 0.05, while after refitting, p-value of 'pir' equals to 0.141 and is
** bigger than 0.05. And almost all coefficients' 95% CIs have bigger range 
** of values than before, which can been seen from the above table.


*----------------*
* Script Cleanup *
*----------------*
log close
exit

* ---------------------------------------------------------------------------- *
