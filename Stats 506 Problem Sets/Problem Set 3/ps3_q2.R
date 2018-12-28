## R Script for Question 2 in Problem Set 3
##
## This is an R script meant to design a Monte Carlo study to compare the
## performance of different methods that adjust for multiple comparisons. 
##
## Author: Xinye Jiang
## Updated: November 17, 2018

# 80:  -------------------------------------------------------------------------

# Setting the values of n, p and beta: -----------------------------------------
n = 1000
p = 100
beta = c(rep(1, 10), rep(0, 100-10))

# Compute the set of indices where beta is not equal to 0: ---------------------
ind = which(beta != 0)


# a. ---------------------------------------------------------------------------

# Function to generate a p-value matrix showing the significance of beta: ------
pval_mc = function(X, beta, sigma_y = 1, mc_rep = 1000){
  # X        - n by p matrix of predictor variables
  # beta     - parameter vector of length p
  # sigma_y  - the standard deviation of the population of Y
  # mc_rep   - number of Monte Carlo replicates
  
  # For testing the function, use 'set.seed(3)' to generate the same Y
  n = dim(X)[1]
  p = dim(X)[2]
  set.seed(3)
  Y_error = rnorm(n*mc_rep, 0, sigma_y)
  dim(Y_error) = c(n, mc_rep)
  Y = as.vector(X %*% beta) + Y_error
  
  # i. Compute beta_hat
  QR = qr(t(X) %*% X)
  beta_hat = solve(qr.R(QR), t(qr.Q(QR)) %*% t(X) %*% Y)
  
  # ii. Estimate the error variance for each Monte Carlo trial
  Y_hat = X %*% beta_hat
  sigma_mc = 1 / (n-p) * colSums( (Y-Y_hat)^2 )
  dim(sigma_mc) = c(1, mc_rep)
  
  # iii. Find the variance of beta_hat for each trial
  v = diag( chol2inv( chol(t(X) %*% X) ) )
  dim(v) = c(p, 1)
  v = as.vector(v %*% sigma_mc)
  
  # iv. Form Z and find p for each trial
  Z = beta_hat / sqrt(v)
  pval = 2 * (1 - pnorm(abs(Z)))
  
  # return a p by mc_rep matrix of p-values
  return(pval)
}

# Test the function with a specific X and Y: -----------------------------------
# Compare its output to the one that generates from lm(Y ~ 0 + X).

# Set a specific test X and Y: 
sigma_X_test = diag(10, p, p)   # Set the testing sigma matrix of X
R_test = chol(sigma_X_test)     # Use the Cholesky factorization to generate X
X_test = rnorm(n*p)
dim(X_test) = c(n, p)
X_test = X_test %*% R_test 
sigma_y_test = 1                # Set the testing sigma of Y
set.seed(3)                     # Use 'set.seed(3)' to generate the same Y
Y_error_test = rnorm(n, 0, sigma_y_test)
Y_test = as.vector(X_test %*% beta) + Y_error_test

# Compute the p-values using the above 'pval_mc' function: 
p_mc_test = pval_mc(X_test, beta, sigma_y_test, mc_rep=1)

# Get the p-values using 'lm' function: 
lm_test = lm(Y_test ~ 0 + X_test)
p_lm_test = summary(lm_test)$coefficients[,4]

# Test whether they're 'equal', allow for precision error less than 1e-3: 
test_result = all.equal(as.vector(p_mc_test), 
                        as.vector(p_lm_test), 
                        tolerance = 1e-3)
## test_result is 'TRUE', showing that 'pval_mc' can calculate what we want.


# b. ---------------------------------------------------------------------------

# Generate X and Y:
sigma_X = diag(1, p, p)        # Set the sigma matrix of X
sigma_y = 1                    # Set the sigma of Y
R = chol(sigma_X)              # Use the Cholesky factorization to generate X
X = rnorm(n*p)
dim(X) = c(n, p)
X = X %*% R 

# Pass X, beta and sigma_y to the 'pval_mc' function: 
p_mc = pval_mc(X, beta, sigma_y, mc_rep=1000)


# c. ---------------------------------------------------------------------------

# Function to compute Monte Carlo estimates for some quantities: 
evaluate = function(pval, ind){
  # pval  - a p by mc_rep matrix of p-values
  # ind   - the set of indices where beta is not equal to 0
  
  # Compute Monte Carlo estimates
  p = dim(pval)[1]
  n_error = colSums( pval[-ind,] < 0.05 )
  p_error = mean( {n_error >= 1} )               # The family wise error rate
  n_power = colSums( pval[ind,] < 0.05 )
  p_false = mean(n_error / (n_error + n_power))  # The false discovery rate
  p_sensitivity = mean(n_power / length(ind))            # The sensitivity
  p_specificity = mean(1 - n_error / (p - length(ind)))  # The specificity
  
  # Return 4 Monte Carlo estimates
  return(c(p_error, p_false, p_sensitivity, p_specificity))
}


# d. ---------------------------------------------------------------------------

# Apply the 'evaluate' function to the matrix of uncorrected p-values: 
est_mc_uncorrected = evaluate(p_mc, ind)

# Correct the p-values using 'Bonferroni', 'Holm', 'BH' and 'BY': 
p_mc_bonferroni = matrix(p.adjust(p_mc, method="bonferroni"), nrow=p)
p_mc_holm = matrix(p.adjust(p_mc, method="holm"), nrow=p)
p_mc_bh = matrix(p.adjust(p_mc, method="BH"), nrow=p)
p_mc_by = matrix(p.adjust(p_mc, method="BY"), nrow=p)

# Use the 'evaluate' function for each set of adjusted p-values:
est_mc_bonferroni = evaluate(p_mc_bonferroni, ind)
est_mc_holm = evaluate(p_mc_holm, ind)
est_mc_bh = evaluate(p_mc_bh, ind)
est_mc_by = evaluate(p_mc_by, ind)

# Generate the matrix for the table: 
est_mc = rbind(est_mc_uncorrected, est_mc_bonferroni, est_mc_holm,
               est_mc_bh, est_mc_by)
rownames(est_mc) = c("Uncorrected", "Bonferroni", "Holm", "BH", "BY")

# Generate the data frame for the figure: 
est_mc_figure = data.frame(
  est_mc = c(est_mc_uncorrected, est_mc_bonferroni, est_mc_holm, 
             est_mc_bh, est_mc_by)*100, 
  method = rep(rownames(est_mc), each = 4),
  type = rep(c("family wise error rate", "false discovery rate",
               "sensitivity", "specificity"), 5)
  )


# 80:  -------------------------------------------------------------------------
