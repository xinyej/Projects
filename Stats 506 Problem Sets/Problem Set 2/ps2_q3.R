## R Script for Question 3 in Problem Set 2
##
## This R script is meant to use logistic regression to learn about 
## the important predictors of whether people lose their primary 
## upper right 2nd bicuspid (ur2b).
## 
## Author: Xinye Jiang
## Updated: October 15, 2018

# 80:  -------------------------------------------------------------------------
# Libraries: -------------------------------------------------------------------
library(SASxport)
library(dplyr)

# Read data: -------------------------------------------------------------------
oralhealth = read.xport("OHX_D.XPT")
demographic = read.xport("DEMO_D.XPT")

# Functions to decode or recode variables: -------------------------------------

# Function to recode the status of primary upper right 2nd bicuspid (ur2b)
# Recode '1' as 'present' and recode '2','4' and '5' as 'lost'
decode_ur2b = function(rvec) {
  sapply(rvec, function(r) switch(r, 'present', 'lost', '', 'lost', 'lost'))
}

# Function to decode race
decode_race = function(rvec) {
  sapply(rvec, function(r) switch(r, 'Mexican', 'Other', 'White', 'Black',
                                  'Other'))
}

# Function to decode gender
decode_gender = function(rvec) {
  sapply(rvec, function(r) switch(r, 'male', 'female'))
}

# a. ---------------------------------------------------------------------------

# Merge both datasets together by the participant id: 
oral_demo = merge(oralhealth, demographic, by="SEQN")


# b. ---------------------------------------------------------------------------

# Select variables and clean data: 
oral_demo_b = oral_demo %>%
  select(ur2b = OHX04HTC, age = RIDAGEMN) %>%
  filter(!is.na(ur2b), !is.na(age)) %>%
  filter(ur2b != 9) %>%
  mutate(ur2b = decode_ur2b(ur2b))

# Fit a logistic regression between age and ur2b: 
logit_age = glm(ur2b=='lost' ~ age, 
                data = oral_demo_b, 
                family = binomial(link='logit'))
# BIC(logit_age) = 1533.407

# Estimate the ages at which 25, 50 and 75% of individuals lose primary ur2b: 
p = c(.25, .5, .75)
age_hat = round((log(p/(1-p)) - coef(logit_age)[1])/coef(logit_age)[2])

# Choose a range of representative age values (in years): 
age_rep = floor(age_hat[1]/12):ceiling(age_hat[3]/12)


# c. ---------------------------------------------------------------------------

# Select variables and clean data: 
oral_demo_c = oral_demo %>%
  select(ur2b = OHX04HTC, age = RIDAGEMN, gender = RIAGENDR, race = RIDRETH1, 
         pir = INDFMPIR) %>%
  filter(!is.na(ur2b), !is.na(age)) %>%
  filter(ur2b != 9) %>%
  mutate(ur2b = decode_ur2b(ur2b), race = decode_race(race), 
         gender = decode_gender(gender), race_mex = (race=='Mexican'), 
         race_black = (race=='Black'), race_other = (race=='Other'))

# Add 'gender' to the model: ---------------------------------------------------
logit_gender = glm(ur2b=='lost' ~ 0 + age + gender, 
                   data = oral_demo_c, 
                   family = binomial(link='logit'))
# BIC(logit_gender) = 1542.055 (does not improve) Do not retain 'gender'.

# Add each race category using the largest (White) as the reference: -----------

# Add 'race_mex' to the model
logit_mex = glm(ur2b=='lost' ~ age + race_mex, 
                data = oral_demo_c, 
                family = binomial(link='logit'))
# BIC(logit_mex) = 1542.285 (does not improve) Do not retain 'race_mex'.

# Add 'race_black' to the model
logit_black = glm(ur2b=='lost' ~ age + race_black, 
                  data = oral_demo_c, 
                  family = binomial(link='logit'))
# BIC(logit_black) = 1529.281 (improves) Retain 'race_black'.

# Add 'race_other' to the model
logit_other = glm(ur2b=='lost' ~ age + race_black + race_other, 
                  data = oral_demo_c, 
                  family = binomial(link='logit'))
# BIC(logit_other) = 1536.103 (does not improve) Do not retain 'race_other'.

# Add 'pir' to the model: ------------------------------------------------------
oral_demo_c = oral_demo_c %>%
  filter(!is.na(pir))

logit_pir = glm(ur2b=='lost' ~ age + race_black + pir, 
                data = oral_demo_c, 
                family = binomial(link='logit'))
# BIC(logit_pir) = 1462.895 (improves) Retain 'pir'.

# The final model contains 'age', 'race_black' and 'pir': ----------------------
logit_final = logit_pir


# d. ---------------------------------------------------------------------------

# d.1 Adjusted predictions at the mean at rep. ages
p_black = c(1-mean(oral_demo_c$race_black), mean(oral_demo_c$race_black))
pir_mean = mean(oral_demo_c$pir)
fit_d1 = sapply(age_rep*12, 
                function(x) sum(coef(logit_final)*c(1, x, p_black[2], pir_mean))
                )
t_q3d1 = tibble(age = age_rep,
              adjusted_pred = exp(fit_d1) / (1 + exp(fit_d1)))

# d.2 Marginal effects at the mean of 'race_black' at rep. ages
fit_d2 = sapply(age_rep*12, 
                function(x) c(sum(coef(logit_final)*c(1, x, 0, pir_mean)),
                              sum(coef(logit_final)*c(1, x, 1, pir_mean)))
                 )
adjusted_pred_d2 = exp(fit_d2) / (1 + exp(fit_d2))
t_q3d2 = tibble(age = age_rep, 
                marginal_effect = adjusted_pred_d2[2,]-adjusted_pred_d2[1,])

# d.3 Average marginal effects of 'race_black' at rep. ages
n = nrow(oral_demo_c)
t_q3d3 = tibble(age = rep(rep(age_rep*12, each=n), 2), 
                race_black = rep(c(FALSE, TRUE), each=5*n), 
                pir = rep(oral_demo_c$pir, 10),
                w_black = rep(p_black, each=5*n))
t_q3d3 = t_q3d3 %>%
  mutate(fit_value = predict(logit_final, t_q3d3[,1:3], type='response')
         ) %>%
  group_by(age, race_black) %>%
  summarize_at(vars(fit_value), mean) %>%
  summarize_at(vars(fit_value), diff) %>%
  transmute(age = age_rep, avg_marginal_effect = fit_value)


# 80: --------------------------------------------------------------------------
