.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Security
========

.. contents:: Table of Contents

Roles and Permissions
---------------------

| Roles and permissions for DMaaP BC API are configured in connected AAF instance if ``UseAAF`` flag is set.
| The roles and permissions are being provisioned to AAF instance during DMaaP BC instance initialization phase only when AAF is in use.
| The default namespace in AAF for storing Bus Controller API roles and permissions is ``org.onap.dmaap-bc.api``.
| Separate permission is created for every HTTP method on each DMaaP BC REST api endpoint.
| Refer to :ref:`offeredapis` for comprehensive api information.
| Default name for DMaaP instance in ONAP is ``mr`` which is reflected in instance part of every created permission under DMaaP BC API.
| Exception of above rule is for ``/dmaap`` endpoint where additionally set of permissions for ``boot`` instance is defined:

.. code-block:: bash

    org.onap.dmaap-bc.api.dmaap|boot|DELETE
    org.onap.dmaap-bc.api.dmaap|boot|GET
    org.onap.dmaap-bc.api.dmaap|boot|POST
    org.onap.dmaap-bc.api.dmaap|boot|PUT

| These permissions are needed during DMaaP initialization phase, until real instance is configured. This set of permissions is also provided in AAF instance by default.

| DMaaP BC api permissions are distributed between several predefined roles:

.. code-block:: bash

    org.onap.dmaap-bc.api.Controller
    org.onap.dmaap-bc.api.Inventory
    org.onap.dmaap-bc.api.Metrics
    org.onap.dmaap-bc.api.Orchestrator
    org.onap.dmaap-bc.api.PortalUser

**Predefined roles brief description:**
    - **Controller** - contains all permissions to DMaaP BC REST api, and should be assigned to identities which requires full admin rights to DMaaP BC, like ``dmaap-bc`` service identity itself.
    - **Inventory** - role defined for functions which require ReadOnly access to the resources provided on DMaaP BC api.
    - **Metrics** - role designed to be used by external function which examines the counts of topics that were replicating between different MR instances. Main permission of this role is to read from DMaaP BC bridge endpoint.
    - **Orchestrator** - main role containing all permissions, which client micro-service might need. One of the example functions is ``dmaap plugin`` which is part of DCAE. The difference between this and Controller role is that Orchestrator is not responsible for deploying new k8s cluster or a message-router into that cluster, so it has limited, RO access to dmaap and dcaeLocations endpoints.
    - **PortalUser** - role designed to be used in DMaaP Bus Controller Web App, which is based on the ONAP Portal SDK. If the UI app is deployed and available in ONAP Portal, portal users which will use DMaaP BC Web App shall be assigned to this role.

Bus Controller API security options
-----------------------------------

| There are three main properties in ``dmaapbc.properties`` responsible for configuring DMaaP BC API security option.
| These are ``enableCADI, useAAF, ApiPermission.Class``. Below table describes purpose of each property:

