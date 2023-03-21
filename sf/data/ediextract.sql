.headers off
.mode csv
.output stdout
select "edp_partner", + " row count ", count(*) from edp_partner;
select "edpd_partner", + " row count ", count(*) from edpd_partner;
select "edi_mstr", + " row count ", count(*) from edi_mstr;
select "wkf_mstr", + " row count ", count(*) from wkf_mstr;
select "wkf_det", + " row count ", count(*) from wkf_det;
select "wkf_log", + " row count ", count(*) from wkf_log;
select "wkfd_meta", + " row count ", count(*) from wkfd_meta;
select "wkfd_log", + " row count ", count(*) from wkfd_log;
select "cron_mstr", + " row count ", count(*) from cron_mstr;
select "cron_log", + " row count ", count(*) from cron_log;
select "edi_file", + " row count ", count(*) from edi_file;
select "edi_idx", + " row count ", count(*) from edi_idx;
select "edi_log", + " row count ", count(*) from edi_log;
.output x_edp_partner.csv
select * from edp_partner;
.output x_edpd_partner.csv
select * from edpd_partner;
.output x_edi_mstr.csv
select * from edi_mstr;
.output x_wkf_mstr.csv
select * from wkf_mstr;
.output x_wkf_det.csv
select * from wkf_det;
.output x_wkfd_meta.csv
select * from wkfd_meta;
.output x_cron_mstr.csv
select * from cron_mstr;
.quit
