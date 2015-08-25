APP_HOME=./

APP_MAINCLASS=cliperDeploy.Main

CLASSPATH=$APP_HOME
for i in "$APP_HOME"/lib/*.jar; do
        CLASSPATH="$CLASSPATH":"$i"
done

JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true -XX:MaxPermSize=128m"

echo -n "Starting $CLASSPATH $APP_MAINCLASS ..."
$JAVA_HOME/bin/java $JAVA_OPTS -classpath $CLASSPATH $APP_MAINCLASS testreadjar
