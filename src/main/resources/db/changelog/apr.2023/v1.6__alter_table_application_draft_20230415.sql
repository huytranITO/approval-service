--liquibase formatted sql
--changeset ManhNV8:v1.6__alter_table_application_draft_20230415-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'application_draft' AND INDEX_NAME = 'application_tab_code_IDX';
ALTER TABLE `application_draft` ADD INDEX `application_tab_code_IDX` (`tab_code` ASC) VISIBLE;

--liquibase formatted sql
--changeset ManhNV8:v1.6__alter_table_application_draft_20230415-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_phone_expertise ' AND COLUMN_NAME = 'phone_number';
ALTER TABLE `application_phone_expertise`
    CHANGE COLUMN `customer_id` `phone_number` VARCHAR(20) NULL DEFAULT NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.6__alter_table_application_draft_20230415-003 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_phone_expertise ' AND COLUMN_NAME = 'person_answer';
ALTER TABLE `application_phone_expertise`
    ADD COLUMN `person_answer` VARCHAR(10) NULL AFTER `updated_at`;

--liquibase formatted sql
--changeset ManhNV8:v1.6__alter_table_application_draft_20230415-004 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE `application_repayment`
    CHANGE COLUMN `dti` `dti` INT NULL DEFAULT NULL ,
    CHANGE COLUMN `dsr` `dsr` INT NULL DEFAULT NULL ;

--liquibase formatted sql
--changeset ManhNV8:v1.6__alter_table_application_draft_20230415-005 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE application_repayment MODIFY COLUMN dti DOUBLE NULL;
ALTER TABLE application_repayment MODIFY COLUMN dsr DOUBLE NULL;
