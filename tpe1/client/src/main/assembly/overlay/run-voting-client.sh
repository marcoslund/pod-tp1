#!/bin/bash

java -DvotesPath=../votes.csv -cp 'lib/jars/*' "ar.edu.itba.pod.client.voting.VotingClient" $*

