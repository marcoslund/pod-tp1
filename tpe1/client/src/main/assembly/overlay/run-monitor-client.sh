#!/bin/bash

java -DserverAddress=localhost:1099 -Did=1000 -Dparty=lynx -cp 'lib/jars/*' "ar.edu.itba.pod.client.monitoring.MonitoringClient" $*

