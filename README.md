# DistributedBenchmark



~~~~
$ mysql -u root -p
mysql> CREATE DATABASE benchmark;
mysql> CREATE USER 'username'@'%' IDENTIFIED BY 'password';
mysql> GRANT ALL PRIVILEGES ON benchmark.* TO 'username'@'%';
mysql> quit

$ mysql -u username -p benchmark < benchmark.sql
~~~~
