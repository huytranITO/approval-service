--liquibase formatted sql
--changeset ManhNV8:v1.80__create_new_table_application_contact_20230828-create_new dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_contact';
CREATE TABLE application_contact (
	application_id BIGINT NOT NULL COMMENT 'PK, -- ref : application.id',
	relationship VARCHAR(10) NULL COMMENT 'Mã quan hệ với người vay',
	relationship_txt varchar(100) NULL COMMENT 'Tên quan hệ với người vay',
	full_name varchar(100) NULL COMMENT 'Họ và tên',
	phone_number varchar(10) NULL COMMENT 'Số điện thoại',
	created_at DATETIME NULL COMMENT 'Ngày khởi tạo',
	created_by varchar(50) NULL COMMENT 'Người khởi tạo',
	updated_at DATETIME NULL COMMENT 'Ngày cập nhật',
	updated_by VARCHAR(50) NULL COMMENT 'Người cập nhật',
	CONSTRAINT application_contact_PK PRIMARY KEY (application_id),
	CONSTRAINT application_contact_FK FOREIGN KEY (application_id) REFERENCES application(id) ON DELETE RESTRICT ON UPDATE RESTRICT
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

--liquibase formatted sql
--changeset ManhNV8:v1.80__create_new_table_application_contact_20230828-update dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer' AND COLUMN_NAME = 'literacy';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'individual_customer' AND COLUMN_NAME = 'literacy_txt';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'salary_income' AND COLUMN_NAME = 'phone_work';
ALTER TABLE individual_customer ADD literacy varchar(20) NULL COMMENT 'Trình độ học vấn';
ALTER TABLE individual_customer CHANGE literacy literacy varchar(20) NULL COMMENT 'Trình độ học vấn' AFTER phone_number;
ALTER TABLE individual_customer ADD literacy_txt varchar(100) NULL COMMENT 'Trình độ học vấn';
ALTER TABLE individual_customer CHANGE literacy_txt literacy_txt varchar(100) NULL COMMENT 'Trình độ học vấn' AFTER literacy;
ALTER TABLE salary_income ADD phone_work varchar(20) NULL COMMENT 'Số điện thoại tại cơ quan';
ALTER TABLE salary_income CHANGE phone_work phone_work varchar(20) NULL COMMENT 'Số điện thoại tại cơ quan' AFTER company_name;