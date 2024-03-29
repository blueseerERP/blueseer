DROP TABLE IF EXISTS `as2_meta`;
CREATE TABLE `as2_meta` (
  `as2m_id` varchar(30) NOT NULL DEFAULT '',
  `as2m_type` varchar(80) NOT NULL DEFAULT '',
  `as2m_key` varchar(80) NOT NULL DEFAULT '',
  `as2m_value` varchar(80) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `brk_meta`;
CREATE TABLE `brk_meta` (
  `brkm_id` varchar(30) NOT NULL DEFAULT '',
  `brkm_type` varchar(80) NOT NULL DEFAULT '',
  `brkm_key` varchar(80) NOT NULL DEFAULT '',
  `brkm_value` varchar(80) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `brk_mstr`;
CREATE TABLE `brk_mstr` (
  `brk_id` varchar(20) NOT NULL DEFAULT '',
  `brk_status` varchar(20) NOT NULL DEFAULT '',
  `brk_name` varchar(80) NOT NULL DEFAULT '',
  `brk_line1` varchar(50) NOT NULL DEFAULT '',
  `brk_line2` varchar(50) NOT NULL DEFAULT '',
  `brk_city` varchar(50) NOT NULL DEFAULT '',
  `brk_state` varchar(50) NOT NULL DEFAULT '',
  `brk_zip` varchar(50) NOT NULL DEFAULT '',
  `brk_country` varchar(3) NOT NULL DEFAULT '',
  `brk_phone` varchar(15) NOT NULL DEFAULT '',
  `brk_contact` varchar(50) NOT NULL DEFAULT '',
  `brk_email` varchar(100) NOT NULL DEFAULT '',
  `brk_type` varchar(10) NOT NULL DEFAULT '',
  `brk_acct` varchar(12) NOT NULL DEFAULT '',
  `brk_cc` varchar(4) NOT NULL DEFAULT '',
  `brk_certificate` varchar(30) NOT NULL DEFAULT '',
  `brk_payrate` decimal(12,5) NOT NULL DEFAULT '0.00000',
  `brk_paytype` varchar(12) NOT NULL DEFAULT '',
  `brk_terms` varchar(30) NOT NULL DEFAULT '',
  `brk_bank` varchar(30) NOT NULL DEFAULT '',
  `brk_taxid` varchar(30) NOT NULL DEFAULT '',
  `brk_rmks` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cfo_det`;
CREATE TABLE `cfo_det` (
  `cfod_nbr` varchar(8) NOT NULL DEFAULT '',
  `cfod_revision` int NOT NULL DEFAULT '0',
  `cfod_stopline` varchar(8) NOT NULL DEFAULT '',
  `cfod_seq` varchar(8) NOT NULL DEFAULT '',
  `cfod_type` varchar(20) NOT NULL DEFAULT '',
  `cfod_code` varchar(10) NOT NULL DEFAULT '',
  `cfod_name` varchar(50) NOT NULL DEFAULT '',
  `cfod_line1` varchar(50) NOT NULL DEFAULT '',
  `cfod_line2` varchar(50) NOT NULL DEFAULT '',
  `cfod_line3` varchar(50) NOT NULL DEFAULT '',
  `cfod_city` varchar(50) NOT NULL DEFAULT '',
  `cfod_state` varchar(3) NOT NULL DEFAULT '',
  `cfod_zip` varchar(15) NOT NULL DEFAULT '',
  `cfod_country` varchar(3) NOT NULL DEFAULT '',
  `cfod_phone` varchar(15) NOT NULL DEFAULT '',
  `cfod_email` varchar(80) NOT NULL DEFAULT '',
  `cfod_contact` varchar(80) NOT NULL DEFAULT '',
  `cfod_misc` varchar(80) NOT NULL DEFAULT '',
  `cfod_rmks` varchar(200) NOT NULL DEFAULT '',
  `cfod_reference` varchar(30) NOT NULL DEFAULT '',
  `cfod_ordnum` varchar(30) NOT NULL DEFAULT '',
  `cfod_weight` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfod_pallet` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfod_ladingqty` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfod_hazmat` varchar(50) NOT NULL DEFAULT '',
  `cfod_datetype` varchar(80) NOT NULL DEFAULT '',
  `cfod_date` date DEFAULT NULL,
  `cfod_timetype1` varchar(50) NOT NULL DEFAULT '',
  `cfod_time1` varchar(8) NOT NULL DEFAULT '',
  `cfod_timetype2` varchar(50) NOT NULL DEFAULT '',
  `cfod_time2` varchar(8) NOT NULL DEFAULT '',
  `cfod_timezone` varchar(20) NOT NULL DEFAULT '',
  `cfod_rate` decimal(14,5) NOT NULL DEFAULT '0.00000',
  `cfod_miles` decimal(14,2) NOT NULL DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cfo_item`;
CREATE TABLE `cfo_item` (
  `cfoi_nbr` varchar(8) NOT NULL DEFAULT '',
  `cfoi_revision` int NOT NULL DEFAULT '0',
  `cfoi_stopline` varchar(8) NOT NULL DEFAULT '',
  `cfoi_itemline` varchar(8) NOT NULL DEFAULT '',
  `cfoi_item` varchar(50) NOT NULL DEFAULT '',
  `cfoi_itemdesc` varchar(100) NOT NULL DEFAULT '',
  `cfoi_order` varchar(30) NOT NULL DEFAULT '',
  `cfoi_qty` decimal(14,5) NOT NULL DEFAULT '0.00000',
  `cfoi_pallets` decimal(14,5) NOT NULL DEFAULT '0.00000',
  `cfoi_weight` decimal(14,5) NOT NULL DEFAULT '0.00000',
  `cfoi_ref` varchar(50) NOT NULL DEFAULT '',
  `cfoi_rmks` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cfo_meta`;
CREATE TABLE `cfo_meta` (
  `cfom_id` varchar(8) NOT NULL,
  `cfom_revision` int NOT NULL DEFAULT '0',
  `cfom_type` varchar(80) NOT NULL,
  `cfom_key` varchar(80) NOT NULL,
  `cfom_value` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cfo_mstr`;
CREATE TABLE `cfo_mstr` (
  `cfo_nbr` varchar(8) NOT NULL DEFAULT '',
  `cfo_cust` varchar(30) NOT NULL DEFAULT '',
  `cfo_custfonbr` varchar(30) NOT NULL DEFAULT '',
  `cfo_revision` int NOT NULL DEFAULT '0',
  `cfo_servicetype` varchar(50) NOT NULL DEFAULT '',
  `cfo_equipmenttype` varchar(50) NOT NULL DEFAULT '',
  `cfo_truckid` varchar(20) NOT NULL DEFAULT '',
  `cfo_trailernbr` varchar(20) NOT NULL DEFAULT '',
  `cfo_orderstatus` varchar(30) NOT NULL DEFAULT '',
  `cfo_deliverystatus` varchar(30) NOT NULL DEFAULT '',
  `cfo_driver` varchar(50) NOT NULL DEFAULT '',
  `cfo_drivercell` varchar(15) NOT NULL DEFAULT '',
  `cfo_type` varchar(30) NOT NULL DEFAULT '',
  `cfo_brokerid` varchar(20) NOT NULL DEFAULT '',
  `cfo_brokercontact` varchar(50) NOT NULL DEFAULT '',
  `cfo_brokercell` varchar(15) NOT NULL DEFAULT '',
  `cfo_ratetype` varchar(30) NOT NULL DEFAULT '',
  `cfo_rate` decimal(14,5) NOT NULL DEFAULT '0.00000',
  `cfo_mileage` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfo_driverrate` decimal(14,5) NOT NULL DEFAULT '0.00000',
  `cfo_driverstd` tinyint(1) NOT NULL DEFAULT '0',
  `cfo_weight` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfo_orddate` varchar(10) NOT NULL DEFAULT '',
  `cfo_confdate` varchar(10) NOT NULL DEFAULT '',
  `cfo_ishazmat` tinyint(1) NOT NULL DEFAULT '0',
  `cfo_miscexpense` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfo_misccharges` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfo_cost` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfo_bol` varchar(30) NOT NULL DEFAULT '',
  `cfo_rmks` varchar(200) NOT NULL DEFAULT '',
  `cfo_derived` varchar(100) NOT NULL DEFAULT '',
  `cfo_logic` varchar(100) NOT NULL DEFAULT '',
  `cfo_site` varchar(10) NOT NULL DEFAULT '',
  `cfo_edi` tinyint(1) NOT NULL DEFAULT '0',
  `cfo_edireason` varchar(50) NOT NULL DEFAULT '',
  `cfo_defaultrev` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cfo_sos`;
CREATE TABLE `cfo_sos` (
  `cfos_nbr` varchar(8) NOT NULL,
  `cfos_revision` int NOT NULL DEFAULT '0',
  `cfos_desc` varchar(80) NOT NULL DEFAULT '',
  `cfos_type` varchar(20) NOT NULL DEFAULT '',
  `cfos_amttype` varchar(20) NOT NULL DEFAULT '',
  `cfos_amt` decimal(14,2) NOT NULL DEFAULT '0.00',
  `cfos_key` varchar(50) NOT NULL DEFAULT '',
  `cfos_value` varchar(50) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cfod_meta`;
CREATE TABLE `cfod_meta` (
  `cfodm_id` varchar(8) NOT NULL,
  `cfodm_revision` int NOT NULL DEFAULT '0',
  `cfodm_stopline` varchar(8) NOT NULL,
  `cfodm_type` varchar(80) NOT NULL,
  `cfodm_key` varchar(80) NOT NULL,
  `cfodm_value` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cfoi_meta`;
CREATE TABLE `cfoi_meta` (
  `cfoim_id` varchar(8) NOT NULL,
  `cfoim_revision` int NOT NULL DEFAULT '0',
  `cfoim_stopline` varchar(8) NOT NULL,
  `cfoim_itemline` varchar(8) NOT NULL,
  `cfoim_type` varchar(80) NOT NULL,
  `cfoim_key` varchar(80) NOT NULL,
  `cfoim_value` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `drv_meta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drv_meta` (
  `drvm_id` varchar(30) NOT NULL DEFAULT '',
  `drvm_type` varchar(80) NOT NULL DEFAULT '',
  `drvm_key` varchar(80) NOT NULL DEFAULT '',
  `drvm_value` varchar(80) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `drv_mstr`;
CREATE TABLE `drv_mstr` (
  `drv_id` varchar(20) NOT NULL DEFAULT '',
  `drv_status` varchar(20) NOT NULL DEFAULT '',
  `drv_lname` varchar(80) NOT NULL DEFAULT '',
  `drv_fname` varchar(80) NOT NULL DEFAULT '',
  `drv_line1` varchar(50) NOT NULL DEFAULT '',
  `drv_line2` varchar(50) NOT NULL DEFAULT '',
  `drv_city` varchar(50) NOT NULL DEFAULT '',
  `drv_state` varchar(50) NOT NULL DEFAULT '',
  `drv_zip` varchar(50) NOT NULL DEFAULT '',
  `drv_country` varchar(3) NOT NULL DEFAULT '',
  `drv_phone` varchar(15) NOT NULL DEFAULT '',
  `drv_email` varchar(100) NOT NULL DEFAULT '',
  `drv_type` varchar(10) NOT NULL DEFAULT '',
  `drv_ap_acct` varchar(12) NOT NULL DEFAULT '',
  `drv_ap_cc` varchar(4) NOT NULL DEFAULT '',
  `drv_terms` varchar(30) NOT NULL DEFAULT '',
  `drv_certificate` varchar(30) NOT NULL DEFAULT '',
  `drv_licensenbr` varchar(20) NOT NULL DEFAULT '',
  `drv_licenseexpire` varchar(8) NOT NULL DEFAULT '',
  `drv_insurancenbr` varchar(80) NOT NULL DEFAULT '',
  `drv_insuranceexpire` varchar(8) NOT NULL DEFAULT '',
  `drv_insurancecarrier` varchar(80) NOT NULL DEFAULT '',
  `drv_dhmiles` int NOT NULL DEFAULT '0',
  `drv_rmks` varchar(200) NOT NULL DEFAULT '',
  `drv_payrate` decimal(12,5) NOT NULL DEFAULT '0.00000',
  `drv_paytype` varchar(12) NOT NULL DEFAULT '',
  `drv_hiredate` varchar(8) NOT NULL DEFAULT '',
  `drv_termdate` varchar(8) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `edi_xcode`;
CREATE TABLE `edi_xcode` (
  `edix_doctype` varchar(8) NOT NULL DEFAULT '',
  `edix_seg` varchar(20) NOT NULL DEFAULT '',
  `edix_ele` varchar(3) NOT NULL DEFAULT '',
  `edix_code` varchar(30) NOT NULL DEFAULT '',
  `edix_value` varchar(80) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `emp_calendar`;
CREATE TABLE `emp_calendar` (
  `empc_nbr` int NOT NULL,
  `empc_year` int NOT NULL DEFAULT '0',
  `empc_type` varchar(50) NOT NULL DEFAULT '',
  `empc_date` date DEFAULT NULL,
  `empc_notes` varchar(200) NOT NULL DEFAULT '',
  `empc_varchar` varchar(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `frt_ctrl`;
CREATE TABLE `frt_ctrl` (
  `frtc_function` tinyint(1) NOT NULL DEFAULT '0',
  `frtc_export990` tinyint(1) NOT NULL DEFAULT '0',
  `frtc_varchar` varchar(50) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `makemodel`;
CREATE TABLE `makemodel` (
  `make` varchar(100) NOT NULL DEFAULT '',
  `model` varchar(100) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `time_zone`;
CREATE TABLE `time_zone` (
  `tz_code` varchar(12) NOT NULL DEFAULT '',
  `tz_desc` varchar(200) NOT NULL DEFAULT '',
  `tz_utc` varchar(30) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `veh_meta`;
CREATE TABLE `veh_meta` (
  `vehm_id` varchar(30) NOT NULL DEFAULT '',
  `vehm_type` varchar(80) NOT NULL DEFAULT '',
  `vehm_key` varchar(80) NOT NULL DEFAULT '',
  `vehm_value` varchar(80) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `veh_mstr`;
CREATE TABLE `veh_mstr` (
  `veh_id` varchar(30) NOT NULL DEFAULT '',
  `veh_desc` varchar(100) NOT NULL DEFAULT '',
  `veh_type` varchar(80) NOT NULL DEFAULT '',
  `veh_subtype` varchar(80) NOT NULL DEFAULT '',
  `veh_status` varchar(80) NOT NULL DEFAULT '',
  `veh_make` varchar(80) NOT NULL DEFAULT '',
  `veh_model` varchar(80) NOT NULL DEFAULT '',
  `veh_submodel` varchar(80) NOT NULL DEFAULT '',
  `veh_engine` varchar(80) NOT NULL DEFAULT '',
  `veh_fueltype` varchar(80) NOT NULL DEFAULT '',
  `veh_year` varchar(4) NOT NULL DEFAULT '',
  `veh_vin` varchar(80) NOT NULL DEFAULT '',
  `veh_rmks` varchar(200) NOT NULL DEFAULT '',
  `veh_servicedate` varchar(8) NOT NULL DEFAULT '',
  `veh_servicefreqdays` int NOT NULL DEFAULT '0',
  `veh_servicefreqmiles` int NOT NULL DEFAULT '0',
  `veh_odometer` int NOT NULL DEFAULT '0',
  `veh_odometerdate` varchar(8) NOT NULL DEFAULT '',
  `veh_regnbr` varchar(20) NOT NULL DEFAULT '',
  `veh_regdate` varchar(8) NOT NULL DEFAULT '',
  `veh_regtax` decimal(14,2) NOT NULL DEFAULT '0.00',
  `veh_regstate` varchar(20) NOT NULL DEFAULT '',
  `veh_weight` int NOT NULL DEFAULT '0',
  `veh_condition` varchar(30) NOT NULL DEFAULT '',
  `veh_loc` varchar(30) NOT NULL DEFAULT '',
  `veh_misc1` varchar(50) NOT NULL DEFAULT '',
  `veh_misc2` varchar(50) NOT NULL DEFAULT '',
  `veh_misc3` varchar(50) NOT NULL DEFAULT '',
  `veh_inspectdate` varchar(8) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `quo_mstr` (
  `quo_nbr` varchar(8) NOT NULL PRIMARY KEY,
  `quo_cust` varchar(30)  NOT NULL DEFAULT '',
  `quo_ship` varchar(8) NOT NULL  DEFAULT '',
  `quo_site` varchar(10) NOT NULL  DEFAULT '',
  `quo_date` date DEFAULT NULL,
  `quo_expire` date DEFAULT NULL,
  `quo_priceexpire` date DEFAULT NULL,
  `quo_status` varchar(20) NOT NULL  DEFAULT '',
  `quo_rmks` varchar(200) NOT NULL  DEFAULT '',
  `quo_ref` varchar(50) NOT NULL  DEFAULT '',
  `quo_type` varchar(30) NOT NULL  DEFAULT '',
  `quo_taxcode` varchar(30) NOT NULL  DEFAULT '',
  `quo_disccode` varchar(30) NOT NULL  DEFAULT '',
  `quo_groupcode` varchar(30) NOT NULL  DEFAULT '',
  `quo_curr` varchar(30) NOT NULL  DEFAULT '',
  `quo_approved` tinyint(1) NOT NULL  DEFAULT '0',
  `quo_approver` varchar(50) NOT NULL  DEFAULT '',
  `quo_varchar` varchar(80) NOT NULL  DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `quo_det` (
  `quod_nbr` varchar(8) NOT NULL PRIMARY KEY,
  `quod_line` int(6)  NOT NULL DEFAULT '0',
  `quod_item` varchar(100)  NOT NULL DEFAULT '',
  `quod_isinv` tinyint(1) NOT NULL  DEFAULT '0',
  `quod_desc` varchar(100)  NOT NULL DEFAULT '',
  `quod_pricetype` varchar(20)  NOT NULL DEFAULT '',
  `quod_listprice` decimal(14,5) NOT NULL  DEFAULT '0',
  `quod_disc` decimal(14,5) NOT NULL  DEFAULT '0',
  `quod_netprice` decimal(14,5) NOT NULL  DEFAULT '0',
  `quod_qty` decimal(14,5) NOT NULL  DEFAULT '0',
  `quod_uom` varchar(3)  NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `quo_voldet` (
  `quov_nbr` varchar(8) NOT NULL PRIMARY KEY,
  `quov_line` int(6)  NOT NULL DEFAULT '0',
  `quov_item` varchar(100)  NOT NULL DEFAULT '',
  `quov_volqty` decimal(14,5) NOT NULL  DEFAULT '0',
  `quov_volprice` decimal(14,5) NOT NULL  DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `quo_meta` (
  `quom_nbr` varchar(8) NOT NULL DEFAULT '',
  `quom_type` varchar(80) NOT NULL DEFAULT '',
  `quom_key` varchar(80) NOT NULL DEFAULT '',
  `quom_value` varchar(80) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `quo_sac` (
  `quos_nbr` varchar(8) NOT NULL,
  `quos_desc` varchar(20)  NOT NULL DEFAULT '',
  `quos_type` varchar(20) NOT NULL DEFAULT '',
  `quos_amttype` varchar(20) NOT NULL DEFAULT '',
  `quos_amt` decimal(14,5) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


/* now drop tables that are no longer used */
DROP TABLE IF EXISTS `fo_mstr`;
DROP TABLE IF EXISTS `fod_det`;
DROP TABLE IF EXISTS `fot_det`;


/* alter field definitions for changes */
alter table car_meta modify carm_id varchar(30) NOT NULL DEFAULT '';
/* alter table car_meta modify carm_type varchar(80) NOT NULL DEFAULT ''; */
alter table car_meta modify carm_key varchar(80) NOT NULL DEFAULT '';
alter table cms_det modify cms_type varchar(30) NOT NULL DEFAULT '';
alter table cpr_mstr modify cpr_volqty decimal(14,5) NOT NULL DEFAULT '0';
alter table dod_mstr modify dod_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table eeo_mstr modify eeo_qty_prod decimal(14,5) NOT NULL DEFAULT '0';
alter table eeo_mstr modify eeo_qty_scrap decimal(14,5) NOT NULL DEFAULT '0';
alter table in_mstr modify in_qoh decimal(14,5) NOT NULL DEFAULT '0';
alter table item_image modify iti_file varchar(200) NOT NULL DEFAULT '';
alter table item_mstr modify it_desc varchar(100) NOT NULL DEFAULT '';
alter table itemr_cost modify itr_op varchar(4) NOT NULL DEFAULT '';
alter table or_meta modify or_bsid varchar(30) NOT NULL DEFAULT '';
/* alter table or_meta modify or_type varchar(80) NOT NULL DEFAULT ''; */
alter table or_meta modify or_key varchar(80) NOT NULL DEFAULT '';
alter table ord_det modify ord_ord_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table ord_det modify ord_ship_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table ord_meta modify ordm_bsid varchar(30) NOT NULL DEFAULT '';
alter table ord_meta modify ordm_bsline varchar(30) NOT NULL DEFAULT '';
/* alter table ord_meta modify ordm_type varchar(80) NOT NULL DEFAULT ''; */
alter table ord_meta modify ordm_key varchar(80) NOT NULL DEFAULT '';
alter table plan_mstr modify plan_qty_req decimal(14,5) NOT NULL DEFAULT '0';
alter table plan_mstr modify plan_qty_comp decimal(14,5) NOT NULL DEFAULT '0';
alter table plan_mstr modify plan_qty_sched decimal(14,5) NOT NULL DEFAULT '0';
alter table pland_mstr modify pland_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table pod_mstr modify pod_ord_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table pod_mstr modify pod_rcvd_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table pos_det modify posd_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table pos_mstr modify pos_totqty decimal(14,5) NOT NULL DEFAULT '0';
alter table qual_mstr modify qual_date_crt varchar(10) NOT NULL DEFAULT '';
alter table qual_mstr modify qual_date_upd varchar(10) NOT NULL DEFAULT '';
alter table qual_mstr modify qual_date_cls varchar(10) NOT NULL DEFAULT '';
alter table req_det modify reqd_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table req_mstr modify req_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table ship_det modify shd_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table ship_det modify shd_cumqty decimal(14,5) NOT NULL DEFAULT '0';
alter table ship_tree modify ship_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table sod_det modify sod_ord_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table sod_det modify sod_shipped_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table sod_det modify sod_all_qty decimal(14,5) NOT NULL DEFAULT '0';
alter table sod_det modify sod_ship varchar(8) NOT NULL DEFAULT '';
alter table sos_det modify sos_desc varchar(80) NOT NULL DEFAULT '';
alter table srl_mstr modify srl_qtyord decimal(14,5) NOT NULL DEFAULT '0';
alter table srl_mstr modify srl_qtyshp decimal(14,5) NOT NULL DEFAULT '0';
alter table svd_det modify svd_qty decimal(14,5) NOT NULL DEFAULT '0';

/* add missing fields */
alter table dfs_mstr add column dfs_delimiter int NOT NULL default '0';
alter table dfs_mstr add column dfs_misc varchar(50) NOT NULL DEFAULT '';
alter table gl_ctrl add column gl_varchar varchar(50) NOT NULL DEFAULT '';
alter table gl_tran drop column glc_varchar;
alter table pks_mstr add column pks_standard varchar(50) NOT NULL DEFAULT '';
alter table pks_mstr add column pks_external tinyint(1) NOT NULL DEFAULT '0';
alter table pks_mstr add column pks_keyid varchar(100) NOT NULL DEFAULT '';
alter table po_mstr add column po_entrytype varchar(10) NOT NULL DEFAULT '';
alter table ship_mstr add column sh_shipfrom varchar(8) NOT NULL DEFAULT '';
alter table ship_mstr add column sh_duedate date DEFAULT NULL;
alter table as2_mstr add column as2_contenttype varchar(70) NOT NULL DEFAULT '';
alter table car_meta add column carm_type varchar(70) NOT NULL DEFAULT '';
alter table or_meta add column or_type varchar(80) NOT NULL DEFAULT '';
alter table ord_meta add column ordm_type varchar(80) NOT NULL DEFAULT '';
alter table cpr_mstr add column cpr_expire date DEFAULT NULL;
alter table vpr_mstr add column vpr_expire date DEFAULT NULL;


/* reset master table records */
delete from panel_mstr;
delete from menu_tree;
delete from menu_mstr;
delete from ov_ctrl;

load data local infile 'patches/bs67/panelmstr.csv' replace into table panel_mstr fields terminated by ',' ignore 0 Lines; show errors; 
load data local infile 'patches/bs67/menumstr.csv' replace into table menu_mstr fields terminated by ',' ignore 0 Lines; show errors; 
load data local infile 'patches/bs67/menutree.csv' replace into table menu_tree fields terminated by ',' ignore 0 Lines; show errors; 
load data local infile 'patches/bs67/ovctrl.csv' replace into table ov_ctrl fields terminated by ',' ignore 0 Lines; show errors; 

