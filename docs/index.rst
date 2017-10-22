.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Sparky - Inventory UI Service
==============================


***************
Overview
***************
Sparky a service that interacts with AAI and provides users a UI to view and analyze AAI data. The main goal behind _Sparky_ is providing a more user friendly and clear view of AAI data.

At this time, _Sparky_ has two views available for use:

[View and Inspect](./VIEW_INSPECT.md) - Graph based view of entities within AAI.

[VNFs](./VNFS.md) - Aggregation based view of VNFs within AAI.


===============
Getting Started
===============


Building _Sparky_
-----------------
After cloning the project, execute the following Maven command from the project's top level directory to build the project:

    > mvn clean install

After a successful install, build the docker image:

    > docker build -t onap/sparky target

Deploying _Sparky_
------------------

Push the Docker image that you have built to your Docker repository and pull it down to the location that you will be running _Sparky_.

**Create the following directories on the host machine:**

    /logs
    /opt/app/sparky/appconfig

You will be mounting these as data volumes when you start the Docker container.

Clone Configuration Repo
------------------------

Clone the "test-config" repo to a seperate directory.
Navigate to <test-config repo location>/sparky/appconfig (will contain files such as aai.properties).
Copy the entire contents of <test-config repo location>/sparky/appconfig into the /opt/app/sparky/appconfig directory you created in an above step.

====================================================
Edits to property files in /opt/app/sparky/appconfig
====================================================

Listed below are the values that will need to be updated to make _Sparky_ operate properly. The config files contain comments on the contents not listed here.

**search-service.properties**

search-service.ipAddress=<ip address / hostname of the search-data-service that this instance will use>
search-service.httpPort=<http port of the search-data-service that this instance will use>

**aai.properties**

aai.rest.host=<ip address / hostname of the aai that this instance will use>
aai.rest.port=<rest port of the aai that this instance will use>

**elasticsearch.properties**

elasticsearch.ipAddress=<ip address / hostname of the elasticsearch that this instance will use>
elasticsearch.httpPort=<http port of the elasticsearch that this instance will use>
elasticsearch.javaApiPort=<java api port of the elasticsearch that this instance will use>

**portal/portal.properties**
**portal/portal-authentication.properties**

If this instance of _Sparky_ will be served in an ONAP Portal instance, use the two files above to configure against the proper Portal instance.

============
Dependencies
============
_Sparky_ requires:

- AAI instance as the main driver behind data.
- Elasticsearch instance for data storage.
- search-data-service instance for search functionality.
- ONAP Portal instance for authentication.
