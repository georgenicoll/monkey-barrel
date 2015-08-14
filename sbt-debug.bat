set SBT_OPTS=-XDebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005
echo %SBT_OPTS%
sbt