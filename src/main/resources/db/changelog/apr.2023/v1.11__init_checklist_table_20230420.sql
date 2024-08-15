--liquibase formatted sql
--changeset DungDA5:v1.11__init_checklist_table_20230420-001 dbms:mysql
CREATE TABLE areas (
                       id BIGINT auto_increment primary key NOT NULL,
                       code varchar(50) NULL,
                       name varchar(100) NULL,
                       order_display INT NULL,
                       parent_code varchar(50) NULL,
                       parent_name varchar(100) NULL,
                       parent_order_display INT NULL,
                       request_code varchar(100) NULL,
                       is_deleted INT DEFAULT 0 NULL,
                       created_by varchar(50) NULL,
                       created_at DATETIME NULL,
                       updated_by varchar(50) NULL,
                       updated_at DATETIME NULL
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

--liquibase formatted sql
--changeset DungDA5:v1.11__init_checklist_table_20230420-002 dbms:mysql
CREATE TABLE checklist_dtl (
                               id BIGINT auto_increment primary key  NOT NULL,
                               areas_id BIGINT NULL,
                               code varchar(50) NULL,
                               name varchar(100) NULL,
                               props BLOB NULL,
                               return_code varchar(100) NULL,
                               return_date DATE NULL,
                               is_required INT NULL,
                               is_error INT NULL,
                               is_generated INT NULL,
                               order_display INT NULL,
                               file_type varchar(255) NULL,
                               max_file_size INT NULL,
                               is_deleted INT DEFAULT 0 NULL,
                               created_by varchar(50) NULL,
                               created_at DATETIME NULL,
                               updated_by varchar(50) NULL,
                               updated_at DATETIME NULL
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

--liquibase formatted sql
--changeset DungDA5:v1.11__init_checklist_table_20230420-003 dbms:mysql
CREATE TABLE saved_file (
                            id BIGINT auto_increment primary key NOT NULL,
                            checklist_dtl_id BIGINT NULL,
                            file_id varchar(100) NULL,
                            file_name varchar(100) NULL,
                            file_path varchar(255) NULL,
                            file_type varchar(50) NULL,
                            is_uploaded INT DEFAULT 0 NULL,
                            is_deleted INT DEFAULT 0 NULL,
                            created_by varchar(50) NULL,
                            created_at DATETIME NULL,
                            updated_by varchar(50) NULL,
                            updated_at DATETIME NULL
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;