#!/bin/bash

java -DserverAddress=192.168.1.25:1099 -Did=1234 -Dparty=lynx -cp 'lib/jars/*' "ar.edu.itba.pod.client.monitoring.MonitoringClient" $*

