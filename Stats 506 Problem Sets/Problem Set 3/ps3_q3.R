## R Script for Question 3 in Problem Set 3
##
## This is an R script meant to translating an analysis from Stata into R.
##
## Author: Xinye Jiang
## Updated: November 17, 2018

# 80:  -------------------------------------------------------------------------
# libraries: -------------------------------------------------------------------
library(data.table)
library(dplyr)

# Get the data table to work with: 
mtcars_dt = as.data.table(mtcars)

# a. ---------------------------------------------------------------------------

# Write a translation using 'data.table' for the computations: 
reg_cols = c("disp", "hp", "wt")
coef_a = mtcars_dt[order(cyl), .(mpg, cyl, disp, hp, wt)] %>%
  .[, lapply(.SD, function(x) 
    {sum((x - mean(x)) * mpg) / sum((x - mean(x)) ^ 2)}),
    keyby=.(cyl), .SDcols = reg_cols]
new_names = c(key(coef_a), paste("beta_cyl", reg_cols, sep='_'))
setnames(coef_a, new_names)


# b. ---------------------------------------------------------------------------

# Function to compute the univariate regression coefficients by group: 
reg_coef_b = function(dep, ind, grp){
  # dep  - an arbitrary dependent variable
  # ind  - an arbitrary independent variable
  # grp  - an arbitrary grouping variable
  
  # Use 'data.table' for computations within the function: 
  dt = data.table(dep, ind, grp)
  dt = dt[order(grp)] %>%
    .[, .(beta = sum((ind - mean(ind)) * dep) / sum((ind - mean(ind)) ^ 2)),
      by=.(grp)]
  
  # Return the univariate regression coefficients
  return(dt)
}

test_disp_b = reg_coef_b(mtcars_dt$mpg, mtcars_dt$disp, mtcars_dt$cyl)
test_hp_b = reg_coef_b(mtcars_dt$mpg, mtcars_dt$hp, mtcars_dt$cyl)
test_wt_b = reg_coef_b(mtcars_dt$mpg, mtcars_dt$wt, mtcars_dt$cyl)

test_result_b = c(all.equal(test_disp_b$beta, coef_a$beta_cyl_disp),
                  all.equal(test_hp_b$beta, coef_a$beta_cyl_hp),
                  all.equal(test_wt_b$beta, coef_a$beta_cyl_wt))
## 'test_result' is "TRUE TRUE TRUE". The function produces the same results. 

# c. ---------------------------------------------------------------------------

# Compute the regression coefficients using the 'dplyr' verb 'summarize_at()': 
coef_c = mtcars %>%
  select(mpg, cyl, disp, hp, wt) %>%
  group_by(cyl) %>%
  summarise_at(vars(disp:wt), 
               .funs = funs( 
                 sum((. - mean(.)) * mpg) / sum((. - mean(.)) ^ 2) ) 
               )


# d. ---------------------------------------------------------------------------

# Function to compute the univariate regression coefficients by group: 
reg_coef_d = function(dep, ind, grp){
  # dep  - an arbitrary dependent variable
  # ind  - an arbitrary independent variable
  # grp  - an arbitrary grouping variable
  
  # Use 'dplyr' for computations within the function: 
  df = data_frame(dep, ind, grp)
  df = df %>%
    group_by(grp) %>%
    summarize_at(vars(ind),
                 .funs = funs(
                   sum((. - mean(.)) * dep) / sum((. - mean(.)) ^ 2) )
                 )
  
  # Return the univariate regression coefficients
  return(df)
}

test_disp_d = reg_coef_d(mtcars_dt$mpg, mtcars_dt$disp, mtcars_dt$cyl)
test_hp_d = reg_coef_d(mtcars_dt$mpg, mtcars_dt$hp, mtcars_dt$cyl)
test_wt_d = reg_coef_d(mtcars_dt$mpg, mtcars_dt$wt, mtcars_dt$cyl)

test_result_d = c(all.equal(test_disp_d$ind, coef_a$beta_cyl_disp),
                  all.equal(test_hp_d$ind, coef_a$beta_cyl_hp),
                  all.equal(test_wt_d$ind, coef_a$beta_cyl_wt))
## 'test_result' is "TRUE TRUE TRUE". The function produces the same results.


# 80:  -------------------------------------------------------------------------
