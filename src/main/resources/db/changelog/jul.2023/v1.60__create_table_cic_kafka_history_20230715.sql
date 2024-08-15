--liquibase formatted sql
--changeset TuanTV10:v1.60__create_table_cic_kafka_history_20230715 dbms:mysql
--preconditions onFail:HALT onError:HALT
    create table cic_kafka_history (
       id bigint not null auto_increment,
        created_at datetime,
        created_by varchar(255),
        updated_at datetime,
        updated_by varchar(255),
        message longtext COMMENT 'message được đẩy lên hoặc nhận về từ kafka',
        request_id varchar(255) COMMENT 'request id tương ứng với mỗi cặp request - response',
        topic varchar(255) COMMENT 'Topic nhận hoặc đẩy message',
        primary key (id)
    );