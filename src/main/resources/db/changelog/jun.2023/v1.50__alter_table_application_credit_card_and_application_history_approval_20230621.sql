--liquibase formatted sql
--changeset ManhNV8:v1.50__alter_table_application_credit_card_and_application_history_approval_20230621-application_credit_card dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'city_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'city_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'district_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'district_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'ward_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'ward_value';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_credit_card' AND COLUMN_NAME = 'address_line';
ALTER TABLE `application_credit_card`
ADD COLUMN `city_code` varchar(10) NULL COMMENT 'Mã Tỉnh/ Thành phố' AFTER `card_receive_addr_value`,
ADD COLUMN `city_value` varchar(50) NULL COMMENT 'Tên Tỉnh/ Thành phố' AFTER `city_code`,
ADD COLUMN `district_code` varchar(10) NULL COMMENT 'Mã Quận/ Huyện' AFTER `city_value`,
ADD COLUMN `district_value` varchar(50) NULL COMMENT 'Tên Quận/ Huyện' AFTER `district_code`,
ADD COLUMN `ward_code` varchar(10) NULL COMMENT 'Mã Phường/ Xã' AFTER `district_value`,
ADD COLUMN `ward_value` varchar(50) NULL COMMENT 'Tên Phường/ Xã' AFTER `ward_code`,
ADD COLUMN `address_line` varchar(255) NULL COMMENT 'Số nhà/ Tên đường' AFTER `ward_value`;

--liquibase formatted sql
--changeset ManhNV8:v1.50__alter_table_application_credit_card_and_application_history_approval_20230621-application_history_approval dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'proposal_approval_reception_title';
ALTER TABLE `application_history_approval`
ADD COLUMN `proposal_approval_reception_title` varchar(255) NULL COMMENT 'Tên cấp thẩm quyền' AFTER `proposal_approval_reception`;