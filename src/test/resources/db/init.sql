CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `user_uuid` VARCHAR(255) NOT NULL UNIQUE,
  `balance` BIGINT NOT NULL,
  `version` BIGINT NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL,
  `updated_at` TIMESTAMP(6) NOT NULL
);

CREATE TABLE `point_history` (
  `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(255) NOT NULL,
  `amount` INT NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL,
  `updated_at` TIMESTAMP(6) NOT NULL,
    INDEX idx_user_id (user_id)
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
    `reserved_until` TIMESTAMP(6),
    `created_at` TIMESTAMP(6) NOT NULL,
    `updated_at` TIMESTAMP(6) NOT NULL,
    INDEX idx_concert_schedule_id (concert_schedule_id)
);

CREATE TABLE `reservation` (
    `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `price` INT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `concert_id` BIGINT NOT NULL,
    `concert_schedule_id` BIGINT NOT NULL,
    `concert_seat_id` BIGINT NOT NULL,
    `expired_at` TIMESTAMP(6),
    `created_at` TIMESTAMP(6) NOT NULL,
    `updated_at` TIMESTAMP(6) NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_concert_id (concert_id),
    INDEX idx_concert_schedule_id (concert_schedule_id),
    INDEX idx_concert_seat_id (concert_seat_id)
);

CREATE TABLE `payment` (
    `id` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `reservation_id` BIGINT NOT NULL,
    `price` INT NOT NULL,
    `created_at` TIMESTAMP(6) NOT NULL,
    `updated_at` TIMESTAMP(6) NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_reservation_id (reservation_id),
    UNIQUE unq_user_id_reservation_id (user_id, reservation_id)
);