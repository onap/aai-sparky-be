.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Installation and Developer Setup
================================

Project Structure
-----------------

Sparky is structured with a top level project that contains two sub-projects (application and service projects). The application project contains the configuration and spring application code that customizes and runs sparky as an application. The service project contains the core sparky code that provides functionality for sparky-fe requests and synchronization.

In regards to the front-end (sparky-fe), sparky-be serves up the sparky-fe web content and sparky-be deals with all inter-microservice communications.

Clone, Build, & Configure
=========================

Clone & Build
-------------

Clone the sparky-be repository into a directory of your choice.

.. code-block:: bash

    git clone ssh://<username>@gerrit.onap.org:29418/aai/sparky-be

After cloning the project (sparky-be), build the project by executing the following Maven command from the project's top level directory:

.. code-block:: bash

   mvn clean install

Configuration
-------------

All configuration for running sparky is found in ``<working directory>/sparkybe-onap-application/config``.

Profiles
--------

*application.properties* is the main configuration point for the sparky application. Within this file the *spring.profiles* are set. Each spring profile has configuration that will be loaded into an associated spring bean. The currently available profiles:

  * camel
  * http | ssl
  * portal
  * fe-dev | fe-prod
  * oxm-default | oxm-override
  * resources | gizmo
  * sync
  * oxm-schema-dev | oxm-schema-prod

Profile descriptions:

  * camel - Enables spring-boot camel routing rules
  * http - Sets Sparky's communication protocol to HTTP
  * ssl - Sets Sparky's communication protocol to HTTPS
  * portal - Adds ONAP portal processing to Sparky's flow
  * fe-dev - Exposes the static folder for UI development when running Sparky locally (target/static)
  * fe-prod - Exposes the standard path for the UI in the docker container
  * oxm-default - Sets the default version and version list of OXM files to be used
  * oxm-override - Sets a custom version and version list of OXM files to be used
  * resources - Sparky will use aai-resources (microservice) as the primary source of inventory information
  * gizmo - Sparky will use gizmo (microservice) as the primary source of inventory information
  * sync - Will cause Sparky to run any configured synchronizers to populate index data in a single large transaction
  * oxm-schema-dev - Sets the location to find the OXM files within a development environment
  * oxm-schema-prod - Sets the location to find the OXM files within a deployed environment

The idea behind the profiles is to create a simple approach to adjusting runtime behavior without needing to edit large xml files (see **Spring Beans** below). Ahead of running Sparky, some of the profiles will need to be edited to work within your environment (e.g. set where your custom OXM files need to be loaded from).

Spring Beans
------------

The *spring-beans* directory contains all the .xml bean representations that will be auto-wired at runtime. Some of the beans are associated with a single profile (see "profile=" in header of bean declaration), and others will be loaded with differing values depending the profile used.

Scanning through the beans and cross-referencing with their associated Java classes is a good way of getting familiar with the startup and runtime of Sparky.

Authorization
-------------

Within the "auth" directory are any certs needed to communicate within the environment you are currently working in (remember Sparky can be configured to run using HTTP).

Filters
-------

The "filters" directory contains the JSON descriptor files that describe the filters used in the VNFs view.

Logging
-------

Sparky uses the Logback framework to generate logs. The logback.xml is contained in the "logging" directory.

Running Locally
===============

The configuration described in this section will be in reference to running Sparky through Eclipse. The same steps can be applied to running via bash/cmd with minor tweaks.

Sparky should be built ahead of running (``mvn clean install``). It's useful to add a build configuration to Eclipse to build Sparky.

The run configuration should contain the following:

* The configuration should be created based off of the "Maven Build" template
* "Main" tab
    * Build directory - ${workspace_loc:/sparky-be/sparkybe-onap-application}
    * Goals - spring-boot:run
    * Parameter table
        * name: CONFIG_HOME value: ${workspace_loc:/sparky-be/sparkybe-onap-application}/config
        * name: APP_HOME value: ${workspace_loc:/sparky-be/sparkybe-onap-application}

Deploying Sparky
================

At time of writing (Oct 2018) Sparky is primarily deployed into a Kubernetes environment or a "pure" docker environment using custom chef parametrization. How you want to deploy Sparky is up to you. At a high level, the cleanest approach is ensuring your configured property (profiles) files are copied into the docker container so the Spring context has access to the values which will in turn start Sparky using your configured values.

See ``sparky-be/sparkybe-onap-application/src/main/docker`` -> Dockerfile for details on how Sparky runs within a Docker container.

Front-End (sparky-fe) Details
=============================

Clone, Build, & Configure
-------------------------

Clone the sparky-fe repository into a directory of your choice.

Dependencies
------------
You will need to install the following tools:

* node.js, including the Node Package Manager (NPM) (if there issues installing the latest version, try 6.10.1)
* Python 2.7.13

After installing node.js and NPM, you need to install the required node.js packages by navigating to the top level sparky-fe directory and executing:

.. code-block:: bash

 npm install

Build
-----

To build sparky-fe (generate a .war file):

Execute:

.. code-block:: bash

 gulp build

The build will create a directory called ``dist`` and add the ``aai.war`` file to it.

If changes to the build flow are required, updating ``webpack.config.js`` and ``gulpfile.js`` will likely provide any build tuning that is required.

Running sparky-fe Locally
=========================

Execute:

.. code-block:: bash

 npm start

By default the local instance of the UI will be served to ``http(s)://localhost:8001/``.

Deploy sparky-fe
================

If you have access to a container repository (e.g. Nexus), push the .war image that you have built to your repository and configure your sparky-be ``sparkybe-onap-application/pom.xml`` to pull your sparky-fe image.
