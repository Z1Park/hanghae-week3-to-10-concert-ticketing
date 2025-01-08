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