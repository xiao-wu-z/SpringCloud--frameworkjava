CREATE database if NOT EXISTS `frameworkjava_nacos_dev` default character set

utf8mb4 collate utf8mb4_general_ci;

CREATE database if NOT EXISTS `frameworkjava_dev` default character set
utf8mb4 collate utf8mb4_general_ci;

CREATE USER 'xiaowudev'@'%' IDENTIFIED BY 'xiaowu@123';
GRANT replication slave, replication client on *.* to 'xiaowudev'@'%';
GRANT ALL PRIVILEGES ON frameworkjava_nacos_dev.* TO 'xiaowudev'@'%';
GRANT ALL PRIVILEGES ON frameworkjava_dev.* TO 'xiaowudev'@'%';


FLUSH PRIVILEGES;