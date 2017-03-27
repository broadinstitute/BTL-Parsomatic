#Deploy Instructions

1) Navigate to local Parsomatic directory via command line tool.
2) execute 'sbt-assembly'
3) If unit tests pass, navigate to Parsomatic/target/scala-2.11/
4) Copy Parsomatic-assembly-<version>.jar to Unix filesystem.
5) Move jar file to /cil/shed/apps/internal/Parsomatic/bin
6) chmod 755 jar file
7) In  /cil/shed/apps/internal/Parsomatic:

`>rm Parsomatic.jar` 

`>ln -s /cil/shed/apps/internal/Parsomatic/bin/Parsomatic-assembly-2017.4.0.jar Parsomatic.jar`

8) Done! 