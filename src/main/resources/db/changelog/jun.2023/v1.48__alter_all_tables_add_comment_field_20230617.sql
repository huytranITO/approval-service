--liquibase formatted sql
--changeset ManhNV8:v1.48__alter_all_tables_add_comment_field_20230617 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE `aml_opr`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'ID khách hàng (tại rb-approval-service)' AFTER `application_id`,
MODIFY COLUMN `identifier_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL AFTER `customer_id`,
MODIFY COLUMN `subject` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL AFTER `identifier_code`,
MODIFY COLUMN `start_date` date NULL DEFAULT NULL AFTER `reason_in_list`,
MODIFY COLUMN `end_date` date NULL DEFAULT NULL AFTER `start_date`;

ALTER TABLE `application`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID khách hàng -- ref : customer.id' AFTER `id`,
MODIFY COLUMN `business_unit_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã đơn vị kinh doanh' AFTER `risk_level`,
MODIFY COLUMN `business_unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Tên đơn vị kinh doanh' AFTER `business_unit_code`,
MODIFY COLUMN `loan_approval_position_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên thẩm quyền phê duyệt khoan vay' AFTER `loan_approval_position`,
MODIFY COLUMN `branch_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã chi nhánh' AFTER `region`,
MODIFY COLUMN `branch_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên chi nhánh' AFTER `branch_code`;

ALTER TABLE `application_appraisal_content`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `content_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Loại nội dung\r\n- SPECIAL_RISK: Nội dung rủi ro đặc thủ\r\n- OTHER_ADDITIONAL: Nội dung khác biệt bổ sung' AFTER `application_id`,
MODIFY COLUMN `criteria_group` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã nhóm tiêu chí' AFTER `content_type`,
MODIFY COLUMN `criteria_group_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên nhóm tiêu chí' AFTER `criteria_group`,
MODIFY COLUMN `detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Danh sách mã chi tiết. Cách nhau bằng dấu \",\"' AFTER `criteria_group_value`,
MODIFY COLUMN `detail_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Danh sách nội dung chi tiết. Cách nhau bằng dấu \",\"' AFTER `detail`,
MODIFY COLUMN `authorization` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã thẩm quyền' AFTER `management_measures`;

ALTER TABLE `application_credit`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `credit_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã khoản cấp tín dụng' AFTER `application_id`,
MODIFY COLUMN `credit_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên khoản cấp tín dụng' AFTER `credit_type`,
MODIFY COLUMN `guarantee_form` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã hình thức đảm bảo' AFTER `credit_type_value`,
MODIFY COLUMN `guarantee_form_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên hình thức đảm bảo' AFTER `guarantee_form`,
MODIFY COLUMN `document_code` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã văn bản' AFTER `guarantee_form_value`,
MODIFY COLUMN `approve_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã ý kiến phê duyệt, Y / N' AFTER `document_code`,
MODIFY COLUMN `approve_result_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Nội dung ý kiến phê duyệt\r\n- Y : đồng ý\r\n- N : từ chối' AFTER `approve_result`;

ALTER TABLE `application_credit_card`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `card_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã loại thẻ' AFTER `product_name`,
MODIFY COLUMN `card_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên loại thẻ' AFTER `card_type`,
MODIFY COLUMN `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Email khách hàng' AFTER `limit_sustentive_period`,
MODIFY COLUMN `auto_deduct_rate` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã tỷ lệ trích nợ tự động' AFTER `email`,
MODIFY COLUMN `auto_deduct_rate_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên tỷ lệ trích nợ tự động' AFTER `auto_deduct_rate`,
MODIFY COLUMN `card_form` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã hình thức thẻ' AFTER `deduct_account_number`,
MODIFY COLUMN `card_form_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên hình thức thẻ' AFTER `card_form`,
MODIFY COLUMN `card_receive_addr` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã địa chỉ nhận thẻ' AFTER `card_form_value`,
MODIFY COLUMN `card_receive_addr_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên địa chỉ nhận thẻ' AFTER `card_receive_addr`;

ALTER TABLE `application_credit_loan`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `loan_purpose` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã mục đích vay' AFTER `payback`,
MODIFY COLUMN `loan_purpose_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên mục đích vay' AFTER `loan_purpose`,
MODIFY COLUMN `ltd` int(0) NULL DEFAULT NULL COMMENT 'LTD' AFTER `equity_capital`,
MODIFY COLUMN `credit_form` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã hình thức cấp tín dụng' AFTER `ltd`,
MODIFY COLUMN `credit_form_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên hình thức cấp tín dụng' AFTER `credit_form`,
MODIFY COLUMN `disburse_frequency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã tần suất giải ngân' AFTER `credit_form_value`,
MODIFY COLUMN `disburse_frequency_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên tần suất giải ngân' AFTER `disburse_frequency`,
MODIFY COLUMN `debt_pay_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã phương thức trả nợ' AFTER `disburse_frequency_value`,
MODIFY COLUMN `debt_pay_method_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên phương thức trả nợ' AFTER `debt_pay_method`,
MODIFY COLUMN `disburse_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã phương thức giải ngân' AFTER `debt_pay_method_value`,
MODIFY COLUMN `disburse_method_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên phương thức giải ngân' AFTER `disburse_method`,
MODIFY COLUMN `principal_pay_unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã đơn vị kỳ trả nợ gốc' AFTER `principal_pay_period`,
MODIFY COLUMN `principal_pay_unit_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên đơn vị kỳ trả nợ gốc' AFTER `principal_pay_unit`,
MODIFY COLUMN `interest_pay_unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã đơn vị kỳ trả lãi' AFTER `interest_pay_period`,
MODIFY COLUMN `interest_pay_unit_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên đơn vị kỳ trả lãi' AFTER `interest_pay_unit`;

ALTER TABLE `application_credit_overdraft`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `loan_purpose` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã mục đích vay' AFTER `product_name`,
MODIFY COLUMN `loan_purpose_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên mục đích vay' AFTER `loan_purpose`,
MODIFY COLUMN `debt_pay_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã phương thức trả nợ' AFTER `limit_sustentive_period`,
MODIFY COLUMN `debt_pay_method_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên phương thức trả nợ' AFTER `debt_pay_method`;

