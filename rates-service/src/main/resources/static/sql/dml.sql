/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Data for the table `company` */

insert  into `company`(`id`,`name`,`db_url`,`db_user`,`db_password`,`container_sizes`,`cost_types`,`created_date`) values (1,'TRANSBORDER USA','jdbc:mysql://35.196.165.140:3306/transborder_usa','logiware','j6qP97t1G68l36Y','20 STD,40 STD,40 HC,40 NOR','Per BL,Per Container','2019-04-23 16:27:56'),(2,'AURORA CHILE','jdbc:mysql://35.196.165.140:3306/transborder_chile','logiware','j6qP97t1G68l36Y','20 STD,40 STD,40 HC,40 NOR','Per BL,Per Container','2019-05-23 19:24:08');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
