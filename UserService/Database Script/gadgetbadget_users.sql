-- phpMyAdmin SQL Dump
-- version 4.7.9
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Apr 19, 2021 at 04:48 AM
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

DELIMITER $$
--
-- Procedures
--
DROP PROCEDURE IF EXISTS `sp_delete_consumer`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_delete_consumer` (IN `uid` VARCHAR(10), OUT `result` INTEGER)  BEGIN
    DECLARE deleteVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    DELETE FROM consumer WHERE `consumer_id`=uid;
    SELECT ROW_COUNT() INTO deleteVal;

    IF (deleteVal < 1) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Could not delete the consumer. Specified consumer id was not found.';
    ELSE
        DELETE FROM paymentmethod WHERE `user_id`=uid;
        DELETE FROM user WHERE `user_id`=uid;
		SELECT ROW_COUNT() INTO deleteVal;

        SET result = deleteVal;
    END IF;    

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_delete_employee`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_delete_employee` (IN `uid` VARCHAR(10), OUT `result` INTEGER)  BEGIN
    DECLARE deleteVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    DELETE FROM employee WHERE `employee_id`=uid;
    SELECT ROW_COUNT() INTO deleteVal;

    IF (deleteVal < 1) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Could not delete the employee. Specified employee id was not found.';
    ELSE
        DELETE FROM paymentmethod WHERE `user_id`=uid;
        DELETE FROM user WHERE `user_id`=uid;
		SELECT ROW_COUNT() INTO deleteVal;

        SET result = deleteVal;
    END IF;    

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_delete_funder`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_delete_funder` (IN `uid` VARCHAR(10), OUT `result` INTEGER)  BEGIN
    DECLARE deleteVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    DELETE FROM funder WHERE `funder_id`=uid;
    SELECT ROW_COUNT() INTO deleteVal;

    IF (deleteVal < 1) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Could not delete the funder. Specified funder id was not found.';
    ELSE
        DELETE FROM paymentmethod WHERE `user_id`=uid;
        DELETE FROM user WHERE `user_id`=uid;
		SELECT ROW_COUNT() INTO deleteVal;

        SET result = deleteVal;
    END IF;    

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_delete_researcher`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_delete_researcher` (IN `uid` VARCHAR(10), OUT `result` INTEGER)  BEGIN
    DECLARE deleteVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    DELETE FROM researcher WHERE `researcher_id`=uid;
    SELECT ROW_COUNT() INTO deleteVal;

    IF (deleteVal < 1) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Could not delete the researcher. Specified researcher id was not found.';
    ELSE
        DELETE FROM paymentmethod WHERE `user_id`=uid;
        DELETE FROM user WHERE `user_id`=uid;
		SELECT ROW_COUNT() INTO deleteVal;

        SET result = deleteVal;
    END IF;    

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_insert_consumer`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_consumer` (IN `username` VARCHAR(40), `password` VARCHAR(40), `role_id` VARCHAR(10), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), OUT `result` INTEGER)  BEGIN
    DECLARE insertVal INTEGER DEFAULT 0;
    DECLARE pref CHAR(2);
    DECLARE date_joined DATETIME;
    DECLARE user_id VARCHAR(10) DEFAULT NULL;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;

	IF role_id = 'CNSMR' THEN
    	SET pref = 'CN';
    ELSE
    	SET pref = NULL;
    END IF;
    
    SET date_joined = CURRENT_TIMESTAMP();
    
	IF (pref = NULL) THEN
    	SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Invalid role detected.';
    ELSE
        INSERT INTO user_seq (prefix) VALUES (pref);
        SET user_id = CONCAT(CONCAT(pref,RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
    END IF;
    
    IF user_id = NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to generate the user_id for consumer.';
    ELSE
        INSERT INTO user VALUES (user_id, username, password, role_id, first_name, last_name, gender, CURRENT_TIMESTAMP(), primary_email, primary_phone, "No");
        SELECT ROW_COUNT() INTO insertVal;

        IF (insertVal < 1) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to insert the consumer.';
        ELSE
            INSERT INTO consumer VALUES(user_id);
			SELECT ROW_COUNT() INTO insertVal;
            
            IF (insertVal < 1) THEN
                DELETE FROM user WHERE `user_id` = user_id;
            END IF;
            SET result = insertVal;
        END IF;
    END IF; 

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
    
END$$

DROP PROCEDURE IF EXISTS `sp_insert_employee`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_employee` (IN `username` VARCHAR(40), `password` VARCHAR(40), `role_id` VARCHAR(10), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), `gb_employee_id` VARCHAR(10), `department` VARCHAR(20), `date_hired` DATETIME, OUT `result` INTEGER)  BEGIN
    DECLARE insertVal INTEGER DEFAULT 0;
    DECLARE pref CHAR(2);
    DECLARE date_joined DATETIME;
    DECLARE user_id VARCHAR(10) DEFAULT NULL;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
	IF role_id = 'ADMIN' THEN
    	SET pref = 'AD';
    ELSEIF role_id = 'FNMGR' THEN
    	SET pref = 'FM';
    ELSEIF role_id = 'EMPLY' THEN
    	SET pref = 'EM';
    ELSE
    	SET pref = NULL;
    END IF;
    
    SET date_joined = CURRENT_TIMESTAMP();
    
	IF (pref = NULL) THEN
    	SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Invalid role detected.';
    ELSE
        INSERT INTO user_seq (prefix) VALUES (pref);
        SET user_id = CONCAT(CONCAT(pref,RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
    END IF;
    
    IF user_id = NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to generate the user_id for consumer.';
    ELSE
        INSERT INTO user VALUES (user_id, username, password, role_id, first_name, last_name, gender, CURRENT_TIMESTAMP(), primary_email, primary_phone, "No");
        SELECT ROW_COUNT() INTO insertVal;

        IF (insertVal < 1) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to insert the employee.';
        ELSE
            INSERT INTO employee VALUES(user_id, gb_employee_id, department, date_hired);
			SELECT ROW_COUNT() INTO insertVal;
            
            IF (insertVal < 1) THEN
                DELETE FROM user WHERE `user_id` = user_id;
            END IF;
            SET result = insertVal;
        END IF;
    END IF;

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
    
END$$

DROP PROCEDURE IF EXISTS `sp_insert_funder`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_funder` (IN `username` VARCHAR(40), `password` VARCHAR(40), `role_id` VARCHAR(10), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), `organization` VARCHAR(100), OUT `result` INTEGER)  BEGIN
    DECLARE insertVal INTEGER DEFAULT 0;
    DECLARE pref CHAR(2);
    DECLARE date_joined DATETIME;
    DECLARE user_id VARCHAR(10) DEFAULT NULL;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;

	IF role_id = 'FUNDR' THEN
    	SET pref = 'FN';
    ELSE
    	SET pref = NULL;
    END IF;
    
    SET date_joined = CURRENT_TIMESTAMP();
    
	IF (pref = NULL) THEN
    	SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Invalid role detected.';
    ELSE
        INSERT INTO user_seq (prefix) VALUES (pref);
        SET user_id = CONCAT(CONCAT(pref,RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
    END IF;
    
    IF user_id = NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to generate the user_id for funder.';
    ELSE
        INSERT INTO user VALUES (user_id, username, password, role_id, first_name, last_name, gender, CURRENT_TIMESTAMP(), primary_email, primary_phone, "No");
        SELECT ROW_COUNT() INTO insertVal;

        IF (insertVal < 1) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to insert the funder.';
        ELSE
            INSERT INTO funder VALUES(user_id, organization);
			SELECT ROW_COUNT() INTO insertVal;
            
            IF (insertVal < 1) THEN
                DELETE FROM user WHERE `user_id` = user_id;
            END IF;
            SET result = insertVal;
        END IF;
    END IF; 

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
    
END$$

DROP PROCEDURE IF EXISTS `sp_insert_researcher`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_insert_researcher` (IN `username` VARCHAR(40), `password` VARCHAR(40), `role_id` VARCHAR(10), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), `institution` VARCHAR(100), `field_of_study` VARCHAR(100), `years_of_exp` INTEGER(11), OUT `result` INTEGER)  BEGIN
    DECLARE insertVal INTEGER DEFAULT 0;
    DECLARE pref CHAR(2);
    DECLARE date_joined DATETIME;
    DECLARE user_id VARCHAR(10) DEFAULT NULL;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;

	IF role_id = 'RSCHR' THEN
    	SET pref = 'RS';
    ELSE
    	SET pref = NULL;
    END IF;
    
    SET date_joined = CURRENT_TIMESTAMP();
    
	IF (pref = NULL) THEN
    	SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Invalid role detected.';
    ELSE
        INSERT INTO user_seq (prefix) VALUES (pref);
        SET user_id = CONCAT(CONCAT(pref,RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
    END IF;
    
    IF user_id = NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to generate the user_id for researcher.';
    ELSE
        INSERT INTO user VALUES (user_id, username, password, role_id, first_name, last_name, gender, CURRENT_TIMESTAMP(), primary_email, primary_phone, "No");
        SELECT ROW_COUNT() INTO insertVal;

        IF (insertVal < 1) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Failed to insert the researcher.';
        ELSE
            INSERT INTO researcher VALUES(user_id, institution, field_of_study, years_of_exp);
			SELECT ROW_COUNT() INTO insertVal;
            
            IF (insertVal < 1) THEN
                DELETE FROM user WHERE `user_id` = user_id;
            END IF;
            SET result = insertVal;
        END IF;
    END IF; 

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
    
END$$

DROP PROCEDURE IF EXISTS `sp_update_consumer`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_consumer` (IN `uid` VARCHAR(10), `username` VARCHAR(40), `password` VARCHAR(40), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), OUT `result` INTEGER)  BEGIN
    DECLARE updateVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    UPDATE user SET `username`=username, `password`=password, `first_name`=first_name, `last_name`=last_name, `gender`=gender, `primary_email`=primary_email, `primary_phone`=primary_phone WHERE `user_id`=uid;
    SELECT ROW_COUNT() INTO updateVal;

    SET result = updateVal;    

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_update_employee`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_employee` (IN `uid` VARCHAR(10), `username` VARCHAR(40), `password` VARCHAR(40), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), `gb_employee_id` VARCHAR(10), `department` VARCHAR(20), `date_hired` DATETIME, OUT `result` INTEGER)  BEGIN
    DECLARE updateVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    UPDATE user SET `username`=username, `password`=password, `first_name`=first_name, `last_name`=last_name, `gender`=gender, `primary_email`=primary_email, `primary_phone`=primary_phone WHERE `user_id`=uid;
    SELECT ROW_COUNT() INTO updateVal;

    IF (updateVal < 1) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Could not update the employee. Specified employee id was not found.';
    ELSE
        UPDATE employee SET `gb_employee_id`=gb_employee_id, `department`=department, `date_hired`=date_hired WHERE `employee_id`=uid;
		SELECT ROW_COUNT() INTO updateVal;

        SET result = updateVal;
    END IF; 

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;   
END$$

DROP PROCEDURE IF EXISTS `sp_update_funder`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_funder` (IN `uid` VARCHAR(10), `username` VARCHAR(40), `password` VARCHAR(40), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), `organization` VARCHAR(100), OUT `result` INTEGER)  BEGIN
    DECLARE updateVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    UPDATE user SET `username`=username, `password`=password, `first_name`=first_name, `last_name`=last_name, `gender`=gender, `primary_email`=primary_email, `primary_phone`=primary_phone WHERE `user_id`=uid;
    SELECT ROW_COUNT() INTO updateVal;

    IF (updateVal < 1) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Could not update the funder. Specified funder id was not found.';
    ELSE
        UPDATE funder SET `organization`=organization WHERE `funder_id`=uid;
		SELECT ROW_COUNT() INTO updateVal;

        SET result = updateVal;
    END IF;    

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$

DROP PROCEDURE IF EXISTS `sp_update_researcher`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_researcher` (IN `uid` VARCHAR(10), `username` VARCHAR(40), `password` VARCHAR(40), `first_name` VARCHAR(40), `last_name` VARCHAR(40), `gender` CHAR(1), `primary_email` VARCHAR(40), `primary_phone` CHAR(10), `institution` VARCHAR(100), `field_of_study` VARCHAR(100), `years_of_exp` INTEGER, OUT `result` INTEGER)  BEGIN
    DECLARE updateVal INTEGER DEFAULT 0;
    DECLARE `_rollback` BOOL DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET `_rollback` = 1;
    START TRANSACTION;
    
    UPDATE user SET `username`=username, `password`=password, `first_name`=first_name, `last_name`=last_name, `gender`=gender, `primary_email`=primary_email, `primary_phone`=primary_phone WHERE `user_id`=uid;
    SELECT ROW_COUNT() INTO updateVal;

    IF (updateVal < 1) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'MYSQL-EXCEPTION: Could not update the researcher. Specified researcher id was not found.';
    ELSE
        UPDATE researcher SET `institution`=institution, `field_of_study`=field_of_study, `years_of_exp`=years_of_exp WHERE `researcher_id`=uid;
		SELECT ROW_COUNT() INTO updateVal;

        SET result = updateVal;
    END IF;    

    IF `_rollback` THEN
        ROLLBACK;
    ELSE
        COMMIT;
    END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `consumer`
--

DROP TABLE IF EXISTS `consumer`;
CREATE TABLE IF NOT EXISTS `consumer` (
  `consumer_id` varchar(10) NOT NULL,
  PRIMARY KEY (`consumer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `consumer`
--

INSERT INTO `consumer` (`consumer_id`) VALUES
('CN21000016'),
('CN21000017');

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

--
-- Dumping data for table `employee`
--

INSERT INTO `employee` (`employee_id`, `gb_employee_id`, `department`, `date_hired`) VALUES
('AD21000001', 'GB21000002', 'IT', '2015-02-21 00:00:00'),
('AD21000009', 'GB21000003', 'IT', '2016-02-04 10:00:00'),
('AD21000028', 'GB21000004', 'IT', '2020-02-21 00:00:10'),
('EM21000018', 'GB21000005', 'Marketing', '2020-02-04 08:00:00'),
('FM21000027', 'GB21000006', 'Finance', '2010-02-21 10:00:00');

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

--
-- Dumping data for table `funder`
--

INSERT INTO `funder` (`funder_id`, `organization`) VALUES
('FN21000031', 'None'),
('FN21000032', 'Funders Inc.'),
('FN21000033', 'GlobalInvestors LLC');

-- --------------------------------------------------------

--
-- Table structure for table `jwt_config`
--

DROP TABLE IF EXISTS `jwt_config`;
CREATE TABLE IF NOT EXISTS `jwt_config` (
  `jwt_kid` varchar(100) NOT NULL,
  `jwt_public` varbinary(2048) NOT NULL,
  `jwt_private` varbinary(2048) NOT NULL,
  `jwt_algo` varchar(100) NOT NULL DEFAULT 'RSA_USING_SHA256',
  `jwt_lifetime` int(2) NOT NULL DEFAULT '20',
  `jwt_issuer` varchar(100) NOT NULL DEFAULT 'gadgetbadget.user.security.JWTHandler',
  `jwt_audience` varchar(100) NOT NULL DEFAULT 'gadgetbadget.webservices.auth',
  `jwt_date_last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`jwt_kid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `jwt_config`
--

INSERT INTO `jwt_config` (`jwt_kid`, `jwt_public`, `jwt_private`, `jwt_algo`, `jwt_lifetime`, `jwt_issuer`, `jwt_audience`, `jwt_date_last_updated`) VALUES
('JWK1', 0x30820122300d06092a864886f70d01010105000382010f003082010a0282010100ca368b0a80b1793658c3d75106f0c94b671132f039f9953c2a6c55293453f24412024cdacb8291ce368c49f0ff18b2b41de3fd1a089358b7e98357a180b70385c18a65472a5b5d0bba2715f2c3aa6e29bff797175094b0255e9b8ce9c60bc8f17fe21228fb1d9b90767b2ebeb45e5a889293a1e4bd0eb4bc15a7702d0991d05bc7449a3de28ade8134bcb7858b8140f3461684eb5582316e86aa26aeeda9338354a9c4043c970724b8fd326485afc1d637aae09c1db282ec8de056b581418f4b1c1974e2a4a4308dce0539ded0c2d0012f1007532d3a61b1181b70c2342837c7e9a28d0a8787b0249dd9546f01485a63fb8c0719b7b533546df8a487a41b87fd0203010001, 0x308204be020100300d06092a864886f70d0101010500048204a8308204a40201000282010100ca368b0a80b1793658c3d75106f0c94b671132f039f9953c2a6c55293453f24412024cdacb8291ce368c49f0ff18b2b41de3fd1a089358b7e98357a180b70385c18a65472a5b5d0bba2715f2c3aa6e29bff797175094b0255e9b8ce9c60bc8f17fe21228fb1d9b90767b2ebeb45e5a889293a1e4bd0eb4bc15a7702d0991d05bc7449a3de28ade8134bcb7858b8140f3461684eb5582316e86aa26aeeda9338354a9c4043c970724b8fd326485afc1d637aae09c1db282ec8de056b581418f4b1c1974e2a4a4308dce0539ded0c2d0012f1007532d3a61b1181b70c2342837c7e9a28d0a8787b0249dd9546f01485a63fb8c0719b7b533546df8a487a41b87fd02030100010282010100be834ed2315783bcd93a81f4fc1605807df56d49794594fd9f767b719bb46f6f048c983e1738c80841ca40abc69b4d5a7742e2a611684950d4b782eb4d2aa02df79f5d36044919cbbfb1ad731037d51c8e994507994accfe1839733498fb6771682be532290cc710ff1fa575d4d7847261aea7fbaac75d2e4b347a72642eb5dee42dad69f10c4a66accc8517f4af1b3b92c707f147b9f27f876cf7f2cdcdbdc589a0fbe55105d0afd11630f9ec8f20d785b5ce4c49926ab69089ac24ef04ad770b6ec208af255c224ffff4ac272d16db9126fd39b6ca1ea27d43951bdbaa5dba7be19d93db6de1dab971bac43058491772f3e5a84330164ec1e8ef96d49bea0102818100f693cfc9fd4e4c1aa19177f97340d6be6fdd5e6276ea8da3a115bfbca21749a390cb23cf5057062527ac3fa621f7684a072329308e31d1c41b44471c25ab597e1a62284eeb44523de2b40465031018a306ca4af14c6657be10f8a01757e8c4a9a0f0930bbefc4a028fbc0d2c8d78a06dea25c0c81295a7bd2754e92e0d57107d02818100d1f0bab2fd18ab480f11d98a05c9f1ead882668b75418762c17939166abaccc4751454fed996835e8c0e06a828ffb805916c3b67fed6b7cbc3dc050ea43b5f2040913c737ab19c88219add8db05274dca4522a0d3931c8c3164d54e7ac01d983c81fedc657bb0b6a07a943018b833bc5a659ae34eadcf1d581fce75c92c36d810281804c20f2c4804a8e40a2a5910f1940698cbe68f05d222de4b12268de9bd4c7afdaaa37adc4b21f4c2c68854bd9751f37b9b35e6db72a0fc39df2753027469212bd5653fbf1f1bd544efb116d51ea922ba919cd9739ccc6c44c505d12c06249e17e25cc60f9fd6b53465b2e4a3af92ac70d687f6377e2150686e5ffd7467aa3e8d902818100aee4606423bc9d53d65a04639b16f4d5b3b04e44f755b3b76d7dd334fd8cc8711127f2f6abc55b833421ab2203a5a463df15cf177f90d86483b192f4f394125707f2f5ed2dd53095a7891ff09d66d3bbcb983737f4e1a861fcfe4731087632023a817ecfb0de3d500339da7c3b0104964f527e71cf0668e078fd7ab000039601028180524d36c72442a52fad2a93641e41aab62757af5ef94cbcb5c7bcafee3e64440cb09efae057c6aaf3113276123d03808a49216c28ea3c6b41b408b17447e0dfa252bb14fb134d9928678cdf1eed91597aba4d90926b1ea269fb8ab1b2b38466f36a688df070a680974bc360e2e3d74d0e6639d59a9e713bc946bb43f16dd88c66, 'RSA_USING_SHA256', 50000, 'gadgetbadget.user.security.JWTHandler', 'gadgetbadget.webservices.auth', '2021-04-18 11:41:09'),
('JWK2', 0x30820122300d06092a864886f70d01010105000382010f003082010a0282010100b7425f198334312cce13119037b34403634d2208d3abd2a7258bc3520bdc8439b56eed40846e0db750b515cf3523e5fc21b8a2b3dcf777815680c4f6b44480bd74b1257eccc1b48bbaa8aa2593028647aacbc5d681e2a09f2d9869f7aa43704d3011ff22faef8cf5514dee9225d46bfaaaac4c3e7cec9f11d5e56d2f8ba58e8e2b33542d02d819d498d5112ebb5ea10337ab361bbc4dca2aa3be7f7fb75867f36a95e5357596f2f638cfe4a6cf78bb66abecea72549b3b23710421245fe184cbf22315bb57a79be49b0afd09325c486ae327309a9f03dae8b1504b23da0acf41dc418bee46bd8ce9c0dd60bff3416fe260edf713cee0c20448f1549deca5437f0203010001, 0x308204be020100300d06092a864886f70d0101010500048204a8308204a40201000282010100b7425f198334312cce13119037b34403634d2208d3abd2a7258bc3520bdc8439b56eed40846e0db750b515cf3523e5fc21b8a2b3dcf777815680c4f6b44480bd74b1257eccc1b48bbaa8aa2593028647aacbc5d681e2a09f2d9869f7aa43704d3011ff22faef8cf5514dee9225d46bfaaaac4c3e7cec9f11d5e56d2f8ba58e8e2b33542d02d819d498d5112ebb5ea10337ab361bbc4dca2aa3be7f7fb75867f36a95e5357596f2f638cfe4a6cf78bb66abecea72549b3b23710421245fe184cbf22315bb57a79be49b0afd09325c486ae327309a9f03dae8b1504b23da0acf41dc418bee46bd8ce9c0dd60bff3416fe260edf713cee0c20448f1549deca5437f02030100010282010100b4b9197bd96e0108c478fd9b11b311e19d6e15a04ace69c1383faa71210d68c058727a3a63defc5bc995ab5a5a777a78b8f092537a17f99c6d2834156f151738bef96b96ae6a6098638dadadbc5a82fdee2b6280f639fe58bbe850a8531a8a87345eab135e101b1c59ffd6c3fdd68c5df92e4d4a5a7c272ab99bb59f6bc1eae011d3b6e62bfa72bf0aebd6e88bd3ff9c226cf3515f74ced75ff2724cf795903884b7699a12deea50e0d234b2a3f25d9ad7f6379e67af6579d15a55a4f26b5aaeb8e85506f7a7e03c0938949e5858d23a4c53091f5a6d867a5d28c102a90d34163d1f831f2c8240c1c8e1c993d60848f65e2225ab289dae748e62ae87aa554fa102818100de6143301ce1cc0a2734ace6d75a213ab5b00c7565d2a1750091cef1da650fc6e2eecaa0d488488a957cccb52109d0a6e3127aaa6836755a28bd2ba242b811fe36ea72d68f81cccf17972a1dcc75f0e9c32fcb826961dcae33974ffe0190880783080b4d0bf57d2de9bfec2f336e3402275e1f793ef139db295b03100dd85c2702818100d2f70711e9740a0d384891a40964391e4986463b2a6b095a7bc9792c74548e68628bd9cfec6e6e268baa46f946aaacaeeb9a5b6b3d127f732498873067edc06e62307e236715734cc58f29d61379779da65db5bf414449b1ade65d98d96bc0aebdf02ee74f2ac7d75ee876903242193cfcc223cef30d790d6ab0e0a1e2c5fce9028180554d33cb99d8973ef1c907e5c8879f25791a1dbd4ea09c24586295e239e6f8454f394fea9f7be36f9d65f0d42de728ed4b3f0464a772f452f03b982836b58ad95bd154d9aed4986e7bdb1561b6d32ae55064de089949dbafcac468ffc333e0aa18fe15efa8fdb2d5d0cb38dae63c88a0a6df38ed76526be2009c13b1adcde7930281801aa0b373bf53b63114f993e8708705ee9cb9260431c670d7cae81333593b92fdf9f24cfbc18beebb4ec59f4fb76bb380209ccb0d2e18379e00f07f9fcc7e65db88e93602a1f0432d5d82447590dfb409620651fa61f28c9ab0a87307e7e981d88c80d46abdc358960694b0e262759559ed4d53d7bb35e8219965d9f494fb088102818100be36fc1997cbb515923074fda68a1018c75d5d81594bede724625ff04325f75f2fe5dbbc788f0451062977e2e37e0479ea13c75d5475784e132c8432143b08bbdeba2655fadb6e61a775ce9d26f93c2528e6b34524ff2f0318c17b743e0018d37ba7ee24ddafe0f6d4c46d786c5404a3846781372ab4084a7cff980b359b9566, 'RSA_USING_SHA256', 0, 'gadgetbadget.user.security.JWTHandler', 'gadgetbadget.webservices.serviceauth', '2021-04-14 23:16:59');

--
-- Triggers `jwt_config`
--
DROP TRIGGER IF EXISTS `tg_jwtconfig_update`;
DELIMITER $$
CREATE TRIGGER `tg_jwtconfig_update` BEFORE UPDATE ON `jwt_config` FOR EACH ROW BEGIN
    SET NEW.jwt_date_last_updated = CURRENT_TIMESTAMP();
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `paymentmethod`
--

DROP TABLE IF EXISTS `paymentmethod`;
CREATE TABLE IF NOT EXISTS `paymentmethod` (
  `user_id` varchar(10) NOT NULL,
  `creditcard_type` varchar(20) NOT NULL,
  `creditcard_no` char(20) NOT NULL,
  `creditcard_security_no` char(10) NOT NULL,
  `exp_date` datetime NOT NULL,
  `billing_address` varchar(400) NOT NULL,
  `date_last_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`creditcard_no`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `paymentmethod`
--

INSERT INTO `paymentmethod` (`user_id`, `creditcard_type`, `creditcard_no`, `creditcard_security_no`, `exp_date`, `billing_address`, `date_last_updated`) VALUES
('CN21000016', 'Master', '1223650065036656', '336', '2021-07-31 00:00:00', '-', '2021-04-18 22:15:55'),
('CN21000017', 'Visa', '6545633226866', '563', '2020-04-12 00:00:00', 'Miami, FL.', '2021-04-19 04:10:19'),
('CN21000017', 'Verizon Credit', '6665552222365202', '5555', '2021-09-17 00:00:00', '-', '2021-04-19 08:56:09'),
('FN21000031', 'Visa', '0000236665452', '666', '2021-04-30 00:00:00', '-', '2021-04-18 23:38:01'),
('FN21000031', 'Vishwa Credit Card', '5555426635212536', '0028', '2025-04-28 00:00:00', '-', '2021-04-19 08:55:39'),
('FN21000031', 'Sampath Debit', '6555366625896645', '6667', '2021-04-30 00:00:00', 'Kandy, Sri Lanka', '2021-04-19 08:56:09'),
('FN21000032', 'Master', '8888556952002300', '333', '2021-04-05 00:00:00', '-\r\n', '2021-04-18 23:36:13'),
('FN21000033', 'American Express', '7778556589632222', '5552', '2028-04-24 00:00:00', 'Malabe, Sri Lanka.', '2021-04-19 08:56:09'),
('RS21000041', 'BOC Debit', '66666359655625', '6688', '2022-09-17 00:00:00', '', '2021-04-19 08:56:09');

--
-- Triggers `paymentmethod`
--
DROP TRIGGER IF EXISTS `tg_paymentmethod_insert`;
DELIMITER $$
CREATE TRIGGER `tg_paymentmethod_insert` BEFORE UPDATE ON `paymentmethod` FOR EACH ROW SET NEW.date_last_updated = CURRENT_TIMESTAMP()
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

--
-- Dumping data for table `researcher`
--

INSERT INTO `researcher` (`researcher_id`, `institution`, `field_of_study`, `years_of_exp`) VALUES
('RS21000041', 'SLIIT', 'AI', 2),
('RS21000042', 'SLIIT Academmy', 'Image Processing', 5),
('RS21000043', 'UoC', 'String Theory', 4);

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
('ADMIN', 'ADMIN@GADGETBADGET UPDATED.', '2021-04-09 03:07:02'),
('CNSMR', 'CONSUMER_GADGETBADGET', '2021-04-05 23:58:12'),
('EMPLY', 'EMPLOYEE@GADGETBADGET UPDATED.', '2021-04-07 21:43:14'),
('FNMGR', 'FINANCIAL_EMPLOYEE@GADGETBADGET UPDATED.', '2021-04-08 01:43:00'),
('FUNDR', 'FUNDER_GADGETBADGET', '2021-04-05 23:58:12'),
('GUEST', 'GUEST_GADGETBADGET', '2021-04-05 23:58:12'),
('RSCHR', 'RESEARCHER_GADGETBADGET', '2021-04-05 23:58:12');

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
  `is_deactivated` varchar(3) NOT NULL DEFAULT 'No',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `primary_email` (`primary_email`),
  UNIQUE KEY `primary_phone` (`primary_phone`),
  KEY `user_role_fk` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `username`, `password`, `role_id`, `first_name`, `last_name`, `gender`, `date_joined`, `primary_email`, `primary_phone`, `is_deactivated`) VALUES
('AD21000001', 'thisisishara', '1234', 'ADMIN', 'Ishara', 'Dissanayake', 'M', '2021-04-18 12:57:37', 'thisismaduishara@gadgetbadget.com', '0710000000', 'No'),
('AD21000009', 'mike.p', 'mike@123', 'ADMIN', 'Mike', 'Pound', 'M', '2021-04-18 14:46:38', 'mikep@gadgetbadget.com', '0710000002', 'No'),
('AD21000028', 'dave114', 'd@v311A', 'ADMIN', 'David', 'Jackson', 'M', '2021-04-18 18:44:52', 'dave.j@gadgetbadget.com', '0710000003', 'No'),
('CN21000016', 'katiejohnes', 'ktj@123', 'CNSMR', 'Katie', 'Johnes', 'M', '2021-04-18 15:39:29', 'ktj@yahoo.com', '0710000004', 'No'),
('CN21000017', 'ttylor_44', 't@yl0rThEb3s7', 'CNSMR', 'Taylor', 'Morningstarr', 'M', '2021-04-18 16:01:01', 'morningtaylor@gmail.com', '0710000005', 'Yes'),
('EM21000018', 'dennisDD', 'denis@123', 'EMPLY', 'Dennis', 'Scottfield', 'F', '2021-04-18 16:47:57', 'dennis@@gadgetbadget.com', '0710000006', 'No'),
('FM21000027', 'sarah.ds', 'D$11d%', 'FNMGR', 'Sarah', 'De Silva', 'F', '2021-04-18 18:44:50', 'sara.d@@gadgetbadget.com', '0710000007', 'No'),
('FN21000031', 'blackthefunder', 'it$B1@k3', 'FUNDR', 'Blake', 'Blackson', 'M', '2021-04-18 19:05:08', 'darkblake@gmail.com', '0710000008', 'No'),
('FN21000032', 'itsshawn', 'nwahs221', 'FUNDR', 'Shawn', 'Robinson', 'F', '2021-04-18 19:05:26', 'itsshawn.rb@shawntec.com', '0710000009', 'No'),
('FN21000033', 'arther_96', 'c@llme4979', 'FUNDR', 'Arthur', 'Rodriguz', 'M', '2021-04-18 19:05:26', 'cotact@artheristheman.com', '0710000010', 'No'),
('RS21000041', 'janemuse@22', 'jan3mus3', 'RSCHR', 'Jane', 'Muse', 'M', '2021-04-18 19:27:28', 'janemuse@researchers.co.ac', '0710000011', 'No'),
('RS21000042', 'peteross9', 'unH@ck4b*3', 'RSCHR', 'Peter', 'Ross', 'M', '2021-04-18 19:27:54', 'pete.ross@gmail.com', '0710000012', 'No'),
('RS21000043', 'nathon_black', 'nathonBLACK@17', 'RSCHR', 'Nathan', 'Blackmirror', 'M', '2021-04-18 19:27:55', 'itsblack@gmail.com', '0710000013', 'No');

-- --------------------------------------------------------

--
-- Table structure for table `user_seq`
--

DROP TABLE IF EXISTS `user_seq`;
CREATE TABLE IF NOT EXISTS `user_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_seq`
--

INSERT INTO `user_seq` (`id`, `prefix`) VALUES
(1, 'AD'),
(2, 'CN'),
(3, 'FM'),
(4, 'AD'),
(5, 'AD'),
(6, 'AD'),
(7, 'AD'),
(8, 'AD'),
(9, 'AD'),
(10, 'AD'),
(12, 'AD'),
(14, 'CN'),
(16, 'CN'),
(17, 'CN'),
(18, 'EM'),
(20, 'FM'),
(21, 'AD'),
(22, 'FM'),
(25, 'FM'),
(27, 'FM'),
(28, 'AD'),
(31, 'FN'),
(32, 'FN'),
(33, 'FN'),
(41, 'RS'),
(42, 'RS'),
(43, 'RS');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `consumer`
--
ALTER TABLE `consumer`
  ADD CONSTRAINT `con_user_fk` FOREIGN KEY (`consumer_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
  ADD CONSTRAINT `emp_user_fk` FOREIGN KEY (`employee_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `funder`
--
ALTER TABLE `funder`
  ADD CONSTRAINT `fun_user_fk` FOREIGN KEY (`funder_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `paymentmethod`
--
ALTER TABLE `paymentmethod`
  ADD CONSTRAINT `paymeth_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `researcher`
--
ALTER TABLE `researcher`
  ADD CONSTRAINT `res_user_fk` FOREIGN KEY (`researcher_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_role_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
