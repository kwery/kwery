create table dash_repo_user (
  id integer generated by default as identity,
  password varchar(255) not null,
  username varchar(255) not null,
  primary key (id)
);

create table datasource (
  id integer generated by default as identity,
  label varchar(255) not null,
  password varchar(255),
  port integer not null check (port>=1),
  type integer,
  url varchar(255) not null,
  username varchar(255) not null,
  primary key (id)
);

create table query_run (
  id integer generated by default as identity,
  cron_expression varchar(255) not null,
  label varchar(255) not null,
  query varchar(255) not null,
  datasource_id_fk integer not null,
  primary key (id)
);

create table query_run_execution (
  id integer generated by default as identity,
  executionEnd bigint,
  executionId varchar(255),
  executionStart bigint,
  result varchar(255),
  status integer,
  query_run_id_fk integer,
  primary key (id)
);

alter table dash_repo_user add constraint UK_oi2a6rr9bbna339uej0kywhfy  unique (username);

alter table datasource add constraint UK_2kxqjqmgkpln474giultd222s  unique (label);

alter table query_run add constraint UK_o8o3g7ixjl43k37fvshp3hu8q  unique (label);

alter table query_run add constraint FK_ktt4mf7e34jmw0f0ogm78ouq0 foreign key (datasource_id_fk) references datasource;

alter table query_run_execution add constraint FK_91fufdgwmkr99ayu6eegqqx95 foreign key (query_run_id_fk) references query_run;