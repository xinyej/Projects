#!/bin/bash  

## Shell Script for Question 1 in Problem Set 1
## 
## This is a shell script meant to illustrate some contents of
## the 2015 Residential Energy Consumption Survey data set.
##
## Author: Xinye Jiang (xinyej@umich.edu)
## Updated: September 30, 2018

# Part A,i. Count the number of rows for region 3:
cut -d ',' -f 2 recs2015_public_v3.csv | grep 3 | wc -l

# Part A,ii. Create a compressed data set containing only some variables:
cut -d ',' -f 1,475-571 recs2015_public_v3.csv > Aii.csv | gzip Aii.csv

# Part B,i. 'for' loop to count and print the number of obs in each region:
awk -F "," 'NR>1 {col[$2]++} END {for(i in col) print i,col[i]}' recs2015_public_v3.csv

# Part B,ii. Produce a file providing a sorted list:
awk -F ',' '{print $2,$3}' recs2015_public_v3.csv | (read -r; printf "%s\n" "$REPLY"; sort -V) | uniq > region_division.txt
