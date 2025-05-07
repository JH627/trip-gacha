DROP DATABASE IF EXISTS ssafydb;
CREATE DATABASE ssafydb;
USE ssafydb;

CREATE TABLE `users` (
    `user_id` INT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(50) NOT NULL,
    `password` TEXT NOT NULL,
    `nickname` VARCHAR(30) NOT NULL,
    `profile_img` TEXT NULL,
    `role` TINYINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT NOW(),
    `updated_at` DATETIME NULL,
    `deleted_at` DATETIME NULL COMMENT '일주일 지나면 삭제',
    `is_delete` BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `destinations` (
    `destination_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `country` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`destination_id`)
);

CREATE TABLE `boards` (
    `board_id` INT NOT NULL AUTO_INCREMENT,
    `uploader_id` INT NOT NULL,
    `title` VARCHAR(50) NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `deleted_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL COMMENT 'false',
    PRIMARY KEY (`board_id`),
    FOREIGN KEY (`uploader_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `board_images` (
    `board_image_id` INT NOT NULL AUTO_INCREMENT,
    `board_id` INT NOT NULL,
    `origin_name` VARCHAR(100) NOT NULL,
    `uuid` TEXT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `deleted_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL,
    PRIMARY KEY (`board_image_id`),
    FOREIGN KEY (`board_id`) REFERENCES `boards` (`board_id`)
);

CREATE TABLE `board_dislikes` (
    `board_dislike_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `board_id` INT NOT NULL,
    PRIMARY KEY (`board_dislike_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`board_id`) REFERENCES `boards` (`board_id`)
);

CREATE TABLE `board_likes` (
    `board_like_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `board_id` INT NOT NULL,
    PRIMARY KEY (`board_like_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`board_id`) REFERENCES `boards` (`board_id`)
);

CREATE TABLE `board_reports` (
    `board_report_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `board_id` INT NOT NULL,
    PRIMARY KEY (`board_report_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`board_id`) REFERENCES `boards` (`board_id`)
);

CREATE TABLE `comments` (
    `comment_id` INT NOT NULL AUTO_INCREMENT,
    `board_id` INT NOT NULL,
    `content` TEXT NOT NULL,
    `uploader_id` INT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `deleted_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`comment_id`),
    FOREIGN KEY (`board_id`) REFERENCES `boards` (`board_id`),
    FOREIGN KEY (`uploader_id`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `spots` (
    `spot_id` INT NOT NULL AUTO_INCREMENT,
    `destination_id` INT NOT NULL,
    `name` VARCHAR(255) NULL,
    `content` TEXT NOT NULL,
    `img` TEXT NOT NULL,
    `address` VARCHAR(100) NOT NULL,
    `likes` INT NOT NULL,
    `stars` INT NOT NULL,
    `category_id` INT NOT NULL,
    `phone` VARCHAR(15) NULL,
    `work_time` TEXT NULL,
    PRIMARY KEY (`spot_id`),
    FOREIGN KEY (`destination_id`) REFERENCES `destinations` (`destination_id`)
);

CREATE TABLE `bookmarks` (
    `bookmark_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `spot_id` INT NOT NULL,
    PRIMARY KEY (`bookmark_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`spot_id`) REFERENCES `spots` (`spot_id`)
);

CREATE TABLE `trip_schedule` (
    `trip_route_id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `destination_id` INT NOT NULL,
    `title` VARCHAR(50) NULL,
    `created_at` DATETIME NOT NULL,
    `is_shared` BOOLEAN NOT NULL DEFAULT FALSE,
    `start_date` DATETIME NOT NULL,
    `end_date` DATE NOT NULL,
    PRIMARY KEY (`trip_route_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
    FOREIGN KEY (`destination_id`) REFERENCES `destinations` (`destination_id`)
);

CREATE TABLE `trip_schedule_items` (
    `trip_schedule_item_id` INT NOT NULL AUTO_INCREMENT,
    `spot_id` INT NOT NULL,
    `trip_schedule_id` INT NOT NULL,
    `day` INT NOT NULL,
    `sequence` INT NOT NULL,
    PRIMARY KEY (`trip_schedule_item_id`),
    FOREIGN KEY (`spot_id`) REFERENCES `spots` (`spot_id`),
    FOREIGN KEY (`trip_schedule_id`) REFERENCES `trip_schedule` (`trip_route_id`)
);
