#!/bin/bash

JARGS="-Xmx120G -Xms120G -XX:+UseParallelGC -XX:MaxGCPauseMillis=2 -XX:ParallelGCThreads=16"


time java ${JARGS} -jar $@

