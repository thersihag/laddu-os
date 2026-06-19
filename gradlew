#!/usr/bin/env sh

# Ensure we can run the script safely
# Recommended Gradle wrapper script for POSIX systems.

# Set explicit shell options to ensure safety and consistent paths
set -e

# Determine the Project Directory
APP_BASE_NAME=$(basename "$0")
APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME" && pwd -P)

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
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
    echo "Please set the JAVA_HOME variable in your environment to match the location of your Java installation." >&2
    exit 1
fi

# Locate the gradle-wrapper.jar file
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -e "$CLASSPATH" ] ; then
    # If the wrapper jar is missing, we invoke global gradle to download/recover it or exit gracefully
    echo "Warning: gradle-wrapper.jar not found at $CLASSPATH. Attempting global gradle invocation..." >&2
    exec gradle "$@"
else
    # Execute Gradle using the wrapper jar
    exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
fi
