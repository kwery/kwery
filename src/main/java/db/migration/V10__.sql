create table kwery_version (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  version varchar(50) not null,
  primary key (id)
);

alter table sql_query_execution add column result_file_name varchar(36);

rename column sql_query_execution.result to execution_error;
