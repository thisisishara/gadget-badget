-- phpMyAdmin SQL Dump
-- version 4.7.9
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Apr 06, 2021 at 03:52 AM
-- Server version: 5.7.21
-- PHP Version: 5.6.35

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gadgetbadget_users`
--

-- --------------------------------------------------------

--
-- Table structure for table `consumer`
--

DROP TABLE IF EXISTS `consumer`;
CREATE TABLE IF NOT EXISTS `consumer` (
  `consumer_id` varchar(10) NOT NULL,
  PRIMARY KEY (`consumer_id`),
  UNIQUE KEY `consumer_id` (`consumer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
CREATE TABLE IF NOT EXISTS `employee` (
  `employee_id` varchar(10) NOT NULL,
  `gb_employee_id` varchar(10) NOT NULL,
  `department` varchar(20) NOT NULL,
  `date_hired` datetime DEFAULT '1000-01-01 00:00:00',
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `employee_id` (`employee_id`),
  UNIQUE KEY `gb_employee_id` (`gb_employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `funder`
--

DROP TABLE IF EXISTS `funder`;
CREATE TABLE IF NOT EXISTS `funder` (
  `funder_id` varchar(10) NOT NULL,
  `organization` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`funder_id`),
  UNIQUE KEY `funder_id` (`funder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `paymentinfo`
--

DROP TABLE IF EXISTS `paymentinfo`;
CREATE TABLE IF NOT EXISTS `paymentinfo` (
  `user_id` varchar(10) NOT NULL,
  `creditcard_type` varchar(10) NOT NULL,
  `creditcard_no` char(20) NOT NULL,
  `creditcard_security_no` char(10) NOT NULL,
  `exp_date` datetime NOT NULL,
  `billing_address` varchar(400) NOT NULL,
  `date_last_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`creditcard_no`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Triggers `paymentinfo`
--
DROP TRIGGER IF EXISTS `tg_paymentinfo_update`;
DELIMITER $$
CREATE TRIGGER `tg_paymentinfo_update` BEFORE UPDATE ON `paymentinfo` FOR EACH ROW BEGIN
    SET NEW.date_last_updated = CURRENT_TIMESTAMP();
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `researcher`
--

DROP TABLE IF EXISTS `researcher`;
CREATE TABLE IF NOT EXISTS `researcher` (
  `researcher_id` varchar(10) NOT NULL,
  `institution` varchar(100) DEFAULT NULL,
  `field_of_study` varchar(100) NOT NULL,
  `years_of_exp` int(11) NOT NULL,
  PRIMARY KEY (`researcher_id`),
  UNIQUE KEY `researcher_id` (`researcher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `role_id` char(5) NOT NULL,
  `role_description` varchar(400) DEFAULT NULL,
  `role_last_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`role_id`, `role_description`, `role_last_updated`) VALUES
('ACCNT', 'ACCOUNTANT@GADGETBADGET', '2021-04-05 23:58:12'),
('ADMIN', 'ADMIN@GADGETBADGET', '2021-04-05 23:58:12'),
('CNSMR', 'CONSUMER_GADGETBADGET', '2021-04-05 23:58:12'),
('EMPLY', 'EMPLOYEE@GADGETBADGET', '2021-04-05 23:58:12'),
('FUNDR', 'FUNDER_GADGETBADGET', '2021-04-05 23:58:12'),
('GUEST', 'GUEST_GADGETBADGET', '2021-04-05 23:58:12'),
('RSCHR', 'RESEARCHER_GADGETBADGET', '2021-04-05 23:58:12'),
('TESTR', 'TESTER@GADGETBADGET', '2021-04-06 03:10:50');

--
-- Triggers `role`
--
DROP TRIGGER IF EXISTS `tg_role_insert`;
DELIMITER $$
CREATE TRIGGER `tg_role_insert` BEFORE INSERT ON `role` FOR EACH ROW BEGIN
	SET NEW.role_last_updated = CURRENT_TIMESTAMP();
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_role_update`;
DELIMITER $$
CREATE TRIGGER `tg_role_update` BEFORE UPDATE ON `role` FOR EACH ROW BEGIN
    SET NEW.role_last_updated = CURRENT_TIMESTAMP();
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` varchar(10) NOT NULL,
  `username` varchar(40) NOT NULL,
  `password` varchar(40) NOT NULL,
  `role_id` varchar(10) NOT NULL,
  `first_name` varchar(40) NOT NULL,
  `last_name` varchar(40) NOT NULL,
  `gender` char(1) DEFAULT NULL,
  `date_joined` datetime NOT NULL,
  `primary_email` varchar(40) DEFAULT NULL,
  `primary_phone` char(10) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `password` (`password`),
  UNIQUE KEY `primary_email` (`primary_email`),
  UNIQUE KEY `primary_phone` (`primary_phone`),
  KEY `user_role_fk` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `username`, `password`, `role_id`, `first_name`, `last_name`, `gender`, `date_joined`, `primary_email`, `primary_phone`) VALUES
('AD21000001', 'thisisishara', '12345', 'ADMIN', 'Ishara', 'Dissanayake', 'M', '2021-04-06 08:06:19', 'me@me.lk', '0710710712');

--
-- Triggers `user`
--
DROP TRIGGER IF EXISTS `tg_user_insert`;
DELIMITER $$
CREATE TRIGGER `tg_user_insert` BEFORE INSERT ON `user` FOR EACH ROW BEGIN
	DECLARE pref CHAR(2);
    IF NEW.role_id = 'ADMIN' THEN
    	SET pref = 'AD';
    ELSEIF NEW.role_id = 'RSCHR' THEN
    	SET pref = 'RS';
    ELSEIF NEW.role_id = 'CNSMR' THEN
    	SET pref = 'CN';
    ELSEIF NEW.role_id = 'FUNDR' THEN
    	SET pref = 'FN';
    ELSEIF NEW.role_id = 'FNMGR' THEN
    	SET pref = 'FM';
    ELSEIF NEW.role_id = 'EMPLY' THEN
    	SET pref = 'EM';
    ELSE
    	SET pref = NULL;
    END IF;
    
    SET NEW.date_joined = CURRENT_TIMESTAMP();
    
	IF (pref = NULL) THEN
    	SIGNAL SQLSTATE '45000';
    ELSE
        INSERT INTO user_seq (prefix) VALUES (pref);
        SET NEW.user_id = CONCAT(CONCAT(pref,RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `user_seq`
--

DROP TABLE IF EXISTS `user_seq`;
CREATE TABLE IF NOT EXISTS `user_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_seq`
--

INSERT INTO `user_seq` (`id`, `prefix`) VALUES
(1, 'AD');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `consumer`
--
ALTER TABLE `consumer`
  ADD CONSTRAINT `con_user_fk` FOREIGN KEY (`consumer_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
  ADD CONSTRAINT `emp_user_fk` FOREIGN KEY (`employee_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `funder`
--
ALTER TABLE `funder`
  ADD CONSTRAINT `fun_user_fk` FOREIGN KEY (`funder_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `paymentinfo`
--
ALTER TABLE `paymentinfo`
  ADD CONSTRAINT `pay_user_pk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `researcher`
--
ALTER TABLE `researcher`
  ADD CONSTRAINT `res_user_fk` FOREIGN KEY (`researcher_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_role_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
