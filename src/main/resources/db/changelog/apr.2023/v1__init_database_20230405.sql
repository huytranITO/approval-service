--liquibase formatted sql
--changeset ManhNV8:v1__init_database_20230405 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'application'
CREATE TABLE `application` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `customer_id` bigint,
  `process_instance_id` varchar(36),
  `bpm_id` varchar(36) COMMENT 'Mã hồ sơ tự sinh của BPM',
  `ref_id` varchar(36) COMMENT 'Mã hồ sơ đẩy từ Kênh hồ sơ về BPM',
  `suggested_amount` decimal(14,0) COMMENT 'Số tiền đề xuất = SUM(hạn mức tại tab khoản vay)',
  `approval_type` varchar(20) COMMENT 'Luồng phê duyệt',
  `process_flow` varchar(10) COMMENT 'Luồng trình',
  `submission_purpose` varchar(10) COMMENT 'Mục đích trình',
  `segment` varchar(6) COMMENT 'Phân khúc KH',
  `created_by` varchar(50) COMMENT 'User khởi tạo (tạo từ BPM hoặc lấy user được gán cho hồ sơ từ CMS)',
  `created_at` datetime COMMENT 'Thời gian khởi tạo tại BPM',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật hoặc thời gian hoàn thành hồ sơ)',
  `assignee` varchar(50) COMMENT 'User được gán xử lý hồ sơ',
  `risk_level` varchar(10) COMMENT 'Mức độ rủi ro',
  `business_unit` varchar(255) COMMENT 'Đơn vị kinh doanh',
  `region` varchar(50) COMMENT 'Miền',
  `source` varchar(20) COMMENT 'Nguồn hồ sơ',
  `status` varchar(10) COMMENT 'Trạng thái hồ sơ',
  `processing_step` varchar(100) COMMENT 'Bước xử lý',
  `processing_role` varchar(20) COMMENT 'Role xử lý',
  `regulatory_code` varchar(20) COMMENT 'Mã quy định = nhiều mã văn bản, cách nhau bởi dấ ";"',
  `distribution_form` varchar(45) COMMENT 'Hình thức cấp',
  `effective_period` int COMMENT 'Thời gian hiệu lực kể từ ngày ký văn bản phê duyệt',
  `effective_period_unit` varchar(20) COMMENT 'DAY | MONTH | YEAR',
  `proposal_approval_position` varchar(100) COMMENT 'Cấp phê duyệt đề xuất',
  `proposal_approval_user` varchar(50) COMMENT 'Cán bộ tiếp nhận phê duyệt đề xuất',
  `loan_approval_position` varchar(100) COMMENT 'Cấp phê duyệt khoản vay',
  `loan_approval_user` varchar(50) COMMENT 'Cán bộ tiếp nhận phê duyệt khoản'
);

