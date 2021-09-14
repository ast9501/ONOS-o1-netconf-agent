# ONOS Agent
Agent application used to register to DMaaP topics.

## Purpose
O-DU send PNF registration signal to ves-collector, then ves-collector will push O-DU message to DMaaP topic.
Finally, other subscriber can get O-DU registraion event from DMaaP.

### start up SMO(OAM) solution (part)
* start up DMaaP and ves-collector
```
docker-compose -f smo/common/docker-compose.yml up -d
docker-compose -f smo/oam/docker-compose.yml up -d
```

### start up ONOS

```
docker run -t -d -p 8181:8181 -p 8101:8101 -p 5005:5005 -p 830:830 --name onos --network oam onosproject/onos:2.6.0
```

### start up Django server
* (Optional) Use virtualenv
* install requirements
```
pip3 install -r requirements.txt
```

* modify host ip
under onosAgent/register/views.py line 19, 37, 52

* start up Django server
```
python3 onosAgent/manage.py runserver
```

### start up O-DU/O-RU
* start up netconf-o1-interface soultion (provided from NTUST)
```
git clone http://192.168.0.185:3000/alan/netconf-o1-interface.git
cd netconf-o1-interface/client/tests
```
make sure O-DU/O-RU are under same docker network
```
# edit .env file for setup ves-collector related setting
nano .env
```
the example .env file as follow:
```
#DOCKER_REPO=nexus3.o-ran-sc.org:10002/o-ran-sc/
DOCKER_REPO=o-ran-sc/
NTS_MANAGER_PORT=8300
NTS_BUILD_VERSION=1.3.2 #1.3.3

IPv6_ENABLED=true
SSH_CONNECTIONS=1
TLS_CONNECTIONS=0
# NTS_HOST_IP=10.20.11.136
NTS_HOST_IP=2001:db8:1::1
NTS_HOST_BASE_PORT=50000
NTS_HOST_NETCONF_SSH_BASE_PORT=50000
NTS_HOST_NETCONF_TLS_BASE_PORT=52000
NTS_HOST_TRANSFER_FTP_BASE_PORT=54000
NTS_HOST_TRANSFER_SFTP_BASE_PORT=56000
NTS_BUILD_DATE=2021-05-01T08:20:54.9Z

NTS_NF_MOUNT_POINT_ADDRESSING_METHOD=docker-mapping
NTS_NF_STANDALONE_START_FEATURES="datastore-populate ves-heartbeat ves-file-ready ves-pnf-registration netconf-call-home web-cut-through"

SDN_CONTROLLER_PROTOCOL=http
SDN_CONTROLLER_IP= 192.168.0.110 # Modify here
SDN_CONTROLLER_PORT=8181
#SDN_CONTROLLER_CALLHOME_PORT=6666
SDN_CONTROLLER_USERNAME=onos
SDN_CONTROLLER_PASSWORD=rocks

#VES_COMMON_HEADER_VERSION=7.1
#VES_ENDPOINT_PROTOCOL=http
#VES_ENDPOINT_IP=172.40.0.90
#VES_ENDPOINT_PORT=8080

# Modify VES setting to fit ves-collector that SMO use
VES_COMMON_HEADER_VERSION=7.2.1
VES_ENDPOINT_PROTOCOL=https
VES_ENDPOINT_PORT=8443
VES_ENDPOINT_IP=172.21.0.2 # Modify here
VES_ENDPOINT_AUTH_METHOD=no-auth
VES_ENDPOINT_USERNAME=sample1
VES_ENDPOINT_PASSWORD=sample1

```
* network setting
modify docker-compose file to make its default docker network as same as SMO and ONOS
```
# edit
nano docker-compose.yml

# add following line at the buttom of file
networks:
    default:
        external:
            name: oam
```

Before start up the solution, make api call first:
```
curl localhost:8000/api/v1/registration
```
other api call:
```
# test Django server status
curl localhost:8000/api/v1/status
# get test data (should manually create DMaaP topic and publish message on topic)
curl localhost:8000/api/v1/testData
```

* start up the simulation
```
docker-compose -f docker-compose.yml up -d
```

## Known issue
### SDNC-web