ALTER TABLE `application_credit_ratings`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`;

ALTER TABLE `application_credit_ratings_dtl`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_credit_ratings_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID xếp hạng tín dụng -- ref : application_credit_ratings.id' AFTER `id`;

ALTER TABLE `application_draft`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `bpm_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã hồ sơ' AFTER `id`,
MODIFY COLUMN `tab_code` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã định danh cho tab hồ sơ tương ứng trên màn hình\r\n- initialize_info - Thông tin hồ sơ\r\n- field_info - Thông tin thực địa\r\n- debt_info - Thông tin khoản vay' AFTER `bpm_id`,
MODIFY COLUMN `processing_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Role xử lý hồ sơ' AFTER `tab_code`,
MODIFY COLUMN `data` blob NULL COMMENT 'Dữ liệu tab hồ sơ lưu tạm' AFTER `processing_role`,
MODIFY COLUMN `status` int(0) NULL DEFAULT NULL COMMENT 'Trạng thái tab hồ sơ\r\n- 1 : Đã hoàn thành\r\n- 0 : Chưa hoàn thành' AFTER `data`;

ALTER TABLE `application_extra_data`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã khối thông tin' AFTER `category`;

ALTER TABLE `application_field_information`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `place_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Loại địa điểm' AFTER `application_id`,
MODIFY COLUMN `place_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Loại địa điểm' AFTER `place_type`,
MODIFY COLUMN `relationship` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Mối quan hệ' AFTER `place_type_value`,
MODIFY COLUMN `relationship_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Mối quan hệ' AFTER `relationship`,
MODIFY COLUMN `city_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Tỉnh TP' AFTER `relationship_value`,
MODIFY COLUMN `city_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Tỉnh TP' AFTER `city_code`,
MODIFY COLUMN `district_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Quận Huyện' AFTER `city_value`,
MODIFY COLUMN `district_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Quận Huyện' AFTER `district_code`,
MODIFY COLUMN `ward_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Phường/xã' AFTER `district_value`,
MODIFY COLUMN `ward_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Phường/xã' AFTER `ward_code`,
MODIFY COLUMN `result` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Kết quả' AFTER `executor`,
MODIFY COLUMN `result_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Kết quả' AFTER `result`,
MODIFY COLUMN `address_link_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'UUID link địa chỉ thực địa với địa chỉ khách hàng/nguồn thu' AFTER `result_value`;

