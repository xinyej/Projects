## R Script for Question 3 in Problem Set 1
##
## This is an R script meant to illustrate some contents about the
## 2015 Residential Energy Consumption Survey (RECS 2015) data set.
##
## Author: Xinye Jiang
## Updated: September 30, 2018

# 80:  -------------------------------------------------------------------------
# libraries: -------------------------------------------------------------------
library(dplyr)

# Get data: --------------------------------------------------------------------
recs_2015 = readr::read_delim("recs2015_public_v3.csv", delim=',')


# Functions to decode variables in the RECS 2015 data set: ---------------------

# Functions to decode divisions
decode_division = function(x){
  if(!is.numeric(x)) {
    stop('decode_division expects numeric input indexed from 1!')
  }
  switch(x, 
         "New England", "Middle Atlantic", "East North Central", 
         "West North Central", "South Atlantic", "East South Central", 
         "West South Central", "Mountain North", "Mountain South", "Pacific"
         )
}

decode_all_division = function(x){
  sapply(x, decode_division)
}

# Functions to decode walltypes
decode_walltype = function(x){
  if(!is.numeric(x)) {
    stop('decode_walltype expects numeric input indexed from 1!')
  }
  switch(x, 
         'Brick', 'Wood', 'Siding', 'Stucco', 'Shingle(composition)', 
         'Stone', 'Concrete of concrete block', '', 'Other'
         )
}

decode_all_walltype = function(x){
  sapply(x, decode_walltype)
}

# Functions to decode urban or rural status
decode_status = function(x){
  if(!is.character(x)) {
    stop('decode_status expects character input!')
  }
  # Note that here assume "Urban Area" and "Urban Cluster" as "Urban".
  switch(x, 
         U="Urban", 
         C="Urban", 
         R="Rural"
         )
}

decode_all_status = function(x){
  sapply(x, decode_status)
}


# a. ---------------------------------------------------------------------------

# Compute the percent of homes with stucco construction in each division: ------
home_div_prop_se = recs_2015 %>%
  select(division = DIVISION, walltype = WALLTYPE, weight = NWEIGHT, 
         BRRWT1:BRRWT96) %>%
  mutate(division = decode_all_division(division), 
         walltype = decode_all_walltype(walltype)
         ) %>%
  group_by(division, walltype) %>%
  summarise_all(funs(sum)) %>%
  mutate_at(vars(weight:BRRWT96), .funs = funs(100*./sum(.))) %>%
  filter(walltype == "Stucco")

# Add standard errors: ---------------------------------------------------------
home_div_prop_se$se = apply(home_div_prop_se[,3:99], 
                            1, 
                            function(x) sqrt(1/96/((1-0.5)^2)*sum((x-x[1])^2))
                            )

# Select the required variables and sort by percent: ---------------------------
home_div_prop_se = home_div_prop_se %>%
  select(division, percent = weight, se) %>%
  arrange(desc(percent))


# b. ---------------------------------------------------------------------------

# Compute average total electricity usage in each division: --------------------
elec_div_se = recs_2015 %>%
  select(division = DIVISION, elec = KWH, weight = NWEIGHT, BRRWT1:BRRWT96) %>%
  mutate(division = decode_all_division(division)) %>%
  group_by(division) %>%
  summarise_at(vars(weight:BRRWT96), .funs = funs(sum(elec*.)/sum(.)))

# Add standard errors: ---------------------------------------------------------
elec_div_se$se = apply(elec_div_se[,2:98],
                       1,
                       function(x) sqrt(1/96/((1-0.5)^2)*sum((x-x[1])^2))
                       )

# Select variables and sort by average total electricity usage: ----------------
elec_div_se = elec_div_se %>%
  select(division, avg_total_elec = weight, se) %>%
  arrange(desc(avg_total_elec))

# Compute average total electricity usage by urban and rural status: -----------
elec_status_se = recs_2015 %>%
  select(status = UATYP10, elec = KWH, weight = NWEIGHT, BRRWT1:BRRWT96) %>%
  mutate(status = decode_all_status(status)) %>%
  group_by(status) %>%
  summarise_at(vars(weight:BRRWT96), .funs = funs(sum(elec*.)/sum(.)))

# Add standard errors: ---------------------------------------------------------
elec_status_se$se = apply(elec_status_se[,2:98],
                          1,
                          function(x) sqrt(1/96/((1-0.5)^2)*sum((x-x[1])^2))
                          )

# Select variables and sort by average total electricity usage: ----------------
elec_status_se = elec_status_se %>%
  select(status, avg_total_elec = weight, se) %>%
  arrange(desc(avg_total_elec))


# c. ---------------------------------------------------------------------------

# Compute the percent disparity between urban and rural areas by division: -----
home_div_status = recs_2015 %>%
  select(division = DIVISION, status = UATYP10, internet = INTERNET, 
         weight = NWEIGHT, BRRWT1:BRRWT96) %>%
  mutate(division = decode_all_division(division), 
         status = decode_all_status(status)
         ) %>%
  group_by(division, status) %>%
  summarise_at(vars(weight:BRRWT96),
               .funs = funs(100*sum(internet*.)/sum(.))) %>%
  summarise_at(vars(weight:BRRWT96),diff)

# Add standard errors: ---------------------------------------------------------
home_div_status$se = apply(home_div_status[,2:98],
                           1,
                           function(x) sqrt(1/96/((1-0.5)^2)*(sum((x-x[1])^2)))
                           )

# Select variables and sort by disparity: --------------------------------------
home_div_status = home_div_status %>%
  select(division, disparity = weight, se) %>%
  arrange(desc(disparity))


# 80: --------------------------------------------------------------------------
