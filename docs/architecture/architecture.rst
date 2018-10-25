.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Architecture
==============


Capabilities
---------
Bus Controller is a RESTful web service used to provision DMaaP topics (on Message Router) and feeds (on Data Router), with associated authorization (on AAF).

Usage Scenarios
---------------
Bus Controller endpoints are used to provision:
- a authorized topic on MR, and to create and grant permission for publishers and subscribers.
- a feed on DR, with associated user authenticatio n.

.. blockdiag::

   blockdiag layers {
   orientation = portrait
   DBC_CLIENT -> DBC_API;
   DBC_API -> MR;
   DBC_API -> DR;
   DBC_API -> AAF;
   group l1 {
        color = blue;
        label = "Bus Controller Container";
        DBC_API;
        }
   group l2 {
        color = yellow;
        label = "MR";
        MR;
        }
   group l3 {
        color = orange;
        label = "DR";
        DR;
        }
    group l4 {
        color = green;
        label = "AAF";
        AAF;
        }
    }
 