ALTER TABLE `application_history_approval`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `executed_at` datetime(0) NULL DEFAULT NULL COMMENT 'Thời gian khi hồ sơ được chuyển bước thành công' AFTER `application_id`,
MODIFY COLUMN `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên CB xử lý hồ sơ' AFTER `executed_at`,
MODIFY COLUMN `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Username CB xử lý hồ sơ' AFTER `full_name`,
MODIFY COLUMN `user_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Role xử lý hồ sơ' AFTER `username`,
MODIFY COLUMN `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Lý do phản hồi (nếu có)' AFTER `user_role`,
MODIFY COLUMN `step_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã bước xử lý' AFTER `reason`,
MODIFY COLUMN `step_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mô tả bước xử lý' AFTER `step_code`,
MODIFY COLUMN `proposal_approval_reception` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Cấp Thẩm quyền tiếp nhận hồ sơ' AFTER `step_description`,
MODIFY COLUMN `proposal_approval_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Username CB tiếp nhận hồ sơ' AFTER `proposal_approval_reception`,
MODIFY COLUMN `proposal_approval_full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên CB tiếp nhận hồ sơ' AFTER `proposal_approval_user`;

ALTER TABLE `application_history_feedback`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `user_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Role CB phản hồi' AFTER `application_id`,
MODIFY COLUMN `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Username CB phản hồi' AFTER `user_role`,
MODIFY COLUMN `feedback_at` datetime(0) NULL DEFAULT NULL COMMENT 'Thời gian cuối cùng tạo/sửa phản hồi' AFTER `username`,
MODIFY COLUMN `feedback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Nội dung phản hồi' AFTER `feedback_at`,
MODIFY COLUMN `created_phone_number` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Số điện thoại CB tạo/sửa phản hồi' AFTER `feedback_content`;

ALTER TABLE `application_income`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `income_recognition_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã phương pháp ghi nhận thu nhập EXCHANGE | ACTUALLY' AFTER `application_id`,
MODIFY COLUMN `income_recognition_method_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên phương pháp ghi nhận thu nhập' AFTER `income_recognition_method`,
MODIFY COLUMN `conversion_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã phương pháp quy đổi' AFTER `currency`,
MODIFY COLUMN `conversion_method_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên phương pháp quy đổi' AFTER `conversion_method`;

ALTER TABLE `application_limit_credit`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `loan_limit` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Số tiền vay/Hạn mức' AFTER `application_id`,
MODIFY COLUMN `loan_limit_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Số tiền vay/Hạn mức' AFTER `loan_limit`;

ALTER TABLE `application_phone_expertise`
MODIFY COLUMN `person_answer` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Người trả lời điện thoại' AFTER `ext`,
MODIFY COLUMN `person_answer_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Người trả lời điện thoại' AFTER `person_answer`,
MODIFY COLUMN `order_display` int(0) NULL DEFAULT NULL COMMENT 'Thứ tự hiển thị' AFTER `person_answer_value`,
MODIFY COLUMN `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User khởi tạo' AFTER `order_display`;