+---------------------+------------------------------------------------------+---------------------------------------------------+
| Property            | Values                                               | Description                                       |
+=====================+======================================================+===================================================+
|enableCADI           | true/false                                           | If set to true CADI filter is enabled on          |
|                     |                                                      | BC REST api and authorization is performed        |
|                     |                                                      | through connected AAF instance.                   |
|                     |                                                      | Otherwise legacy authorization mechanism is       |
|                     |                                                      | used, which depends on api policy defined         |
|                     |                                                      | with ApiPermission.Class property setting.        |
+---------------------+------------------------------------------------------+---------------------------------------------------+
|useAAF               | true/false                                           | The purpose of this flag is to configure if       |
|                     |                                                      | specific namespaces, roles, and permissions       |
|                     |                                                      | should be created in AAF instance when            |
|                     |                                                      | calling some of DMaaP BC api endpoints.           |
|                     |                                                      | Setting it to true will cause automatic           |
|                     |                                                      | operation in AAF:                                 |
|                     |                                                      |                                                   |
|                     |                                                      | - create set of BC API permissions and assign it  |
|                     |                                                      |   to predefined roles during DMaaP instance init  |
|                     |                                                      | - create topic namespace, permissions and roles   |
|                     |                                                      |   when secure topic is created using topics       |
|                     |                                                      |   endpoint                                        |
|                     |                                                      | - assign mr client to specified role in AAF when  |
|                     |                                                      |   adding new client for the topic using           |
|                     |                                                      |   ``mr_clients`` endpoint and clientRole defined  |
|                     |                                                      |   in request                                      |
+---------------------+------------------------------------------------------+---------------------------------------------------+
|ApiPermission.Class  | - org.onap.dmaap.dbcapi.authentication.AllowAll      | when CADI filter is not in use, API security is   |
|                     | - org.onap.dmaap.dbcapi.authentication.AafLurAndFish | fulfilled with policy defined by class given in   |
|                     |                                                      | this property. Currently available options are:   |
|                     |                                                      |                                                   |
|                     |                                                      | - AllowAll - authentication and authorization is  |
|                     |                                                      |   skipped, everyone can invoke any method from BC |
|                     |                                                      |   API                                             |
|                     |                                                      | - AafLurAndFish - authentication and authorization|
|                     |                                                      |   is performed with direct call to AAF instance   |
|                     |                                                      |                                                   |
|                     |                                                      | This property allows to define custom policy,     |
|                     |                                                      | for example to external authorization system      |
|                     |                                                      | by implementing ``ApiAuthorizationCheckInterface``|
|                     |                                                      |                                                   |
+---------------------+------------------------------------------------------+---------------------------------------------------+

.. note::
   | When CADI filter is in use it caches internally authorization information for the identities calling BC api by default for 10 minutes.
   | It can have negative impact on the functions which needs to call the api several times and use newly created permissions in next call.
   | CADI cache time can be changed by setting ``aaf_user_expires`` property (value in ms) in DMaaP BC ``cadi.properties`` file.
   | However the lowest achievable cache expiration time is 1 min due to internal CADI framework logic.

**Security properties combination and its implications**

.. note::
   | DMaaP-MR references in below table are used only to describe security Use Case between DMaaP internal components.
   | To set-up DMaaP-MR security options properly, please refer DMaaP Message Router documentation.
   | Each properties combination takes effect only on DMaaP BC API security.

+-------------------------------+----------------------------------------------+--------------------------------+
| Properties combination        | Security result                              | Use Case                       |
+===============================+==============================================+================================+
| | enableCADI = true           | | AAF is in use for DMaaP-BC and DMaaP-MR    | | DMaaP-BC - secured with AAF  |
| | useAAF = true               |   can also rely on AAF.                      | | DMaaP-MR - secured with AAF  |
| | ApiPermission.Class N/A     | | CADI filter is in use, authorization data  |                                |
|                               |   caching is in use, function can authorize  |                                |
|                               |   using x509 certificate or Basic Auth.      |                                |
+-------------------------------+----------------------------------------------+--------------------------------+
| | enableCADI = true           | | AAF is not in use for resources            | | DMaaP-BC - secured with AAF  |
| | useAAF = false              |   configuration.                             | | DMaaP-MR - unsecured         |
| | ApiPermission.Class N/A     | | CADI filter is in use, authorization data  |                                |
|                               |   caching is in use, function can authorize  |                                |
|                               |   using x509 certificate or Basic Auth.      |                                |
+-------------------------------+----------------------------------------------+--------------------------------+
| | enableCADI = false          | | AAF is in use for DMaaP-BC and DMaaP-MR    | | DMaaP-BC - secured with AAF  |
| | useAAF = true               |   can also rely on AAF.                      | | DMaaP-MR - secured with AAF  |
| | ApiPermission.Class =       | | Legacy authorization is in use, no caching |                                |
|   <pckg>.AafLurAndFish        |   for authorization data, function can       |                                |
|                               |   authorize using Basic Auth only.           |                                |
+-------------------------------+----------------------------------------------+--------------------------------+
| | enableCADI = false          | | AAF is not in use for resources            | | DMaaP-BC - secured with AAF  |
| | useAAF = false              |   configuration.                             | | DMaaP-MR - unsecured         |
| | ApiPermission.Class =       | | Legacy authorization is in use, no caching |                                |
|   <pckg>.AafLurAndFish        |   for authorization data, function can       |                                |
|                               |   authorize using Basic Auth only.           |                                |
+-------------------------------+----------------------------------------------+--------------------------------+
| | enableCADI = false          | | AAF is in use for DMaaP-BC resources and   | | DMaaP-BC - unsecured         |
| | useAAF = true               |   DMaaP-MR can also rely on AAF.             | | DMaaP-MR - secured with AAF  |
| | ApiPermission.Class =       | | No authentication and authorization is     |                                |
|   <pckg>.AllowAll             |   performed on DMaaP BC REST api             |                                |
+-------------------------------+----------------------------------------------+--------------------------------+
| | enableCADI = false          | | AAF is not in use for resources            | | DMaaP-BC - unsecured         |
| | useAAF = false              |   configuration.                             | | DMaaP-MR - unsecured         |
| | ApiPermission.Class =       | | No authentication and authorization is     |                                |
|   <pckg>.AllowAll             |   performed on DMaaP BC REST api             |                                |
+-------------------------------+----------------------------------------------+--------------------------------+

