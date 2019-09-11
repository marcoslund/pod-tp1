#!/bin/bash

java -DserverAddress=localhost:1099 -Daction=open -cp 'lib/jars/*' "ar.edu.itba.pod.client.administration.AdministrationClient" $*

