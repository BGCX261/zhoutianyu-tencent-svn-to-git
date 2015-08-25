#!/bin/sh
APP_MAINCLASS="cliperDeploy.Main"
p_classpath="$CLASSPATH:./lib/cliperDeploy.jar:./lib/mina-core-2.0.4.jar:./lib/slf4j-api-1.5.8.jar:./lib/slf4j-log4j12-1.5.8.jar:./lib/spymemcached-2.7.1.jar"
echo "$p_classpath"
JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true"
echo -n "Starting $CLASSPATH $APP_MAINCLASS ..."
$JAVA_HOME/bin/java $JAVA_OPTS -classpath $p_classpath $APP_MAINCLASS updateKeyToRemote

