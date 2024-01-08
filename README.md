# mq-distri-transaction
通过消息队列（mq）实现分布式事务

### 前言
分布式事务是要保证多个服务下的多个数据库操作的一致性。分布式事务常见解决方案有：二阶段、三阶段和TCC实现强一致性事务，其实还有一种广为人知的方案就是利用消息队列来实现分布式事务，保证数据的最终一致性，也就是我们常说的柔性事务。本次使用MQ+本地事务+消息校对的方式来实现分布式事务。
### 案例描述
有两张银行卡为bankcard1和bankcard2，且这两张银行卡存在于不同的服务中，bankcard1存在于payment服务中，专门用于转账支付，bankcard2存在于collection服务中，用于接收收款。下面为了方便讨论，将转账的payment服务记做主服务，收账的collection服务记为从服务。
### 解决思路
- 增加一张事务表，用作记录事务的id，事务状态和事务交易数据。且这张表在不同的服务中和服务中的银行卡当作本地事务，一同更新。其中，事务状态有三种，分别为：started、success和failed。started在主服务本地事务中更新，success在从服务本地事务成功中更新，failed在从服务消费失败时更新，并用做回滚主服务本地事物的提交。
- 使用定时任务，主服务开启定时任务，定时查询状态为strated事务，并将事物涉及到的交易信息封装为事务的data信息，将事物信息一并通过kafka发送给从服务。
- 幂等性问题解决，在从服务在进行本地事物的时候，会检查当前事物ID是否已经是success，如果是说明已经重复消费，回滚本地事物。
- 消费者手动确认消费，保证从服务的可靠性。
消息队列默认的自动ack机制是在消费者拿到消息就会将这条消息在队列中清除，那这边会出现了一个问题，消费者怎么能确定自己手上这条信息在流程中不会出问题呢，按道理我们是要消费者做完事情在告诉队列去删除，我出问题了你下次再给我重发我再次消费，所以这里我们要开启手动ack在执行完业务逻辑后手动提交，以此来保证整个流程的数据一致性。
- 最后，为了进一步保证分布式事务的一致性，在以后操作这些关联事务的表的之前，需要多一次查表操作，先看一下该表所关联的事务的状态是否是success。
### 流程图

![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/e9740efa0e7f4b668df12d4ab9f528f1.jpeg#pic_center)
### 数据库设计

```sql
-- MySQL dump 10.13  Distrib 8.0.34, for macos13 (x86_64)
--
-- Host: 127.0.0.1    Database: mq-transaction
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bankcard1`
--

DROP TABLE IF EXISTS `bankcard1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bankcard1` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `card_number` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '银行卡号',
  `amount` double NOT NULL DEFAULT '0' COMMENT '银行卡余额',
  `transaction_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联事务uuid',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bankcard1_card_number_uindex` (`card_number`) COMMENT '银行卡号唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='银行卡1，用于支付';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bankcard1`
--

LOCK TABLES `bankcard1` WRITE;
/*!40000 ALTER TABLE `bankcard1` DISABLE KEYS */;
INSERT INTO `bankcard1` VALUES (1,'7b3904c584d74c6fa5bce8daa65f7c9c',900,'74ad50c1a82c4b2fb322f2708a9aafb7');
/*!40000 ALTER TABLE `bankcard1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bankcard2`
--

DROP TABLE IF EXISTS `bankcard2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bankcard2` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `card_number` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '银行卡号',
  `amount` double NOT NULL DEFAULT '0' COMMENT '总金额',
  `transaction_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联的事务uuid',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bankcard2_card_number_uindex` (`card_number`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='银行卡2，用于收款';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bankcard2`
--

LOCK TABLES `bankcard2` WRITE;
/*!40000 ALTER TABLE `bankcard2` DISABLE KEYS */;
INSERT INTO `bankcard2` VALUES (1,'9fc5e9e265c14e5eb9bf77c6eb5c5d98',200,'74ad50c1a82c4b2fb322f2708a9aafb7');
/*!40000 ALTER TABLE `bankcard2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_record`
--

DROP TABLE IF EXISTS `transaction_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `transaction_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '事务uuid',
  `data` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '涉及到的交易数据',
  `status` varchar(16) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '事务状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `transaction_record_transaction_id_uindex` (`transaction_id`) COMMENT 'transaction_id uuid 唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='事务记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_record`
--

LOCK TABLES `transaction_record` WRITE;
/*!40000 ALTER TABLE `transaction_record` DISABLE KEYS */;
INSERT INTO `transaction_record` VALUES (1,'e2d68d8593594239beec2f68da6a01d0','{\"amount\":50,\"cardNumber2\":\"9fc5e9e265c14e5eb9bf77c6eb5c5d98\",\"cardNumber1\":\"7b3904c584d74c6fa5bce8daa65f7c9c\"}','success','2024-01-06 20:20:32','2024-01-06 20:20:32',0),(2,'74ad50c1a82c4b2fb322f2708a9aafb7','{\"amount\":50,\"cardNumber2\":\"9fc5e9e265c14e5eb9bf77c6eb5c5d98\",\"cardNumber1\":\"7b3904c584d74c6fa5bce8daa65f7c9c\"}','success','2024-01-06 20:29:04','2024-01-06 20:29:04',0);
/*!40000 ALTER TABLE `transaction_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-01-07 16:56:36

```
### 结语
如果本篇文章对你有用，请帮忙点个star
### 参考资料
[https://blog.csdn.net/alili0619/article/details/118729348](https://blog.csdn.net/alili0619/article/details/118729348)
[https://blog.csdn.net/xueping_wu/article/details/127143322](https://blog.csdn.net/xueping_wu/article/details/127143322)
[https://mp.weixin.qq.com/s?__biz=MjM5NTY1MjY0MQ==&mid=2650860292&idx=3&sn=21be1aef473c4ceb1f2b00116fd4f671&chksm=bd017e4a8a76f75c89aea1b820f6e76071406c9e3cdd7c61cad99c92d6a59baff1daaf751d38&scene=27](https://mp.weixin.qq.com/s?__biz=MjM5NTY1MjY0MQ==&mid=2650860292&idx=3&sn=21be1aef473c4ceb1f2b00116fd4f671&chksm=bd017e4a8a76f75c89aea1b820f6e76071406c9e3cdd7c61cad99c92d6a59baff1daaf751d38&scene=27)
