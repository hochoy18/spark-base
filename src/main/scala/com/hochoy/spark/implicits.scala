package com.hochoy.spark {

  object implicits {
    def buildPath(dirs: String*): String = innerBuildPath(dirs)(separator = java.io.File.separator)

    private def innerBuildPath(dirs: Seq[String])(separator: String = java.io.File.separator): String = dirs.mkString(separator)
  }

}
