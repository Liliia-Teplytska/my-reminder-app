#!/usr/bin/env sh
set -e
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
