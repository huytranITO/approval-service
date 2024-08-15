--liquibase formatted sql
--changeset ManhNV8:v1.46__alter_table_application_phone_expertise_increase_max_size_columns_20230614 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_phone_expertise' AND COLUMN_NAME = 'person_answer';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_phone_expertise' AND COLUMN_NAME = 'person_answer_value';
ALTER TABLE application_phone_expertise MODIFY COLUMN person_answer VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL;
ALTER TABLE application_phone_expertise CHANGE person_answer person_answer VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL AFTER created_by;
ALTER TABLE application_phone_expertise MODIFY COLUMN person_answer_value VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL;
ALTER TABLE application_phone_expertise CHANGE person_answer_value person_answer_value VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL AFTER person_answer;
ALTER TABLE application_phone_expertise MODIFY COLUMN id bigint auto_increment NOT NULL COMMENT 'PK';
ALTER TABLE application_phone_expertise MODIFY COLUMN application_id bigint NULL COMMENT 'FK, ID hồ sơ -- ref : application.id';
ALTER TABLE application_phone_expertise MODIFY COLUMN phone_number varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Số điện thoại';
ALTER TABLE application_phone_expertise MODIFY COLUMN called_at date NULL COMMENT 'Thời gian gọi';
ALTER TABLE application_phone_expertise MODIFY COLUMN note text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Ghi chú';
ALTER TABLE application_phone_expertise MODIFY COLUMN ext varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Máy lẻ CBTĐ';

--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'give_back_role';
ALTER TABLE application MODIFY COLUMN give_back_role varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Role trả hồ sơ về RM';

--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_approval' AND COLUMN_NAME = 'proposal_approval_full_name';
ALTER TABLE application_history_approval ADD proposal_approval_full_name varchar(100) NULL;
ALTER TABLE application_history_approval CHANGE proposal_approval_full_name proposal_approval_full_name varchar(100) NULL AFTER proposal_approval_user;
