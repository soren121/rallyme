SET NAMES utf8;
USE `rallyme`;

# Create rallyme user
GRANT ALL ON `rallyme`.* TO 'rallyme'@'localhost' IDENTIFIED BY 'admin';

CREATE TABLE IF NOT EXISTS `users` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(256) NOT NULL,
    `email` varchar(120) NOT NULL,
    `first_name` varchar(50) NOT NULL,
    `last_name` varchar(50) NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `rallies` (
    `id` int(11) unsigned NOT NULL,
    `description` varchar(1000) NOT NULL,
    `twitter_handle` varchar(100) NOT NULL,
    `start_time` datetime NOT NULL,
    `geolocation` varchar(100) NOT NULL,
    `latitude` float(11) NOT NULL,
    `longitude` float(11) NOT NULL,
    `user_id` int(11) unsigned NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `rallies_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
