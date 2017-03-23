SET NAMES utf8;
USE `rallyme`;

# Create rallyme user
GRANT ALL ON `rallyme`.* TO 'rallyme'@'localhost' IDENTIFIED BY 'admin';

CREATE TABLE `users` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(256) NOT NULL,
    `email` varchar(120) NOT NULL,
    `first_name` varchar(50) NOT NULL,
    `last_name` varchar(50) NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `email` (`email`)
);
