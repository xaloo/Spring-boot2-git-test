
/*Table structure for table `nota` */

CREATE TABLE `nota` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nota` double NOT NULL,
  `fkserie` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `nota_fkserie` (`fkserie`),
  CONSTRAINT `nota_fkserie` FOREIGN KEY (`fkserie`) REFERENCES `serie` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `serie` */

CREATE TABLE `serie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `director` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `anyo` int(11) DEFAULT NULL,
  `nombre` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci;

/*Table structure for table `usuario` */

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(40) NOT NULL,
  `password` varchar(60) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;