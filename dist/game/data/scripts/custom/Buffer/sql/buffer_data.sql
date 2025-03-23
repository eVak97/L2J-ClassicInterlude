-- CREATE TABLE `buffer_user_list_buffs` (
--   `user_buff_list_id` int(10) unsigned NOT NULL,
--   `buff_id` varchar(255) NOT NULL,
--   PRIMARY KEY (`user_buff_list_id`,`buff_id`),
--   CONSTRAINT `buffer_user_list_buffs_ibfk_1` FOREIGN KEY (`user_buff_list_id`) REFERENCES `buffer_user_lists` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- CREATE TABLE `buffer_user_lists` (
--   `id` int(10) unsigned PRIMARY KEY AUTO_INCREMENT NOT NULL ,
--   `character_id` int(10) unsigned NOT NULL,
--   `list_name` varchar(255) NOT NULL,
--   UNIQUE KEY `character_id` (`character_id`,`list_name`),
--   CONSTRAINT `buffer_user_lists_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `characters` (`charId`) ON DELETE CASCADE ON UPDATE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create buffer_user_lists table
CREATE TABLE IF NOT EXISTS `buffer_user_lists` (
  `id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
  `character_id` INT UNSIGNED NOT NULL,
  `list_name` VARCHAR(255) NOT NULL,
  UNIQUE KEY `character_id` (`character_id`,`list_name`),
  CONSTRAINT `buffer_user_lists_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `characters` (`charId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create buffer_user_list_buffs table with modified foreign key constraint
CREATE TABLE IF NOT EXISTS `buffer_user_list_buffs` (
  `user_buff_list_id` INT UNSIGNED NOT NULL,
  `buff_id` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`user_buff_list_id`, `buff_id`),
  CONSTRAINT `buffer_user_list_buffs_ibfk_1` FOREIGN KEY (`user_buff_list_id`) REFERENCES `buffer_user_lists` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;