/*Table structure for table `company` */

drop table if exists `company`;

create table `company` (
  `id` int(10) not null auto_increment,
  `name` varchar(250) not null,
  `db_url` varchar(500) not null,
  `db_user` varchar(100) not null,
  `db_password` varchar(100) not null,
  `container_sizes` text not null,
  `cost_types` text not null,
  `created_date` datetime not null,
  primary key (`id`),
  unique key `uk_company_name` (`name`(191))
) engine=innodb default charset=utf8mb4;

/*Table structure for table `error` */

drop table if exists `error`;

create table `error` (
  `id` int(10) not null auto_increment,
  `history_id` int(10) not null,
  `type` enum('Charge Code','Container Size') default null,
  `error` varchar(500) default null,
  primary key (`id`),
  key `error_fk1` (`history_id`),
  constraint `error_fk1` foreign key (`history_id`) references `history` (`id`) on delete cascade on update cascade
) engine=innodb default charset=utf8mb4;

/*Table structure for table `file` */

drop table if exists `file`;

create table `file` (
  `id` int(10) not null auto_increment,
  `company_id` int(10) not null,
  `name` varchar(500) not null,
  `path` varchar(500) not null,
  `shipment_type` enum('A','F','L') not null default 'A',
  `rates_type` enum('F','O') not null default 'F',
  `loaded_date` datetime not null,
  primary key (`id`),
  key `file_fk1` (`company_id`),
  constraint `file_fk1` foreign key (`company_id`) references `company` (`id`) on delete cascade on update cascade
) engine=innodb default charset=utf8mb4;

/*Table structure for table `history` */

drop table if exists `history`;

create table `history` (
  `id` int(10) not null auto_increment,
  `file_id` int(10) not null,
  `company_id` int(10) not null,
  primary key (`id`),
  key `history_fk1` (`company_id`),
  key `history_fk2` (`file_id`),
  constraint `history_fk1` foreign key (`company_id`) references `company` (`id`) on delete cascade on update cascade,
  constraint `history_fk2` foreign key (`file_id`) references `file` (`id`) on delete cascade on update cascade
) engine=innodb default charset=utf8mb4;

/*Table structure for table `partner` */

drop table if exists `partner`;

create table `partner` (
  `id` int(10) not null auto_increment,
  `company_id` int(10) not null,
  `partner_id` int(10) not null,
  primary key (`id`),
  unique key `partner_uk1` (`company_id`,`partner_id`),
  key `partner_fk2` (`partner_id`),
  constraint `partner_fk1` foreign key (`company_id`) references `company` (`id`) on delete cascade on update cascade,
  constraint `partner_fk2` foreign key (`partner_id`) references `company` (`id`) on delete cascade on update cascade
) engine=innodb default charset=utf8mb4;

-- 10th Oct 2020 by Lakshmi
ALTER TABLE `company`
  ADD COLUMN `catapult_user` VARCHAR (100) NULL AFTER `created_date`,
  ADD COLUMN `catapult_password` VARCHAR (100) NULL AFTER `catapult_user`;


