-- Adminer 4.2.5 MySQL dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

DROP DATABASE IF EXISTS `festivali`;
CREATE DATABASE `festivali` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `festivali`;

DROP TABLE IF EXISTS `festival`;
CREATE TABLE `festival` (
  `idFes` bigint(20) NOT NULL AUTO_INCREMENT,
  `naziv` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `datumOd` date NOT NULL,
  `datumDo` date NOT NULL,
  `mesto` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `detalji` text COLLATE utf8_unicode_ci NOT NULL,
  `kapacitetPoDanu` int(11) NOT NULL,
  `maxKarataPoKorisniku` int(11) NOT NULL,
  `cenaPaket` int(11) NOT NULL,
  `cenaDan` int(11) NOT NULL,
  `pregledi` bigint(20) NOT NULL DEFAULT '0',
  `facebook` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `twitter` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `instagram` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `youtube` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `otkazano` tinyint(1) NOT NULL DEFAULT '0',
  `brojKupljenih` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idFes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `festival` (`idFes`, `naziv`, `datumOd`, `datumDo`, `mesto`, `detalji`, `kapacitetPoDanu`, `maxKarataPoKorisniku`, `cenaPaket`, `cenaDan`, `pregledi`, `facebook`, `twitter`, `instagram`, `youtube`, `otkazano`, `brojKupljenih`) VALUES
(3,	'Exit',	'2017-08-10',	'2017-08-14',	'Novi Sad, Petrovaradin',	'',	15000,	6,	800,	200,	0,	'https://www.facebook.com/exit',	'http://twitter.com/exit',	'http://instagram.com/exit',	'http://www.youtube.com/exit',	0,	0),
(4,	'Beer Fest',	'2017-08-17',	'2017-08-21',	'Belgrade, Usce',	'',	500,	30,	800,	200,	0,	'https://www.facebook.com/belgradebeerfest',	'http://twitter.com/bgbeerfest',	NULL,	'http://www.youtube.com/Belgradebeerfest',	0,	0),
(5,	'Sziget',	'2017-02-17',	'2017-02-28',	'Budimpesta',	'Festival koji jos traje',	5000,	10,	5000,	500,	0,	'',	'',	'',	'',	0,	0);

DROP TABLE IF EXISTS `galerija`;
CREATE TABLE `galerija` (
  `idGal` bigint(20) NOT NULL AUTO_INCREMENT,
  `idFes` bigint(20) NOT NULL,
  `putanja` varchar(511) COLLATE utf8_unicode_ci NOT NULL,
  `tip` int(11) NOT NULL,
  PRIMARY KEY (`idGal`),
  KEY `idFes` (`idFes`),
  CONSTRAINT `galerija_ibfk_1` FOREIGN KEY (`idFes`) REFERENCES `festival` (`idFes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `galerija` (`idGal`, `idFes`, `putanja`, `tip`) VALUES
(7,	3,	'C:\\Temp\\Img\\Exit\\exit1182380850602191244.jpg',	1),
(8,	3,	'C:\\Temp\\Img\\Exit\\exit22645391748359037553.jpg',	1),
(9,	3,	'https://www.youtube.com/v/WJnhTXQ6a9Y',	2),
(10,	4,	'C:\\Temp\\Img\\Beer Fest\\beerfest18269153661032345177.jpg',	1),
(11,	5,	'C:\\Temp\\Img\\Sziget\\sziget15028659678750654080.png',	1),
(12,	5,	'https://www.youtube.com/v/2-6NB2SEgLk',	2);

DROP TABLE IF EXISTS `korisnik`;
CREATE TABLE `korisnik` (
  `idKor` bigint(20) NOT NULL AUTO_INCREMENT,
  `ime` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `prezime` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `username` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `telefon` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `admin` tinyint(1) NOT NULL DEFAULT '0',
  `odobren` tinyint(1) NOT NULL DEFAULT '0',
  `poslednjaPrijava` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `brojUpozorenja` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idKor`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `korisnik` (`idKor`, `ime`, `prezime`, `username`, `password`, `telefon`, `email`, `admin`, `odobren`, `poslednjaPrijava`, `brojUpozorenja`) VALUES
(14,	'Petar',	'Djukic',	'Petar',	'Sifra123',	'123135',	'Petar@Djukic.com',	1,	1,	'2017-02-19 18:30:54',	0),
(15,	'Neregistrovani',	'Korisnik',	'neregistrovani',	'NeregistrovaniKorisnik',	'1241325',	'a@b.c',	0,	1,	'2017-02-19 17:44:44',	0);

DROP TABLE IF EXISTS `nastup`;
CREATE TABLE `nastup` (
  `idNas` bigint(20) NOT NULL AUTO_INCREMENT,
  `izvodjac` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  `datumOd` date NOT NULL,
  `datumDo` date NOT NULL,
  `vremeOd` time NOT NULL,
  `vremeDo` time NOT NULL,
  `idFes` bigint(20) NOT NULL,
  `otkazano` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idNas`),
  KEY `idFes` (`idFes`),
  CONSTRAINT `nastup_ibfk_1` FOREIGN KEY (`idFes`) REFERENCES `festival` (`idFes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `nastup` (`idNas`, `izvodjac`, `datumOd`, `datumDo`, `vremeOd`, `vremeDo`, `idFes`, `otkazano`) VALUES
(2,	'Viva Vox',	'2017-08-10',	'2017-08-10',	'20:00:00',	'21:00:00',	3,	0),
(3,	'Rudimental Live',	'2017-08-10',	'2017-08-10',	'21:15:00',	'22:30:00',	3,	0),
(4,	'Elemental',	'2017-08-10',	'2017-08-10',	'22:45:00',	'23:45:00',	3,	0),
(5,	'LET 3',	'2017-08-11',	'2017-08-11',	'00:00:00',	'01:00:00',	3,	0),
(6,	'Bajaga',	'2017-08-10',	'2017-08-10',	'20:00:00',	'22:00:00',	3,	0),
(7,	'Riblja Corba',	'2017-08-11',	'2017-08-11',	'20:00:00',	'22:00:00',	3,	0),
(8,	'YU Grupa',	'2017-08-12',	'2017-08-12',	'21:00:00',	'23:00:00',	3,	0),
(9,	'Kerber',	'2017-08-13',	'2017-08-14',	'23:00:00',	'01:00:00',	3,	0),
(10,	'Viva Vox',	'2017-08-17',	'2017-08-17',	'20:00:00',	'21:30:00',	4,	0),
(11,	'Van Gog',	'2017-08-20',	'2017-08-21',	'22:45:00',	'01:30:00',	4,	0),
(12,	'Metallica',	'2017-02-21',	'2017-02-22',	'22:00:00',	'23:30:00',	5,	0);

DROP TABLE IF EXISTS `notifikacija`;
CREATE TABLE `notifikacija` (
  `idNot` bigint(20) NOT NULL AUTO_INCREMENT,
  `idKor` bigint(20) NOT NULL,
  `tekst` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `pregledano` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idNot`),
  KEY `idKor` (`idKor`),
  CONSTRAINT `notifikacija_ibfk_1` FOREIGN KEY (`idKor`) REFERENCES `korisnik` (`idKor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `ocena`;
CREATE TABLE `ocena` (
  `idOce` bigint(20) NOT NULL AUTO_INCREMENT,
  `idKor` bigint(20) NOT NULL,
  `idFes` bigint(20) NOT NULL,
  `ocena` int(11) NOT NULL,
  `komentar` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `imeFestivala` varchar(45) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`idOce`),
  KEY `idKor` (`idKor`),
  KEY `idFes` (`idFes`),
  CONSTRAINT `ocena_ibfk_1` FOREIGN KEY (`idKor`) REFERENCES `korisnik` (`idKor`),
  CONSTRAINT `ocena_ibfk_2` FOREIGN KEY (`idFes`) REFERENCES `festival` (`idFes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `ulaznica`;
CREATE TABLE `ulaznica` (
  `idUla` bigint(20) NOT NULL AUTO_INCREMENT,
  `idFes` bigint(20) NOT NULL,
  `datumNastupa` date DEFAULT NULL,
  `idKor` bigint(20) NOT NULL,
  `kolicina` int(11) NOT NULL,
  `vremeKreiranja` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `prodato` tinyint(1) NOT NULL DEFAULT '0',
  `isteklo` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idUla`),
  KEY `idFes` (`idFes`),
  KEY `idKor` (`idKor`),
  CONSTRAINT `ulaznica_ibfk_1` FOREIGN KEY (`idFes`) REFERENCES `festival` (`idFes`),
  CONSTRAINT `ulaznica_ibfk_2` FOREIGN KEY (`idKor`) REFERENCES `korisnik` (`idKor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- 2017-02-19 18:35:57
