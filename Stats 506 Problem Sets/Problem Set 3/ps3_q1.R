## R Script for Question 1 in Problem Set 3
##
## This is an R script meant to illustrate some contents about the
## 2015 Residential Energy Consumption Survey (RECS 2015) data set.
##
## Author: Xinye Jiang (xinyej@umich.edu)
## Updated: November 3, 2018

# 80:  -------------------------------------------------------------------------
# libraries: -------------------------------------------------------------------
library(data.table)
library(magrittr)

# Get data: --------------------------------------------------------------------
recs2015 = fread("https://www.eia.gov/consumption/residential/data/2015/csv/recs2015_public_v3.csv")

# Functions to decode variables in the RECS 2015 data set: ---------------------

# Functions to decode divisions
decode_division = function(xvec){
  sapply(xvec, function(x) switch(x, "New England", "Middle Atlantic", 
                               "East North Central", "West North Central", 
                               "South Atlantic", "East South Central", 
                               "West South Central", "Mountain North", 
                               "Mountain South", "Pacific"))
}

# Functions to decode walltypes
decode_walltype = function(xvec){
  sapply(xvec, function(x) switch(x, 'Brick', 'Wood', 'Siding', 'Stucco',
                                  'Shingle(composition)', 'Stone', 
                                  'Concrete of concrete block', '', 'Other'))
}

# Functions to decode urban or rural status
# Note that here assume "Urban Area" and "Urban Cluster" as "Urban".
decode_status = function(xvec){
  sapply(xvec, function(x) switch(x, U="Urban", C="Urban", R="Rural"))
}

# Functions to decode regions
decode_region = function(xvec){
  sapply(xvec, function(x) switch(x, "Northeast", "Midwest", "South", "West"))
}

# Functions to decode house types
decode_house = function(xvec){
  sapply(xvec, function(x) switch(x, "Mobile", "Single-family detached", 
                                  "Single-family attached", 
                                  "Apartment 2-4 units", 
                                  "Apartment 5- units"))
}

# Get the character vector of column names with 'BRRWT' in 'recs2015' ----------
weight_cols = names(recs2015)[grep('BRRWT', names(recs2015))]

# Multiplier for confidence level: ---------------------------------------------
m = qnorm(.975)

# a. ---------------------------------------------------------------------------

# Compute the percent of homes with stucco construction in each division: 
home_stucco_div = recs2015[, c("DIVISION", "WALLTYPE", "NWEIGHT", weight_cols),
                           with=FALSE] %>%
  .[, `:=`(division = decode_division(DIVISION), 
           walltype = decode_walltype(WALLTYPE), 
           BRRWT0 = NWEIGHT)] %>%
  .[, c("division", "walltype", "BRRWT0", weight_cols), with=FALSE] %>%
  melt(id.vars = c("division", "walltype"), variable.name = "weight_id", 
       value.name = "weight") %>%
  .[, .(weight = sum(weight)), by = .(weight_id, division, walltype)] %>%
  .[, p_stucco := 100 * weight / sum(weight), by = .(weight_id, division)] %>%
  .[walltype == "Stucco"] %>%
  .[, .(p_stucco = p_stucco[1], 
        se_stucco = 2*sqrt(mean({p_stucco-p_stucco[1]}^2)*97/96)),
    by = .(division)] %>%
  .[, `:=`(lwr = pmax(p_stucco - m*se_stucco, 0), 
           upr = p_stucco + m*se_stucco)] %>%
  .[, CI := sprintf('(%6.2f, %6.2f)', lwr, upr)] %>%
  .[order(-p_stucco)]


# b. ---------------------------------------------------------------------------

# Compute average total electricity usage in each division: 
elec_div = recs2015[, c("DIVISION", "KWH", "NWEIGHT", weight_cols),
                    with=FALSE] %>%
  .[, `:=`(division = decode_division(DIVISION), 
           elec = KWH, BRRWT0 = NWEIGHT)] %>%
  .[, c("division", "elec", "BRRWT0", weight_cols), with=FALSE] %>%
  melt(id.vars = c("division", "elec"), variable.name = "weight_id", 
       value.name = "weight") %>%
  .[, .(avg_elec = sum(weight*elec) / sum(weight)), 
    by = .(weight_id, division)] %>%
  .[, .(avg_elec = avg_elec[1], 
        avg_elec_se = 2*sqrt(mean({avg_elec-avg_elec[1]}^2)*97/96)),
    by = .(division)] %>%
  .[, `:=`(lwr = pmax(avg_elec - m*avg_elec_se, 0), 
           upr = avg_elec + m*avg_elec_se)] %>%
  .[, CI := sprintf('(%6.2f, %6.2f)', lwr, upr)] %>%
  .[order(-avg_elec)]

