#!/bin/bash 


./init.sh 
./execute.sh 1

./runnodes.sh init 1
./runnodes.sh index 1
./move-index.sh 1
./runnodes.sh search 1