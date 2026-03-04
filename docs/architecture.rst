.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. _architecture:

Architecture
============


Capabilities
------------
Bus Controller is a RESTful web service used to provision DMaaP topics on MR (Message Router)
and feeds on DR (Data Router), with associated authorization via AAF.

Usage Scenarios
---------------
Bus Controller endpoints are used to provision:

- an authorized topic on MR, and to create and grant permission for publishers and subscribers.
- a feed on DR, with associated user authentication.

.. mermaid::

   graph TD
       DBC_CLIENT --> DBC_API
       DBC_API --> MR
       DBC_API --> DR
       DBC_API --> AAF

       subgraph "Bus Controller Container"
           DBC_API
       end

       subgraph "MR"
           MR
       end

       subgraph "DR"
           DR
       end

       subgraph "AAF"
           AAF
       end

       classDef blue fill:#33f,stroke:#333,color:#fff
       classDef yellow fill:#ff0,stroke:#333,color:#000
       classDef orange fill:#f90,stroke:#333,color:#000
       classDef green fill:#0c0,stroke:#333,color:#000

       class DBC_API blue
       class MR yellow
       class DR orange
       class AAF green
