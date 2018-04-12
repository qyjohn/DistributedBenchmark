#!/bin/bash
cd ~/DistributedBenchmark
java -cp target/DistributedBenchmark-jar-with-dependencies.jar:. net.qyjohn.DistributedBenchmark.ObjectStorageTest $1 $2 $3
