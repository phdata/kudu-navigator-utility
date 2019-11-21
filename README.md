# Kudu Navigator Utility

Java utility to read from a Kudu Table that contains metadata information. Goes through Kudu Table record by record.  
 For each record, it:  
  - Gets the entity id for the column using the Navigator GET API  
  - Takes the Kudu record and turns it into the JSON payload  
  - Uses the Navigator PUT API to upload the metadata 


# Config Files

- config.properties file: environment property file including all env variables. This file can be placed anywhere outside the jar. Full path to the file has to be provided as an argument.  Example of file: 
```
---
navigator.url=http://server1:7187/api/v13/entities
navigator.username=xxxx
navigator.password=xxxx

```

# Getting Started

- Installation process

Git clone the repo from Bitbucket. Navigate to kudu-navigator-utility folder.

```
cd kudu-navigator-utility
```

- Software dependencies

The utility will leverage the Java Kudu-client, GSON and HTTP Client libraries. Ensure Maven is installed as it's used for build automation.  

  
```java
//Compile and Build Package
$ mvn package
```
```java
// Run the artifact
$ java -DkuduMaster=<comma-separated list of masters> -DkuduTable=<database_name.table_name> -DconfigFile=<PATH_TO_CONFIG_FILE> -jar kudu-navigator-utility-1.0-SNAPSHOT.jar
```  
  

- Usage

Use below command to run the utility on valhalla cluster .
```
java -DkuduMaster=server1,server2,server3 -DkuduTable=default.governance_metadata -DconfigFile=/home/config.properties -jar kudu-navigator-utility-1.0-SNAPSHOT.jar
```

- Validate

Use Cloudera Navigator Search to find the metadata you added.



