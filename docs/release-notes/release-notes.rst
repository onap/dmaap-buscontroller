.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. _release_notes:


..      ===========================

..      * * *    FRANKFURT    * * *

..      ===========================


Abstract

========


This document provides the release notes for the Frankfurt release.


Summary

=======



Release Data

============


+--------------------------------------+--------------------------------------+
| **DMaaP Bus Controller Project**     |                                      |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Docker images**                    | - dmaap-bc 2.0.4                     |
|                                      | - dbc-client 1.0.9                   |
+--------------------------------------+--------------------------------------+
| **Release designation**              | 6.0.0 frankfurt                      |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Release date**                     | 2020-05-14 (TBD)                     |
|                                      |                                      |
+--------------------------------------+--------------------------------------+


New features
------------

* Implement boolean flag in OOM to disable HTTP ports
* Dynamic cert distribution from OOM AAF


Known Limitations, Issues and Workarounds
=========================================


System Limitations
------------------


Known Vulnerabilities
---------------------

* CVE-2018-11307
* CVE-2018-12022
* CVE-2018-12023
* CVE-2019-17571
* CVE-2016-2510
* CVE-2017-18640


Workarounds
-----------



Security Notes
--------------



References
==========


For more information on the ONAP Frankfurt release, please see:


#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_



.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://wiki.onap.org
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org



Release Notes
=============

Version: 6.0.0 (Frankfurt Release)
-----------------------------------
: Release Date: 2020-05-14

Docker Image Version: 2.0.4 

**New Features**

- the core library, dbcapi, now utilizes Java 11 and new base images were required, so we consider this a major release increment.
- The dmaap-bc pod now utilizes an initContainer to acquire SSL certificates from AAF 

**Bug Fixes**
       NA

**Known Issues**
       NA

**Security Notes**
DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `Dublin <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_.

Quick Links:
- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_

**Upgrade Notes**
       NA

**Deprecation Notes**
- `Project Vulnerability Review Table for DMAAP seems to be discontinued`



Version: 1.1.5 (El Alto)
: Release Date: 2019-06-06

**New Features**

 - No new features

**Bug Fixes**
       NA

**Known Issues**
       NA

**Security Notes**
DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `Dublin <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_.

Quick Links:
- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_
- `El Alto Project Vulnerability Review Table for DMAAP <https://wiki.onap.org/pages/viewpage.action?pageId=71835817>`

**Upgrade Notes**
       NA

**Deprecation Notes**
       NA
	


Version: 1.1.5 (Dublin)
-----------------------
: Release Date: 2019-06-06

**New Features**

 - DMaaP Provisioning via Bus Controller

**Bug Fixes**
       NA

**Known Issues**
       NA

**Security Notes**
DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `Dublin <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_.

Quick Links:
- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_
- `Dublin Project Vulnerability Review Table for DMAAP <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_

**Upgrade Notes**
NA

**Deprecation Notes**


Version: 1.0.23
---------------
: Release Date: 2018-10-18

**New Features**

 - configMap for properties
 - AAF integration

**Bug Fixes**
       NA

**Known Issues**
       NA

**Security Notes**
DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `project <https://wiki.onap.org/pages/viewpage.action?pageId=28379799>`_.

Quick Links:
- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_
- `Project Vulnerability Review Table for DMAAP <https://wiki.onap.org/pages/viewpage.action?pageId=28379799>`_

**Upgrade Notes**
NA

**Deprecation Notes**
