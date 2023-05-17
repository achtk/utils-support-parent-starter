nohup ./mysqld_safe  --defaults-file=/home/mysql/mysql-8.0.31-linux-glibc2.12-x86_64/my.cnf > /home/mysql/mysql-8.0.31-linux-glibc2.12-x86_64/logs/mysql.log 2>&1 &
tail -f ../logs/mysql.log
