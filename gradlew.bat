#!/usr/bin/env sh

# Set explicit shell options to ensure safety
set -e

# Determine the Project Directory
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME" && pwd -P)

# Default JVM options
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Find Java executable
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

# Locate the gradle-wrapper.jar file
WRAPPER_DIR="$APP_HOME/gradle/wrapper"
CLASSPATH="$WRAPPER_DIR/gradle-wrapper.jar"

# 🔥 FIX: Agar jar file missing hai, toh cloud me direct live download karo
if [ ! -e "$CLASSPATH" ] ; then
    echo "Warning: gradle-wrapper.jar missing. Downloading it directly on cloud..." >&2
    mkdir -p "$WRAPPER_DIR"
    
    # Downloading official gradle-wrapper.jar loader safely
    curl -sLo "$CLASSPATH" https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar
    
    if [ ! -e "$CLASSPATH" ]; then
        echo "ERROR: Failed to download gradle-wrapper.jar inside cloud container." >&2
        exit 1
    fi
    echo "Success: gradle-wrapper.jar recovered successfully!" >&2
fi

# Execute Gradle using the recovered wrapper jar
exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
