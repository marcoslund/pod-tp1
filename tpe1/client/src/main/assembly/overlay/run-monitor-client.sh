#!/bin/bash

java -Did=1234 -Dparty=lynx -cp 'lib/jars/*' "ar.edu.itba.pod.client.monitoring.MonitoringClient" $*

