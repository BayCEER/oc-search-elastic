# Installation
`apt-get install apt-transport-https gnupg`

`wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -`

`echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" | tee -a /etc/apt/sources.list.d/elastic-7.x.list`

`apt-get update`

`apt-get install oc-search-elastic`

