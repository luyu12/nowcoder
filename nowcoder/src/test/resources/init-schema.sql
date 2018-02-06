drop table if EXISTS  `user`;
CREATE TABLE `user`(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) not null default '',
  `password` varchar(128) not null default '',
  `salt` varchar(32) NOT NULL DEFAULT '',
  `head_url` VARCHAR(256) not null default '',
  primary key (`id`),
  unique key `name`(`name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if EXISTS `news`;
CREATE TABLE `news`(
  `id` int(11) unsigned not null AUTO_INCREMENT,
  `title` varchar(128) not null DEFAULT '',
  `link` varchar(256) not null DEFAULT '',
  `image` varchar(256) not null default '',
  `like_count` int not null,
  `comment_count` int not null,
  `created_date` datetime not null,
  `user_id` int(11) not null,
  PRIMARY key (`id`)
)ENGINE=InnoDB default CHARSET=utf8;

drop table if EXISTS `login_ticket`;
CREATE TABLE `login_ticket`(
  `id` int not null AUTO_INCREMENT,
  `user_id` int not null,
  `ticket` varchar(45) not null,
  `expired` DATETIME not null,
  `status` INT null default 0,
  PRIMARY key (`id`),
  UNIQUE INDEX `ticket_UNIQUE` (`ticket` ASC))
ENGINE=InnoDB default CHARSET=utf8;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL,
  `user_id` INT NOT NULL,
  `entity_id` INT NOT NULL,
  `entity_type` INT NOT NULL,
  `created_date` DATETIME NOT NULL,
  `status` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `entity_index` (`entity_id` ASC, `entity_type` ASC)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `from_id` INT NOT NULL,
  `to_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `created_date` DATETIME NOT NULL,
  `has_read` INT NOT NULL,
  `conversation_id` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



