--liquibase formatted sql
--changeset ManhNV8:v1.31__alter_table_customer_adding_column_ref_customer_id_20230531 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer' AND COLUMN_NAME = 'ref_customer_id';
ALTER TABLE `customer`
ADD COLUMN `ref_customer_id` bigint(0) NULL COMMENT 'ID khách hàng từ customer-additional-info service' AFTER `id`,
ADD INDEX `ref_customer_id_idx`(`ref_customer_id`) USING BTREE;

--liquibase formatted sql
--changeset ManhNV8:v1.31__alter_table_customer_address_adding_column_ref_address_id_20230531 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_address' AND COLUMN_NAME = 'ref_address_id';
ALTER TABLE `customer_address`
ADD COLUMN `ref_address_id` bigint(0) NULL COMMENT 'ID địa chỉ khách hàng từ customer-additional-info service' AFTER `customer_id`,
ADD INDEX `ref_address_id_idx`(`ref_address_id`) USING BTREE;

--liquibase formatted sql
--changeset ManhNV8:v1.31__alter_table_customer_identity_adding_column_ref_identity_id_20230531 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_identity' AND COLUMN_NAME = 'ref_identity_id';
ALTER TABLE `customer_identity`
ADD COLUMN `ref_identity_id` bigint(0) NULL COMMENT 'ID định danh từ customer-additional-info service' AFTER `customer_id`,
ADD INDEX `ref_identity_id_idx`(`ref_identity_id`) USING BTREE;

--liquibase formatted sql
--changeset ManhNV8:v1.31__alter_table_application_history_approval_20230531 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'reception_level';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'proposal_approval_user';
ALTER TABLE `application_history_approval`
CHANGE COLUMN `reception_level` `proposal_approval_reception` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Cấp Thẩm quyền tiếp nhận' AFTER `step_description`,
ADD COLUMN `proposal_approval_user` varchar(50) NULL COMMENT 'CB tiếp nhận' AFTER `proposal_approval_reception`;

--liquibase formatted sql
--changeset ManhNV8:v1.31__alter_table_application_20230531 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE `application`
DROP COLUMN `proposal_approval_reception`,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID khách hàng trình hồ sơ' AFTER `id`,
MODIFY COLUMN `process_instance_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID process workflow camunda' AFTER `customer_id`,
MODIFY COLUMN `process_flow` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã luồng trình' AFTER `approval_type`,
MODIFY COLUMN `process_flow_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên luồng trình' AFTER `process_flow`,
MODIFY COLUMN `submission_purpose` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã mục đích trình' AFTER `process_flow_value`,
MODIFY COLUMN `submission_purpose_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên mục đích trình' AFTER `submission_purpose`,
MODIFY COLUMN `distribution_form_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã hình thức cấp' AFTER `regulatory_code`,
MODIFY COLUMN `distribution_form` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên hình thức cấp' AFTER `distribution_form_code`,
MODIFY COLUMN `effective_period_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Đơn vị Thời gian hiệu lực kể từ ngày ký văn bản phê duyệt DAY | MONTH | YEAR' AFTER `effective_period`,
MODIFY COLUMN `proposal_approval_position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã thẩm quyền phê duyệt đề xuất' AFTER `effective_period_unit`,
MODIFY COLUMN `proposal_approval_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User CB tiếp nhận phê duyệt đề xuất/Thẩm định/Kiểm soát' AFTER `proposal_approval_position`,
MODIFY COLUMN `loan_approval_position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã thẩm quyền phê duyệt khoản vay' AFTER `proposal_approval_user`,
MODIFY COLUMN `application_authority_level` int(0) NULL DEFAULT NULL COMMENT 'Cấp độ ưu tiên thẩm quyền phê duyệt khoản vay' AFTER `loan_approval_position`,
MODIFY COLUMN `priority_authority` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Cấp thẩm quyền phê duyệt khoản vay' AFTER `application_authority_level`,
MODIFY COLUMN `partner_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã đối tác' AFTER `priority_authority`,
MODIFY COLUMN `proposal_approval_full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên người phê duyệt/Thẩm định/Kiểm soát' AFTER `created_phone_number`,
MODIFY COLUMN `proposal_approval_phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SĐT người phê duyệt/Thẩm định/Kiểm soát' AFTER `proposal_approval_full_name`,
MODIFY COLUMN `area_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã vùng' AFTER `proposal_approval_phone_number`,
MODIFY COLUMN `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên vùng' AFTER `area_code`,
MODIFY COLUMN `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên miền' AFTER `region_code`,
MODIFY COLUMN `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User khởi tạo (tạo từ BPM hoặc lấy user được gán cho hồ sơ từ CMS)' AFTER `region`,
MODIFY COLUMN `created_at` datetime(0) NULL DEFAULT NULL COMMENT 'Thời gian khởi tạo tại BPM' AFTER `created_by`,
MODIFY COLUMN `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User cập nhật' AFTER `created_at`,
MODIFY COLUMN `updated_at` datetime(0) NULL DEFAULT NULL COMMENT 'Thời gian cập nhật hoặc thời gian hoàn thành hồ sơ)' AFTER `updated_by`;

--liquibase formatted sql
--changeset ManhNV8:v1.31__alter_table_process_instance_add_new_column_20230531 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'process_instance' AND COLUMN_NAME = 'task_id';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'process_instance' AND COLUMN_NAME = 'task_definition_key';
ALTER TABLE process_instance ADD task_id varchar(36) NULL;
ALTER TABLE process_instance CHANGE task_id task_id varchar(36) NULL AFTER sub_workflow_version;
ALTER TABLE process_instance ADD task_definition_key varchar(100) NULL;
ALTER TABLE process_instance CHANGE task_definition_key task_definition_key varchar(100) NULL AFTER task_id;
