## R Script for Question 2c in Problem Set 4
##
## This is an R script that uses 'future' package for parallelism.
## 
## Author: Xinye Jiang
## Updated: December 10, 2018

# 80:  -------------------------------------------------------------------------

# R args: ----------------------------------------------------------------------
args = commandArgs(trailingOnly = TRUE)
ind = grep('=', args)  
args_list = strsplit(args[ind], '=')
lapply(args_list, 
       function(x) assign(x[1], as.numeric(x[2]), envir = .GlobalEnv))

# libraries: -------------------------------------------------------------------
library(future)
library(parallel)

# Source functions: ------------------------------------------------------------
source("ps4_q2_funcs.R")

# Parameters: ------------------------------------------------------------------
n = 1e3; p = 1e2; r = .1; tp_ind = 1:10
beta_rp = c( rep(.1, floor(r*p)), rep(0, p - floor(r*p)) ) 
dim(beta_rp) = c(p, 1)
rho_q4c = 0.25 * (-3:3)

# Simulation function: ---------------------------------------------------------
sim_q4c = function(rho, sigma_y = 1, beta = beta_rp, n = 1e3, p = 1e2, 
                   mc_rep = 1e4, tp_ind = 1:10){
  # rho       - correlation between xi and xj when i is not equal to j
  # sigma_y   - std deviation for Y|X,  Y|X ~ N(XB, sigma^2 I)
  # beta      - a p by 1 numeric matrix
  # n         - the number of observations
  # p         - the number of parameters
  # mc_rep    - the number of Monte Carlo replications to use
  # tp_ind    - the set of indices where beta is not equal to 0
  #
  # Output: a long data frame with columns (rho,sigma,metric,method,est,se)
  
  # Generate X and record method names: 
  sigma_x = rho * beta %*% t(beta)
  diag(sigma_x) = 1
  R = chol(sigma_x)
  X = matrix( rnorm(n*p), n, p) %*%  R
  multicp_method = c('holm', 'bonferroni', 'BH', 'BY')
  
  # Calculate a p by mc_rep matrix of p-values: 
  P = sim_beta(X, beta, sigma_y, mc_rep)
  
  # Calculate the assessment measure values regarding each method: 
  eval = do.call('rbind', 
                 lapply(multicp_method, function(x){
                   evaluate( apply(P, 2, p.adjust, method = x), 
                             tp_ind = tp_ind) } ) )
  
  # Return a long data frame of results: 
  data.frame(rho = rho, 
             sigma = sigma_y, 
             metric = eval$metric, 
             method = rep(multicp_method, each = 4), 
             est = eval$est, 
             se = eval$se)
}

# Run parallel simulations using 'plan(multisession)': -------------------------
plan(multisession)
results_q4c_list = list()
for(i in 1:length(rho_q4c)){
  results_q4c_list[[i]] = future(sim_q4c(rho_q4c[i], sigma, mc_rep = mc_rep))
}
results_q4c = do.call('rbind',
                      lapply(results_q4c_list, value))

# Print the results: -----------------------------------------------------------
print(results_q4c)


# 80:  -------------------------------------------------------------------------
