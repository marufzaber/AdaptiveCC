#!/bin/bash


rm -rf SourcererCC/clone-detector/sourcerer-cc.properties
javac ConfigGenerator.java
java -cp . ConfigGenerator
mv sourcerer-cc.properties SourcererCC/clone-detector/

cd SourcererCC/clone-detector/
cp blocks.file input/dataset/
rm -rf input/dataset/oldData/
rm -rf build dist NODE_1 SCC_LOGS gtpmindex fwdindex index
./execute.sh 1
./runnodes.sh init 1
./runnodes.sh index 1
./move-index.sh 1


for i in {1..100}
do
cd ~

rm -rf SourcererCC/clone-detector/sourcerer-cc.properties
javac ConfigGenerator.java
java -cp . ConfigGenerator

mv sourcerer-cc.properties SourcererCC/clone-detector/
cd SourcererCC/clone-detector/

rm -rf NODE_1/output8.0/recovery.txt

./runnodes.sh search 1

cd SCC_LOGS/NODE_1/



cd ~


mv SourcererCC/clone-detector/SCC_LOGS/NODE_1/scc.log ~/SCC_LOGS/


done