ALTER TABLE `application_repayment`
MODIFY COLUMN `application_id` bigint(0) NOT NULL COMMENT 'PK -- ref : application.id' FIRST,
MODIFY COLUMN `total_repay` decimal(14, 0) NULL DEFAULT NULL COMMENT 'Tổng nghĩa vụ trả nợ' AFTER `application_id`,
MODIFY COLUMN `dti` double NULL DEFAULT NULL COMMENT 'DTI' AFTER `total_repay`,
MODIFY COLUMN `dsr` double NULL DEFAULT NULL COMMENT 'DSR' AFTER `dti`,
MODIFY COLUMN `mue` decimal(14, 0) NULL DEFAULT NULL COMMENT 'MUE' AFTER `dsr`,
MODIFY COLUMN `evaluate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Đánh giá' AFTER `mue`;

ALTER TABLE `cic`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'ID khách hàng ' AFTER `application_id`,
MODIFY COLUMN `subject` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Đối tượng' AFTER `customer_id`,
MODIFY COLUMN `identifier_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã định danh' AFTER `subject`,
MODIFY COLUMN `deft_group_current` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Nhóm nợ 12 tháng gần nhất' AFTER `identifier_code`,
MODIFY COLUMN `cic_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã đối tượng liên quan' AFTER `query_at`,
MODIFY COLUMN `client_question_id` bigint(0) NULL DEFAULT NULL COMMENT 'Mã hệ thống hỏi tin tới CIC' AFTER `cic_code`;

ALTER TABLE `customer_address`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID khách hàng -- ref : customer.id' AFTER `id`,
MODIFY COLUMN `address_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Loại địa chỉ/Thông tin địa chỉ' AFTER `ref_address_id`,
MODIFY COLUMN `address_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Loại địa chỉ/Thông tin địa chỉ' AFTER `address_type`,
MODIFY COLUMN `city_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Tỉnh/ Thành phố' AFTER `same_permanent_residence`,
MODIFY COLUMN `city_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Tỉnh/ Thành phố' AFTER `city_code`,
MODIFY COLUMN `district_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Quận/ Huyện' AFTER `city_value`,
MODIFY COLUMN `district_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Quận/ Huyện' AFTER `district_code`,
MODIFY COLUMN `ward_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Phường/ Xã' AFTER `district_value`,
MODIFY COLUMN `ward_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Phường/ Xã' AFTER `ward_code`,
MODIFY COLUMN `can_delete` tinyint(1) NULL DEFAULT 0 COMMENT 'Có thể xóa hay không ?' AFTER `address_link_id`;

ALTER TABLE `customer_contact`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID khách hàng -- ref : customer.id' AFTER `id`;

ALTER TABLE `customer_identity`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID khách hàng -- ref : customer.id' AFTER `id`,
MODIFY COLUMN `document_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Loại giấy tờ' AFTER `ref_identity_id`,
MODIFY COLUMN `document_type_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Loại giấy tờ' AFTER `document_type`,
MODIFY COLUMN `issued_by` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Cấp bởi' AFTER `issued_at`,
MODIFY COLUMN `issued_by_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Cấp bởi' AFTER `issued_by`,
MODIFY COLUMN `issued_place` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Nơi cấp' AFTER `issued_by_value`,
MODIFY COLUMN `issued_place_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Nơi cấp' AFTER `issued_place`,
MODIFY COLUMN `priority` tinyint(1) NULL DEFAULT 0 COMMENT 'Định danh chính?' AFTER `issued_place_value`;

ALTER TABLE `customer_relation_ship`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID khách hàng -- ref : customer.id' AFTER `id`,
MODIFY COLUMN `customer_ref_id` bigint(0) NULL DEFAULT NULL COMMENT 'ID khách hàng liên quan' AFTER `customer_id`,
MODIFY COLUMN `relationship` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Mối quan hệ' AFTER `customer_ref_id`,
MODIFY COLUMN `relationship_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Mối quan hệ' AFTER `relationship`;

ALTER TABLE `enterprise_customer`
MODIFY COLUMN `customer_id` bigint(0) NOT NULL COMMENT 'PK, ID khách hàng -- ref : customer.id' FIRST;

