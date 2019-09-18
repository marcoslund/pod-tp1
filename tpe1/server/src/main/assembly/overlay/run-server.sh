#!/bin/bash

java -DserverAddress=localhost -cp 'lib/jars/*' "ar.edu.itba.pod.server.Server" $*

