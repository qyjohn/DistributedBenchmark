#!/bin/bash
cd ~/DistributedBenchmark
java -cp target/DistributedBenchmark-jar-with-dependencies.jar:libs/*:. net.qyjohn.DistributedBenchmark.ObjectStorageTest $1 $2 $3 $4
