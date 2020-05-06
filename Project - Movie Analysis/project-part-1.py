import json
import math
import re
from pyspark import SparkConf, SparkContext
import pandas as pd
import csv

# Manipulation:
## Step 1
movie = pd.read_csv("movies_metadata.csv")
movie = movie[['id','genres','revenue','release_date','vote_average','status']]
movie = movie.dropna()
movie = movie[movie['genres']!='[]']
movie = movie[movie['vote_average']!=0]
movie = movie[movie['status']=='Released']
movie['release_year'] = movie['release_date'].map(lambda x: int(str(x)[:4]))
movie = movie[(movie['release_year']>=1985) & (movie['release_year']<=2015)]
movie = movie[['id','genres','revenue','release_year','vote_average']]
movie['id'] = movie['id'].astype(int)

credit = pd.read_csv('credits.csv')
credit = credit[credit['crew']!='[]']

## Step 2
def find_director_gender(line):
    for i in eval(line):
        if i['job']=='Director':
            return i['name'], i['gender']

tuple_d_g = credit['crew'].map(lambda x: find_director_gender(x))
credit = credit[pd.notnull(tuple_d_g)]
tuple_d_g = tuple_d_g[pd.notnull(tuple_d_g)]
credit['director'] = [i[0] for i in tuple_d_g]
credit['gender'] = [i[1] for i in tuple_d_g]

credit = credit[['id','director','gender']]

## Step 3
conf = SparkConf().setAppName('project1')
sc = SparkContext(conf=conf)

credits = sc.parallelize(credit.values.tolist()).map(lambda line: (line[0], line[1:]))
movies = sc.parallelize(movie.values.tolist()).map(lambda line: (line[0], line[1:]))
movies_credits = movies.join(credits)

# Analysis
## Task 1
task1 = movies_credits.map(lambda line: (line[1][1][0], line[1][0][1], 1)) \
    .filter(lambda x: x[1] > 0) \
    .map(lambda x: (x[0], x[1:])) \
    .reduceByKey(lambda a,b: (a[0]+b[0], a[1]+b[1])) \
    .filter(lambda x: x[1][1] > 5) \
    .mapValues(lambda v: (v[0]/v[1])) \
    .sortBy(lambda x: x[1], ascending = False) \
    .map(lambda x: ','.join([str(i) for i in x])) \
    .saveAsTextFile("task1")

## Task 2
task2 = movies_credits.map(lambda line: (line[1][1][0], line[1][0][3], 1)) \
    .map(lambda x: (x[0], x[1:])) \
    .reduceByKey(lambda a,b: (a[0]+b[0], a[1]+b[1])) \
    .filter(lambda x: x[1][1] > 5) \
    .mapValues(lambda v: (v[0]/v[1])) \
    .sortBy(lambda x: x[1], ascending = False) \
    .map(lambda x: ','.join([str(i) for i in x])) \
    .saveAsTextFile("task2")

## Task 3
def gen_genre_year_tuples(line):
    tuples = []
    for i in eval(line[1][0][0]):
        genre = i['name']
        gender = line[1][1][1]
        if gender == 1 or gender == 2:
            ### female takes 1, male takes 0
            ### genre, release year, gender, count
            tuples.append( ( (genre, line[1][0][2]), (2-gender, 1) ) )
    return tuples

task3 = movies_credits.flatMap(gen_genre_year_tuples) \
    .reduceByKey(lambda a,b: (a[0]+b[0], a[1]+b[1])) \
    .mapValues(lambda v: (float(v[0])/float(v[1])*100)) \
    .sortBy(lambda x: x[1], ascending = False) \
    .map(lambda x: (x[0][0], x[0][1], x[1])) \
    .map(lambda x: ','.join([str(i) for i in x])) \
    .saveAsTextFile("task3")
