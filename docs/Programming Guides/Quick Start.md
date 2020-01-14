# [Quick Start](http://spark.apache.org/docs/latest/quick-start.html)

Note that, before Spark 2.0, the main programming interface of Spark was the Resilient Distributed Dataset (RDD). After Spark 2.0, RDDs are replaced by Dataset,
which is strongly-typed like an RDD, but with richer optimizations under the hood. The RDD interface is still supported, and you can get a more detailed reference 
at the RDD programming guide. However, we highly recommend you to switch to use Dataset, which has better performance than RDD. See the SQL programming guide to 
get more information about Dataset.


## Security
Security in Spark is OFF by default. This could mean you are vulnerable to attack by default. Please see [Spark Security](http://spark.apache.org/docs/latest/security.html) before running Spark.

## Interactive Analysis with the Spark Shell

