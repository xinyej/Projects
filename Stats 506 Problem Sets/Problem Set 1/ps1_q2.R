## R Script for Question 2 in Problem Set 1
##
## This is an R script meant to illustrate some contents about
## flights originating in New York City, NY (NYC) in 2013 and 2014.
## 
## Author: Xinye Jiang
## Updated: September 30, 2018

# 80:  -------------------------------------------------------------------------

# libraries: -------------------------------------------------------------------
library(nycflights13)
library(dplyr)

# Get data: --------------------------------------------------------------------
flights14 = readr::read_delim(
  "https://raw.githubusercontent.com/wiki/arunsrinivasan/flights/NYCflights14/flights14.csv", 
  delim = ',')


# a. ---------------------------------------------------------------------------

# Compute each airline's flights' precent and 95% CI in 2013: ------------------
flights2013_airline_percent = flights %>%
  filter(month <= 10) %>%
  select(carrier) %>%
  group_by(carrier) %>%
  summarise(number_2013 = n()) %>%
  mutate(tot_2013 = sum(number_2013), 
         pct_2013 = 100*number_2013/tot_2013, 
         err_2013 = qnorm(0.975)*sqrt(pct_2013*(100-pct_2013)/tot_2013),
         lwr_2013 = pct_2013-err_2013, 
         upr_2013 = pct_2013+err_2013
         ) %>%
  filter(pct_2013 >= 1)

# Join with the airline name and sort by percent: ------------------------------
flights2013_airline_percent = airlines %>%
  inner_join(flights2013_airline_percent) %>%
  arrange(desc(pct_2013))


# b. ---------------------------------------------------------------------------

# Compute each airline's flights' precent and 95% CI in 2014: ------------------
flights2014_airline_percent = flights14 %>%
  filter(month <= 10) %>%
  select(carrier) %>%
  group_by(carrier) %>%
  summarise(number_2014 = n()) %>%
  mutate(tot_2014 = sum(number_2014), 
         pct_2014 = 100*number_2014/tot_2014, 
         err_2014 = qnorm(0.975)*sqrt(pct_2014*(100-pct_2014)/tot_2014), 
         lwr_2014 = pct_2014-err_2014,
         upr_2014 = pct_2014+err_2014
         )

# Join with the data of 2013 and compute change in percent with 95% CI: --------
flights_airline_percent = flights2013_airline_percent %>%
  left_join(flights2014_airline_percent, by="carrier") %>%    
  mutate(number_change = number_2014-number_2013,
         pct_change = pct_2014 - pct_2013,  
         err_change = qnorm(0.975)*sqrt(pct_2014*(100-pct_2014)/tot_2014
                                        +pct_2013*(100-pct_2013)/tot_2013), 
         lwr_change = pct_change-err_change,
         upr_change = pct_change+err_change
         ) %>%
  select(-c(tot_2014, tot_2013, err_2014, err_2013, err_change, carrier)) %>%
  arrange(pct_change)


# c. ---------------------------------------------------------------------------

# Compute each airline's flights' percent and CI in each airport in 2013: ------
flights2013_airport_percent = flights %>%
  select(origin, carrier) %>%
  group_by(origin, carrier) %>%
  summarise(number_2013 = n()) %>%
  mutate(tot_2013 = sum(number_2013), 
         pct_2013 = 100*number_2013/tot_2013, 
         err_2013 = qnorm(0.975)*sqrt(pct_2013*(100-pct_2013)/tot_2013), 
         lwr_2013 = pct_2013-err_2013,
         upr_2013 = pct_2013+err_2013
        ) %>%
  filter(carrier%in%flights2013_airline_percent$carrier) %>%
  right_join(airlines, by="carrier") %>%
  rename(faa = origin, airline = name) %>%
  inner_join(airports, by="faa") %>%
  rename(airport = name) %>%
  ungroup() %>%
  select(airport, airline, pct_2013, lwr_2013, upr_2013) %>%
  arrange(airport, desc(pct_2013))


# 80: --------------------------------------------------------------------------
