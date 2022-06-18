/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : localhost:3306
 Source Schema         : fivechess

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 18/06/2022 20:53:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `rating` double(11, 0) NULL DEFAULT NULL COMMENT '等级分',
  `integral` int(11) NULL DEFAULT NULL COMMENT '积分',
  `rank_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '段位名称',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('20', '茅峰腰', 1560, 5, '草民');
INSERT INTO `user` VALUES ('32', '益荐极', 1520, 3, '草民');
INSERT INTO `user` VALUES ('70', '宗政', 1580, 2, '草民');
INSERT INTO `user` VALUES ('e4787d9440c1676f', '公西', 1927, 7, '草民');

SET FOREIGN_KEY_CHECKS = 1;
