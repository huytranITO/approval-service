--liquibase formatted sql
--changeset ManhNV8:v1.16__alter_table_individual_customer_20230421-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer' AND COLUMN_NAME = 'email';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer' AND COLUMN_NAME = 'phone_number';
ALTER TABLE individual_customer ADD email varchar(50) NULL;
ALTER TABLE individual_customer ADD phone_number varchar(20) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.16__alter_table_individual_customer_20230421-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'customer_contact';
DROP TABLE customer_contact;

--liquibase formatted sql
--changeset ManhNV8:v1.16__alter_table_individual_customer_20230421-003 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_phone_expertise' AND COLUMN_NAME = 'person_answer_value';
ALTER TABLE application_phone_expertise ADD person_answer_value varchar(100) NULL;
