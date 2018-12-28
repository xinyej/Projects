## R Script of functions for Question 2 in Problem Set 4
##
## This is an R script that includes the functions from  
## part a and c of PS3 Q2.
## 
## Author: Xinye Jiang
## Updated: December 9, 2018

# 80:  -------------------------------------------------------------------------

# Part a, Monte Carlo simulation function for fixed X: -------------------------
sim_beta = function(X, beta, sigma = 1, mc_rep = 1e4){
  # Simulate Y from Y|X ~ N(XB, sigma^2 I) and compute p-values corresponding to
  # Wald tests for B != 0. Repeat mc_rep times.
  #
  # Arguments:
  #   X       - an n by p numeric matrix
  #   beta    - a p by 1 numeric matrix
  #   sigma   - std deviation for Y|X,  Y|X ~ N(XB, sigma^2 I)
  #   mc_rep  - The number of Monte Carlo replications to use
  #
  # Output: A p by mc_rep matrix of p-values
  
  # This part doesn't need to change for each replication
  QR = qr( crossprod(X) )
  QX = X %*% qr.Q(QR) 
  XtXinv = solve( qr.R(QR), t( qr.Q(QR) ))
  
  n = nrow(X)
  p = ncol(X)
  
  # Generate mc_rep copies of Y at once, each in a column.
  Y = as.numeric(X %*% beta) + rnorm(n*mc_rep)
  dim(Y) = c(n, mc_rep)
  
  # estimate betas and residual standard errors
  b = solve(qr.R(QR), crossprod( QX, Y ) )
  
  # It's okay if you divide by {n - p} outside the sum, but this
  # is more comparable to what is done by .lm.fit()
  s_sq = colSums( {Y - as.numeric(X %*% b)}^2 / {n - p})
  
  # standard error of b
  v = sqrt( diag(XtXinv) * rep(s_sq, each = p) )
  
  # return a matirx of p-values
  # Use pt to replicate lm, but the normal approximation is fine here. 
  matrix( 2*pt( abs( b / v ), df = {n-p}, lower.tail = FALSE ), p, mc_rep )  
}


# Part c, evaluate: ------------------------------------------------------------
evaluate = function(P, tp_ind, alpha = .05){
  # P        - a p by mc_rep matrix of p-values
  # tp_ind   - the set of indices where beta is not equal to 0
  # alpha    - significance level
  
  P = P < alpha
  p = nrow(P)
  n = ncol(P)
  
  # Compute TP, FP, TN, FN for each replcation
  TP = colSums(P[tp_ind, ])
  FP = colSums(P[-tp_ind,])
  TN = colSums(!P[-tp_ind,])
  FN = colSums(!P[tp_ind,])
  
  # Call FDR 0 when no discoveries. 
  P = FP + TP
  fdr = ifelse(P > 0, FP  / {FP + TP}, 0)
  fwer = mean( FP > 0 )
  sens = TP / {TP + FN}
  spec = TN / {FP + TN}
  
  data.frame( metric = c("FWER", "FDR", "Sensitivity", "Specificity"),
              est = c(fwer, mean(fdr), mean(sens), mean(spec)),
              se = c(sqrt(fwer * {1 - fwer} / n), sd(fdr) / sqrt(n),
                     sd(sens) / sqrt(n), sd(spec) / sqrt(n))
  )
}

# 80:  -------------------------------------------------------------------------
