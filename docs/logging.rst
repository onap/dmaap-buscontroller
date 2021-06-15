.. This work is licensed under a Creative Commons Attribution 4.0 International License.
   .. http://creativecommons.org/licenses/by/4.0

Logging
=======

Where to Access Information
---------------------------
Bus Controller uses logback framework to generate logs found under logs/ONAP.
Logs are organized into files:

- application.log - contains general logs
- error.log - contains errors
- audit.log - contains transactions for audit trail
- server.log - contains jetty server specific logging

Error / Warning Messages
------------------------
Logging to error.log will distinguish critical errors from warnings.
