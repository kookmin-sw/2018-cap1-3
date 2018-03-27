-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema dalarm
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema dalarm
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dalarm` DEFAULT CHARACTER SET utf8 ;
USE `dalarm` ;

-- -----------------------------------------------------
-- Table `dalarm`.`alarm_inform`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dalarm`.`alarm_inform` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `voice` VARCHAR(45) NULL,
  `message` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dalarm`.`voice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dalarm`.`voice` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `voice_path` VARCHAR(45) NULL,
  `alarm_inform_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_voice_alarm_inform_idx` (`alarm_inform_id` ASC),
  CONSTRAINT `fk_voice_alarm_inform`
    FOREIGN KEY (`alarm_inform_id`)
    REFERENCES `dalarm`.`alarm_inform` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
