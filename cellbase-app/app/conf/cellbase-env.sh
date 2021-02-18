#!/usr/bin/env bash

# Variables defined in main script
# BASEDIR
# PRGDIR
# JAVA_OPTS

MONITOR_AGENT=""
## TODO We must make sure we load any existing JAR file, only one can exist.
if [ -e "${BASEDIR}/monitor/dd-java-agent.jar" ]; then
    MONITOR_AGENT="-javaagent:${BASEDIR}/monitor/dd-java-agent.jar"
fi


JAVA_HEAP="2048m"
CELLBASE_LOG_DIR=${CELLBASE_LOG_DIR:-$(grep "logDir" "${BASEDIR}/conf/configuration.yml" | cut -d ":" -f 2 | tr -d '" ')}
CELLBASE_LOG_LEVEL=${CELLBASE_LOG_LEVEL:-$(grep "logLevel" "${BASEDIR}/conf/configuration.yml" | cut -d ":" -f 2 | tr -d '" ')}
CELLBASE_LOG_OUPUT=${CELLBASE_LOG_OUPUT:-$(grep "logOuput" "${BASEDIR}/conf/configuration.yml" | cut -d ":" -f 2 | tr -d '" ')}

CELLBASE_LOG_CONFIG="log4j2.xml"

if [ `basename $PRG` = "cellbase-admin.sh" ]; then
    JAVA_HEAP="8192m"
  CELLBASE_LOG_CONFIG="log4j2.console.xml"
  if [ CELLBASE_LOG_OUPUT = "file" ]; then
    CELLBASE_LOG_CONFIG="log4j2.file.xml"
  fi
fi

#Set log4j properties file
export JAVA_OPTS="${JAVA_OPTS} -Dlog4j.configurationFile=file:${BASEDIR}/conf/${CELLBASE_LOG_CONFIG}"
export JAVA_OPTS="${JAVA_OPTS} -Dcellbase.log.level=${CELLBASE_LOG_LEVEL}"
export JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
export JAVA_OPTS="${JAVA_OPTS} -Xms256m -Xmx${JAVA_HEAP}"
export JAVA_OPTS="${JAVA_OPTS} ${MONITOR_AGENT}"

if [ -n "$CELLBASE_LOG_DIR" ]; then
    export JAVA_OPTS="${JAVA_OPTS} -Dcellbase.log.dir=${CELLBASE_LOG_DIR}"
fi
