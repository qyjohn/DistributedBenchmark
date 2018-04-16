#!/bin/bash
mdadm --create --verbose /dev/md0 --level=0 --name=MY_RAID --raid-devices=4 /dev/vdb /dev/vdc /dev/vdd /dev/vde
mkfs.ext4 -E lazy_itable_init=0,lazy_journal_init=0 /dev/md0
mkdir -p /data
mount /dev/md0 /data
