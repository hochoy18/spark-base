


- "${SPARK_HOME}"/bin/spark-submit
    exec "${SPARK_HOME}"/bin/spark-class org.apache.spark.deploy.SparkSubmit "$@"

- "${SPARK_HOME}"/bin/spark-class  
    org.apache.spark.launcher.Main "$@"
        Main#main() 
            -> new SparkSubmitCommandBuilder() 
            -> Main#buildCommand(AbstractCommandBuilder,Map<String, String>,boolean)
                -> SparkSubmitCommandBuilder#buildCommand(Map<String, String> env)
                    ->SparkSubmitCommandBuilder#buildSparkSubmitCommand(Map<String, String> env) 该方法会生成一个类似
                    ```${JAVA_HOME}/bin/java  -cp xxx/*:xxx/yyy.jar:${HADOOP_HOME}/etc/hadoop/  -Xmx1024m org.apache.spark.deploy.SparkSubmit --master yarn --deploy-mode cluster --class org.apache.spark.examples.SparkPi --queue defalut ${SPARK_HOME}/examples/jars/spark-examples_2.11-2.3.3.jar ```
                    的命令行，然后通过```${JAVA_HOME}/bin/java```命令 执行```org.apache.spark.deploy.SparkSubmit#main(args: Array[String])```

- org.apache.spark.deploy.SparkSubmit-> mainMethod.invoke(null, args)
