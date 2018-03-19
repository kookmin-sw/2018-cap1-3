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
-- Table `dalarm`.`alarm`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dalarm`.`alarm` (
  `id` INT NOT NULL,
  `hour` INT NULL,
  `minutes` INT NULL,
  `ampm` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dalarm`.`inform`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dalarm`.`inform` (
  `id` INT NULL,
  `text` VARCHAR(45) NULL,
  `celebrity` VARCHAR(45) NULL,
  `alarm_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_inform_alarm_idx` (`alarm_id` ASC),
  CONSTRAINT `fk_inform_alarm`
    FOREIGN KEY (`alarm_id`)
    REFERENCES `dalarm`.`alarm` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dalarm`.`voice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dalarm`.`voice` (
  `id` INT NULL,
  `voice_path` VARCHAR(45) NULL,
  `alarm_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_voice_alarm1_idx` (`alarm_id` ASC),
  CONSTRAINT `fk_voice_alarm1`
    FOREIGN KEY (`alarm_id`)
    REFERENCES `dalarm`.`alarm` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dalarm`.`ch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dalarm`.`ch` (
  `id` INT NULL,
  `onoff` TINYINT(1) NOT NULL,
  `alarm_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ch_alarm1_idx` (`alarm_id` ASC),
  CONSTRAINT `fk_ch_alarm1`
    FOREIGN KEY (`alarm_id`)
    REFERENCES `dalarm`.`alarm` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dalarm`.`days`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dalarm`.`days` (
  `id` INT NULL,
  `days` VARCHAR(45) NULL,
  `alarm_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_days_alarm1_idx` (`alarm_id` ASC),
  CONSTRAINT `fk_days_alarm1`
    FOREIGN KEY (`alarm_id`)
    REFERENCES `dalarm`.`alarm` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
