-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: 188.166.65.26    Database: Diabetes
-- ------------------------------------------------------
-- Server version	8.0.19-0ubuntu0.19.10.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Patients`
--

DROP TABLE IF EXISTS `Patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Patients` (
  `PatientID` int NOT NULL AUTO_INCREMENT,
  `DoctorID` int DEFAULT NULL,
  `FirstName` varchar(255) DEFAULT NULL,
  `LastName` varchar(255) DEFAULT NULL,
  `UserName` varchar(255) DEFAULT NULL,
  `Height` varchar(255) DEFAULT NULL,
  `Pregnant` tinyint(1) DEFAULT '0',
  `MobileNumber` bigint DEFAULT NULL,
  `PhotoDataUrl` varchar(255) DEFAULT NULL,
  `Password` char(90) DEFAULT NULL,
  `BslUnit` tinyint NOT NULL DEFAULT '0',
  `SessionToken` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`PatientID`),
  KEY `FK_Patients_1` (`DoctorID`),
  CONSTRAINT `FK_Patients_1` FOREIGN KEY (`DoctorID`) REFERENCES `Doctors` (`DoctorID`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-03-09 14:23:36
