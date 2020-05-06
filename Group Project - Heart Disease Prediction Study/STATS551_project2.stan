data {
  int k;        // number of predictors including intercept       
  int n;        // number of observations
  matrix[n,k] x;                    // matrix dimension n*k, data of predictors
  int<lower=0,upper=1> y[n];         // vector y of length n, data of response variable
}

parameters {
  real<lower=0> sigma2[k]; //hyper parameter, variances
  real<lower=0> lambda;    //interested parameter, shrinkage parameter
  vector[k] beta;          //interested parameter, beta

}

model {
  lambda ~ gamma(5,5);            //hyper prior of lambda
  sigma2 ~ exponential(lambda);   //hyper prior of varaince
  beta ~ normal(0,sqrt(sigma2));   // prior of beta, normal
  y ~ bernoulli_logit(x*beta);    //likelihood
}
