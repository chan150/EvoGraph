name := "EvoGraph"

version := "1.0"

scalaVersion := "2.11.8"

javacOptions ++= Seq("-source", "1.8", "-g:none")

scalacOptions ++= Seq("-optimise", "-optimize","-target:jvm-1.8", "-Yinline-warnings")

libraryDependencies += "it.unimi.dsi" % "fastutil" % "8.1.1"

libraryDependencies += "it.unimi.dsi" % "dsiutils" % "2.4.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.1"

libraryDependencies += "org.apache.spark" %% "spark-graphx" % "2.3.1"
