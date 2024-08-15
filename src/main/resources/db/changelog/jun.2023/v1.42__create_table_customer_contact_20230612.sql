--liquibase formatted sql
--changeset TuanHA13:v1.42__create_table_customer_contact_20230612 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'customer_contact'
CREATE TABLE `customer_contact` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `customer_id` bigint DEFAULT NULL,
                                    `contact_type` varchar(10) DEFAULT NULL COMMENT 'Loại contact',
                                    `value` varchar(50) DEFAULT NULL COMMENT 'Giá trị',
                                    `created_by` varchar(50) DEFAULT NULL COMMENT 'User khởi tạo',
                                    `created_at` datetime DEFAULT NULL COMMENT 'Thời gian khởi tạo',
                                    `updated_by` varchar(50) DEFAULT NULL COMMENT 'User cập nhật',
                                    `updated_at` datetime DEFAULT NULL COMMENT 'Thời gian cập nhật',
                                    PRIMARY KEY (`id`),
                                    KEY `customer_id` (`customer_id`),
                                    CONSTRAINT `customer_contact_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
);