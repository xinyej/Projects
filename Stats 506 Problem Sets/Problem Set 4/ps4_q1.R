## R Script for Question 1 in Problem Set 4
##
## This is an R script that shows an SQL query to construct a table 
## showing the all-time leader in hits for each birth country.
## 
## Author: Xinye Jiang (xinyej@umich.edu)
## Updated: December 10, 2018

# 80:  -------------------------------------------------------------------------

# libraries: -------------------------------------------------------------------
library(tidyverse)
library(dbplyr)
library(Lahman)

# Create a local SQLite database of the Lahman data: ---------------------------
# Need to run 'install.packages("RSQLite")' first.
lahman = lahman_sqlite()

# SQL Query to find the all-time leader in hits for each birth country: --------
hit_birth = lahman %>% tbl(sql(
'
  SELECT nameFirst, nameLast, debut, birthCountry, Hits 
  FROM master m
  INNER JOIN
    (SELECT sum(H) as Hits, playerID
     FROM batting
     GROUP BY playerID
     HAVING Hits >= 200
    ) b 
  ON b.playerID = m.playerID
  GROUP BY birthCountry
  HAVING Hits == max(Hits)
  ORDER BY -Hits
'
))

# Create the corresponding table: ----------------------------------------------
hit_birth_table = hit_birth %>% 
  collect() %>%
  transmute(Player = paste(nameFirst, nameLast), 
            Debut = debut,
            `Country of Birth` = birthCountry,
            Hits = format(Hits, big.mark = ','))
  

# 80:  -------------------------------------------------------------------------