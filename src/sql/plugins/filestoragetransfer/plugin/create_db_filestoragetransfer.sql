-- liquibase formatted sql
-- changeset filestoragetransfer:create_db_filestoragetransfer.sql
-- preconditions onFail:MARK_RAN onError:WARN

--
-- Structure for table filestoragetransfer_request
--

DROP TABLE IF EXISTS filestoragetransfer_request;
-- a tab, not a space, follows request_context: the HSQL translation of the test build rewrites 'text ' into 'VARCHAR '
CREATE TABLE filestoragetransfer_request (
id_request int AUTO_INCREMENT,
old_file_key varchar(255) default '' NOT NULL,
source_fileserviceprovider_name varchar(255) default '' NOT NULL,
new_file_key varchar(255) default NULL,
target_fileserviceprovider_name varchar(255) default '' NOT NULL,
request_status VARCHAR(10) default 'TODO' NOT NULL,
retry_count int default 0 NOT NULL,
execution_time TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
creation_time TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
request_context	varchar(255) default NULL,
contact_mail varchar(255) default NULL,
PRIMARY KEY (id_request)
);

--
-- Structure for table filestoragetransfer_error
--

DROP TABLE IF EXISTS filestoragetransfer_error;
CREATE TABLE filestoragetransfer_error (
id_error int AUTO_INCREMENT,
id_request int default '0' NOT NULL,
code int default '0' NOT NULL,
error_message long varchar NOT NULL,
error_trace long varchar NOT NULL,
execution_time TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
PRIMARY KEY (id_error)
);

ALTER TABLE filestoragetransfer_error ADD CONSTRAINT fk_filestoragetransfer_error_id_request FOREIGN KEY (id_request) REFERENCES filestoragetransfer_request(id_request);