SSL DMaaP Certificates and Configuration
----------------------------------------

Configuration related to ssl can be found in the ``dmaapbc.properties``.
File is located in the ``/opt/app/dmaapbc/etc`` on the dmaap-bc pod. Directory contains also truststore and keystore files used in the ssl setup.
Each change in the configuration file requires restart of the application container

.. code-block:: bash

    #
    #	Allow http access to API
    #
    HttpAllowed:	true
    #
    #	The port number for http as seen within the server
    #
    IntHttpPort:	8080
    #
    #	The port number for https as seen within the server
    #   Set to 0 if no certificate is available yet...
    #
    IntHttpsPort:	8443
    #
    #	The external port number for https taking port mapping into account
    #
    ExtHttpsPort:	443
    #
    #	The type of keystore for https
    #
    KeyStoreType:	jks
    #
    #	The path to the keystore for https
    #
    KeyStoreFile:	etc/keystore
    #
    #	The password for the https keystore
    #
    KeyStorePassword:	<keystore_password>
    #
    #	The password for the private key in the https keystore
    #
    KeyPassword:	<key_password>
    #
    #	The type of truststore for https
    #
    TrustStoreType:	jks
    #
    #	The path to the truststore for https
    #
    TrustStoreFile:	etc/org.onap.dmaap-bc.trust.jks
    #
    #	The password for the https truststore
    #
    TrustStorePassword:	<truststore_password>


AAF configuration
-----------------

Usage of AAF can be turned on/off by setting ``UseAAF`` flag to ``true/false`` in the ``dmaapbc.properties`` file. By default AAF usage is turned on.
Property ``cadi.properties`` points to absolute path of the property file generated by AAF for the DmaaP BC application (``dmaap-bc@dmaap-bc.onap.org`` user).
This file is one of the AAF configuration files enabling authentication and authorization for DMaaP BC REST API.

.. code-block:: bash

    #################
    # AAF Properties:
    UseAAF: true

    #################
    #
    # path to cadi.properties
    #
    cadi.properties: /opt/app/osaaf/local/org.onap.dmaap-bc.props


Complete AAF configuration consist of following files:
    - org.onap.dmaap-bc.props - main configuration file
    - org.onap.dmaap-bc.location.props - geographic coordinates of the application
    - org.onap.dmaap-bc.cred.props - properties related to credentials, keystore and truststore
    - org.onap.dmaap-bc.keyfile - keyfile
    - org.onap.dmaap-bc.p12 - keystore
    - org.onap.dmaap-bc.trust.jks - truststore


