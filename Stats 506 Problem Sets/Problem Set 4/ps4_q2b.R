## R Script for Question 2b in Problem Set 4
##
## This is an R script that uses 'doParallel' and nested foreach
## loops to run simulations. 
## 
## Author: Xinye Jiang
## Updated: December 10, 2018

# 80:  -------------------------------------------------------------------------

# libraries: -------------------------------------------------------------------
library(parallel)
library(doParallel)

# Set up a cluster: ------------------------------------------------------------
ncores = 4
cl = makeCluster(ncores)

# Register the parallel backend: -----------------------------------------------
registerDoParallel(cl)

# Source functions: ------------------------------------------------------------
source("ps4_q2_funcs.R")

# Parameters: ------------------------------------------------------------------
n = 1e3; p = 1e2; r = .1; tp_ind = 1:10
beta_rp = c( rep(.1, floor(r*p)), rep(0, p - floor(r*p)) ) 
dim(beta_rp) = c(p, 1)
rho_q4b = 0.25 * (-3:3)
sigma_y_q4b = c(0.25, 0.5, 1)

# Simulation function: ---------------------------------------------------------
sim_q4b = function(rho, sigma_y = 1, beta = beta_rp, n = 1e3, p = 1e2, 
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

# Run simulations using doParallel and reorganize the results: -----------------
results_q4b =
  foreach(rho=rho_q4b, 
          .combine='rbind') %:% 
    foreach(sigma_y=sigma_y_q4b, 
            .combine='rbind') %dopar% {
              sim_q4b(rho, sigma_y)
            }

# Save the results to a file: --------------------------------------------------
save(results_q4b, file = "results_q4b.RData")

# Shut down the cluster after use: ---------------------------------------------
stopCluster(cl)


# 80:  -------------------------------------------------------------------------
