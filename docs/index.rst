.. This work is licensed under a Creative Commons Attribution 4.0 International License.

=============================
Sparky - Inventory UI Service
=============================

Architecture
============
Sparky a service that interacts with AAI and provides users with a user interface to view and analyze AAI data. The main goal behind Sparky is to provide a clear and user friendly view of AAI data.

It is divided into both a front-end (the code that constructs the GUI) and a back-end (the Java code and other technologies that provide the front-end with its data). When Sparky is to be deployed, a .war file containing the front-end needs to be copied into the ``/src/main/resources/extApps`` directory. The back-end will then use this .war to present the front-end to users.

At this time, Sparky has two views available for use:

.. toctree::
   :maxdepth: 1

   Graph-based view of entities within AAI <./view_inspect>
   Aggregation-based view of VNFs within AAI VNFs <./vnfs>

Interactions
------------
Sparky requires connections to the following additional services:

Front-end:

- A Sparky back-end to serve the front-end

Back-end:

- An AAI instance as the main driver behind data
- An Elasticsearch instance for data storage (a Synapse service instance is an implicit dependency which populates the Elasticsearch indexes)
- A Search Data Service instance for search functionality
- An eCOMP Portal instance for authentication

Logging
=======
Sparky uses the Logback framework to generate logs. The logback.xml file can be found under the ``src/main/resources/`` folder

Installation
============

Steps: Back-end
---------------

Clone Git Repository
********************
Clone the Sparky back-end Git repository

Build
*****

After cloning the project, build the project by executing the following Maven command from the project's top level directory:

.. code-block:: bash

   mvn clean install

After a successful install, build the docker image:

.. code-block:: bash

   docker build -t openecomp/sparky target

Deploy
******

Push the Docker image that you have built to your Docker repository and pull it down to the location that you will be running Sparky.

Create the following directories on the host machine:

- /logs
- /opt/app/sparky/appconfig

You will be mounting these as data volumes when you start the Docker container.

Clone Configuration Repository
******************************

Clone the "test-config" repo to a seperate directory.
Navigate to ``[test-config repo location]/sparky/appconfig`` (will contain files such as ``aai.properties``).

Copy the entire contents of ``[test-config repo location]]/sparky/appconfig`` into the ``/opt/app/sparky/appconfig`` directory you created in an above step.

Steps: Front-end
----------------

Clone Git Repository
********************
Clone the ``sparky-fe.git`` Sparky back-end Git repository

Install Required Tools
**********************
You will need to install the following tools:

- node.js, including the Node Package Manager (NPM) (if there issues installing the latest version, try 6.10.1)
- Python 2.7.13

After installing node.js and NPM, you need to install the required node.js packages by executing:

.. code-block:: bash

 npm install

Build
*****

**To build the front-end (generate a .war file)**:

Execute:

.. code-block:: bash

 gulp build

The build will create a directory called ``dist`` and add the ``aai.war`` file to it.

If changes to the build flow are required, updating ``webpack.config.js`` and ``gulpfile.js`` will likely provide any build tuning that is required.

**To run the front-end:**

Execute:

.. code-block:: bash

 npm start

By default the local instance of the UI will be served to ``https://localhost:8001/aai/#/viewInspect``.

This can be configured in the file ``webpack.devConfig.js``.

Deploy
******

Push the Docker image that you have built to your Docker repository and pull it down to the location that you will be running Sparky.

**Create the following directories on the host machine:**

- /logs
- /opt/app/sparky/appconfig

You will be mounting these as data volumes when you start the Docker container.

Configuration
=============

Steps: Back-end
---------------

Edit property files in /opt/app/sparky/appconfig
************************************************

Listed below are the values that will need to be updated to make Sparky operate properly. The configuration files contain comments for contents not listed here.

**search-service.properties:**

search-service.ipAddress=*[ip address / hostname of the search-data-service that this instance will use]*
search-service.httpPort=[http port of the search-data-service that this instance will use]

**aai.properties:**

aai.rest.host= *[ip address / hostname of the aai that this instance will use]*

aai.rest.port= *[rest port of the aai that this instance will use]*

**elasticsearch.properties:**

elasticsearch.ipAddress= *[ip address / hostname of the elasticsearch that this instance will use*]
elasticsearch.httpPort=*[http port of the elasticsearch that this instance will use*]
elasticsearch.javaApiPort=*[java api port of the elasticsearch that this instance will use*]

**portal/portal.properties:**
**portal/portal-authentication.properties:**

If this instance of Sparky will be served in an eCOMP Portal instance, use the two files above to configure against the proper Portal instance.
