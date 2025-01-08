CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `user_uuid` varchar(255) NOT NULL UNIQUE,
  `balance` BIGINT NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL,
  `updated_at` TIMESTAMP(6) NOT NULL
);

CREATE TABLE `queue` (
    `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `user_uuid` varchar(255) NOT NULL UNIQUE,
    `token_uuid` varchar(255) NOT NULL UNIQUE,
    `activate_status` varchar(255) NOT NULL,
    `expired_at` TIMESTAMP(6) NULL,
    `created_at` TIMESTAMP(6) NOT NULL,
    `updated_at` TIMESTAMP(6) NOT NULL
);

CREATE TABLE `concert` (
    `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `provider` VARCHAR(255) NOT NULL,
    `finished` BIT NOT NULL,
    `created_at` TIMESTAMP(6) NOT NULL,
    `updated_at` TIMESTAMP(6) NOT NULL
);

CREATE TABLE `concert_schedule` (
    `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `concert_id` BIGINT NOT NULL,
    `total_seat` INT NOT NULL,
    `start_at` TIMESTAMP(6) NOT NULL,
    `end_at` TIMESTAMP(6) NOT NULL,
    `created_at` TIMESTAMP(6) NOT NULL,
    `updated_at` TIMESTAMP(6) NOT NULL,
    INDEX idx_concert_id (concert_id)
);

CREATE TABLE `concert_seat` (
    `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `concert_schedule_id` BIGINT NOT NULL,
    `seat_number` INT NOT NULL,
    `price` INT NOT NULL,
    `created_at` TIMESTAMP(6) NOT NULL,
    `updated_at` TIMESTAMP(6) NOT NULL,
    INDEX idx_concert_schedule_id (concert_schedule_id)
);