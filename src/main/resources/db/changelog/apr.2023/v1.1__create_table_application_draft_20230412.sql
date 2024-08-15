--liquibase formatted sql
--changeset ManhNV8:v1.1__create_table_application_draft_20230412 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'application_draft'
CREATE TABLE `application_draft` (
                                     `id` bigint PRIMARY KEY AUTO_INCREMENT,
                                     `bpm_id` varchar(36),
                                     `tab_code` varchar(45) COMMENT 'common_info | customer_relations | income | field_information | credit | document_list',
                                     `data` blob,
                                     `status` int
);