ALTER TABLE `individual_customer`
CHANGE COLUMN `martial_status` `marital_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Tình trạng hôn nhân' AFTER `age`,
CHANGE COLUMN `martial_status_value` `marital_status_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Tình trạng hôn nhân' AFTER `marital_status`,
MODIFY COLUMN `customer_id` bigint(0) NOT NULL COMMENT 'PK, ID khách hàng -- ref : customer.id' FIRST,
MODIFY COLUMN `gender` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Giới tính' AFTER `last_name`,
MODIFY COLUMN `gender_value` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Giới tính' AFTER `gender`,
MODIFY COLUMN `nation` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Quốc tịch' AFTER `marital_status_value`,
MODIFY COLUMN `nation_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Quốc tịch' AFTER `nation`,
MODIFY COLUMN `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Email' AFTER `employee_code`,
MODIFY COLUMN `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Số điện thoại' AFTER `email`;

ALTER TABLE `individual_enterprise_income`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `business_place_ownership` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Hình thức sở hữu địa điểm kinh doanh' AFTER `capital_contribution_rate`,
MODIFY COLUMN `business_place_ownership_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Hình thức sở hữu địa điểm kinh doanh' AFTER `business_place_ownership`,
MODIFY COLUMN `city_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Địa chỉ kinh doanh thực tế - Tỉnh/TP' AFTER `business_place_ownership_value`,
MODIFY COLUMN `city_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Địa chỉ kinh doanh thực tế - Tỉnh/TP' AFTER `city_code`,
MODIFY COLUMN `district_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Địa chỉ kinh doanh thực tế - Quận/huyện/thị trấn' AFTER `city_value`,
MODIFY COLUMN `district_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Địa chỉ kinh doanh thực tế - Quận/huyện/thị trấn' AFTER `district_code`,
MODIFY COLUMN `ward_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Địa chỉ kinh doanh thực tế - Phường/xã/thôn' AFTER `district_value`,
MODIFY COLUMN `ward_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Địa chỉ kinh doanh thực tế - Phường/xã/thôn' AFTER `ward_code`,
MODIFY COLUMN `evaluation_period` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Kỳ đánh giá kết quả' AFTER `inventory`,
MODIFY COLUMN `evaluation_period_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Kỳ đánh giá kết quả' AFTER `evaluation_period`,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'ID khách hàng' AFTER `address_link_id`,
ADD COLUMN `ref_customer_id` bigint(0) NULL COMMENT 'ID khách hàng (từ customer-additional-info)' AFTER `customer_id`,
MODIFY COLUMN `income_owner` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (Mã mối quan hệ với khách hàng)' AFTER `customer_id`,
MODIFY COLUMN `income_owner_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (Tên mối quan hệ với khách hàng)' AFTER `income_owner`,
MODIFY COLUMN `income_owner_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ Tên chủ nguồn thu' AFTER `income_owner_value`,
MODIFY COLUMN `income_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã loại nguồn thu' AFTER `income_owner_name`,
MODIFY COLUMN `income_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên loại nguồn thu' AFTER `income_type`,
MODIFY COLUMN `order_display` int(0) NULL DEFAULT NULL COMMENT 'Thứ tự ưu tiên' AFTER `income_type_value`,
MODIFY COLUMN `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User khởi tạo' AFTER `order_display`,
MODIFY COLUMN `created_at` datetime(0) NULL DEFAULT NULL COMMENT 'Thời gian khởi tạo' AFTER `created_by`,
MODIFY COLUMN `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User cập nhật' AFTER `created_at`,
MODIFY COLUMN `updated_at` datetime(0) NULL DEFAULT NULL COMMENT 'Thời gian cập nhật' AFTER `updated_by`;

ALTER TABLE `other_income`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `income_detail` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Chi tiết nguồn thu khác' AFTER `income_info`,
MODIFY COLUMN `income_detail_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Nội dung Chi tiết nguồn thu khác' AFTER `income_detail`,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'ID khách hàng' AFTER `income_detail_value`,
ADD COLUMN `ref_customer_id` bigint(0) NULL COMMENT 'ID khách hàng (từ customer-additional-info)' AFTER `customer_id`,
MODIFY COLUMN `income_owner` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (mã mối quan hệ với khách hàng)' AFTER `customer_id`,
MODIFY COLUMN `income_owner_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (tên mối quan hệ với khách hàng)' AFTER `income_owner`,
MODIFY COLUMN `income_owner_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên chủ nguồn thu' AFTER `income_owner_value`,
MODIFY COLUMN `income_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã loại nguồn thu' AFTER `income_owner_name`,
MODIFY COLUMN `income_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên loại nguồn thu' AFTER `income_type`,
MODIFY COLUMN `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Diễn giải' AFTER `income_type_value`;

ALTER TABLE `process_instance`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `task_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ID Task xử lý' AFTER `sub_workflow_version`,
MODIFY COLUMN `task_definition_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Key Task xử lý' AFTER `task_id`,
MODIFY COLUMN `created_at` datetime(0) NULL DEFAULT NULL COMMENT 'Ngày khởi tạo' AFTER `task_definition_key`,
MODIFY COLUMN `updated_at` datetime(0) NULL DEFAULT NULL COMMENT 'Ngày cập nhật' AFTER `created_at`;

ALTER TABLE `rental_income`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `asset_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Loại tài sản' AFTER `id`,
MODIFY COLUMN `asset_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Loại tài sản' AFTER `asset_type`,
MODIFY COLUMN `asset_owner` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Chủ sở hữu' AFTER `asset_type_value`,
MODIFY COLUMN `asset_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Địa chỉ tài sản/Loại xe' AFTER `asset_owner`,
MODIFY COLUMN `rental_purpose` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Mục đích thuê' AFTER `renter_phone`,
MODIFY COLUMN `rental_purpose_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Mục đích thuê' AFTER `rental_purpose`,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'ID khách hàng' AFTER `rental_price`,
ADD COLUMN `ref_customer_id` bigint(0) NULL COMMENT 'ID khách hàng (từ customer-additional-info)' AFTER `customer_id`,
MODIFY COLUMN `income_owner` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (mã mối quan hệ với khách hàng)' AFTER `customer_id`,
MODIFY COLUMN `income_owner_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (tên mối quan hệ với khách hàng)' AFTER `income_owner`,
MODIFY COLUMN `income_owner_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên chủ nguồn thu' AFTER `income_owner_value`,
MODIFY COLUMN `income_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã loại nguồn thu' AFTER `income_owner_name`,
MODIFY COLUMN `income_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên loại nguồn thu' AFTER `income_type`,
MODIFY COLUMN `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Diễn giải (nếu có)' AFTER `income_type_value`;

