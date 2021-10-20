# nctu.winlab.o1-netconf-agent-app
## Environment required
* ONOS
```
2.6.0
```

* java
```
alan@ONOS:~/work/onos-agent$ java -version
java version "1.8.0_301"
Java(TM) SE Runtime Environment (build 1.8.0_301-b09)
Java HotSpot(TM) 64-Bit Server VM (build 25.301-b09, mixed mode)
```

* Maven
```
alan@ONOS:~/work/onos-agent$ mvn -version
Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 11.0.11, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.11.0-37-generic", arch: "amd64", family: "unix"
```

* Linux
```
Ubuntu 20.04
```

## usage
* install app
```
onos-app localhost install! target/o1-netconf-agent-app-1.0-SNAPSHOT.oar
```

Some onos-app command:
* new app
```
# indecate version of ONOS api
export ONOS_POM_VERSION=2.6.0

# Build the current version of ONOS application archetypes
cd $ONOS_ROOT/tools/package/archetypes
mvn clean install -DskipTests

# create new app
onos-create-app
```

* compile
```
mvn clean install -DskipTests
```

## Feature log
* Using external package, Jackson, for processing data with json format
* Get "single" pnf device per PNF registration message from DMaaP
* Fill in username, password, ip, port according to PNF registration message into payload used to register device on ONOS