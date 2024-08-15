--liquibase formatted sql
--changeset BaoNV2:v1.38_create_table_application_historic_transaction_20220806 dbms:mysql
--preconditions onFail:HALT onError:HALT`
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'application_historic_transaction'
CREATE TABLE `application_historic_transaction` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `application_id` bigint DEFAULT NULL,
    `bpm_id` varchar(36) DEFAULT NULL,
    `credit_type` varchar(10) DEFAULT NULL COMMENT 'Cho vay/ Thấu Chi / Thẻ',
    `card_type` varchar(10) DEFAULT NULL COMMENT 'Thẻ chính/phụ',
    `guarantee_form` varchar(10) DEFAULT NULL COMMENT 'Có TSBĐ / không TSBĐ',
    `integrated_system` varchar(16) DEFAULT NULL COMMENT 'Hệ thống tích hợp',
    `cif` varchar(45) DEFAULT NULL COMMENT 'Số CIF tại Core',
    `identifier_code` varchar(16) DEFAULT NULL COMMENT 'Mã số định danh',
    `application_credit_id` bigint DEFAULT NULL COMMENT 'ID khoản vay',
    `loan_amount` decimal(14,0) DEFAULT NULL COMMENT 'Số tiền vay/Hạn mức cấp/Hạn mức thẻ',
    `integrated_status` varchar(20) DEFAULT NULL COMMENT 'Trạng thái tích hợp',
    `next_integrated_time` datetime DEFAULT NULL COMMENT 'Thời gian tích hợp lại ',
    `error_code` varchar(50) DEFAULT NULL COMMENT 'Mã lỗi',
    `error_description` varchar(1000) DEFAULT NULL COMMENT 'Mô tả lỗi',
    `first_name` varchar(255) DEFAULT NULL,
    `last_name` varchar(255) DEFAULT NULL,
    `created_by` varchar(50) DEFAULT NULL COMMENT 'User khởi tạo',
    `created_at` datetime DEFAULT NULL COMMENT 'Thời gian khởi tạo',
    `updated_by` varchar(50) DEFAULT NULL COMMENT 'User cập nhật',
    `updated_at` datetime DEFAULT NULL COMMENT 'Thời gian cập nhật',
    `integrated_status_detail` varchar(1000) DEFAULT NULL COMMENT 'Chi tiết trạng thái',
    `effective_date` date DEFAULT NULL COMMENT 'Trạng thái active',
    `business_unit` varchar(255) DEFAULT NULL COMMENT 'Đơn vị kinh doanh',
    `document_code` varchar(45) DEFAULT NULL COMMENT 'Mã văn bản',
    `json_response` varchar(1000) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;