| All listed files are located in the ``/opt/app/dmaapbc/etc`` directory.
| File ``org.onap.dmaap-bc.props`` links together all property files by defining them in the ``cadi_prop_files`` property.
| By default all paths to other AAF related configuration points to ``/opt/app/osaaf/local/`` directory.
| This directory is default location that can be changed during generation of configuration files in the AAF application.
| In order to not duplicate mentioned files on the dmaap-bc pod following symbolic link is created in the filesystem:

.. code-block:: bash

    ln -s /opt/app/dmaapbc/etc /opt/app/osaaf/local


User configured and used in DMaaP BC
------------------------------------

dmaap-bc@dmaap-bc.onap.org
~~~~~~~~~~~~~~~~~~~~~~~~~~

It is main user for the DMaaP BC application. It has permissions to validate if user accessing DMaaP BC REST api has appropriate permissions to
perform an action.


AAF Permissions
+++++++++++++++

.. code-block:: bash

    List Permissions by User[dmaap-bc@dmaap-bc.onap.org]
    --------------------------------------------------------------------------------
    PERM Type                      Instance                       Action
    --------------------------------------------------------------------------------
    org.onap.dmaap-bc.api.access   *                              read
    org.onap.dmaap-bc.certman      local                          request,ignoreIPs,showpass
    org.onap.dmaap-dr.feed         *                              *
    org.onap.dmaap-dr.sub          *                              *
    org.onap.dmaap.mr.access       *                              *
    org.onap.dmaap.mr.topic        *                              *
    org.onap.dmaap.mr.topic        *                              view
    org.onap.dmaap.mr.topicFactory :org.onap.dmaap.mr.topic:org.onap.dmaap.mr create,destroy


dmaap-bc-topic-mgr@dmaap-bc-topic-mgr.onap.org
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When ``UseAAF`` is set to true then creating topic also will create required perms in AAF. The perms will be created in ``org.onap.dmaap.mr`` namespace.
User ``dmaap-bc-topic-mgr`` is used in the process of creating such permissions.

**Example:**
    Topic name:
        aSimpleTopic

    Permissions
        | org.onap.dmaap.mr.topic|:topic.org.onap.dmaap.mr.aSimpleTopic|pub
        | org.onap.dmaap.mr.topic|:topic.org.onap.dmaap.mr.aSimpleTopic|sub
        | org.onap.dmaap.mr.topic|:topic.org.onap.dmaap.mr.aSimpleTopic|view


AAF Permissions
+++++++++++++++

.. code-block:: bash

    List Permissions by User[dmaap-bc-topic-mgr@dmaap-bc-topic-mgr.onap.org]
    ---------------------------------------------------------------------------------------
    PERM Type                                  Instance                       Action
    ---------------------------------------------------------------------------------------
    org.onap.dmaap-dr.feed                     *                              *
    org.onap.dmaap-dr.sub                      *                              *
    org.onap.dmaap.mr.PNF_READY.access         *                              *
    org.onap.dmaap.mr.PNF_REGISTRATION.access  *                              *
    org.onap.dmaap.mr.access                   *                              *
    org.onap.dmaap.mr.dgl_ready.access         *                              *
    org.onap.dmaap.mr.mirrormaker              *                              admin
    org.onap.dmaap.mr.mirrormaker              *                              user
    org.onap.dmaap.mr.topic                    *                              view
    org.onap.dmaap.mr.topic        :topic.org.onap.dmaap.mr.mirrormakeragent pub
    org.onap.dmaap.mr.topic        :topic.org.onap.dmaap.mr.mirrormakeragent sub
    org.onap.dmaap.mr.topicFactory :org.onap.dmaap.mr.topic:org.onap.dmaap.mr create
    org.onap.dmaap.mr.topicFactory :org.onap.dmaap.mr.topic:org.onap.dmaap.mr destroy


aaf_admin@people.osaaf.org
~~~~~~~~~~~~~~~~~~~~~~~~~~

This user is used in the process of the post-installation during which appropriate namespaces and permissions are created in AAF.



