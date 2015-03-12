#!/bin/sh
export SBT_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
echo $SBT_OPTS
sbt