-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 17, 2015 at 01:40 PM
-- Server version: 5.5.44-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `auctionhouse_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `Accepted_Users`
--

CREATE TABLE IF NOT EXISTS `Accepted_Users` (
  `accepted_users_id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  PRIMARY KEY (`accepted_users_id`),
  UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `Accepted_Users`
--

INSERT INTO `Accepted_Users` (`accepted_users_id`, `uid`) VALUES
(1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `Admins`
--

CREATE TABLE IF NOT EXISTS `Admins` (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `Admins`
--

INSERT INTO `Admins` (`admin_id`, `uid`) VALUES
(1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `Bid`
--

CREATE TABLE IF NOT EXISTS `Bid` (
  `bid_id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) NOT NULL,
  `bidder` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`bid_id`),
  KEY `item_id` (`item_id`),
  KEY `bidder` (`bidder`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Items`
--

CREATE TABLE IF NOT EXISTS `Items` (
  `item_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `seller` int(11) NOT NULL,
  `first_bid` int(11) NOT NULL,
  `buy_price` int(11) DEFAULT NULL,
  `started` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ends` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `description` longtext NOT NULL,
  `status` varchar(60) NOT NULL DEFAULT 'active',
  PRIMARY KEY (`item_id`),
  KEY `seller` (`seller`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1679306429 ;

-- --------------------------------------------------------

--
-- Table structure for table `Item_has_Category`
--

CREATE TABLE IF NOT EXISTS `Item_has_Category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(60) NOT NULL,
  `item_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `item_id` (`item_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- Table structure for table `Item_has_Location`
--

CREATE TABLE IF NOT EXISTS `Item_has_Location` (
  `item_id` int(11) NOT NULL,
  `idLocation` int(11) NOT NULL,
  PRIMARY KEY (`item_id`,`idLocation`),
  KEY `fk_Item_has_Location_2_idx` (`idLocation`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Location`
--

CREATE TABLE IF NOT EXISTS `Location` (
  `idLocation` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `country` varchar(45) NOT NULL,
  `longitude` decimal(10,6) DEFAULT NULL,
  `latitude` decimal(10,6) DEFAULT NULL,
  PRIMARY KEY (`idLocation`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1044 ;

-- --------------------------------------------------------

--
-- Table structure for table `Messages`
--

CREATE TABLE IF NOT EXISTS `Messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender` int(11) NOT NULL,
  `receiver` int(11) NOT NULL,
  `msg` longtext NOT NULL,
  `timesend` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'unread',
  PRIMARY KEY (`id`),
  KEY `sender` (`sender`,`receiver`),
  KEY `receiver` (`receiver`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE IF NOT EXISTS `Users` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL,
  `password` varchar(60) NOT NULL,
  `firstName` varchar(60) NOT NULL,
  `lastName` varchar(60) NOT NULL,
  `email` varchar(60) NOT NULL,
  `phone` varchar(60) DEFAULT NULL,
  `afm` varchar(60) DEFAULT NULL,
  `userImage` varchar(100) DEFAULT NULL COMMENT 'image path or url',
  `about` longtext COMMENT 'description',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `rating` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=537 ;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`uid`, `username`, `password`, `firstName`, `lastName`, `email`, `phone`, `afm`, `userImage`, `about`, `created`, `rating`) VALUES
(1, 'admin', 'admin', 'Admin', 'Istrator', 'admin@adminmail.ad', '210ADMIN08', 'ADM3455', NULL, NULL, '2015-08-12 12:57:53', 0);

-- --------------------------------------------------------

--
-- Table structure for table `User_has_Location`
--

CREATE TABLE IF NOT EXISTS `User_has_Location` (
  `uid` int(11) NOT NULL,
  `idLocation` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`idLocation`),
  KEY `fk_User_has_Location_2_idx` (`idLocation`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `User_won_Item`
--

CREATE TABLE IF NOT EXISTS `User_won_Item` (
  `uid` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  PRIMARY KEY (`uid`,`item_id`),
  KEY `User_won_Item_ibfk_2` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Accepted_Users`
--
ALTER TABLE `Accepted_Users`
  ADD CONSTRAINT `Accepted_Users_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `Admins`
--
ALTER TABLE `Admins`
  ADD CONSTRAINT `Admins_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `Bid`
--
ALTER TABLE `Bid`
  ADD CONSTRAINT `Bid_ibfk_2` FOREIGN KEY (`bidder`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `Bid_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `Items` (`item_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `Items`
--
ALTER TABLE `Items`
  ADD CONSTRAINT `Items_ibfk_1` FOREIGN KEY (`seller`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `Item_has_Category`
--
ALTER TABLE `Item_has_Category`
  ADD CONSTRAINT `Item_has_Category_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `Items` (`item_id`);

--
-- Constraints for table `Item_has_Location`
--
ALTER TABLE `Item_has_Location`
  ADD CONSTRAINT `fk_Item_has_Location_2` FOREIGN KEY (`idLocation`) REFERENCES `Location` (`idLocation`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Item_has_Location_1` FOREIGN KEY (`item_id`) REFERENCES `Items` (`item_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `Messages`
--
ALTER TABLE `Messages`
  ADD CONSTRAINT `Messages_ibfk_2` FOREIGN KEY (`receiver`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `Messages_ibfk_1` FOREIGN KEY (`sender`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `User_has_Location`
--
ALTER TABLE `User_has_Location`
  ADD CONSTRAINT `fk_User_has_Location_2` FOREIGN KEY (`idLocation`) REFERENCES `Location` (`idLocation`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_User_has_Location_1` FOREIGN KEY (`uid`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `User_won_Item`
--
ALTER TABLE `User_won_Item`
  ADD CONSTRAINT `User_won_Item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `Items` (`item_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `User_won_Item_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `Users` (`uid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
