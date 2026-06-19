#!/usr/bin/env sh
set -e

APP_BASE_NAME=$(basename "$0")
APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME" && pwd -P)

# 🔥 FIX: Removed extra inner quotes that caused ClassNotFoundException
DEFAULT_JVM_OPTS="-Xmx2048m -Xms512m"

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    elif [ -x "$JAVA_HOME/bin/java" ] ; then
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD="java"
fi

if [ ! -x "$JAVACMD" ] ; then
    echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
    exit 1
fi

WRAPPER_DIR="$APP_HOME/gradle/wrapper"
CLASSPATH="$WRAPPER_DIR/gradle-wrapper.jar"

if [ ! -e "$CLASSPATH" ] ; then
    echo "Warning: gradle-wrapper.jar missing. Downloading online..." >&2
    mkdir -p "$WRAPPER_DIR"
    curl -sLo "$CLASSPATH" https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar
fi

# 🔥 FIX: Executing without broken quotes expansion
exec "$JAVACMD" $DEFAULT_JVM_OPTS "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
