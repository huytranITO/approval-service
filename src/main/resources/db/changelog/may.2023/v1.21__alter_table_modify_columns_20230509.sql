--liquibase formatted sql
--changeset ManhNV8:v1.21__alter_table_modify_columns_20230509 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE application MODIFY COLUMN partner_code varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL;
ALTER TABLE application MODIFY COLUMN region varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Miền';
ALTER TABLE customer_identity MODIFY COLUMN identifier_code varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã số định danh';
ALTER TABLE enterprise_customer MODIFY COLUMN business_registration_number varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã số ĐKKD';
ALTER TABLE cic MODIFY COLUMN identifier_code varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL;
ALTER TABLE aml_opr MODIFY COLUMN identifier_code varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL;
ALTER TABLE salary_income MODIFY COLUMN business_registration_number varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã số ĐKKD ĐVCT';
ALTER TABLE salary_income MODIFY COLUMN group_of_working varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Phân nhóm ĐVCT';
ALTER TABLE salary_income MODIFY COLUMN `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Chức vụ';
ALTER TABLE rental_income MODIFY COLUMN asset_owner varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Chủ sở hữu';
ALTER TABLE rental_income MODIFY COLUMN renter varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Bên Thuê';
ALTER TABLE individual_enterprise_income MODIFY COLUMN business_registration_number varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Mã số ĐKKD';
ALTER TABLE individual_enterprise_income MODIFY COLUMN production_process varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Quy trình sản xuất kinh doanh';
ALTER TABLE individual_enterprise_income MODIFY COLUMN `input` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Nguồn hàng đầu vào ';
ALTER TABLE individual_enterprise_income MODIFY COLUMN `output` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Đầu ra';
ALTER TABLE individual_enterprise_income MODIFY COLUMN business_scale varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Quy mô kinh doanh';
ALTER TABLE individual_enterprise_income MODIFY COLUMN inventory varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Hàng tồn kho, phải thu, phải trả';
ALTER TABLE individual_enterprise_income MODIFY COLUMN profit_margin DOUBLE NULL COMMENT 'Biên lợi nhuận';
ALTER TABLE individual_enterprise_income MODIFY COLUMN evaluate_result TEXT NULL COMMENT 'Đánh giá kết quả kinh doanh';
ALTER TABLE individual_enterprise_income MODIFY COLUMN record_method varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Phương thức ghi chép';
ALTER TABLE application_credit_ratings MODIFY COLUMN rating_result varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Kết quả xếp hạng';
ALTER TABLE application_appraisal_content MODIFY COLUMN regulation varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Quy định';
ALTER TABLE application_appraisal_content MODIFY COLUMN management_measures TEXT NULL COMMENT 'Cơ sở đề xuất và biện pháp quản lý';
