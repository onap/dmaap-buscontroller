.. This work is licensed under a Creative Commons Attribution 4.0 International License.
   .. http://creativecommons.org/licenses/by/4.0

Logging
=======

.. note::
   * This section is used to describe the informational or diagnostic messages emitted from
     a software component and the methods or collecting them.

   * This section is typically: provided for a platform-component and sdk; and
     referenced in developer and user guides

   * This note must be removed after content has been added.


Where to Access Information
---------------------------
Bus Controller uses logback framework to generate logs found under logs/ONAP.
Logs are organized into files:
application.log - contains general logs
error.log - contains errors
audit.log - contains transactions for audit trail
server.log - contains jetty server specific logging

Error / Warning Messages
------------------------
Logging to error.log will distinguish critical errors from warnings.
