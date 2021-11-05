scalaVersion := "2.12.15"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xsource:2.11",
  "-language:reflectiveCalls"
  // Enables autoclonetype2
  // "-P:chiselplugin:useBundlePlugin"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

// Chisel 3.5
// addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.5-SNAPSHOT" cross CrossVersion.full)
libraryDependencies += "edu.berkeley.cs" %% "chisel3"          % "3.5-SNAPSHOT"
libraryDependencies += "edu.berkeley.cs" %% "chisel-iotesters" % "1.5.3"
libraryDependencies += "edu.berkeley.cs" %% "chiseltest"       % "0.3.3"
