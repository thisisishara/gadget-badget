-- phpMyAdmin SQL Dump
-- version 4.7.9
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Apr 06, 2021 at 01:25 PM
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
-- Database: `gadgetbadget_researchhub`
--

-- --------------------------------------------------------

--
-- Table structure for table `collaborator`
--

DROP TABLE IF EXISTS `collaborator`;
CREATE TABLE IF NOT EXISTS `collaborator` (
  `project_id` varchar(10) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `institution` varchar(100) DEFAULT NULL,
  `date_joined` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`,`full_name`),
  UNIQUE KEY `project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `researchproject`
--

DROP TABLE IF EXISTS `researchproject`;
CREATE TABLE IF NOT EXISTS `researchproject` (
  `project_id` varchar(10) NOT NULL,
  `researcher_id` varchar(10) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `project_description` varchar(400) NOT NULL,
  `category_id` varchar(10) NOT NULL,
  `project_start_date` datetime NOT NULL,
  `project_end_date` datetime NOT NULL,
  `expected_total_budget` decimal(12,2) NOT NULL,
  `date_added` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `project_id` (`project_id`),
  KEY `proj_cat_fk` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `researchproject_seq`
--

DROP TABLE IF EXISTS `researchproject_seq`;
CREATE TABLE IF NOT EXISTS `researchproject_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `research_category`
--

DROP TABLE IF EXISTS `research_category`;
CREATE TABLE IF NOT EXISTS `research_category` (
  `category_id` varchar(10) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `category_description` varchar(400) DEFAULT NULL,
  `date_last_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified_by` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Triggers `research_category`
--
DROP TRIGGER IF EXISTS `tg_research_category_insert`;
DELIMITER $$
CREATE TRIGGER `tg_research_category_insert` BEFORE INSERT ON `research_category` FOR EACH ROW BEGIN
  SET NEW.date_last_updated= CURRENT_TIMESTAMP();
  INSERT INTO research_category_seq (prefix) VALUES ("RC");
  SET NEW.category_id = CONCAT(CONCAT("RC",RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_research_category_update`;
DELIMITER $$
CREATE TRIGGER `tg_research_category_update` BEFORE UPDATE ON `research_category` FOR EACH ROW BEGIN
    SET NEW.date_last_updated = CURRENT_TIMESTAMP();
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `research_category_seq`
--

DROP TABLE IF EXISTS `research_category_seq`;
CREATE TABLE IF NOT EXISTS `research_category_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `collaborator`
--
ALTER TABLE `collaborator`
  ADD CONSTRAINT `col_proj_fk` FOREIGN KEY (`project_id`) REFERENCES `researchproject` (`project_id`);

--
-- Constraints for table `researchproject`
--
ALTER TABLE `researchproject`
  ADD CONSTRAINT `proj_cat_fk` FOREIGN KEY (`category_id`) REFERENCES `research_category` (`category_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
