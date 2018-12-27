/*****************************************************
SAS Script for Question 3 in Problem Set 4

This is a SAS Script that does some data processing and analyzing work 
regarding the 2016 Medicare Provider Utilization and Payment data.

Author: Xinye Jiang (xinyej@umich.edu)
Updated: December 10, 2018
 *****************************************************
*/

/* 80: ************************************************************************/

/* a. Read the data into SAS */

DATA Medicare_PS_PUF;
	LENGTH
		npi              					$ 10
		nppes_provider_last_org_name 		$ 70
		nppes_provider_first_name 			$ 20
		nppes_provider_mi					$ 1
		nppes_credentials 					$ 20
		nppes_provider_gender				$ 1
		nppes_entity_code 					$ 1
		nppes_provider_street1 				$ 55
		nppes_provider_street2				$ 55
		nppes_provider_city 				$ 40
		nppes_provider_zip 					$ 20
		nppes_provider_state				$ 2
		nppes_provider_country				$ 2
		provider_type 						$ 55
		medicare_participation_indicator 	$ 1
		place_of_service					$ 1
		hcpcs_code       					$ 5
		hcpcs_description 					$ 256
		hcpcs_drug_indicator				$ 1
		line_srvc_cnt      					8
		bene_unique_cnt    					8
		bene_day_srvc_cnt   				8
		average_Medicare_allowed_amt   		8
		average_submitted_chrg_amt  		8
		average_Medicare_payment_amt   		8
		average_Medicare_standard_amt		8;
	INFILE './data/Medicare_Provider_Util_Payment_PUF_CY2016.txt'

		lrecl=32767
		dlm='09'x
		pad missover
		firstobs = 3
		dsd;

	INPUT
		npi             
		nppes_provider_last_org_name 
		nppes_provider_first_name 
		nppes_provider_mi 
		nppes_credentials 
		nppes_provider_gender 
		nppes_entity_code 
		nppes_provider_street1 
		nppes_provider_street2 
		nppes_provider_city 
		nppes_provider_zip 
		nppes_provider_state 
		nppes_provider_country 
		provider_type 
		medicare_participation_indicator 
		place_of_service 
		hcpcs_code       
		hcpcs_description 
		hcpcs_drug_indicator
		line_srvc_cnt    
		bene_unique_cnt  
		bene_day_srvc_cnt 
		average_Medicare_allowed_amt 
		average_submitted_chrg_amt 
		average_Medicare_payment_amt
		average_Medicare_standard_amt;

	LABEL
		npi     							= "National Provider Identifier"       
		nppes_provider_last_org_name 		= "Last Name/Organization Name of the Provider"
		nppes_provider_first_name 			= "First Name of the Provider"
		nppes_provider_mi					= "Middle Initial of the Provider"
		nppes_credentials 					= "Credentials of the Provider"
		nppes_provider_gender 				= "Gender of the Provider"
		nppes_entity_code 					= "Entity Type of the Provider"
		nppes_provider_street1 				= "Street Address 1 of the Provider"
		nppes_provider_street2 				= "Street Address 2 of the Provider"
		nppes_provider_city 				= "City of the Provider"
		nppes_provider_zip 					= "Zip Code of the Provider"
		nppes_provider_state 				= "State Code of the Provider"
		nppes_provider_country 				= "Country Code of the Provider"
		provider_type	 					= "Provider Type of the Provider"
		medicare_participation_indicator 	= "Medicare Participation Indicator"
		place_of_service 					= "Place of Service"
		hcpcs_code       					= "HCPCS Code"
		hcpcs_description 					= "HCPCS Description"
		hcpcs_drug_indicator				= "Identifies HCPCS As Drug Included in the ASP Drug List"
		line_srvc_cnt    					= "Number of Services"
		bene_unique_cnt  					= "Number of Medicare Beneficiaries"
		bene_day_srvc_cnt 					= "Number of Distinct Medicare Beneficiary/Per Day Services"
		average_Medicare_allowed_amt 		= "Average Medicare Allowed Amount"
		average_submitted_chrg_amt 			= "Average Submitted Charge Amount"
		average_Medicare_payment_amt 		= "Average Medicare Payment Amount"
		average_Medicare_standard_amt		= "Average Medicare Standardized Payment Amount";
RUN;


/* b. Reduce the data set */

data medicare_b;
  set Medicare_PS_PUF;
  where prxmatch("/MRI/", hcpcs_description) and prxmatch("/^7/", hcpcs_code);
run;


/* c. Determine the MRI procedures using proc means or proc summary */

/* Get the summary data */

data medicare_c;
  set medicare_b;
  total_payment = line_srvc_cnt*average_Medicare_payment_amt;

proc summary data=medicare_c;
  class hcpcs_description;
  output out=summary0
    sum(line_srvc_cnt) = volume
	sum(total_payment) = total;

data summary;
  set summary0;
  average = total/volume;
  if _TYPE_ = 0 then delete;
  drop _TYPE_ _FREQ_;

/* Pick MRI procedures with highest volume, total payment, average payment */

proc sort data=summary out=summary_vol;
  by descending volume;

data summary_vol;
  set summary_vol(obs=1);

proc sort data=summary out=summary_tot;
  by descending total;

data summary_tot;
  set summary_tot(obs=1);

proc sort data=summary out=summary_avg;
  by descending average;

data summary_avg;
  set summary_avg(obs=1);

/* Merge them into one table */

data mriproc_c;
  merge summary_vol summary_tot summary_avg;
  by hcpcs_description;
run;

/* d. Repeat part b-c using PROC SQL */

/* Reduce the dataset and determine the MRI procedures */
proc sql;
  create table mriproc_d as
  select hcpcs_description, volume, total, average
  from (
   select hcpcs_description, sum(line_srvc_cnt) as volume, 
   sum(line_srvc_cnt*average_Medicare_payment_amt) as total, 
   sum(line_srvc_cnt*average_Medicare_payment_amt)/sum(line_srvc_cnt) as average
   from Medicare_PS_PUF
   where prxmatch("/MRI/",hcpcs_description) and prxmatch("/^7/",hcpcs_code)
   group by hcpcs_description
   )
  having volume=max(volume) or total=max(total) or average=max(average);
quit;
run;

/* e. Export the results from part c and d to csv */

proc export data=mriproc_c
  outfile='./data/ps4_q3c.csv'
  dbms=dlm replace;
  delimiter = ",";

proc export data=mriproc_d
  outfile='./data/ps4_q3d.csv'
  dbms=dlm replace;
  delimiter = ",";
run;

/* Print out tables to verify whether the results are the same. */
/* And find out that the results are the same. */

proc print data=mriproc_c;

proc print data=mriproc_d;
run;
