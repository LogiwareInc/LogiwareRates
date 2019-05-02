/*!40101 SET NAMES utf8mb4 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Data for the table `company` */

insert  into `company`(`id`,`name`,`logo`,`db_url`,`db_user`,`db_password`,`loading_query`,`created_date`) values (1,'Transborder USA','','jdbc:mysql://35.200.181.132:3306/auroraimport','root','Password123','load data local infile \'${fileName}\' into table `csvrates` fields terminated by \',\' enclosed by \'\\\"\' lines terminated by \'\\r\\n\' ignore 1 lines (`quote_id`, `rate_id`,`contract_id`, `amendment_id`, @rate_effective_date, @rate_expiration_date, `origin_trade`, `origin_city`, `origin_country`, `origin_code`, `origin_via_city`, `destination_trade`, `destination_city`, `destination_country`, `destination_code`, `destination_via_city`, `carrier`, `price`, `service`, @contract_expire, `currency`, `currency_rate`, `shipment_size`, `shipment_type`, `commodity_brief`, @commodity_code, `commodity_exclusions`, `commodity_full`, `hazardous_charge`, `transit`, `contract_notes`, `customer`, `scac`, `surcharge_line_rate_id`, @surcharge_code, `surcharge_desc`, `surcharge_price`, `surcharge_type`, `surcharge_currency`, @surcharge_expire_dt, @surcharge_effective_dt, `rate_basis`, @ship_date) set `rate_effective_date` = str_to_date(@rate_effective_date, \'%d-%M-%Y\'), `rate_expiration_date` = str_to_date(@rate_expiration_date, \'%d-%M-%Y\'), `contract_expire` = str_to_date(@contract_expire, \'%d-%M-%Y\'), `surcharge_expire_dt` = str_to_date(@surcharge_expire_dt, \'%d-%M-%Y\'), `surcharge_effective_dt` = str_to_date(@surcharge_effective_dt, \'%d-%M-%Y\'), `ship_date` = str_to_date(@ship_date, \'%d-%M-%Y\'), `commodity_code` = substring_index(@commodity_code, \'.\', -1), `surcharge_code` = if(@surcharge_code = \'BR\', \'OFR\', if(locate(\'GRI\', @surcharge_code) = 1, \'GRI\', @surcharge_code))','2019-04-23 16:27:56');

/*Data for the table `file` */

/*Data for the table `role` */

insert  into `role`(`id`,`name`,`created_date`) values (1,'Admin','2019-04-23 16:30:18');

/*Data for the table `site` */

insert  into `site`(`id`,`company_id`,`name`,`db_url`,`db_user`,`db_password`) values (1,1,'Aurora','jdbc:mysql://35.200.181.132:3306/auroraimport','root','Password123');

/*Data for the table `site_file` */

/*Data for the table `user` */

insert  into `user`(`id`,`company_id`,`role_id`,`username`,`password`,`email`,`first_name`,`last_name`,`active`,`created_date`) values (1,1,1,'lakshh','$2a$12$bdBIGZIih7/FTiHWK1KJb.X8T4DB18LaoelJ9xrEouDyrLg/YamYy','lakshminarayanan.v@logiwareinc.com','Lakshmi','Narayanan',1,'2019-04-23 16:31:03');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
