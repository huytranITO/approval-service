--liquibase formatted sql
--changeset ManhNV8:v1.2__alter_table_application_20230412 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE `application`
    ADD COLUMN `partner_code` VARCHAR(45) NULL AFTER `loan_approval_user`;

ALTER TABLE `customer_address` ADD `can_delete` TINYINT(1) NULL;

ALTER TABLE `individual_customer` DROP COLUMN `partner_code`;

CREATE UNIQUE INDEX application_bpm_id_IDX USING BTREE ON application (bpm_id);
