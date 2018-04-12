#!/bin/bash
/efs/DistributedBenchmark/bin/iozone -s $1 -r 256 -i 0 -t 1 -F $2
