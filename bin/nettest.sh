#!/bin/bash
cd ~/DistributedBenchmark
java -cp target/DistributedBenchmark-jar-with-dependencies.jar:. net.qyjohn.DistributedBenchmark.SpeedTest  $1
