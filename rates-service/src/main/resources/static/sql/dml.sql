/*Data for the table `company` */

insert  into `company`(`id`,`name`,`db_url`,`db_user`,`db_password`,`container_sizes`,`cost_types`,`created_date`) values (1,'TRANSBORDER USA','jdbc:mysql://35.196.165.140:3306/transborder_usa','logiware','j6qP97t1G68l36Y','20 STD,40 STD,40 HC,40 NOR','Per BL,Per Container','2019-04-23 16:27:56'),(2,'AURORA CHILE','jdbc:mysql://35.196.165.140:3306/transborder_chile','logiware','j6qP97t1G68l36Y','20 STD,40 STD,40 HC,40 NOR','Per BL,Per Container','2019-05-23 19:24:08');

-- 10th Oct 2020 by Lakshmi
INSERT INTO `logiware_rates`.`company` (`name`, `db_url`, `db_user`, `db_password`, `created_date`, `catapult_user`, `catapult_password`) VALUES ('LOGIWARE', 'jdbc:mysql://localhost:3306/craft_hkg', 'root', 'L0g!w@r3', '2020-10-06 19:29:53', 'SOAP_TEST_USER', 'Change1234'); 