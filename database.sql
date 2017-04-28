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
INSERT INTO `rallies` (`id`, `creator_id`, `parent_id`, `name`, `type`, `description`, `twitter_handle`, `url`, `start_time`, `location`, `latitude`, `longitude`) VALUES
(1,	1,	NULL,	'Tax March',	'national',	'Tell Trump to release his tax returns. What\'s he hiding?',	'taxmarch',	NULL,	'2017-04-29 16:00:00',	'Washington, DC 20037, USA',	38.889,	-77.052),
(2,	1,	NULL,	'March for Science',	'national',	'We are political. We value diversity. We won\'t put up with harassment.',	'ScienceMarchDC',	NULL,	'2017-04-30 16:00:00',	'Washington, DC 20037, USA',	38.889,	-77.052),
(3,	1,	2,	'March for Science, Athens',	'local',	'We are rallying in Athens that morning from 11 to 12 in front of the Federal Courthouse (and Post Office) downtown. This is a peaceful, nonpartisan, family friendly event. Please come out and add your voice to the chorus.',	'',	NULL,	'2017-04-30 16:00:00',	'Athens, GA 30601, USA',	33.9519,	-83.3576),
(4,	1,	NULL,	'Women\'s March of Washington',	'national',	'We are the Women\'s March. For inquiries contact info@womensmarch.com.',	'womensmarch',	NULL,	'2017-05-10 16:00:00',	'Washington, DC 20230, USA',	38.8901,	-77.0364),
(5,	1,	2,	'March for Science, Boston',	'local',	'#ScienceMarch #ScienceMarchBoston Donate: http://tinyurl.com/MfSBOS',	'Mrch4ScienceBOS',	NULL,	'2017-04-30 16:00:00',	'Boston, MA 02112, USA',	42.3601,	-71.0589),
(6,	1,	2,	'March for Science, SF',	'local',	'Official March For Science SF Bay Area Collective. FAQ: http://marchforsciencesf.com/faq/ ',	'ScienceMarchSF',	NULL,	'2017-04-30 16:00:00',	'San Francisco, CA 94103, USA',	37.7749,	-122.419);
