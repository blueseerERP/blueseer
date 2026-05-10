#!/bin/bash


if [ "$#" -lt 1 ]; then
  echo "Error: missing arguments.  Must provide passwd"
  exit 1
fi

# MySQL database credentials
DB_USER=root
DB_PASS=$1
DB_NAMES=("bsdb" "bstest") 

# Backup directory (ensure this directory exists)
BACKUP_DIR="/some/directory/bkup"

# Get current date and time for filename
DATE=$(date +"%Y%m%d_%H%M%S")


for DB_NAME in "${DB_NAMES[@]}"; do

# Create backup filename
BACKUP_FILE="$BACKUP_DIR/$DB_NAME.$DATE.sql"
echo "creating backup of $DB_NAME"

# Perform the MySQL dump
mysqldump --user=$DB_USER --password=$DB_PASS $DB_NAME --result-file $BACKUP_FILE 2>/dev/null

if [ "$?" -ne 0 ]; then
        echo "Error during mysqldump for database $DB_NAME"
else
	echo "MySQL backup of $DB_NAME completed to $BACKUP_FILE"
fi

done

# Optional: Compress the backup file
# gzip $BACKUP_FILE

# Optional: Remove old backups (e.g., older than 7 days)
# MESSG=$(find $BACKUP_DIR -maxdepth 1 -type f -name "*.sql" -mtime +7 -delete -print)
#echo "remove old backups:  $MESSG"
# find $BACKUP_DIR -maxdepth 1 -type f -name "*.sql.gz" -mtime +7 -delete

