* ---------------------------------------------------------------------------- *
* Stata Script for Question 1 in Problem Set 2
* 
* This script uses Stata to estimate some national totals for residential
* energy consumption with their standard errors.
* 
* Data: recs2015_public_v3.csv
*   imported from the working directory
* 
* Author: Xinye Jiang
* Updated: October 16, 2018
* ---------------------------------------------------------------------------- *

*---------------------------------*
* Import and process the data set *
*---------------------------------*

// Import data
import delimited recs2015_public_v3.csv

// Keep only needed variables
keep kwh cufeetng gallonlp gallonfo nweight brrwt1-brrwt96

// Rename variables
rename (nweight kwh cufeetng gallonlp gallonfo)(brrwt0 elec gas propane kerosene)

// Generate case number
generate casenum = _n

// Reshape data wide to long
reshape long brrwt, i(casenum) j(wtnum)

*---------------------------------------*
* Compute estimates and standard errors *
*---------------------------------------*

// Generate weighted values
generate w_elec = elec*brrwt
generate w_gas = gas*brrwt
generate w_propane = propane*brrwt
generate w_kerosene = kerosene*brrwt

// Compute weighted totals
collapse (sum) w_elec w_gas w_propane w_kerosene, by(wtnum)
save recs_estimate.dta

// Compute standard errors
replace w_elec = ( w_elec - w_elec[1] )^2/96*4 if _n>=2
replace w_gas = ( w_gas - w_gas[1] )^2/96*4 if _n>=2
replace w_propane = ( w_propane - w_propane[1] )^2/96*4 if _n>=2
replace w_kerosene = ( w_kerosene - w_kerosene[1] )^2/96*4 if _n>=2
collapse (sum) w_elec w_gas w_propane w_kerosene if _n >1
replace w_elec = sqrt( w_elec )
replace w_gas = sqrt( w_gas )
replace w_propane = sqrt( w_propane )
replace w_kerosene = sqrt( w_kerosene )
save recs_se.dta

// Merge recs_estimate and recs_se
use recs_estimate.dta, clear
keep if _n==1
drop wtnum
merge m:m w_elec w_gas w_propane w_kerosene using recs_se.dta
drop _merge

// Print and export table
list
export delimited recs2015_usage.csv, replace

* ---------------------------------------------------------------------------- *
