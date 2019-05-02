/*!40101 SET NAMES utf8mb4 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `company` */

drop table if exists `company`;

create table `company` (
  `id` int(10) unsigned not null auto_increment,
  `name` varchar(250) not null,
  `logo` blob not null,
  `db_url` varchar(50) not null,
  `db_user` varchar(100) not null,
  `db_password` varchar(100) not null,
  `loading_query` text not null,
  `created_date` datetime not null,
  primary key (`id`),
  unique key `uk_company_name` (`name`)
) engine=innodb default charset=utf8mb4;

/*Table structure for table `file` */

drop table if exists `file`;

create table `file` (
  `id` int(10) unsigned not null auto_increment,
  `company_id` int(10) unsigned not null,
  `name` varchar(500) not null,
  `path` varchar(500) not null,
  `carrier` varchar(500) not null,
  `scac` varchar(10) not null,
  `effective_date` date not null,
  `expiration_date` date not null,
  `surcharge_type` varchar(50) not null,
  `surcharge_currency` varchar(50) not null,
  `rate_basis` varchar(50) not null,
  `loaded_date` datetime not null,
  primary key (`id`),
  key `fk_file_company` (`company_id`),
  constraint `fk_file_company` foreign key (`company_id`) references `company` (`id`) on delete cascade on update cascade
) engine=innodb default charset=utf8mb4;

/*Table structure for table `role` */

drop table if exists `role`;

create table `role` (
  `id` int(10) unsigned not null auto_increment,
  `name` varchar(100) not null,
  `created_date` datetime not null,
  primary key (`id`),
  unique key `uk_role_name` (`name`)
) engine=innodb default charset=utf8mb4;

/*Table structure for table `site` */

drop table if exists `site`;

create table `site` (
  `id` int(10) unsigned not null auto_increment,
  `company_id` int(10) unsigned not null,
  `name` varchar(100) not null,
  `db_url` varchar(50) not null,
  `db_user` varchar(100) not null,
  `db_password` varchar(100) not null,
  primary key (`id`),
  key `fk_site_company` (`company_id`),
  constraint `fk_site_company` foreign key (`company_id`) references `company` (`id`) on delete cascade on update cascade
) engine=innodb default charset=utf8mb4;

/*Table structure for table `site_file` */

drop table if exists `site_file`;

create table `site_file` (
  `id` int(10) unsigned not null auto_increment,
  `site_id` int(10) unsigned not null,
  `file_id` int(10) unsigned not null,
  primary key (`id`),
  key `fk_site_file_site` (`site_id`),
  key `fk_site_file_file` (`file_id`),
  constraint `fk_site_file_file` foreign key (`file_id`) references `file` (`id`) on delete cascade on update cascade,
  constraint `fk_site_file_site` foreign key (`site_id`) references `site` (`id`) on delete cascade on update cascade
) engine=innodb default charset=utf8mb4;

/*Table structure for table `user` */

drop table if exists `user`;

create table `user` (
  `id` int(10) unsigned not null auto_increment,
  `company_id` int(10) unsigned not null,
  `role_id` int(10) unsigned not null,
  `username` varchar(50) not null,
  `password` varchar(60) not null,
  `email` varchar(50) not null,
  `first_name` varchar(50) not null,
  `last_name` varchar(50) not null,
  `active` tinyint(1) not null default '0',
  `created_date` datetime not null,
  primary key (`id`),
  unique key `uk_user_email` (`email`),
  unique key `uk_user_company_login_name` (`company_id`,`username`),
  key `fk_user_role` (`role_id`),
  key `fk_user_company` (`company_id`),
  constraint `fk_user_company` foreign key (`company_id`) references `company` (`id`) on delete cascade on update cascade,
  constraint `fk_user_role` foreign key (`role_id`) references `role` (`id`) on update cascade
) engine=innodb default charset=utf8mb4;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
