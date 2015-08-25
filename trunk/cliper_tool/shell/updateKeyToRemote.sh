#!/bin/sh
APP_HOME=./
APP_MAINCLASS="cliperDeploy.Main"

p_classpath="$CLASSPATH:./lib/cliperDeploy.jar:./lib/mina-core-2.0.4.jar:./lib/slf4j-api-1.5.8.jar:./lib/slf4j-log4j12-1.5.8.jar:./lib/spymemcached-2.7.1.jar"
#p_classpath="$CLASSPATH:/usr/local/shell/cliper_tool/lib/cliperDeploy.jar:/usr/local/shell/cliper_tool/lib/mina-core-2.0.4.jar:/usr/local/shell/cliper_tool/lib/slf4j-api-1.5.8.jar:/usr/local/shell/cliper_tool/lib/slf4j-log4j12-1.5.8.jar:/usr/local/shell/cliper_tool/lib/spymemcached-2.7.1.jar"

echo "$p_classpath"

JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true"

echo -n "Starting $CLASSPATH $APP_MAINCLASS ..."
 $JAVA_HOME/bin/java $JAVA_OPTS -classpath $p_classpath $APP_MAINCLASS updateKeyToRemote


