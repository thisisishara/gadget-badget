<<<<<<< HEAD
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
-- Database: `gadgetbadget_marketplace`
--

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` varchar(10) NOT NULL,
  `researcher_id` varchar(10) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `product_description` varchar(400) DEFAULT NULL,
  `category_id` varchar(10) NOT NULL,
  `available_items` int(11) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `date_added` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_id` (`product_id`),
  KEY `prod_cat_fk` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Triggers `product`
--
DROP TRIGGER IF EXISTS `tg_product_insert`;
DELIMITER $$
=======
-- phpMyAdmin SQL Dump
-- version 4.7.9
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Apr 06, 2021 at 01:21 PM
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
-- Database: `gadgetbadget_marketplace`
--

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` varchar(10) NOT NULL,
  `researcher_id` varchar(10) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `product_description` varchar(400) DEFAULT NULL,
  `category_id` varchar(10) NOT NULL,
  `available_items` int(11) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `date_added` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_id` (`product_id`),
  KEY `prod_cat_fk` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`product_id`, `researcher_id`, `product_name`, `product_description`, `category_id`, `available_items`, `price`, `date_added`) VALUES
('PR21000001', 'rer', 'erer', 'rer', 'PC21000001', 2, '222.22', '2021-04-06 09:43:14');

--
-- Triggers `product`
--
DROP TRIGGER IF EXISTS `tg_product_insert`;
DELIMITER $$
>>>>>>> branch 'master' of https://github.com/thisisishara/GadgetBadget.git
CREATE TRIGGER `tg_product_insert` BEFORE INSERT ON `product` FOR EACH ROW BEGIN
  SET NEW.date_added= CURRENT_TIMESTAMP();
  INSERT INTO product_seq (prefix) VALUES ("PR");
  SET NEW.product_id = CONCAT(CONCAT("PR",RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
<<<<<<< HEAD
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
CREATE TABLE IF NOT EXISTS `product_category` (
  `category_id` varchar(10) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `category_description` varchar(400) DEFAULT NULL,
  `date_last_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified_by` varchar(10) NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_category`
--

INSERT INTO `product_category` (`category_id`, `category_name`, `category_description`, `date_last_updated`, `last_modified_by`) VALUES
('PC21000001', 'SOFTWARE', 'THIS IS SOFTWARE CATEGORY', '2021-04-06 08:53:30', 'AD21000001');

--
-- Triggers `product_category`
--
DROP TRIGGER IF EXISTS `tg_product_category_insert`;
DELIMITER $$
=======
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
CREATE TABLE IF NOT EXISTS `product_category` (
  `category_id` varchar(10) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `category_description` varchar(400) DEFAULT NULL,
  `date_last_updated` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified_by` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_category`
--

INSERT INTO `product_category` (`category_id`, `category_name`, `category_description`, `date_last_updated`, `last_modified_by`) VALUES
('PC21000001', 'SOFTWARE', 'THIS IS SOFTWARE CATEGORY', '2021-04-06 08:53:30', 'AD21000001');

--
-- Triggers `product_category`
--
DROP TRIGGER IF EXISTS `tg_product_category_insert`;
DELIMITER $$
>>>>>>> branch 'master' of https://github.com/thisisishara/GadgetBadget.git
CREATE TRIGGER `tg_product_category_insert` BEFORE INSERT ON `product_category` FOR EACH ROW BEGIN
  SET NEW.date_last_updated= CURRENT_TIMESTAMP();
  INSERT INTO product_category_seq (prefix) VALUES ("PC");
  SET NEW.category_id = CONCAT(CONCAT("PC",RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
<<<<<<< HEAD
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product_category_seq`
--

DROP TABLE IF EXISTS `product_category_seq`;
CREATE TABLE IF NOT EXISTS `product_category_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_category_seq`
--

INSERT INTO `product_category_seq` (`id`, `prefix`) VALUES
(1, 'PC');

-- --------------------------------------------------------

--
-- Table structure for table `product_seq`
--

DROP TABLE IF EXISTS `product_seq`;
CREATE TABLE IF NOT EXISTS `product_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `prod_cat_fk` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`category_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
=======
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `tg_product_category_update`;
DELIMITER $$
CREATE TRIGGER `tg_product_category_update` BEFORE UPDATE ON `product_category` FOR EACH ROW BEGIN
    SET NEW.date_last_updated = CURRENT_TIMESTAMP();
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product_category_seq`
--

DROP TABLE IF EXISTS `product_category_seq`;
CREATE TABLE IF NOT EXISTS `product_category_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_category_seq`
--

INSERT INTO `product_category_seq` (`id`, `prefix`) VALUES
(1, 'PC');

-- --------------------------------------------------------

--
-- Table structure for table `product_seq`
--

DROP TABLE IF EXISTS `product_seq`;
CREATE TABLE IF NOT EXISTS `product_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_seq`
--

INSERT INTO `product_seq` (`id`, `prefix`) VALUES
(1, 'PR');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `prod_cat_fk` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`category_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
>>>>>>> branch 'master' of https://github.com/thisisishara/GadgetBadget.git
