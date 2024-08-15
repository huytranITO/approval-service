--liquibase formatted sql
--changeset KhanhNQ15:v1.25__created_new_table_credit_condition_20230524 dbms:mysql
--preconditions onFail:CONTINUE onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'credit_condition'
CREATE TABLE `credit_condition` (
	id BIGINT auto_increment NOT NULL,
	code varchar(100) NOT NULL COMMENT 'Mã ĐKTD',
	customer_segment varchar(100) NULL COMMENT 'Phân khúc khách hàng',
	`group_code` varchar(20) NULL COMMENT 'Mã Nhóm điều kiện',
	`group` varchar(100) NULL COMMENT 'Nhóm điều kiện',
	detail varchar(1000) NULL COMMENT 'Điều kiện chi tiết',
	credit_type varchar(100) NULL COMMENT 'Khoản cấp tín dụng',
	`object` varchar(100) NULL COMMENT 'Đối tượng áp dụng',
	control_time varchar(100) NULL COMMENT 'Thời điểm kiểm soát',
	control_unit varchar(100) NULL COMMENT 'Đơn vị kiểm soát',
	created_at datetime NULL,
	created_by varchar(100) NULL,
	updated_at datetime NULL,
	updated_by varchar(100) NULL,
	CONSTRAINT credit_condition_PK PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;
