# Installation
```
apt-get -y update
apt-get -y install apt-transport-https gnupg
wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" | tee -a /etc/apt/sources.list.d/elastic-7.x.list
wget -O - https://www.bayceer.uni-bayreuth.de/repos/apt/conf/bayceer_repo.gpg.key | apt-key add -
echo "deb https://www.bayceer.uni-bayreuth.de/repos/apt/debian stretch main" | tee -a /etc/apt/sources.list.d/bayceer.list
apt-get -y update
apt-get -y install oc-search-elastic
```

