#!/bin/bash

java -DserverAddress=192.168.0.1:1234 -Daction=open -cp 'lib/jars/*' "ar.edu.itba.pod.client.administration.AdministrationClient" $*