CREATE TABLE `customer` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `ref_cus_id` varchar(36) COMMENT 'Mã KH của hồ sơ đẩy từ Kênh hồ sơ về BPM',
  `bpm_cif` varchar(45) COMMENT 'Số CIF tại BPM',
  `cif` varchar(45) COMMENT 'Số CIF tại Core',
  `customer_type` varchar(6) COMMENT 'Loại khách hàng RB | EB',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `individual_customer` (
  `customer_id` bigint PRIMARY KEY,
  `first_name` varchar(50) COMMENT 'Tên',
  `last_name` varchar(50) COMMENT 'Họ',
  `gender` varchar(6) COMMENT 'Giới tính',
  `date_of_birth` date COMMENT 'Ngày sinh',
  `age` int COMMENT 'Tuổi',
  `martial_status` varchar(10) COMMENT 'Tình trạng hôn nhân',
  `partner_code` varchar(45) COMMENT 'Mã đối tác',
  `nation` varchar(4) COMMENT 'Quốc tịch',
  `subject` varchar(10) COMMENT 'Đối tượng khách hàng',
  `msb_member` boolean COMMENT 'Là đối tượng CBNV MSB/Tập đoàn',
  `employee_code` varchar(20) COMMENT 'Mã nhân viên',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `enterprise_customer` (
  `customer_id` bigint PRIMARY KEY,
  `income_chose` boolean COMMENT 'Chọn làm thông tin nguồn thu',
  `business_type` varchar(20) COMMENT 'Loại hình kinh doanh',
  `business_registration_number` varchar(45) COMMENT 'Mã số ĐKKD',
  `company_name` varchar(255) COMMENT 'Tên cơ sở kinh doanh',
  `first_registration_at` date COMMENT 'Ngày cấp ĐKKD đầu tiên',
  `deputy` varchar(100) COMMENT 'Người đại diện',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `customer_relation_ship` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `customer_id` bigint,
  `customer_ref_id` bigint,
  `relationship` varchar(10) COMMENT 'Mối quan hệ',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `customer_contact` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `customer_id` bigint,
  `contact_type` varchar(10) COMMENT 'Loại contact',
  `value` varchar(50) COMMENT 'Giá trị',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `customer_identity` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `customer_id` bigint,
  `document_type` varchar(10) COMMENT 'Loại giấy tờ',
  `identifier_code` varchar(16) COMMENT 'Mã số định danh',
  `issued_at` date COMMENT 'Ngày cấp',
  `issued_by` varchar(10) COMMENT 'Cấp bởi',
  `issued_place` varchar(10) COMMENT 'Nơi cấp',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `customer_address` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `customer_id` bigint,
  `address_type` varchar(10) COMMENT 'Loại địa chỉ/Thông tin địa chỉ',
  `same_permanent_residence` boolean COMMENT 'Giống HKTT',
  `city_code` varchar(10) COMMENT 'Tỉnh/ Thành phố',
  `district_code` varchar(10) COMMENT 'Quận/ Huyện',
  `ward_code` varchar(10) COMMENT 'Phường/ Xã',
  `address_line` varchar(255) COMMENT 'Số nhà/ Tên đường',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `aml_opr` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `customer_id` bigint,
  `query_type` varchar(6) COMMENT 'Loại truy vấn AML | OPR',
  `result_code` varchar(20) COMMENT 'Mã kết quả',
  `result_description` varchar(255) COMMENT 'Nội dung kết quả',
  `reason_in_list` varchar(500) COMMENT 'Lý do nằm trong danh sách',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_income` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `income_recognition_method` varchar(20) COMMENT 'Phương pháp ghi nhận thu nhập EXCHANGE | ACTUALLY',
  `recognized_income` decimal(14,0) COMMENT 'Thu nhập ghi nhận (tháng)',
  `currency` varchar(4) DEFAULT "VND" COMMENT 'Đơn vị tiền tệ',
  `customer_id` bigint,
  `income_owner` varchar(10) COMMENT 'Chủ nguồn thu',
  `income_owner_name` varchar(100) COMMENT 'Tên chủ nguồn thu',
  `conversion_method` varchar(10) COMMENT 'Phương pháp quy đổi',
  `explanation` text COMMENT 'Diễn giải',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `salary_income` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `tax_code` varchar(20) COMMENT 'Mã số thuế cá nhân',
  `social_insurance_code` varchar(45) COMMENT 'Mã số bảo hiểm xã hội',
  `rank_type` varchar(10) COMMENT 'Cấp bậc',
  `kpi_rating` varchar(10) COMMENT 'Xếp loại KPI',
  `pay_type` varchar(10) COMMENT 'Hình thức trả lương',
  `labor_type` varchar(10) COMMENT 'Loại hình HĐLĐ',
  `business_registration_number` varchar(45) COMMENT 'Mã số ĐKKD ĐVCT',
  `group_of_working` varchar(30) COMMENT 'Phân nhóm ĐVCT',
  `company_name` varchar(255) COMMENT 'Tên Cơ quan công tác',
  `position` varchar(50) COMMENT 'Chức vụ',
  `start_working_day` date COMMENT 'Ngày vào làm việc',
  `city_code` varchar(10) COMMENT 'Tỉnh/ Thành phố',
  `district_code` varchar(10) COMMENT 'Quận/ Huyện',
  `ward_code` varchar(10) COMMENT 'Phường/ Xã',
  `address_line` varchar(255) COMMENT 'Số nhà/ Tên đường',
  `explanation` text COMMENT 'Diễn giải (nếu có)',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `rental_income` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `asset_type` varchar(10) COMMENT 'Loại tài sản',
  `asset_owner` varchar(100) COMMENT 'Chủ sở hữu',
  `asset_address` varchar(255) COMMENT 'Địa chỉ tài sản/Loại xe',
  `renter` varchar(100) COMMENT 'Bên Thuê',
  `renter_phone` varchar(20) COMMENT 'SĐT bên thuê',
  `rental_purpose` varchar(10) COMMENT 'Mục đích thuê',
  `rental_price` decimal(14,0) COMMENT 'Giá thuê',
  `explanation` text COMMENT 'Diễn giải (nếu có)',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `individual_enterprise_income` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `business_registration_number` varchar(45) COMMENT 'Mã số ĐKKD',
  `company_name` varchar(255) COMMENT 'Tên cơ sở kinh doanh/Tên công ty',
  `main_business_sector` varchar(255) COMMENT 'Ngành nghề kinh doanh chính',
  `capital_contribution_rate` double COMMENT 'Tỷ lệ góp vốn của KH',
  `business_place_ownership` varchar(10) COMMENT 'Hình thức sở hữu địa điểm kinh doanh',
  `city_code` varchar(10) COMMENT 'Địa chỉ kinh doanh thực tế - Tỉnh/TP',
  `district_code` varchar(10) COMMENT 'Địa chỉ kinh doanh thực tế - Quận/huyện/thị trấn',
  `ward_code` varchar(10) COMMENT 'Địa chỉ kinh doanh thực tế - Phường/xã/thôn',
  `address_line` varchar(255) COMMENT 'Địa chỉ kinh doanh thực tế - Đường',
  `production_process` varchar(1000) COMMENT 'Quy trình sản xuất kinh doanh',
  `record_method` varchar(1000) COMMENT 'Phuowng thức ghi chép',
  `input` varchar(1000) COMMENT 'Nguồn hàng đầu vào ',
  `output` varchar(1000) COMMENT 'Đầu ra',
  `business_scale` varchar(1000) COMMENT 'Quy mô kinh doanh',
  `inventory` varchar(1000) COMMENT 'Hàng tồn kho, phải thu, phải trả',
  `evaluation_period` varchar(10) COMMENT 'Kỳ đánh giá kết quả',
  `income_monthly` decimal(14,0) COMMENT 'Doanh thu/ tháng',
  `expense_monthly` decimal(14,0) COMMENT 'Chi phí/ tháng',
  `profit_monthly` decimal(14,0) COMMENT 'Lợi nhuận/ tháng',
  `profit_margin` decimal(14,0) COMMENT 'Biên lợi nhuận',
  `evaluate_result` varchar(1000) COMMENT 'Đánh giá kết quả kinh doanh',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `other_income` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `income_future` boolean COMMENT 'Nguồn thu hình thành trong tương lai',
  `income_info` varchar(255) COMMENT 'Thông tin nguồn thu',
  `income_detail` varchar(10) COMMENT 'Chi tiết nguồn thu khác',
  `explanation` text COMMENT 'Diễn giải',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `cic` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `customer_id` bigint,
  `deft_group_current` varchar(10) COMMENT 'Nhóm nợ 12 tháng gần nhất',
  `deft_group_last_12` varchar(10) COMMENT 'Nhóm nợ 12 tháng gần nhất',
  `deft_group_last_24` varchar(10) COMMENT 'Nhóm nợ 24 tháng gần nhất',
  `total_loan_collateral` decimal(14,0) COMMENT 'Tổng dư nợ có TSĐB (trđ)',
  `total_unsecure_loan` decimal(14,0) COMMENT 'Tổng dư nợ không có TSĐB (trđ)',
  `total_credit_card_limit` decimal(14,0) COMMENT 'Tổng HM TTD (trđ)',
  `status_code` varchar(10) COMMENT 'Trạng thái tra cứu',
  `status_description` varchar(255) COMMENT 'Mô tả trạng thái',
  `explanation` text COMMENT 'Ghi chú nếu có',
  `query_at` datetime COMMENT 'Ngày trả kết quả',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_limit_credit` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `loan_limit` varchar(45) COMMENT 'Số tiền vay/Hạn mức',
  `loan_product_collateral` decimal(14,0) COMMENT 'Khoản cấp tín dụng có TSBĐ - SP vay mua sản phẩm tại dự án hợp tác chiến lược',
  `other_loan_product_collateral` decimal(14,0) COMMENT 'Khoản cấp tín dụng có TSBĐ - SP khác SP vay mua sản phẩm tại dự án hợp tác chiến lược',
  `unsecure_product` decimal(14,0) COMMENT 'Khoản cấp tín dụng không TSBĐ - SP vay mua sản phẩm tại dự án hợp tác chiến lược',
  `other_unsecure_product` decimal(14,0) COMMENT 'Khoản cấp tín dụng không TSBĐ - SP khác SP vay mua sản phẩm tại dự án hợp tác chiến lược',
  `total` decimal(14,0) COMMENT 'Tổng cộng',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_field_information` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `place_type` varchar(10) COMMENT 'Loại địa điểm',
  `relationship` varchar(10) COMMENT 'Mối quan hệ ',
  `city_code` varchar(10) COMMENT 'Tỉnh TP',
  `district_code` varchar(10) COMMENT 'Quận Huyện',
  `ward_code` varchar(10) COMMENT 'Phường/xã',
  `address_line` varchar(255) COMMENT 'Đường Phố',
  `time_at` datetime COMMENT 'Thời gian',
  `instructor` varchar(100) COMMENT 'Người hướng dẫn',
  `executor` varchar(100) COMMENT 'Người Thực hiện XMTĐ',
  `result` varchar(10) COMMENT 'Kết quả',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_credit` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `credit_type` varchar(255) COMMENT 'Khoản cấp tín dụng: LOAN_UNSECURE | OVERDRAFT_UNSECURE | CARD_UNSECURE',
  `document_code` varchar(10) COMMENT 'Mã văn bản',
  `approve_result` text COMMENT 'Ý kiến phê duyệt: APPROVED | REJECTED',
  `loan_amount` decimal(14,0) COMMENT 'Số tiền vay/Hạn mức cấp/Hạn mức thẻ',
  `currency` varchar(4) DEFAULT "VND" COMMENT 'Đơn vị tiền tệ',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `credit_loan_mapping` (
  `application_credit_id` bigint,
  `application_credit_loan_id` bigint,
  PRIMARY KEY (`application_credit_id`, `application_credit_loan_id`)
);

CREATE TABLE `application_credit_loan` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `product_code` varchar(10) COMMENT 'Mã sản phẩm',
  `product_name` varchar(255) COMMENT 'Tên sản phẩm',
  `payback` boolean COMMENT 'Hoàn vốn',
  `loan_purpose` varchar(10) COMMENT 'Mục đích vay',
  `loan_purpose_explanation` text COMMENT 'Diễn giải mục đích ',
  `loan_period` int COMMENT 'Thời gian vay',
  `kunn_period` int COMMENT 'Thời gian KUNN',
  `original_period` int COMMENT 'Thời gian ân hạn gốc',
  `total_capital` decimal(14,0) COMMENT 'Tổng nhu cầu vốn',
  `equity_capital` decimal(14,0) COMMENT 'Vốn tự có',
  `ltd` int,
  `credit_form` varchar(10) COMMENT 'Hình thức cấp tín dụng',
  `disburse_frequency` varchar(10) COMMENT 'Tần suất giải ngân',
  `debt_pay_method` varchar(10) COMMENT 'Phương thức trả nợ',
  `disburse_method` varchar(10) COMMENT 'Phương thức giải ngân',
  `disburse_method_explanation` text COMMENT 'Diễn giải chi tiết phương thức giải ngân (nếu có)',
  `principal_pay_period` int COMMENT 'Kỳ trả nợ gốc',
  `principal_pay_unit` varchar(10) COMMENT 'Đơn vị kỳ trả nợ gốc',
  `interest_pay_period` int COMMENT 'Kỳ trả lãi',
  `interest_pay_unit` varchar(10) COMMENT 'Đơn vị kỳ trả lãi',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `credit_overdraft_mapping` (
  `application_credit_id` bigint,
  `application_credit_overdraft_id` bigint,
  PRIMARY KEY (`application_credit_id`, `application_credit_overdraft_id`)
);

CREATE TABLE `application_credit_overdraft` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `interest_rate_code` varchar(10) COMMENT 'Mã lãi suất',
  `product_name` varchar(255) COMMENT 'Tên sản phẩm',
  `loan_purpose` varchar(10) COMMENT 'Mục đích vay',
  `limit_sustentive_period` int COMMENT 'Thời gian duy trì hạn mức',
  `debt_pay_method` varchar(10) COMMENT 'Phương thức trả nợ',
  `info_additional` text COMMENT 'Thông tin bổ sung',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `credit_card_mapping` (
  `application_credit_id` bigint,
  `application_credit_card_id` bigint,
  PRIMARY KEY (`application_credit_id`, `application_credit_card_id`)
);

CREATE TABLE `application_credit_card` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `card_policy_code` varchar(10) COMMENT 'Mã chính sách thẻ',
  `product_name` varchar(255) COMMENT 'Tên sản phẩm',
  `card_type` varchar(10) COMMENT 'Loại thẻ',
  `way4_card_code` varchar(10) COMMENT 'Mã Way4 thẻ',
  `card_name` varchar(255) COMMENT 'Tên in trên thẻ',
  `secret_question` varchar(255) COMMENT 'Câu hỏi bảo mật',
  `limit_sustentive_period` int COMMENT 'Thời gian duy trì hạn mức ',
  `email` varchar(50),
  `auto_deduct_rate` varchar(10) COMMENT 'Tỷ lệ trích nợ tự động',
  `deduct_account_number` varchar(30) COMMENT 'STK trích nợ',
  `card_form` varchar(10) COMMENT 'Hình thức thẻ',
  `card_receive_addr` varchar(10) COMMENT 'Địa chỉ nhận thẻ',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `sub_credit_card` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_credit_card_id` bigint,
  `card_owner_name` varchar(100) COMMENT 'Tên chủ thẻ',
  `email` varchar(50),
  `phone_number` varchar(20) COMMENT 'Số điện thoại',
  `card_limit_amount` decimal(14,0) COMMENT 'Hạn mức thẻ phụ',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_credit_ratings` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `rating_system` varchar(10) COMMENT 'Hệ thống xếp hạng',
  `rating_id` varchar(30) COMMENT 'ID xếp hạng',
  `rating_result` varchar(20) COMMENT 'Kết quả xếp hạng',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_credit_ratings_dtl` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_credit_ratings_id` bigint,
  `identity_card` varchar(20) COMMENT 'CMND',
  `score` double COMMENT 'Điểm',
  `rank` varchar(255) COMMENT 'Hạng',
  `executor` varchar(50) COMMENT 'Cán bộ xử lý',
  `role` varchar(20) COMMENT 'Role xử lý',
  `status` varchar(10) COMMENT 'Trạng thái',
  `created_at` datetime COMMENT 'Thời điểm chấm điểm',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_appraisal_content` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `content_type` varchar(255) COMMENT 'SPECIAL_RISK | OTHER_ADDITIONAL',
  `criteria_group` varchar(10) COMMENT 'Nhóm tiêu chí',
  `detail` varchar(500) COMMENT 'Chi tiết',
  `regulation` varchar(45) COMMENT 'Quy định',
  `management_measures` varchar(500) COMMENT 'Cơ sở đề xuất và biện pháp quản lý',
  `authorization` varchar(45) COMMENT 'Thẩm quyền',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `md_credit_conditions` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `code` varchar(50) COMMENT 'Mã điều kiện tín dụng',
  `segment` varchar(10) COMMENT 'Phân khúc khách hàng',
  `group` varchar(10) COMMENT 'Nhóm điều kiện',
  `detail` text COMMENT 'Điều kiện chi tiết',
  `credit_type` varchar(10) COMMENT 'Khoản cấp TD',
  `time_of_control` varchar(10) COMMENT 'Thời điểm kiểm soát',
  `applicable_subject` varchar(10) COMMENT 'Đối tượng áp dụng',
  `control_unit` varchar(10) COMMENT 'Đơn vị kiểm soát: ĐVKD | TNTD | KSTT',
  `time_control_disburse` varchar(100) COMMENT 'Thời điểm kiểm soát kể từ thời điểm giải ngân',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_credit_conditions` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `segment` varchar(10),
  `group` varchar(10) COMMENT 'Nhóm điều kiện',
  `code` varchar(10) COMMENT 'Mã điều kiện',
  `detail` text COMMENT 'Điều kiện chi tiết',
  `time_of_control` varchar(10) COMMENT 'Thời điểm kiểm soát',
  `applicable_subject` varchar(10) COMMENT 'Đối tượng áp dụng',
  `control_unit` varchar(10) COMMENT 'Đơn vị kiểm soát',
  `time_control_disburse` varchar(100) COMMENT 'Thời điểm kiểm soát kể từ thời điểm giải ngân',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `cms_docs` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `bpm_application_id` bigint,
  `cms_application_id` bigint,
  `cms_code` varchar(20) COMMENT 'Mã checklist tại CMS',
  `name` varchar(255) COMMENT 'Tên checklist tại CMS',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `cms_doc_dtl` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `cms_docs_id` bigint,
  `file_name` varchar(255) COMMENT 'Tên file tại CMS',
  `file_size` int COMMENT 'Dung lượng file',
  `file_extension` int COMMENT 'Loại file',
  `file_path` text COMMENT 'URL file',
  `status` int COMMENT 'Trạng thái download: 0 - chưa download, 1 - đã download',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_extra_data` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `category` varchar(20) COMMENT 'Tab hồ sơ',
  `key` varchar(20) COMMENT 'mã khối thông tin',
  `value` blob COMMENT 'Data',
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `salary_income_mapping` (
  `application_income_id` bigint,
  `salary_income_id` bigint
);

CREATE TABLE `rental_income_mapping` (
  `application_income_id` bigint,
  `rental_income_id` bigint
);

CREATE TABLE `individual_enterprise_income_mapping` (
  `application_income_id` bigint,
  `individual_enterprise_income_id` bigint
);

CREATE TABLE `other_income_mapping` (
  `application_income_id` bigint,
  `other_income_id` bigint
);

CREATE TABLE `application_history_approval` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `executed_at` datetime,
  `full_name` varchar(100),
  `username` varchar(50),
  `user_role` varchar(50),
  `reason` text,
  `step_code` varchar(10),
  `step_description` varchar(255),
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_history_feedback` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `user_role` varchar(50),
  `username` varchar(50),
  `feedback_at` datetime,
  `feedback_content` text,
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_phone_expertise` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `application_id` bigint,
  `customer_id` bigint,
  `called_at` date,
  `note` text,
  `ext` varchar(20),
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `application_repayment` (
  `application_id` bigint PRIMARY KEY,
  `total_repay` decimal(14,0),
  `dti` decimal(14,0),
  `dsr` decimal(14,0),
  `mue` decimal(14,0),
  `evaluate` text,
  `created_by` varchar(50) COMMENT 'User khởi tạo',
  `created_at` datetime COMMENT 'Thời gian khởi tạo',
  `updated_by` varchar(50) COMMENT 'User cập nhật',
  `updated_at` datetime COMMENT 'Thời gian cập nhật'
);

CREATE TABLE `md_cms_bpm_checklist_mapping` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `cms_checklist_code` varchar(20),
  `cms_checklist_name` varchar(255),
  `cms_doc_code` varchar(20),
  `cms_doc_name` varchar(255),
  `bpm_doc_code` varchar(20),
  `bpm_doc_name` varchar(255),
  `bpm_area_id` bigint,
  `source` varchar(20) COMMENT 'Nguồn'
);

ALTER TABLE `md_credit_conditions` COMMENT = 'Master data điều kiện tín dụng';

ALTER TABLE `application` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `individual_customer` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `enterprise_customer` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customer_relation_ship` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customer_contact` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customer_identity` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `customer_address` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`);

ALTER TABLE `aml_opr` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_income` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `cic` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_limit_credit` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_field_information` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_credit` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `credit_loan_mapping` ADD FOREIGN KEY (`application_credit_id`) REFERENCES `application_credit` (`id`);

ALTER TABLE `credit_loan_mapping` ADD FOREIGN KEY (`application_credit_loan_id`) REFERENCES `application_credit_loan` (`id`);

ALTER TABLE `credit_overdraft_mapping` ADD FOREIGN KEY (`application_credit_id`) REFERENCES `application_credit` (`id`);

ALTER TABLE `credit_overdraft_mapping` ADD FOREIGN KEY (`application_credit_overdraft_id`) REFERENCES `application_credit_overdraft` (`id`);

ALTER TABLE `credit_card_mapping` ADD FOREIGN KEY (`application_credit_id`) REFERENCES `application_credit` (`id`);

ALTER TABLE `credit_card_mapping` ADD FOREIGN KEY (`application_credit_card_id`) REFERENCES `application_credit_card` (`id`);

ALTER TABLE `sub_credit_card` ADD FOREIGN KEY (`application_credit_card_id`) REFERENCES `application_credit_card` (`id`);

ALTER TABLE `application_credit_ratings` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_credit_ratings_dtl` ADD FOREIGN KEY (`application_credit_ratings_id`) REFERENCES `application_credit_ratings` (`id`);

ALTER TABLE `application_appraisal_content` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_credit_conditions` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `cms_doc_dtl` ADD FOREIGN KEY (`cms_docs_id`) REFERENCES `cms_docs` (`id`);

ALTER TABLE `application_extra_data` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `salary_income_mapping` ADD FOREIGN KEY (`application_income_id`) REFERENCES `application_income` (`id`);

ALTER TABLE `salary_income_mapping` ADD FOREIGN KEY (`salary_income_id`) REFERENCES `salary_income` (`id`);

ALTER TABLE `rental_income_mapping` ADD FOREIGN KEY (`application_income_id`) REFERENCES `application_income` (`id`);

ALTER TABLE `rental_income_mapping` ADD FOREIGN KEY (`rental_income_id`) REFERENCES `rental_income` (`id`);

ALTER TABLE `individual_enterprise_income_mapping` ADD FOREIGN KEY (`application_income_id`) REFERENCES `application_income` (`id`);

ALTER TABLE `individual_enterprise_income_mapping` ADD FOREIGN KEY (`individual_enterprise_income_id`) REFERENCES `individual_enterprise_income` (`id`);

ALTER TABLE `other_income_mapping` ADD FOREIGN KEY (`application_income_id`) REFERENCES `application_income` (`id`);

ALTER TABLE `other_income_mapping` ADD FOREIGN KEY (`other_income_id`) REFERENCES `other_income` (`id`);

ALTER TABLE `application_history_approval` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_history_feedback` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_phone_expertise` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

ALTER TABLE `application_repayment` ADD FOREIGN KEY (`application_id`) REFERENCES `application` (`id`);

