#!/bin/bash
mdadm --create --verbose /dev/md0 --level=0 --name=MY_RAID --raid-devices=4 /dev/xvdb /dev/xvdc /dev/xvdd /dev/xvde
mkfs.ext4 -E lazy_itable_init=0,lazy_journal_init=0 /dev/md0
mkdir -p /data
mount /dev/md0 /data
