/*
Navicat MySQL Data Transfer

Source Server         : sq_h170775
Source Server Version : 50403
Source Host           : 118.193.146.127:3306
Source Database       : sq_h170775

Target Server Type    : MYSQL
Target Server Version : 50403
File Encoding         : 65001

Date: 2015-10-28 20:57:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_enshrine
-- ----------------------------
DROP TABLE IF EXISTS `t_enshrine`;
CREATE TABLE `t_enshrine` (
  `r_id` int(11) DEFAULT NULL,
  `u_id` int(11) DEFAULT NULL,
  KEY `FK_T_Enshrine_T_User_u_id` (`u_id`),
  KEY `FK_T_Enshrine_T_Reply_r_id` (`r_id`),
  CONSTRAINT `FK_T_Enshrine_T_Reply_r_id` FOREIGN KEY (`r_id`) REFERENCES `t_reply` (`r_id`),
  CONSTRAINT `FK_T_Enshrine_T_User_u_id` FOREIGN KEY (`u_id`) REFERENCES `t_user` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for t_praise
-- ----------------------------
DROP TABLE IF EXISTS `t_praise`;
CREATE TABLE `t_praise` (
  `r_id` int(11) DEFAULT NULL,
  `u_id` int(11) DEFAULT NULL,
  KEY `FK_T_Praise_T_User_u_id` (`u_id`),
  KEY `FK_T_Praise_T_Reply_r_id` (`r_id`),
  CONSTRAINT `FK_T_Praise_T_Reply_r_id` FOREIGN KEY (`r_id`) REFERENCES `t_reply` (`r_id`),
  CONSTRAINT `FK_T_Praise_T_User_u_id` FOREIGN KEY (`u_id`) REFERENCES `t_user` (`u_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for t_problem
-- ----------------------------
DROP TABLE IF EXISTS `t_problem`;
CREATE TABLE `t_problem` (
  `p_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` int(11) NOT NULL,
  `t_id` int(11) DEFAULT NULL,
  `p_problem` text COLLATE utf8_unicode_ci NOT NULL,
  `p_explain` mediumtext COLLATE utf8_unicode_ci,
  `p_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`p_id`),
  KEY `FK_T_Problem_T_User_u_id` (`u_id`),
  KEY `FK_T_Problem_T_Topic_t_id` (`t_id`),
  CONSTRAINT `FK_T_Problem_T_Topic_t_id` FOREIGN KEY (`t_id`) REFERENCES `t_topic` (`t_id`),
  CONSTRAINT `FK_T_Problem_T_User_u_id` FOREIGN KEY (`u_id`) REFERENCES `t_user` (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for t_reply
-- ----------------------------
DROP TABLE IF EXISTS `t_reply`;
CREATE TABLE `t_reply` (
  `r_id` int(11) NOT NULL AUTO_INCREMENT,
  `p_id` int(11) NOT NULL,
  `u_id` int(11) NOT NULL,
  `r_reply` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `r_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`r_id`),
  KEY `FK_T_Reply_T_User_u_id` (`u_id`),
  KEY `FK_T_Reply_T_Problem_u_id` (`p_id`),
  CONSTRAINT `FK_T_Reply_T_Problem_u_id` FOREIGN KEY (`p_id`) REFERENCES `t_problem` (`p_id`),
  CONSTRAINT `FK_T_Reply_T_User_u_id` FOREIGN KEY (`u_id`) REFERENCES `t_user` (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for t_topic
-- ----------------------------
DROP TABLE IF EXISTS `t_topic`;
CREATE TABLE `t_topic` (
  `t_id` int(11) NOT NULL AUTO_INCREMENT,
  `t_topic` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `t_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`t_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `u_id` int(11) NOT NULL AUTO_INCREMENT,
  `u_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `u_sex` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `u_email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `u_passwd` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `u_intro` text COLLATE utf8_unicode_ci,
  `u_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
