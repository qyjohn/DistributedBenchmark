# Distributed and Parallel Testing Framework


**(1) Installation and Configuration (Master Node)**

We assume that you will run the Distributed and Parallel Testing Framework on Ubuntu 16.04. You need to have a recent version of JDK and RabbitMQ. Then you can build the project from source code with the following steps. 

~~~~
$ sudo apt-get update
$ sudo apt-get install openjdk-8-jdk maven rabbitmq-server mysql-server git
$ sudo apt-get install screen mdadm apache2 gcc make libx11-dev libgl1-mesa-dev libxext-dev
$ sudo dd if=/dev/zero of=/var/www/html/test.dat count=100 bs=1M
$ cd ~
$ git clone https://github.com/qyjohn/ScaleBench
$ cd ScaleBench
$ mvn package
~~~~

The Distributed and Parallel Testing Framework pushes test results into a MySQL database. Use the following steps to setup the database.

~~~~
$ mysql -u root -p
mysql> CREATE DATABASE benchmark;
mysql> CREATE USER 'username'@'%' IDENTIFIED BY 'password';
mysql> GRANT ALL PRIVILEGES ON benchmark.* TO 'username'@'%';
mysql> quit

$ cd ~/DistributedBenchmark
$ mysql -u username -p benchmark < benchmark.sql
~~~~

Modify config.properties with the connection information needed for your RabbitMQ and MySQL installations.

~~~~
mqHostname=172.31.0.20
dbHostname=172.31.0.20
dbDatabase=benchmark
dbUsername=username
dbPassword=password
~~~~

You might need to enable guest access to your RabbitMQ. This can be achieved by adding the following line to your RabbitMQ configuration file /etc/rabbitmq/rabbitmq.config. If the configuration file does not exist, create it. Yes you need to restart rabbitmq-server for the new configuration to take effect. 

~~~~
[{rabbit, [{loopback_users, []}]}].
~~~~

You might need to enable remote access to your MySQL server. This can be achieved by modifying the bind-address from 127.0.0.1 to 0.0.0.0 in your MySQL daemon configuration file /etc/mysql/mysql.conf.d/mysqld.cnf. Yes you need to restart your MySQL server for the new configuration to take effect.

~~~~
bind-address		= 0.0.0.0
~~~~

Restart MySQL server and RabbitMQ with the following commands:

~~~~
$ sudo service mysql restart
$ sudo service rabbitmq-server restart
~~~~

For the network I/O tests you need to have the Apache web server installed, with a test file of 100 MB:

~~~~
$ sudo apt-get install apache2
$ sudo dd if=/dev/zero of=/var/www/html/test.dat count=100 bs=1M
~~~~

**(2) Installation and Configuration (Worker Node)**

~~~~
$ sudo apt-get update
$ sudo apt-get install openjdk-8-jdk maven apache2 git
$ cd ~
$ git clone https://github.com/qyjohn/ScaleBench
$ cd ScaleBench
$ mvn package
$ sudo dd if=/dev/zero of=/var/www/html/test.dat count=100 bs=1M
~~~~


**(3) Running Tests**

Open two SSH connections to your server, one to submit jobs (pretend that this is the master node), the other to execute jobs (pretend that this is the worker node).

On the worker node, start the worker:

~~~~
$ cd ~/ScaleBench
$ java -cp target/DistributedBenchmark-jar-with-dependencies.jar:. net.qyjohn.DistributedBenchmark.Worker 
~~~~

On the master node, submit a test job:

~~~~
$ cd ~/ScaleBench
$ java -cp target/DistributedBenchmark-jar-with-dependencies.jar:. net.qyjohn.DistributedBenchmark.Submit ls.json
$ java -cp target/DistributedBenchmark-jar-with-dependencies.jar:. net.qyjohn.DistributedBenchmark.Submit df.json
~~~~

Observe how the jobs are executed on the worker node. If you add another worker node (even on the same server), the new worker node will also execute the same jobs.

**(4) Composing Tests**

Use the following example to compose your tests. In the Distributed and Parallel Testing Framework, there are two types of jobs - node-generic jobs and node-specific jobs. For node-generic jobs, the value for the "node" attribute should be the wildcard character "*". For node-specific jobs, the value for the "node" attribute should be the IP address of the worker node. 

The value for the "command" attribute is the command to run on the worker nodes. The command must be present in the PATH, which is represented by the "path" attribute. 

~~~~
{
    "testName" : "ls",
    "jobs" : [
        {
            "node" : "*",
            "path" : "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "command" : "df -h"
        },
        {
            "node" : "172.31.0.20",
            "path" : "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "command" : "ifconfig"
        },
        {
            "node" : "172.31.0.209",
            "path" : "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "command" : "df -h -i"
        }
    ]
}
~~~~

**(5) Web Frontend**

The web front end can be used to view the test results. This is still work in progress.

You need to install the Apache webserver, along with PHP to use the web front end. Then you simple create a symlink to the web front end under /var/www/html. 

~~~~
$ sudo apt-get update
$ sudo apt-get install apache2 php libapache2-mod-php php-mcrypt php-mysql
$ cd /var/www/html
$ sudo ln -s ~/ScaleBench/web/ benchmark
~~~~

Update web/config.php with your database connection information. Then, access the web front end from a browser via http://[ip-address]/benchmark.

