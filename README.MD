# Sparky - Inventory UI Service

[![alt text](https://bestpractices.coreinfrastructure.org/projects/1737/badge)](https://bestpractices.coreinfrastructure.org/projects/1737)

## Overview
_Sparky_ is a service that interacts with AAI and provides users a UI to view and analyze AAI data. The main goal behind _Sparky_ is providing a more user friendly and clear view of AAI data.

At this time, _Sparky_ has two views available for use:

[View and Inspect](./VIEW_INSPECT.md) - Graph based view of entities within AAI.

[VNFs](./VNFS.md) - Aggregation based view of VNFs within AAI.

## Interfaces

User/Browser -> _Sparky_ port 9517 (HTTP) (can be configured as port 8000 HTTPS) 
Purposes: 
 - Suggestions requests
 - data retrieval

_Sparky_ -> AAI HA proxy port 8443 (REST)
Purposes: 
 - data retrieval
 - Index synchronization
   
_Sparky_ -> Elastic Search port 9200 (REST) 
Purpose: 
 - data retrieval (VNFs queries) 

_Sparky_ -> Search data Service port 9509 (REST) 
Purpose: 
 - Data retrieval for search bar

## Getting Started

### Building _Sparky_

After cloning the project, execute the following Maven command from the project's top level directory to build the project:

    > mvn clean install

After a successful install, build the docker image:

    > docker build -t openecomp/sparky target 

### Deploying _Sparky_

Push the Docker image that you have built to your Docker repository and pull it down to the location that you will be running _Sparky_.

**Create the following directories on the host machine:**

    /logs
    /opt/app/sparky/appconfig

You will be mounting these as data volumes when you start the Docker container.

#### Clone Configuration Repo

Clone the "test-config" repo to a seperate directory.
Navigate to <test-config repo location>/sparky/appconfig (will contain files such as aaiui_filters.json and authentication files).
Copy the entire contents of <test-config repo location>/sparky/appconfig into the /opt/app/sparky/appconfig directory you created in an above step.
Copy the entire contents of <test-config repo location>/sparky/dynamic into the /opt/app/sparky/dynamic directory you created in an above step.

The *dynamic* folder contains spring-beans and camel routes.  The spring-beans must be updated with environmental configuration, to enable correct operation. 

#### Edits to property files in /opt/app/sparky/appconfig

Listed below are the values that will need to be updated to make _Sparky_ operate properly. The config files contain comments on the contents not listed here.

**sparky-core.xml**

- searchServiceRestEndpointConfig
	- endpointIpAddress ( ip address of the Search Data Service )
	- endpointServerPort ( server port of Search Data Service ) 

**sparky-core-sync.xml**
 
- elasticSearchRestEndpointConfig
    - endpointIpAddress (ElasticSearch server ip address)
    - endpointServerPort (ElasticSearch transport server port.  Default: 9200)

- aaiRestEndpointConfig
    - endpointIpAddress (AAI server ip address)
    - endpointServerPort (AAI server port.  Default: 8443)

**portal/portal.properties**
**portal/portal-authentication.properties**

If this instance of _Sparky_ will be served in an ONAP Portal instance, use the two files above to configure against the proper Portal instance.

### Dependencies

_Sparky_ requires:

- AAI instance as the main driver behind data.
- Elasticsearch instance for data storage.
- Search-Data-Service instance for search functionality.
- ONAP Portal instance for authentication.