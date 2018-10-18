name := "EvoGraph"

version := "1.0"

scalaVersion := "2.11.8"

javacOptions ++= Seq("-source", "1.8", "-g:none")

scalacOptions ++= Seq("-optimise", "-optimize","-target:jvm-1.8", "-Yinline-warnings")

libraryDependencies += "it.unimi.dsi" % "fastutil" % "8.1.1"

libraryDependencies += "it.unimi.dsi" % "dsiutils" % "2.4.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.2"

libraryDependencies += "org.apache.spark" %% "spark-graphx" % "2.3.2"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.2"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.7.7"

lazy val root = (project in file(".")).dependsOn(dependencyTrillionG)

lazy val dependencyTrillionG = RootProject(uri("https://github.com/chan150/TrillionG.git"))
