/* add tables */
CREATE TABLE IF NOT EXISTS `so_meta` (
  `som_id` varchar(30) NOT NULL DEFAULT '',
  `som_type` varchar(80) NOT NULL DEFAULT '',
  `som_key` varchar(80) NOT NULL DEFAULT '',
  `som_value` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `sh_meta` (
  `shm_id` varchar(30) NOT NULL DEFAULT '',
  `shm_type` varchar(80) NOT NULL DEFAULT '',
  `shm_key` varchar(80) NOT NULL DEFAULT '',
  `shm_value` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `sys_meta` (
  `sysm_id` varchar(30) NOT NULL DEFAULT '',
  `sysm_type` varchar(80) NOT NULL DEFAULT '',
  `sysm_key` varchar(80) NOT NULL DEFAULT '',
  `sysm_value` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `hr_meta` (
  `hrm_id` varchar(30) NOT NULL DEFAULT '',
  `hrm_type` varchar(80) NOT NULL DEFAULT '',
  `hrm_key` varchar(80) NOT NULL DEFAULT '',
  `hrm_value` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `it_meta` (
  `itm_id` varchar(30) NOT NULL DEFAULT '',
  `itm_type` varchar(80) NOT NULL DEFAULT '',
  `itm_key` varchar(80) NOT NULL DEFAULT '',
  `itm_value` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `fin_meta` (
  `finm_id` varchar(30) NOT NULL DEFAULT '',
  `finm_type` varchar(80) NOT NULL DEFAULT '',
  `finm_key` varchar(80) NOT NULL DEFAULT '',
  `finm_value` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




/* add missing fields */
alter table perm_mstr add column perm_readonly tinyint(1) NOT NULL default '0';


/* load master table records */
insert into sys_meta values('system','sysdir','attachment_directory','attachments');
insert into sys_meta values('system','sysdir','temp_directory','temp');
insert into sys_meta values('system','sysdir','log_directory','logs');
insert into sys_meta values('system','sysdir','image_directory','images');
insert into sys_meta values('system','sysdir','jasper_directory','jasper');
insert into sys_meta values('system','sysdir','label_directory','zebra');
insert into sys_meta values('system','sysdir','edi_directory','edi');

