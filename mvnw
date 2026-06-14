#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.2.0
# ----------------------------------------------------------------------------

if [ -z "$JAVA_HOME" ]; then
  javaExecutable="`which java`"
  if [ -n "$javaExecutable" ] && ! [ "`expr \"$javaExecutable\" : '\([^ ]*\)'`" = "no" ]; then
    readLink=$(which readlink)
    if [ -n "$readLink" ]; then
      javaExecutable=$(readlink -f "$javaExecutable")
    fi
    javaHome=$(dirname "$javaExecutable")
    javaHome=$(dirname "$javaHome")
    JAVA_HOME="$javaHome"
    export JAVA_HOME
  fi
fi

MAVEN_WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"
DOWNLOAD_URL=""

if [ -f "$MAVEN_WRAPPER_PROPERTIES" ]; then
  DOWNLOAD_URL=$(grep 'wrapperUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
fi

if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  echo "Downloading Maven wrapper..."
  if command -v wget > /dev/null 2>&1; then
    wget "$DOWNLOAD_URL" -O "$MAVEN_WRAPPER_JAR" 2>/dev/null
  elif command -v curl > /dev/null 2>&1; then
    curl -o "$MAVEN_WRAPPER_JAR" "$DOWNLOAD_URL" 2>/dev/null
  else
    echo "ERROR: Cannot download maven-wrapper.jar. Install wget or curl." >&2
    exit 1
  fi
fi

exec "$JAVA_HOME/bin/java" \
  -classpath ".mvn/wrapper/maven-wrapper.jar" \
  "-Dmaven.multiModuleProjectDirectory=$PWD" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
