create table dhtml_cache (
  id                        integer not null auto_increment,
  url                       TEXT,
  html                      MEDIUMTEXT,
  ua                        MEDIUMTEXT,
  last_update               datetime not null,
  constraint pk_dhtml_cache primary key (id))
;

create table dhtml_cache_ua (
  id                        integer,
  url                       varchar(255),
  html                      varchar(255),
  ua                        varchar(255),
  url_ua                    varchar(255),
  last_update               datetime)
;

create table dstore (
  id                        integer not null auto_increment,
  store_id                  integer,
  models_id                 integer,
  unit_price                float,
  unit_price_text           varchar(255),
  number                    integer,
  created_date              datetime,
  constraint pk_dstore primary key (id))
;

create table dstore_img (
  id                        integer not null auto_increment,
  store_id                  integer,
  img_url                   varchar(255),
  created_date              datetime not null,
  constraint pk_dstore_img primary key (id))
;

create table irand (
  id                        integer not null auto_increment,
  台番号                       varchar(255),
  回数                        varchar(255),
  時間                        varchar(255),
  スタート                      varchar(255),
  出メダル                      varchar(255),
  ステータス                     varchar(255),
  constraint pk_irand primary key (id))
;

create table mmodels_group (
  id                        integer not null auto_increment,
  group_id                  integer,
  models_id                 integer,
  constraint pk_mmodels_group primary key (id))
;

create table mmodels (
  id                        integer not null auto_increment,
  name                      varchar(255),
  probability               varchar(255),
  output_sum                varchar(255),
  output                    varchar(255),
  round                     varchar(255),
  short_num                 varchar(255),
  st                        varchar(255),
  remarks                   TEXT,
  link                      TEXT,
  published_at              integer,
  last_update               datetime not null,
  constraint pk_mmodels primary key (id))
;

create table mstore (
  id                        integer not null auto_increment,
  name                      TEXT,
  m_address_id              integer,
  address                   TEXT,
  hours                     TEXT,
  link                      TEXT,
  sp_link                   TEXT,
  tel                       varchar(255),
  type                      tinyint(1) default 0,
  updated_at                datetime,
  constraint pk_mstore primary key (id))
;

create table note (
  id                        integer not null auto_increment,
  title                     varchar(255),
  text                      longtext,
  version                   float,
  constraint pk_note primary key (id))
;

create table tnumber (
  id                        integer not null auto_increment,
  url                       varchar(255),
  num                       varchar(255),
  constraint pk_tnumber primary key (id))
;

alter table dstore add constraint fk_dstore_models_1 foreign key (models_id) references mmodels (id) on delete restrict on update restrict;
create index ix_dstore_models_1 on dstore (models_id);


