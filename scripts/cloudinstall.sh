#!/usr/bin/bash

set -uo pipefail

LOG_FILE=cloudinstall.log
NO_APT_UPDATE="0"
NO_P12="0"
KEYNAME="bscert"
KEYPASS="placeholder"

log() {
echo "$(date '+%Y%m%d%H%M%S') -> $1" |tee -a "$LOG_FILE"
}

installMySQL() {

if [ "$NO_APT_UPDATE" == "0" ]; then
  aptupdate
fi

log "step:  checking for mysql installation"
if command -v mysql &> /dev/null
then
	echo "mysql is installed."
        mysql --version
else
	echo "mysql is not installed...attempting to install non-interactively"
#	log "step:  attempting to wget latest mysql-apt-config and install"
#	wget https://dev.mysql.com/get/mysql-apt-config_0.8.39-1_all.deb
#	dpkg -i mysql-apt-config_0.8.39-1_all.deb
        log "step:  attempting to silently install mysql with entered mysql admin passwd applied"
	debconf-set-selections <<< "mysql-server mysql-server/root_password password $mysqlpasswd"
	debconf-set-selections <<< "mysql-server mysql-server/root_password_again password $mysqlpasswd"
     	DEBIAN_FRONTEND=noninteractive apt install -y mysql-server
        systemctl enable mysql
        systemctl start mysql
	mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '$mysqlpasswd'; FLUSH PRIVILEGES;"
fi
}

aptupdate() {
### apt update
if apt update -y >>"$LOG_FILE" 2>&1; then
 log "apt update completed successfully"
else
 log "apt update failed...check $LOG_FILE for details"
 exit 1
fi
}

createP12() {
### create .p12 with keypair for TLS remote call
log "step:  creating .p12 with keypair"
read -r -p "Enter keystore/key passwd: " KEYPASS
if [[ -z "$KEYPASS" || "$KEYPASS" == "placeholder" ]]; then
   echo "Error...must enter keystore/key passwd" >&2
   exit 1
fi
ssh-keygen -t rsa -b 2048 -m PEM -f "$KEYNAME" -N "$KEYPASS"

log "sleeping 10 secs to generate key with ssh-keygen..."
sleep 10


log "now attempting to create .p12 from key"
openssl req -new -x509 -key "$KEYNAME" -out "$KEYNAME.crt" -days 365 \
-subj "/C=US/ST=somestate/L=somecity/O=someorg/CN=localhost" \
-passin pass:"$KEYPASS"
openssl pkcs12 -export -out "$KEYNAME.p12" \
-inkey "$KEYNAME" \
-in "$KEYNAME.crt" \
-name "$KEYNAME" \
-password pass:"$KEYPASS" \
-passin pass:"$KEYPASS"

log "sleeping 5 secs after openssl command issued to generate .p12..."
sleep 5 


if [[ -f "$KEYNAME.p12" ]]; then
log "p12 file was created...$kEYNAME.p12 ...copying to /opt/blueseer/conf"
cp $KEYNAME.p12 /opt/blueseer/conf/
cp $KEYNAME.pub /opt/blueseer/conf/
cp $KEYNAME.crt /opt/blueseer/conf/
rm $KEYNAME.p12
rm $KEYNAME
rm $KEYNAME.pub
rm $KEYNAME.crt
else
log "p12 was not created...must create manually"
fi

}


### entry of script
if [[ $UID -ne 0 ]]; then
echo "Error...script must be executed as sudo $0"
exit 1
fi

for arg in "$@"; do
    if [ "$arg" == "noapt" ]; then
        NO_APT_UPDATE="1"
    fi
    if [ "$arg" == "nop12" ]; then
        NO_P12="1"
    fi
done


read -r -p "Enter mysql admin passwd: " mysqlpasswd
if [[ -z "$mysqlpasswd" ]]; then
   echo "Error...must enter mysql admin passwd" >&2
   exit 1
fi

read -r -p "Enter client side access IP: " clientsideip
if [[ -z "$clientsideip" ]]; then
   echo "Error...must enter client side IP" >&2
   exit 1
fi

echo "Choose the install option number (1 = BlueSeer, 2 = MySQL + BlueSeer, 3 = MySQL Only) "
read -r -p "Enter choice number (default=1): " choice

if [[ "$choice" == "2" ]]; then
installMySQL
fi

if [[ "$choice" == "3" ]]; then
installMySQL
exit 0
fi


### add unzip if not exists
if apt install -y unzip  >>"$LOG_FILE" 2>&1; then
 log "apt install unzip package installed successfully"
else
 log "apt install unzip failed...check $LOG_FILE for details"
 exit 1
fi


### create .p12 with keypair for TLS remote call
if [ "$NO_P12" == "0" ]; then
  createP12
fi


log "step:  download blueseer server"
wget https://github.com/blueseerERP/blueseer/releases/download/v8.0/blueseer.generic.linux.v8.0.zip

log "step:  unzip into /opt/blueseer"
unzip -o blueseer.generic.linux.v8.0.zip -d /opt/blueseer &
PID=$!
wait $PID

log "step:  run blueseer mysql install script"
cd /opt/blueseer
./mysql_install.sh


log "step:  update relevant tables for access"
mysql -u root -p$mysqlpasswd bsdb -e "insert into usr_meta values ('access', 'ip', 'admin', '$clientsideip','');"
mysql -u root -p$mysqlpasswd bsdb -e "update ov_mstr set ov_currency = 'USD';"
mysql -u root -p$mysqlpasswd bsdb -e "update user_mstr set user_passwd = '' where  user_id = 'admin';"


log "creating web.properties file"
echo "keystore=/opt/blueseer/conf/$KEYNAME.p12" >/opt/blueseer/conf/web.properties
echo "storepass=$KEYPASS" >>/opt/blueseer/conf/web.properties
echo "keypass=$KEYPASS" >>/opt/blueseer/conf/web.properties



log "step:  copy service file to systemd"
cp /opt/blueseer/bsapi.service /usr/lib/systemd/system/
chmod 700 /opt/blueseer/bsapi.service.sh
chmod 644 /usr/lib/systemd/system/bsapi.service
systemctl daemon-reload
systemctl start bsapi.service
systemctl enable bsapi.service



log "script is complete"
