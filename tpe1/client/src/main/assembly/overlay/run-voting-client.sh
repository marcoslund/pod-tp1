#!/bin/bash

java -DserverAddress=192.168.1.25:1099 -DvotesPath=./votes.csv -cp 'lib/jars/*' "ar.edu.itba.pod.client.voting.VotingClient" $*