ALTER TABLE `rule_version_mapping`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_id` bigint(0) NOT NULL COMMENT 'FK, ID hồ sơ -- ref : application.id' AFTER `id`,
MODIFY COLUMN `rule_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã decision rule' AFTER `application_id`,
MODIFY COLUMN `group_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã nhóm đối tượng (checklist)' AFTER `rule_code`,
MODIFY COLUMN `rule_version` int(0) NULL DEFAULT NULL COMMENT 'Phiên bản decision rule hiện tại sử dụng' AFTER `group_code`,
MODIFY COLUMN `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mô tả' AFTER `rule_version`,
MODIFY COLUMN `created_at` datetime(0) NULL DEFAULT NULL COMMENT 'Ngày khởi tạo' AFTER `description`,
MODIFY COLUMN `created_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Người khởi tạo' AFTER `created_at`,
MODIFY COLUMN `updated_at` datetime(0) NULL DEFAULT NULL COMMENT 'Ngày cập nhật' AFTER `created_by`,
MODIFY COLUMN `updated_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Người cập nhật' AFTER `updated_at`;

ALTER TABLE `salary_income`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `rank_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Cấp bậc' AFTER `social_insurance_code`,
MODIFY COLUMN `rank_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Cấp bậc' AFTER `rank_type`,
MODIFY COLUMN `kpi_rating` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Xếp loại KPI' AFTER `rank_type_value`,
MODIFY COLUMN `kpi_rating_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Xếp loại KPI' AFTER `kpi_rating`,
MODIFY COLUMN `pay_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Hình thức trả lương' AFTER `kpi_rating_value`,
MODIFY COLUMN `pay_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Hình thức trả lương' AFTER `pay_type`,
MODIFY COLUMN `labor_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Loại hình HĐLĐ' AFTER `pay_type_value`,
MODIFY COLUMN `labor_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tê Loại hình HĐLĐ' AFTER `labor_type`,
MODIFY COLUMN `city_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Tỉnh/ Thành phố' AFTER `start_working_day`,
MODIFY COLUMN `city_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Tỉnh/ Thành phố' AFTER `city_code`,
MODIFY COLUMN `district_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Quận/ Huyện' AFTER `city_value`,
MODIFY COLUMN `district_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Quận/ Huyện' AFTER `district_code`,
MODIFY COLUMN `ward_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã Phường/ Xã' AFTER `district_value`,
MODIFY COLUMN `ward_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên Phường/ Xã' AFTER `ward_code`,
MODIFY COLUMN `customer_id` bigint(0) NULL DEFAULT NULL COMMENT 'ID khách hàng' AFTER `address_line`,
ADD COLUMN `ref_customer_id` bigint(0) NULL COMMENT 'ID khách hàng (từ customer-additional-info)' AFTER `customer_id`,
MODIFY COLUMN `income_owner` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (mã mối quan hệ với khách hàng)' AFTER `customer_id`,
MODIFY COLUMN `income_owner_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Chủ nguồn thu (tên mối quan hệ với khách hàng)' AFTER `income_owner`,
MODIFY COLUMN `income_owner_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Họ tên chủ nguồn thu' AFTER `income_owner_value`,
MODIFY COLUMN `income_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Mã loại nguồn thu' AFTER `income_owner_name`,
MODIFY COLUMN `income_type_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Tên loại nguồn thu' AFTER `income_type`,
MODIFY COLUMN `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Diễn giải (nếu có)' AFTER `income_type_value`;

ALTER TABLE `sub_credit_card`
MODIFY COLUMN `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'PK' FIRST,
MODIFY COLUMN `application_credit_card_id` bigint(0) NULL DEFAULT NULL COMMENT 'FK, ID thẻ chính -- ref : application_credit_card.id' AFTER `id`,
MODIFY COLUMN `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NULL DEFAULT NULL COMMENT 'Email' AFTER `card_owner_name`;