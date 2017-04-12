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
    `creator_id` int(11) unsigned NOT NULL,
    `parent_id` int(11) unsigned,
    `name` varchar(100) NOT NULL,
    `type` ENUM('local', 'national') NOT NULL DEFAULT 'national',
    `description` TEXT,
    `twitter_handle` varchar(100),
    `url` varchar(255),
    `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `location` varchar(100) NOT NULL,
    `latitude` float(11) NOT NULL,
    `longitude` float(11) NOT NULL,
    `event_capacity` int(7) unsigned NOT NULL DEFAULT 100,

    PRIMARY KEY (`id`),
    KEY `location` (`latitude`, `longitude`),
    KEY `sister` (`parent_id`),
    CONSTRAINT `rallies_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# Create admin user (password is "rallyme")
INSERT INTO `users` (`id`, `username`, `password`, `email`, `first_name`, `last_name`) VALUES
(1, 'admin', '$2a$10$7KuZRPvizaIrI5GsRkdtXOXRLi7evQ75j/oystWFyBta0eE8X6qtm', 'admin@example.com', 'RallyMe', 'Admin');

# Insert default rallies
INSERT INTO `rallies` (`id`, `creator_id`, `name`, `description`, `twitter_handle`, `start_time`, `location`, `latitude`, `longitude`) VALUES
(1, 1, 'Tax March', 'Tell Trump to release his tax returns. What\'s he hiding?', 'taxmarch', '2017-04-15 16:00:00', 'The National Mall', 38.8892, -77.0523),
(2, 1, 'March for Science', 'We are political. We value diversity. We won\'t put up with harassment.', 'ScienceMarchDC', '2017-04-22 16:00:00', 'The National Mall', 38.8892, -77.0523);
