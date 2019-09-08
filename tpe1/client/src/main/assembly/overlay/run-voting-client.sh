#!/bin/bash

java -DserverAddress=192.168.1.1:1234 -DvotesPath=./votes.csv -cp 'lib/jars/*' "ar.edu.itba.pod.client.voting.VotingClient" $*

