data {
  int k;         // number of predictors including intercept
  int n;         // number of observations
  matrix[n,k] x;                       // dim=n*k, data of predictors
  int<lower=0,upper=1> y[n];          // vector y of length n, data of response
}

parameters {
  real mu[k];              //mean of the normal prior
  real<lower=0> sigma2[k];  //variance in normal prior
  vector[k] beta;           // interested parameters for logistic regression, beta, including intercept
  real<lower=0> a;          //hyper parametr
  real<lower=0> b;          // hyper parametr
}

model {
  sigma2 ~ inv_gamma(a,b);        //hyper prior of variance
  beta ~ normal(mu,sqrt(sigma2));  //prior
  y ~ bernoulli_logit(x*beta);       //likelihood
}
