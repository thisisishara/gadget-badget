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
-- Database: `gadgetbadget_marketplace`
--

DELIMITER $$
--
-- Procedures
--
DROP PROCEDURE IF EXISTS `sp_product_summary_resid`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_product_summary_resid` (IN `res_id` VARCHAR(10), OUT `no_products_in_mkt` INTEGER, OUT `latest_uploading_date` DATETIME, OUT `networth_of_products` DECIMAL(20,2), OUT `no_prod_cats` INTEGER, OUT `retrieved_date` DATETIME)  BEGIN
    DECLARE readVal INTEGER DEFAULT 0;
  
    SELECT MAX(`date_added`) INTO latest_uploading_date FROM `product` WHERE `researcher_id`=res_id;
    SELECT COUNT(DISTINCT `product_id`) INTO no_products_in_mkt FROM `product` WHERE `researcher_id`=res_id;
    SELECT SUM(`available_items` * `price`) INTO networth_of_products FROM `product` WHERE `researcher_id`=res_id;
    SELECT COUNT(DISTINCT `category_id`) INTO no_prod_cats FROM `product` WHERE `researcher_id`=res_id;
    SET retrieved_date = CURRENT_TIMESTAMP();
    
END$$

DELIMITER ;

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
('JWK1', 0x30820122300d06092a864886f70d01010105000382010f003082010a0282010100ca368b0a80b1793658c3d75106f0c94b671132f039f9953c2a6c55293453f24412024cdacb8291ce368c49f0ff18b2b41de3fd1a089358b7e98357a180b70385c18a65472a5b5d0bba2715f2c3aa6e29bff797175094b0255e9b8ce9c60bc8f17fe21228fb1d9b90767b2ebeb45e5a889293a1e4bd0eb4bc15a7702d0991d05bc7449a3de28ade8134bcb7858b8140f3461684eb5582316e86aa26aeeda9338354a9c4043c970724b8fd326485afc1d637aae09c1db282ec8de056b581418f4b1c1974e2a4a4308dce0539ded0c2d0012f1007532d3a61b1181b70c2342837c7e9a28d0a8787b0249dd9546f01485a63fb8c0719b7b533546df8a487a41b87fd0203010001, 0x308204be020100300d06092a864886f70d0101010500048204a8308204a40201000282010100ca368b0a80b1793658c3d75106f0c94b671132f039f9953c2a6c55293453f24412024cdacb8291ce368c49f0ff18b2b41de3fd1a089358b7e98357a180b70385c18a65472a5b5d0bba2715f2c3aa6e29bff797175094b0255e9b8ce9c60bc8f17fe21228fb1d9b90767b2ebeb45e5a889293a1e4bd0eb4bc15a7702d0991d05bc7449a3de28ade8134bcb7858b8140f3461684eb5582316e86aa26aeeda9338354a9c4043c970724b8fd326485afc1d637aae09c1db282ec8de056b581418f4b1c1974e2a4a4308dce0539ded0c2d0012f1007532d3a61b1181b70c2342837c7e9a28d0a8787b0249dd9546f01485a63fb8c0719b7b533546df8a487a41b87fd02030100010282010100be834ed2315783bcd93a81f4fc1605807df56d49794594fd9f767b719bb46f6f048c983e1738c80841ca40abc69b4d5a7742e2a611684950d4b782eb4d2aa02df79f5d36044919cbbfb1ad731037d51c8e994507994accfe1839733498fb6771682be532290cc710ff1fa575d4d7847261aea7fbaac75d2e4b347a72642eb5dee42dad69f10c4a66accc8517f4af1b3b92c707f147b9f27f876cf7f2cdcdbdc589a0fbe55105d0afd11630f9ec8f20d785b5ce4c49926ab69089ac24ef04ad770b6ec208af255c224ffff4ac272d16db9126fd39b6ca1ea27d43951bdbaa5dba7be19d93db6de1dab971bac43058491772f3e5a84330164ec1e8ef96d49bea0102818100f693cfc9fd4e4c1aa19177f97340d6be6fdd5e6276ea8da3a115bfbca21749a390cb23cf5057062527ac3fa621f7684a072329308e31d1c41b44471c25ab597e1a62284eeb44523de2b40465031018a306ca4af14c6657be10f8a01757e8c4a9a0f0930bbefc4a028fbc0d2c8d78a06dea25c0c81295a7bd2754e92e0d57107d02818100d1f0bab2fd18ab480f11d98a05c9f1ead882668b75418762c17939166abaccc4751454fed996835e8c0e06a828ffb805916c3b67fed6b7cbc3dc050ea43b5f2040913c737ab19c88219add8db05274dca4522a0d3931c8c3164d54e7ac01d983c81fedc657bb0b6a07a943018b833bc5a659ae34eadcf1d581fce75c92c36d810281804c20f2c4804a8e40a2a5910f1940698cbe68f05d222de4b12268de9bd4c7afdaaa37adc4b21f4c2c68854bd9751f37b9b35e6db72a0fc39df2753027469212bd5653fbf1f1bd544efb116d51ea922ba919cd9739ccc6c44c505d12c06249e17e25cc60f9fd6b53465b2e4a3af92ac70d687f6377e2150686e5ffd7467aa3e8d902818100aee4606423bc9d53d65a04639b16f4d5b3b04e44f755b3b76d7dd334fd8cc8711127f2f6abc55b833421ab2203a5a463df15cf177f90d86483b192f4f394125707f2f5ed2dd53095a7891ff09d66d3bbcb983737f4e1a861fcfe4731087632023a817ecfb0de3d500339da7c3b0104964f527e71cf0668e078fd7ab000039601028180524d36c72442a52fad2a93641e41aab62757af5ef94cbcb5c7bcafee3e64440cb09efae057c6aaf3113276123d03808a49216c28ea3c6b41b408b17447e0dfa252bb14fb134d9928678cdf1eed91597aba4d90926b1ea269fb8ab1b2b38466f36a688df070a680974bc360e2e3d74d0e6639d59a9e713bc946bb43f16dd88c66, 'RSA_USING_SHA256', 20, 'gadgetbadget.user.security.JWTHandler', 'gadgetbadget.webservices.auth', '2021-04-13 10:44:56'),
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
('PR21000001', 'RS21000041', 'Python Based Virtual Emotion Detector.', 'Python Based Virtual Emotion Detector for NLP projects. Multi-User Licenses are Limited.', 'PC21000005', 126, '15000.00', '2021-04-19 01:52:38'),
('PR21000002', 'RS21000041', 'How to minimize transactions processing time through smooth query optimization.', 'A highly requested book on query optimization by Sam Hunt, which has practical scenarios and example optimization methodologies explained clearly with both SQL and NoSQL Database types. Limited Number of copies available. 3rd Edition.', 'PC21000002', 1000, '25000.00', '2021-04-19 01:52:38'),
('PR21000003', 'RS21000043', 'Hi-bandwidth bluetooth session hijacking jammer.', 'Secure your bluetooth connections and access points of your personal networks. Prevent script kiddies from hijacking your precious music. Only supports Bluetooth v5 and above.', 'PC21000008', 8, '1049.99', '2021-04-19 01:52:38');

--
-- Triggers `product`
--
DROP TRIGGER IF EXISTS `tg_product_insert`;
DELIMITER $$
CREATE TRIGGER `tg_product_insert` BEFORE INSERT ON `product` FOR EACH ROW BEGIN
  SET NEW.date_added= CURRENT_TIMESTAMP();
  INSERT INTO product_seq (prefix) VALUES ("PR");
  SET NEW.product_id = CONCAT(CONCAT("PR",RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
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
('PC21000001', 'Network Devices', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000002', 'Books and Magazines', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000003', 'Clothes and Wearable ', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000004', 'Research Publications', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000005', 'Software Products', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000006', 'Healthcare Devices and Tools', 'N/A', '2021-04-19 01:36:50', 'AD21000001'),
('PC21000007', 'Vehicles', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000008', 'Consumer Electronics', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000009', 'Technologies and Patents', 'N/A', '2021-04-19 01:36:39', 'AD21000001'),
('PC21000010', 'Services', 'N/A', '2021-04-19 01:36:39', 'AD21000001');

--
-- Triggers `product_category`
--
DROP TRIGGER IF EXISTS `tg_product_category_insert`;
DELIMITER $$
CREATE TRIGGER `tg_product_category_insert` BEFORE INSERT ON `product_category` FOR EACH ROW BEGIN
  SET NEW.date_last_updated= CURRENT_TIMESTAMP();
  INSERT INTO product_category_seq (prefix) VALUES ("PC");
  SET NEW.category_id = CONCAT(CONCAT("PC",RIGHT(CAST(YEAR(CURDATE()) AS CHAR),2)), LPAD(LAST_INSERT_ID(), 6, '0'));
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_category_seq`
--

INSERT INTO `product_category_seq` (`id`, `prefix`) VALUES
(1, 'PC'),
(2, 'PC'),
(3, 'PC'),
(4, 'PC'),
(5, 'PC'),
(6, 'PC'),
(7, 'PC'),
(8, 'PC'),
(9, 'PC'),
(10, 'PC');

-- --------------------------------------------------------

--
-- Table structure for table `product_seq`
--

DROP TABLE IF EXISTS `product_seq`;
CREATE TABLE IF NOT EXISTS `product_seq` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `prefix` char(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_seq`
--

INSERT INTO `product_seq` (`id`, `prefix`) VALUES
(1, 'PR'),
(2, 'PR'),
(3, 'PR');

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