# Compute average total electricity usage by urban status in each division: 
elec_urban_div = recs2015[, c("DIVISION", "UATYP10", "KWH", "NWEIGHT", 
                              weight_cols), with=FALSE] %>%
  .[, `:=`(division = decode_division(DIVISION), 
           status = decode_status(UATYP10), 
           elec = KWH, BRRWT0 = NWEIGHT)] %>%
  .[, c("division", "status", "elec", "BRRWT0", weight_cols), with=FALSE] %>%
  melt(id.vars = c("division", "status", "elec"), 
       variable.name = "weight_id", 
       value.name = "weight") %>%
  .[, .(avg_elec = sum(weight*elec) / sum(weight)), 
    by = .(weight_id, division, status)] %>%
  .[, .(avg_elec = avg_elec[1], 
        avg_elec_se = 2*sqrt(mean({avg_elec-avg_elec[1]}^2)*97/96)),
    by = .(division, status)] %>%
  .[, `:=`(lwr = pmax(avg_elec - m*avg_elec_se, 0), 
           upr = avg_elec + m*avg_elec_se)] %>%
  .[order(division, -avg_elec)]  


# c. ---------------------------------------------------------------------------

# Compute homes with internet percent by urban status in each division: 
internet_urban_div = recs2015[, c("DIVISION", "UATYP10", "INTERNET", 
                                  "NWEIGHT", weight_cols), with=FALSE] %>%
  .[, `:=`(division = decode_division(DIVISION), 
           status = decode_status(UATYP10), 
           internet = INTERNET, BRRWT0 = NWEIGHT)] %>%
  .[, c("division", "status", "internet", "BRRWT0", weight_cols), 
    with=FALSE] %>%
  melt(id.vars = c("division", "status", "internet"), 
       variable.name = "weight_id", 
       value.name = "weight") %>%
  .[, .(p = 100 * sum(weight*internet) / sum(weight)), 
    by = .(weight_id, division, status)]

# Compute proportion disparity with between urban and rural areas: 
internet_disp = internet_urban_div[, c("division", "status", "p", 
                                       "weight_id"), with=FALSE] %>% 
  .[order(weight_id, division, status)] %>%
  .[, .(disp = p[2]-p[1]), by = .(weight_id, division)] %>%
  .[, .(disp = disp[1], 
        disp_se = 2*sqrt(mean({disp-disp[1]}^2)*97/96)),
    by = .(division)] %>%
  .[, `:=`(disp_lwr = disp - m*disp_se, disp_upr = disp + m*disp_se)] %>%
  .[, disp_CI := sprintf('(%6.1f, %6.1f)', disp_lwr, disp_upr)]

# Calculate proportion's se and CI: 
internet_urban_div = internet_urban_div %>%
  .[, .(p = p[1], se = 2*sqrt(mean({p-p[1]}^2)*97/96)),
    by = .(division, status)] %>%
  .[, `:=`(lwr = pmax(p - m*se, 0), upr = p + m*se)]

# Combine two datasets together by 'division': 
internet = internet_urban_div %>%
  dcast(division ~ status, 
        value.var = c("p", "se", "lwr", "upr")) %>%
  .[, `:=`(urban_CI = sprintf('(%6.1f, %6.1f)', lwr_Urban, upr_Urban),
           rural_CI = sprintf('(%6.1f, %6.1f)', lwr_Rural, upr_Rural))] %>%
  merge(., internet_disp, by = 'division', all = TRUE) %>%
  .[order(-disp)]


# d. ---------------------------------------------------------------------------

# The question formulated is stated below:
# What is the percent of homes of mobile home type within each region?
# Which region has the highest proportion? Which has the lowest?

# Compute the percent of homes of mobile home type within each region: 
home_mobile_reg = recs2015[, c("REGIONC", "TYPEHUQ", "NWEIGHT", weight_cols),
                           with=FALSE] %>%
  .[, `:=`(region = decode_region(REGIONC), 
           house = decode_house(TYPEHUQ), 
           BRRWT0 = NWEIGHT)] %>%
  .[, c("region", "house", "BRRWT0", weight_cols), with=FALSE] %>%
  melt(id.vars = c("region", "house"), variable.name = "weight_id", 
       value.name = "weight") %>%
  .[, .(weight = sum(weight)), by = .(weight_id, region, house)] %>%
  .[, p := 100 * weight / sum(weight), by = .(weight_id, region)] %>%
  .[house == "Mobile"] %>%
  .[, .(p_mobile = p[1], 
        se_mobile = 2*sqrt(mean({p-p[1]}^2)*97/96)),
    by = .(region)] %>%
  .[, `:=`(lwr = pmax(p_mobile - m*se_mobile, 0), 
           upr = p_mobile + m*se_mobile)] %>%
  .[, CI := sprintf('(%6.2f, %6.2f)', lwr, upr)] %>%
  .[order(-p_mobile)]


# 80: --------------------------------------------------------------------------