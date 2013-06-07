credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo <<= version { v: String =>
val nexus = "http://tomcat.corux.de/nexus/"
if (v.trim.endsWith("SNAPSHOT"))
  Some("snapshots" at nexus + "content/repositories/snapshots")
else                            
  Some("releases" at nexus + "content/repositories/releases")
}

pomExtra := (
  <url>https://github.com/corux/play-messages</url>
  <licenses>
    <license>
      <name>Apache 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>scm:git:git@github.com:corux/play-messages.git</url>
    <connection>scm:git:git@github.com:corux/play-messages.git</connection>
  </scm>
  <developers>
    <developer>
      <id>corux</id>
      <name>Tobias Wallura</name>
      <url>http://www.corux.de/</url>
    </developer>
  </developers>)
