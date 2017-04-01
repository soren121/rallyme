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
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `description` TEXT NOT NULL DEFAULT '',
    `twitter_handle` varchar(100),
    `url` varchar(255),
    `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `location` varchar(100) NOT NULL,
    `latitude` float(11) NOT NULL,
    `longitude` float(11) NOT NULL,
    `user_id` int(11) unsigned NOT NULL,
    `event_capacity` int(7) unsigned NOT NULL DEFAULT 100,

    PRIMARY KEY (`id`),
    KEY `location` (`latitude`, `longitude`),
    CONSTRAINT `rallies_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# Create admin user (password is "rallyme")
INSERT INTO `users` (`id`, `username`, `password`, `email`, `first_name`, `last_name`) VALUES
(1, 'admin', '$2a$10$7KuZRPvizaIrI5GsRkdtXOXRLi7evQ75j/oystWFyBta0eE8X6qtm', 'RallyMe', 'Admin', 'admin@example.com');

# Insert default rallies
INSERT INTO `rallies` (`id`, `name`, `description`, `twitter_handle`, `start_time`, `location`, `latitude`, `longitude`, `user_id`) VALUES
(1,	'Tax March', 'wayo wayo', 'taxmarch', '2017-04-15 16:00:00', 'Washington, DC', 38.8892,	-77.0523, 1),
(2,	'March for Science', 'wayo wayo', 'ScienceMarchDC', '2017-04-22 16:00:00', 'Washington, DC', 38.8892, -77.0523, 1);
