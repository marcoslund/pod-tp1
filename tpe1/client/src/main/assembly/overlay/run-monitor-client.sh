#!/bin/bash

java -DserverAddress=x.x.x.x:x -Did=1234 -Dparty=lynx -cp 'lib/jars/*' "ar.edu.itba.pod.client.monitoring.MonitoringClient" $*

