--liquibase formatted sql
--changeset ManhNV8:v1.7__alter_table_income_20230417-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'customer_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'income_owner';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income ' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE salary_income ADD customer_id BIGINT NULL;
ALTER TABLE salary_income ADD income_owner varchar(10) NULL;
ALTER TABLE salary_income ADD income_owner_name varchar(100) NULL;

--liquibase formatted sql
--changeset v1.7__alter_table_income_20230417-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'customer_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'income_owner';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income ' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE rental_income ADD customer_id BIGINT NULL;
ALTER TABLE rental_income ADD income_owner varchar(10) NULL;
ALTER TABLE rental_income ADD income_owner_name varchar(100) NULL;

--liquibase formatted sql
--changeset v1.7__alter_table_income_20230417-003 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'customer_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'income_owner';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_enterprise_income ' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE individual_enterprise_income ADD customer_id BIGINT NULL;
ALTER TABLE individual_enterprise_income ADD income_owner varchar(10) NULL;
ALTER TABLE individual_enterprise_income ADD income_owner_name varchar(100) NULL;

--liquibase formatted sql
--changeset v1.7__alter_table_income_20230417-004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income ' AND COLUMN_NAME = 'customer_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income ' AND COLUMN_NAME = 'income_owner';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'other_income ' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE other_income ADD customer_id BIGINT NULL;
ALTER TABLE other_income ADD income_owner varchar(10) NULL;
ALTER TABLE other_income ADD income_owner_name varchar(100) NULL;

--liquibase formatted sql
--changeset v1.7__alter_table_income_20230417-005 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income ' AND COLUMN_NAME = 'customer_id';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income ' AND COLUMN_NAME = 'income_owner';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_income ' AND COLUMN_NAME = 'income_owner_name';
ALTER TABLE application_income DROP COLUMN customer_id;
ALTER TABLE application_income DROP COLUMN income_owner;
ALTER TABLE application_income DROP COLUMN income_owner_name;
