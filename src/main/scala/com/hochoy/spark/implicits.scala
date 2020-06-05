package com.hochoy.spark {

  object implicits {
    final val SPARK_PATH: String =
      buildPath("file:///" + System.getProperty("user.dir"), "src", "main", "scala", "com", "hochoy", "spark")

    final val DATA_PATH: String =
      buildPath(SPARK_PATH, "sql", "data")

    def buildPath(dirs: String*): String = innerBuildPath(dirs)(separator = java.io.File.separator)

    private def innerBuildPath(dirs: Seq[String])(separator: String = java.io.File.separator): String = dirs.mkString(separator)


  }

}
