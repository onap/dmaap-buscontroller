DMaaP Bus Controller REST API 1.1.0
===================================

Description
~~~~~~~~~~~

    Bus Controller provides an API for OpenDCAE components which need to provision
    underlying DMaaP technologies (Data Router and Message Router).
    Primary clients for this API are anticipated to be the OpenDCAE
    Controller, OpenDCAE Orchestrator, OpenDCAE Inventory and the
    ECOMP Portal.

    Objects managed by DMaaP are deployed in a dcaeLocation which is a
    unique identifier for an OpenStack tenant for a dcaeLayer,
    opendcae-central (aka ecomp) or opendcae-local-ntc (aka edge).

    A dcaeEnvironment (e.g. FTL or prod) has a single DMaaP. A
    DMaaP is managed by a one or more stateless DMaaP Bus
    Controller(s), though Bus Controller relies on PGaaS for
    persistence. Each DMaaP has a single instance of Data Router,
    which has 1 or more DR_Nodes deployed at each dcaeLocation. DR
    Clients of type DR_Pub generally publish to a DR_Node that is
    local to its dcaeLocation. Routing for a Feed is determined by
    the dcaelocation of its DR_Sub clients.

    A DMaaP may have many Message Router instances. Each instance is
    deployed as an MR_Cluster. One MR_Cluster is deployed at each
    dcaeLocation. MR_Clients generally communicate to the
    MR_Cluster at the same dcaeLocation. Replication of messages
    between MR_Clusters is accomplished by MR Bridge, which is
    provioned by DMaaP Bus Controller based on Topic attributes.

    Therefore, the role of DMaaP Bus Controller is to support other
    DCAE infrastructure components to dynamically provision DMaaP
    services on behalf of DMaaP clients, and to assist in any
    management or discovery activity of its clients.

    A convention of this API is to return JSON responses per
    OpenStack style.



Contact Information
~~~~~~~~~~~~~~~~~~~




http://www.onap.org




License
~~~~~~~


`Licensed under the Apache License, Version 2.0 <http://www.apache.org/licenses/LICENSE-2.0>`_




Base URL
~~~~~~~~

http://www.[host]:[port]/webapi
https://www.[host]:[port]/webapi

BRIDGE
~~~~~~


Endpoint for retreiving MR Bridge metrics





GET ``/bridge``
---------------


Summary
+++++++

return BrTopic details

Description
+++++++++++

.. raw:: html

    Returns array of  `BrTopic` objects. If source and target query params are specified, only report on that bridge.  If detail param is true, list topics names, else just a count is returned.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        mmagent | query | No | string |  |  |
        detail | query | No | boolean |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`BrTopic <d_d71baea9d8e4e59bc395ef51f45dff1b>`

**Example:**

.. code-block:: javascript

    {
        "brSource": "somestring",
        "brTarget": "somestring",
        "mmAgentName": "somestring",
        "topicCount": 1
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/bridge``
---------------


Summary
+++++++

update MirrorMaker details

Description
+++++++++++

.. raw:: html

    replace the topic list for a specific Bridge.  Use JSON Body for value to replace whitelist, but if refreshFlag param is true, simply refresh using existing whitelist.If split param is true, spread whitelist over smaller mmagents.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        mmagent | query | No | string |  |  |
        refresh | query | No | boolean |  |  |
        split | query | No | boolean |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`BrTopic <d_d71baea9d8e4e59bc395ef51f45dff1b>`

**Example:**

.. code-block:: javascript

    {
        "brSource": "somestring",
        "brTarget": "somestring",
        "mmAgentName": "somestring",
        "topicCount": 1
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




DCAELOCATIONS
~~~~~~~~~~~~~


an OpenStack tenant purposed for OpenDCAE (i.e. where OpenDCAE components might be deployed)





POST ``/dcaeLocations``
-----------------------


Summary
+++++++

return dcaeLocation details

Description
+++++++++++

.. raw:: html

    Create some `dcaeLocation` which is a unique identifier for an *OpenStack* tenant purposed for a *dcaeLayer*  (ecomp or edge).


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DcaeLocation <d_47d80e451933beb623fcf5257867cbcb>`

**Example:**

.. code-block:: javascript

    {
        "central": true,
        "clli": "somestring",
        "dcaeLayer": "somestring",
        "dcaeLocationName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "local": true,
        "openStackAvailabilityZone": "somestring",
        "status": "EMPTY",
        "subnet": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/dcaeLocations/{locationName}``
----------------------------------------


Summary
+++++++

return dcaeLocation details

Description
+++++++++++

.. raw:: html

    delete a dcaeLocation

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        locationName | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`DcaeLocation <d_47d80e451933beb623fcf5257867cbcb>`

**Example:**

.. code-block:: javascript

    {
        "central": true,
        "clli": "somestring",
        "dcaeLayer": "somestring",
        "dcaeLocationName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "local": true,
        "openStackAvailabilityZone": "somestring",
        "status": "EMPTY",
        "subnet": "somestring"
    }

**204**
^^^^^^^

Success


Type: :ref:`DcaeLocation <d_47d80e451933beb623fcf5257867cbcb>`

**Example:**

.. code-block:: javascript

    {
        "central": true,
        "clli": "somestring",
        "dcaeLayer": "somestring",
        "dcaeLocationName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "local": true,
        "openStackAvailabilityZone": "somestring",
        "status": "EMPTY",
        "subnet": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dcaeLocations/{locationName}``
-------------------------------------


Summary
+++++++

return dcaeLocation details

Description
+++++++++++

.. raw:: html

    Returns a specific `dcaeLocation` object with specified tag

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        locationName | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DcaeLocation <d_47d80e451933beb623fcf5257867cbcb>`

**Example:**

.. code-block:: javascript

    {
        "central": true,
        "clli": "somestring",
        "dcaeLayer": "somestring",
        "dcaeLocationName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "local": true,
        "openStackAvailabilityZone": "somestring",
        "status": "EMPTY",
        "subnet": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dcaeLocations``
----------------------


Summary
+++++++

return dcaeLocation details

Description
+++++++++++

.. raw:: html

    Returns array of  `dcaeLocation` objects.  All objects managed by DMaaP are deployed in some `dcaeLocation` which is a unique identifier for an *OpenStack* tenant purposed for a *dcaeLayer*  (ecomp or edge).


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DcaeLocation <d_47d80e451933beb623fcf5257867cbcb>`

**Example:**

.. code-block:: javascript

    {
        "central": true,
        "clli": "somestring",
        "dcaeLayer": "somestring",
        "dcaeLocationName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "local": true,
        "openStackAvailabilityZone": "somestring",
        "status": "EMPTY",
        "subnet": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/dcaeLocations/{locationName}``
-------------------------------------


Summary
+++++++

return dcaeLocation details

Description
+++++++++++

.. raw:: html

    update the openStackAvailabilityZone of a dcaeLocation

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        locationName | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DcaeLocation <d_47d80e451933beb623fcf5257867cbcb>`

**Example:**

.. code-block:: javascript

    {
        "central": true,
        "clli": "somestring",
        "dcaeLayer": "somestring",
        "dcaeLocationName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "local": true,
        "openStackAvailabilityZone": "somestring",
        "status": "EMPTY",
        "subnet": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




DMAAP
~~~~~


V2 Endpoint for this instance of DMaaP object containing values for this OpenDCAE deployment





POST ``/dmaap_v2``
------------------


Summary
+++++++

return dmaap details

Description
+++++++++++

.. raw:: html

    Create a new DMaaP set system wide configuration settings for the *dcaeEnvironment*.  Deprecated with introduction of persistence in 1610.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Dmaap <d_4ea0e7758a1f8502222793e4a13b04f7>`

**Example:**

.. code-block:: javascript

    {
        "accessKeyOwner": "somestring",
        "bridgeAdminTopic": "somestring",
        "dmaapName": "somestring",
        "drProvUrl": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "loggingUrl": "somestring",
        "nodeKey": "somestring",
        "status": "EMPTY",
        "topicNsRoot": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





POST ``/dmaap``
---------------


Summary
+++++++

return dmaap details

Description
+++++++++++

.. raw:: html

    Create a new DMaaP set system wide configuration settings for the *dcaeEnvironment*.  Deprecated with introduction of persistence in 1610.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Dmaap <d_4ea0e7758a1f8502222793e4a13b04f7>`

**Example:**

.. code-block:: javascript

    {
        "accessKeyOwner": "somestring",
        "bridgeAdminTopic": "somestring",
        "dmaapName": "somestring",
        "drProvUrl": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "loggingUrl": "somestring",
        "nodeKey": "somestring",
        "status": "EMPTY",
        "topicNsRoot": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dmaap_v2``
-----------------


Summary
+++++++

return dmaap details

Description
+++++++++++

.. raw:: html

    returns the `dmaap` object, which contains system wide configuration settings


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Dmaap <d_4ea0e7758a1f8502222793e4a13b04f7>`

**Example:**

.. code-block:: javascript

    {
        "accessKeyOwner": "somestring",
        "bridgeAdminTopic": "somestring",
        "dmaapName": "somestring",
        "drProvUrl": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "loggingUrl": "somestring",
        "nodeKey": "somestring",
        "status": "EMPTY",
        "topicNsRoot": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dmaap``
--------------


Summary
+++++++

return dmaap details

Description
+++++++++++

.. raw:: html

    returns the `dmaap` object, which contains system wide configuration settings


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Dmaap <d_4ea0e7758a1f8502222793e4a13b04f7>`

**Example:**

.. code-block:: javascript

    {
        "accessKeyOwner": "somestring",
        "bridgeAdminTopic": "somestring",
        "dmaapName": "somestring",
        "drProvUrl": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "loggingUrl": "somestring",
        "nodeKey": "somestring",
        "status": "EMPTY",
        "topicNsRoot": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/dmaap_v2``
-----------------


Summary
+++++++

return dmaap details

Description
+++++++++++

.. raw:: html

    Update system settings for *dcaeEnvironment*.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Dmaap <d_4ea0e7758a1f8502222793e4a13b04f7>`

**Example:**

.. code-block:: javascript

    {
        "accessKeyOwner": "somestring",
        "bridgeAdminTopic": "somestring",
        "dmaapName": "somestring",
        "drProvUrl": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "loggingUrl": "somestring",
        "nodeKey": "somestring",
        "status": "EMPTY",
        "topicNsRoot": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/dmaap``
--------------


Summary
+++++++

return dmaap details

Description
+++++++++++

.. raw:: html

    Update system settings for *dcaeEnvironment*.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Dmaap <d_4ea0e7758a1f8502222793e4a13b04f7>`

**Example:**

.. code-block:: javascript

    {
        "accessKeyOwner": "somestring",
        "bridgeAdminTopic": "somestring",
        "dmaapName": "somestring",
        "drProvUrl": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "loggingUrl": "somestring",
        "nodeKey": "somestring",
        "status": "EMPTY",
        "topicNsRoot": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




DR_NODES
~~~~~~~~


Endpoint for a Data Router Node server





POST ``/dr_nodes``
------------------


Summary
+++++++

return DR_Node details

Description
+++++++++++

.. raw:: html

    create a `DR_Node` in a *dcaeLocation*.  Note that multiple `DR_Node`s may exist in the same `dcaeLocation`.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Node <d_d15e2cee407536866c875375e3f705e0>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "hostName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "status": "EMPTY",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/dr_nodes/{fqdn}``
---------------------------


Summary
+++++++

No Content

Description
+++++++++++

.. raw:: html

    Delete a single `DR_Node` object.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        fqdn | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`DR_Node <d_d15e2cee407536866c875375e3f705e0>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "hostName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "status": "EMPTY",
        "version": "somestring"
    }

**204**
^^^^^^^

Success


Type: :ref:`DR_Node <d_d15e2cee407536866c875375e3f705e0>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "hostName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "status": "EMPTY",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dr_nodes/{fqdn}``
------------------------


Summary
+++++++

return DR_Node details

Description
+++++++++++

.. raw:: html

    Retrieve a single `DR_Node` object.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        fqdn | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Node <d_d15e2cee407536866c875375e3f705e0>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "hostName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "status": "EMPTY",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dr_nodes``
-----------------


Summary
+++++++

return DR_Node details

Description
+++++++++++

.. raw:: html

    Returns array of `DR_Node` object array.  Need to add filter by dcaeLocation.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Node <d_d15e2cee407536866c875375e3f705e0>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "hostName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "status": "EMPTY",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/dr_nodes/{fqdn}``
------------------------


Summary
+++++++

return DR_Node details

Description
+++++++++++

.. raw:: html

    Update a single `DR_Node` object.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        fqdn | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Node <d_d15e2cee407536866c875375e3f705e0>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "hostName": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "status": "EMPTY",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




DR_PUBS
~~~~~~~


Endpoint for a Data Router client that implements a Publisher





POST ``/dr_pubs``
-----------------


Summary
+++++++

return DR_Pub details

Description
+++++++++++

.. raw:: html

    create a DR Publisher in the specified environment.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "pubId": "somestring",
        "status": "EMPTY",
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/dr_pubs/{pubId}``
---------------------------


Summary
+++++++

return DR_Pub details

Description
+++++++++++

.. raw:: html

    delete a DR Publisher in the specified environment. Delete a `DR_Pub` object by pubId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pubId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "pubId": "somestring",
        "status": "EMPTY",
        "username": "somestring",
        "userpwd": "somestring"
    }

**204**
^^^^^^^

Success


Type: :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "pubId": "somestring",
        "status": "EMPTY",
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dr_pubs/{pubId}``
------------------------


Summary
+++++++

return DR_Pub details

Description
+++++++++++

.. raw:: html

    returns a DR Publisher in the specified environment. Gets a `DR_Pub` object by pubId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pubId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "pubId": "somestring",
        "status": "EMPTY",
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dr_pubs``
----------------


Summary
+++++++

return DR_Pub details

Description
+++++++++++

.. raw:: html

    Returns array of  `DR_Pub` objects.  Add filter for feedId.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "pubId": "somestring",
        "status": "EMPTY",
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/dr_pubs/{pubId}``
------------------------


Summary
+++++++

return DR_Pub details

Description
+++++++++++

.. raw:: html

    update a DR Publisher in the specified environment.  Update a `DR_Pub` object by pubId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pubId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "pubId": "somestring",
        "status": "EMPTY",
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




DR_SUBS
~~~~~~~


Endpoint for a Data Router client that implements a Subscriber





POST ``/dr_subs``
-----------------


Summary
+++++++

return DR_Sub details

Description
+++++++++++

.. raw:: html

    Create a  `DR_Sub` object.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Sub <d_48cf328d246f41e1d11a09251b042f02>`

**Example:**

.. code-block:: javascript

    {
        "bytes": [
            "somestring",
            "somestring"
        ],
        "dcaeLocationName": "somestring",
        "deliveryURL": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "status": "EMPTY",
        "subId": "somestring",
        "suspended": true,
        "use100": true,
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/dr_subs/{subId}``
---------------------------


Summary
+++++++

return DR_Sub details

Description
+++++++++++

.. raw:: html

    Delete a  `DR_Sub` object, selected by subId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        subId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Sub <d_48cf328d246f41e1d11a09251b042f02>`

**Example:**

.. code-block:: javascript

    {
        "bytes": [
            "somestring",
            "somestring"
        ],
        "dcaeLocationName": "somestring",
        "deliveryURL": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "status": "EMPTY",
        "subId": "somestring",
        "suspended": true,
        "use100": true,
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dr_subs/{subId}``
------------------------


Summary
+++++++

return DR_Sub details

Description
+++++++++++

.. raw:: html

    Retrieve a  `DR_Sub` object, selected by subId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        subId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Sub <d_48cf328d246f41e1d11a09251b042f02>`

**Example:**

.. code-block:: javascript

    {
        "bytes": [
            "somestring",
            "somestring"
        ],
        "dcaeLocationName": "somestring",
        "deliveryURL": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "status": "EMPTY",
        "subId": "somestring",
        "suspended": true,
        "use100": true,
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/dr_subs``
----------------


Summary
+++++++

return DR_Sub details

Description
+++++++++++

.. raw:: html

    Returns array of  `DR_Sub` objects.  Add filter for feedId.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Sub <d_48cf328d246f41e1d11a09251b042f02>`

**Example:**

.. code-block:: javascript

    {
        "bytes": [
            "somestring",
            "somestring"
        ],
        "dcaeLocationName": "somestring",
        "deliveryURL": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "status": "EMPTY",
        "subId": "somestring",
        "suspended": true,
        "use100": true,
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/dr_subs/{subId}``
------------------------


Summary
+++++++

return DR_Sub details

Description
+++++++++++

.. raw:: html

    Update a  `DR_Sub` object, selected by subId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        subId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Sub <d_48cf328d246f41e1d11a09251b042f02>`

**Example:**

.. code-block:: javascript

    {
        "bytes": [
            "somestring",
            "somestring"
        ],
        "dcaeLocationName": "somestring",
        "deliveryURL": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "status": "EMPTY",
        "subId": "somestring",
        "suspended": true,
        "use100": true,
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




FEEDS
~~~~~


Endpoint for a Data Router Feed





POST ``/feeds``
---------------


Summary
+++++++

return Feed details

Description
+++++++++++

.. raw:: html

    Create a of  `Feed` object.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        useExisting | query | No | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Feed <d_289ad39619725df26c9ff382d4c97c75>`

**Example:**

.. code-block:: javascript

    {
        "asprClassification": "somestring",
        "bytes": [
            "somestring",
            "somestring"
        ],
        "feedDescription": "somestring",
        "feedId": "somestring",
        "feedName": "somestring",
        "feedVersion": "somestring",
        "formatUuid": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "publishURL": "somestring",
        "pubs": [
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "status": "EMPTY",
        "subs": [
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "subscribeURL": "somestring",
        "suspended": true
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/feeds/{id}``
----------------------


Summary
+++++++

return Feed details

Description
+++++++++++

.. raw:: html

    Delete a  `Feed` object, specified by id.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        id | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`Feed <d_289ad39619725df26c9ff382d4c97c75>`

**Example:**

.. code-block:: javascript

    {
        "asprClassification": "somestring",
        "bytes": [
            "somestring",
            "somestring"
        ],
        "feedDescription": "somestring",
        "feedId": "somestring",
        "feedName": "somestring",
        "feedVersion": "somestring",
        "formatUuid": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "publishURL": "somestring",
        "pubs": [
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "status": "EMPTY",
        "subs": [
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "subscribeURL": "somestring",
        "suspended": true
    }

**204**
^^^^^^^

Success


Type: :ref:`Feed <d_289ad39619725df26c9ff382d4c97c75>`

**Example:**

.. code-block:: javascript

    {
        "asprClassification": "somestring",
        "bytes": [
            "somestring",
            "somestring"
        ],
        "feedDescription": "somestring",
        "feedId": "somestring",
        "feedName": "somestring",
        "feedVersion": "somestring",
        "formatUuid": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "publishURL": "somestring",
        "pubs": [
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring
            },
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "status": "EMPTY",
        "subs": [
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "subscribeURL": "somestring",
        "suspended": true
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




GET ``/feeds/{id}``
-------------------


Summary
+++++++

return Feed details

Description
+++++++++++

.. raw:: html

    Retrieve a  `Feed` object, specified by id.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        id | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "feedId": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "pubId": "somestring",
        "status": "EMPTY",
        "username": "somestring",
        "userpwd": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/feeds``
--------------


Summary
+++++++

return Feed details

Description
+++++++++++

.. raw:: html

    Returns array of  `Feed` objects.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        feedName | query | No | string |  |  |
        version | query | No | string |  |  |
        match | query | No | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Feed <d_289ad39619725df26c9ff382d4c97c75>`

**Example:**

.. code-block:: javascript

    {
        "asprClassification": "somestring",
        "bytes": [
            "somestring",
            "somestring"
        ],
        "feedDescription": "somestring",
        "feedId": "somestring",
        "feedName": "somestring",
        "feedVersion": "somestring",
        "formatUuid": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "publishURL": "somestring",
        "pubs": [
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "status": "EMPTY",
        "subs": [
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "subscribeURL": "somestring",
        "suspended": true
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/feeds/{id}``
-------------------


Summary
+++++++

return Feed details

Description
+++++++++++

.. raw:: html

    Update a  `Feed` object, specified by id.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        id | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Feed <d_289ad39619725df26c9ff382d4c97c75>`

**Example:**

.. code-block:: javascript

    {
        "asprClassification": "somestring",
        "bytes": [
            "somestring",
            "somestring"
        ],
        "feedDescription": "somestring",
        "feedId": "somestring",
        "feedName": "somestring",
        "feedVersion": "somestring",
        "formatUuid": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "logURL": "somestring",
        "owner": "somestring",
        "publishURL": "somestring",
        "pubs": [
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "dcaeLocationName": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "pubId": "somestring",
                "status": "EMPTY",
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "status": "EMPTY",
        "subs": [
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            },
            {
                "bytes": [
                    "somestring",
                    "somestring"
                ],
                "dcaeLocationName": "somestring",
                "deliveryURL": "somestring",
                "feedId": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "logURL": "somestring",
                "owner": "somestring",
                "status": "EMPTY",
                "subId": "somestring",
                "suspended": true,
                "use100": true,
                "username": "somestring",
                "userpwd": "somestring"
            }
        ],
        "subscribeURL": "somestring",
        "suspended": true
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




INFO
~~~~


Endpoint for this instance of DBCL.  Returns health info.





GET ``/info``
-------------


Summary
+++++++

return info details

Description
+++++++++++

.. raw:: html

    returns the `info` object


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Dmaap <d_4ea0e7758a1f8502222793e4a13b04f7>`

**Example:**

.. code-block:: javascript

    {
        "accessKeyOwner": "somestring",
        "bridgeAdminTopic": "somestring",
        "dmaapName": "somestring",
        "drProvUrl": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "loggingUrl": "somestring",
        "nodeKey": "somestring",
        "status": "EMPTY",
        "topicNsRoot": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




MR_CLIENTS
~~~~~~~~~~


Endpoint for a Message Router Client that implements a Publisher or a Subscriber





POST ``/mr_clients``
--------------------


Summary
+++++++

Associate an MR_Client object to a Topic

Description
+++++++++++

.. raw:: html

    Create a  `MR_Client` object.The `dcaeLocation` attribute is used to match an `MR_Cluster` object with the same value, with the intent of localizing message traffic.  In legacy implementation, the `clientRole` is granted appropriate permission in AAF.  Newer implementions may instead specify an AAF Identity, which will be added to the appropriate `Topic` role.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Client <d_56ff81dc98986e27074d9be2731e3f4c>`

**Example:**

.. code-block:: javascript

    {
        "action": [
            "somestring",
            "somestring"
        ],
        "clientIdentity": "somestring",
        "clientRole": "somestring",
        "dcaeLocationName": "somestring",
        "fqtn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "mrClientId": "somestring",
        "status": "EMPTY",
        "topicURL": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/mr_clients/{subId}``
------------------------------


Summary
+++++++

Delete an MR_Client object

Description
+++++++++++

.. raw:: html

    Delete a  `MR_Client` object, specified by clientId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        subId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`MR_Client <d_56ff81dc98986e27074d9be2731e3f4c>`

**Example:**

.. code-block:: javascript

    {
        "action": [
            "somestring",
            "somestring"
        ],
        "clientIdentity": "somestring",
        "clientRole": "somestring",
        "dcaeLocationName": "somestring",
        "fqtn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "mrClientId": "somestring",
        "status": "EMPTY",
        "topicURL": "somestring"
    }

**204**
^^^^^^^

Success


Type: :ref:`MR_Client <d_56ff81dc98986e27074d9be2731e3f4c>`

**Example:**

.. code-block:: javascript

    {
        "action": [
            "somestring",
            "somestring"
        ],
        "clientIdentity": "somestring",
        "clientRole": "somestring",
        "dcaeLocationName": "somestring",
        "fqtn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "mrClientId": "somestring",
        "status": "EMPTY",
        "topicURL": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/mr_clients``
-------------------


Summary
+++++++

return MR_Client details

Description
+++++++++++

.. raw:: html

    Returns array of  `MR_Client` objects.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Client <d_56ff81dc98986e27074d9be2731e3f4c>`

**Example:**

.. code-block:: javascript

    {
        "action": [
            "somestring",
            "somestring"
        ],
        "clientIdentity": "somestring",
        "clientRole": "somestring",
        "dcaeLocationName": "somestring",
        "fqtn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "mrClientId": "somestring",
        "status": "EMPTY",
        "topicURL": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/mr_clients/{subId}``
---------------------------


Summary
+++++++

return MR_Client details

Description
+++++++++++

.. raw:: html

    Retrieve a  `MR_Client` object, specified by clientId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        subId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Client <d_56ff81dc98986e27074d9be2731e3f4c>`

**Example:**

.. code-block:: javascript

    {
        "action": [
            "somestring",
            "somestring"
        ],
        "clientIdentity": "somestring",
        "clientRole": "somestring",
        "dcaeLocationName": "somestring",
        "fqtn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "mrClientId": "somestring",
        "status": "EMPTY",
        "topicURL": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/mr_clients/{clientId}``
------------------------------


Summary
+++++++

Update an MR_Client object

Description
+++++++++++

.. raw:: html

    Update a  `MR_Client` object, specified by clientId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        clientId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Client <d_56ff81dc98986e27074d9be2731e3f4c>`

**Example:**

.. code-block:: javascript

    {
        "action": [
            "somestring",
            "somestring"
        ],
        "clientIdentity": "somestring",
        "clientRole": "somestring",
        "dcaeLocationName": "somestring",
        "fqtn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "mrClientId": "somestring",
        "status": "EMPTY",
        "topicURL": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




MR_CLUSTERS
~~~~~~~~~~~


Endpoint for a Message Router servers in a Cluster configuration





POST ``/mr_clusters``
---------------------


Summary
+++++++

return MR_Cluster details

Description
+++++++++++

.. raw:: html

    Create an  `MR_Cluster` object.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Cluster <d_eec7176a0080debe1b19c2dad2e97c24>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "replicationGroup": "somestring",
        "sourceReplicationPort": "somestring",
        "status": "EMPTY",
        "targetReplicationPort": "somestring",
        "topicPort": "somestring",
        "topicProtocol": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/mr_clusters/{clusterId}``
-----------------------------------


Summary
+++++++

return MR_Cluster details

Description
+++++++++++

.. raw:: html

    Delete an  `MR_Cluster` object, specified by clusterId.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        clusterId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`MR_Cluster <d_eec7176a0080debe1b19c2dad2e97c24>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "replicationGroup": "somestring",
        "sourceReplicationPort": "somestring",
        "status": "EMPTY",
        "targetReplicationPort": "somestring",
        "topicPort": "somestring",
        "topicProtocol": "somestring"
    }

**204**
^^^^^^^

Success


Type: :ref:`MR_Cluster <d_eec7176a0080debe1b19c2dad2e97c24>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "replicationGroup": "somestring",
        "sourceReplicationPort": "somestring",
        "status": "EMPTY",
        "targetReplicationPort": "somestring",
        "topicPort": "somestring",
        "topicProtocol": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/mr_clusters/{clusterId}``
--------------------------------


Summary
+++++++

return MR_Cluster details

Description
+++++++++++

.. raw:: html

    Retrieve an  `MR_Cluster` object, specified by clusterId.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        clusterId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Cluster <d_eec7176a0080debe1b19c2dad2e97c24>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "replicationGroup": "somestring",
        "sourceReplicationPort": "somestring",
        "status": "EMPTY",
        "targetReplicationPort": "somestring",
        "topicPort": "somestring",
        "topicProtocol": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/mr_clusters``
--------------------


Summary
+++++++

return MR_Cluster details

Description
+++++++++++

.. raw:: html

    Returns array of  `MR_Cluster` objects.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Cluster <d_eec7176a0080debe1b19c2dad2e97c24>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "replicationGroup": "somestring",
        "sourceReplicationPort": "somestring",
        "status": "EMPTY",
        "targetReplicationPort": "somestring",
        "topicPort": "somestring",
        "topicProtocol": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/mr_clusters/{clusterId}``
--------------------------------


Summary
+++++++

return MR_Cluster details

Description
+++++++++++

.. raw:: html

    Update an  `MR_Cluster` object, specified by clusterId.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        clusterId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`MR_Cluster <d_eec7176a0080debe1b19c2dad2e97c24>`

**Example:**

.. code-block:: javascript

    {
        "dcaeLocationName": "somestring",
        "fqdn": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "replicationGroup": "somestring",
        "sourceReplicationPort": "somestring",
        "status": "EMPTY",
        "targetReplicationPort": "somestring",
        "topicPort": "somestring",
        "topicProtocol": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




TOPICS
~~~~~~


Endpoint for retreiving MR Topics





POST ``/topics``
----------------


Summary
+++++++

Create a Topic object

Description
+++++++++++

.. raw:: html

    Create  `Topic` object.For convenience, the message body may populate the `clients` array, in which case each entry will be added as an `MR_Client`.  Beginning in ONAP Dublin Release, dbcapi will create two AAF Roles by default, one each for the publisher and subscriber per topic.  MR_Clients can then specify an AAF Identity to be added to the appropriate default Role, avoiding the need to create Role(s) in advance.

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        useExisting | query | No | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Topic <d_2e99841971da81b9d240071b86bf168d>`

**Example:**

.. code-block:: javascript

    {
        "clients": [
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            },
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            }
        ],
        "formatUuid": "somestring",
        "fqtn": "somestring",
        "fqtnStyle": "FQTN_NOT_SPECIFIED",
        "globalMrURL": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "owner": "somestring",
        "partitionCount": "somestring",
        "publisherRole": "somestring",
        "replicationCase": "REPLICATION_NOT_SPECIFIED",
        "replicationCount": "somestring",
        "status": "EMPTY",
        "subscriberRole": "somestring",
        "tnxEnabled": "somestring",
        "topicDescription": "somestring",
        "topicName": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





DELETE ``/topics/{topicId}``
----------------------------


Summary
+++++++

return Topic details

Description
+++++++++++

.. raw:: html

    Delete a  `Topic` object, identified by topicId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        topicId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

successful operation


Type: :ref:`Topic <d_2e99841971da81b9d240071b86bf168d>`

**Example:**

.. code-block:: javascript

    {
        "clients": [
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            },
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            }
        ],
        "formatUuid": "somestring",
        "fqtn": "somestring",
        "fqtnStyle": "FQTN_NOT_SPECIFIED",
        "globalMrURL": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "owner": "somestring",
        "partitionCount": "somestring",
        "publisherRole": "somestring",
        "replicationCase": "REPLICATION_NOT_SPECIFIED",
        "replicationCount": "somestring",
        "status": "EMPTY",
        "subscriberRole": "somestring",
        "tnxEnabled": "somestring",
        "topicDescription": "somestring",
        "topicName": "somestring",
        "version": "somestring"
    }

**204**
^^^^^^^

Success


Type: :ref:`Topic <d_2e99841971da81b9d240071b86bf168d>`

**Example:**

.. code-block:: javascript

    {
        "clients": [
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            },
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            }
        ],
        "formatUuid": "somestring",
        "fqtn": "somestring",
        "fqtnStyle": "FQTN_NOT_SPECIFIED",
        "globalMrURL": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "owner": "somestring",
        "partitionCount": "somestring",
        "publisherRole": "somestring",
        "replicationCase": "REPLICATION_NOT_SPECIFIED",
        "replicationCount": "somestring",
        "status": "EMPTY",
        "subscriberRole": "somestring",
        "tnxEnabled": "somestring",
        "topicDescription": "somestring",
        "topicName": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/topics/{topicId}``
-------------------------


Summary
+++++++

return Topic details

Description
+++++++++++

.. raw:: html

    Retrieve a  `Topic` object, identified by topicId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        topicId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Topic <d_2e99841971da81b9d240071b86bf168d>`

**Example:**

.. code-block:: javascript

    {
        "clients": [
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            },
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            }
        ],
        "formatUuid": "somestring",
        "fqtn": "somestring",
        "fqtnStyle": "FQTN_NOT_SPECIFIED",
        "globalMrURL": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "owner": "somestring",
        "partitionCount": "somestring",
        "publisherRole": "somestring",
        "replicationCase": "REPLICATION_NOT_SPECIFIED",
        "replicationCount": "somestring",
        "status": "EMPTY",
        "subscriberRole": "somestring",
        "tnxEnabled": "somestring",
        "topicDescription": "somestring",
        "topicName": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





GET ``/topics``
---------------


Summary
+++++++

return Topic details

Description
+++++++++++

.. raw:: html

    Returns array of  `Topic` objects.


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Topic <d_2e99841971da81b9d240071b86bf168d>`

**Example:**

.. code-block:: javascript

    {
        "clients": [
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            },
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            }
        ],
        "formatUuid": "somestring",
        "fqtn": "somestring",
        "fqtnStyle": "FQTN_NOT_SPECIFIED",
        "globalMrURL": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "owner": "somestring",
        "partitionCount": "somestring",
        "publisherRole": "somestring",
        "replicationCase": "REPLICATION_NOT_SPECIFIED",
        "replicationCount": "somestring",
        "status": "EMPTY",
        "subscriberRole": "somestring",
        "tnxEnabled": "somestring",
        "topicDescription": "somestring",
        "topicName": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }





PUT ``/topics/{topicId}``
-------------------------


Summary
+++++++

return Topic details

Description
+++++++++++

.. raw:: html

    Update a  `Topic` object, identified by topicId

Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        topicId | path | Yes | string |  |  |


Request
+++++++


Responses
+++++++++

**200**
^^^^^^^

Success


Type: :ref:`Topic <d_2e99841971da81b9d240071b86bf168d>`

**Example:**

.. code-block:: javascript

    {
        "clients": [
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            },
            {
                "action": [
                    "somestring",
                    "somestring"
                ],
                "clientIdentity": "somestring",
                "clientRole": "somestring",
                "dcaeLocationName": "somestring",
                "fqtn": "somestring",
                "lastMod": "2015-01-01T15:00:00.000Z",
                "mrClientId": "somestring",
                "status": "EMPTY",
                "topicURL": "somestring"
            }
        ],
        "formatUuid": "somestring",
        "fqtn": "somestring",
        "fqtnStyle": "FQTN_NOT_SPECIFIED",
        "globalMrURL": "somestring",
        "lastMod": "2015-01-01T15:00:00.000Z",
        "owner": "somestring",
        "partitionCount": "somestring",
        "publisherRole": "somestring",
        "replicationCase": "REPLICATION_NOT_SPECIFIED",
        "replicationCount": "somestring",
        "status": "EMPTY",
        "subscriberRole": "somestring",
        "tnxEnabled": "somestring",
        "topicDescription": "somestring",
        "topicName": "somestring",
        "version": "somestring"
    }

**400**
^^^^^^^

Error


Type: :ref:`ApiError <d_a3a7580ce9d87225d7f62e6b67b4d036>`

**Example:**

.. code-block:: javascript

    {
        "code": 1,
        "fields": "somestring",
        "is2xx": true,
        "message": "somestring"
    }




Data Structures
~~~~~~~~~~~~~~~

.. _d_a3a7580ce9d87225d7f62e6b67b4d036:

ApiError Model Structure
------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        code | No | integer | int32 |  |
        fields | No | string |  |  |
        is2xx | No | boolean |  |  |
        message | No | string |  |  |

.. _d_d71baea9d8e4e59bc395ef51f45dff1b:

BrTopic Model Structure
-----------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        brSource | No | string |  |  |
        brTarget | No | string |  |  |
        mmAgentName | No | string |  |  |
        topicCount | No | integer | int32 |  |

.. _d_d15e2cee407536866c875375e3f705e0:

DR_Node Model Structure
-----------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        dcaeLocationName | No | string |  |  |
        fqdn | No | string |  |  |
        hostName | No | string |  |  |
        lastMod | No | string | date-time |  | datestamp for last update to this object
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        version | No | string |  |  |

.. _d_e926d3fa8701e0cc9c8ed1761b3255cd:

DR_Pub Model Structure
----------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        dcaeLocationName | No | string |  |  |
        feedId | No | string |  |  |
        lastMod | No | string | date-time |  | datestamp for last update to this object
        pubId | No | string |  |  |
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        username | No | string |  |  |
        userpwd | No | string |  |  |

.. _d_48cf328d246f41e1d11a09251b042f02:

DR_Sub Model Structure
----------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        bytes | No | array of string |  |  |
        dcaeLocationName | No | string |  |  |
        deliveryURL | No | string |  |  |
        feedId | No | string |  |  |
        lastMod | No | string | date-time |  | datestamp for last update to this object
        logURL | No | string |  |  |
        owner | No | string |  |  |
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        subId | No | string |  |  |
        suspended | No | boolean |  |  |
        use100 | No | boolean |  |  |
        username | No | string |  |  |
        userpwd | No | string |  |  |

.. _d_47d80e451933beb623fcf5257867cbcb:

DcaeLocation Model Structure
----------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        central | No | boolean |  |  |
        clli | No | string |  |  |
        dcaeLayer | No | string |  |  |
        dcaeLocationName | No | string |  |  |
        lastMod | No | string | date-time |  | datestamp for last update to this object
        local | No | boolean |  |  |
        openStackAvailabilityZone | No | string |  |  |
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        subnet | No | string |  |  |

.. _d_4ea0e7758a1f8502222793e4a13b04f7:

Dmaap Model Structure
---------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        accessKeyOwner | No | string |  |  |
        bridgeAdminTopic | No | string |  |  |
        dmaapName | No | string |  |  |
        drProvUrl | No | string |  |  |
        lastMod | No | string | date-time |  | datestamp for last update to this object
        loggingUrl | No | string |  |  |
        nodeKey | No | string |  |  |
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        topicNsRoot | No | string |  |  |
        version | No | string |  |  |

.. _d_289ad39619725df26c9ff382d4c97c75:

Feed Model Structure
--------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        asprClassification | No | string |  |  |
        bytes | No | array of string |  |  |
        feedDescription | No | string |  |  |
        feedId | No | string |  |  |
        feedName | No | string |  |  |
        feedVersion | No | string |  |  |
        formatUuid | No | string |  |  |
        lastMod | No | string | date-time |  | datestamp for last update to this object
        logURL | No | string |  |  |
        owner | No | string |  |  |
        publishURL | No | string |  |  |
        pubs | No | array of :ref:`DR_Pub <d_e926d3fa8701e0cc9c8ed1761b3255cd>` |  |  |
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        subs | No | array of :ref:`DR_Sub <d_48cf328d246f41e1d11a09251b042f02>` |  |  |
        subscribeURL | No | string |  |  |
        suspended | No | boolean |  |  |

.. _d_56ff81dc98986e27074d9be2731e3f4c:

MR_Client Model Structure
-------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        action | No | array of string |  |  | one or more actions from the set ('pub', 'sub', 'view') for which this client needs Permission
        clientIdentity | No | string |  |  | an AAF identity to be associated to an appropriate topic Role
        clientRole | No | string |  |  | an AAF Role to be granted an appropriate Permission.  If specified, takes precedence over clientIdentity, for backwards compatibility.
        dcaeLocationName | No | string |  |  | a tag indicating a logical deployment site
        fqtn | No | string |  |  | Fully Qualified Topic Name constructed by dbcapi
        lastMod | No | string | date-time |  | datestamp for last update to this object
        mrClientId | No | string |  |  | a unique identifier generated by dbcapi for this client
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        topicURL | No | string |  |  | the URL for a MR instance - typically in the same dcaeLocation - that this client should use to access the topic

.. _d_eec7176a0080debe1b19c2dad2e97c24:

MR_Cluster Model Structure
--------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        dcaeLocationName | No | string |  |  |
        fqdn | No | string |  |  |
        lastMod | No | string | date-time |  |
        replicationGroup | No | string |  |  |
        sourceReplicationPort | No | string |  |  |
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        targetReplicationPort | No | string |  |  |
        topicPort | No | string |  |  |
        topicProtocol | No | string |  |  |

.. _d_08fb211d40d6deb9b6e04b000fd988e4:

MirrorMaker Model Structure
---------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        lastMod | No | string | date-time |  | datestamp for last update to this object
        mmName | No | string |  |  |
        sourceCluster | No | string |  |  |
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        targetCluster | No | string |  |  |
        topicCount | No | integer | int32 |  |
        topics | No | array of string |  |  |
        whitelistUpdateJSON | No | string |  |  |

.. _d_2e99841971da81b9d240071b86bf168d:

Topic Model Structure
---------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        clients | No | array of :ref:`MR_Client <d_56ff81dc98986e27074d9be2731e3f4c>` |  |  | an array of `MR_Client` objects associated to this `Topic`
        formatUuid | No | string |  |  | a reference to an identifier that describes a data format used for this `Topic`
        fqtn | No | string |  |  | Fully Qualified Topic Name constructed by dbcapi, following the rules for `fqtnStyle`
        fqtnStyle | No | string |  | {'enum': ['FQTN_NOT_SPECIFIED', 'FQTN_LEGACY_FORMAT', 'FQTN_PROJECTID_FORMAT', 'FQTN_PROJECTID_VERSION_FORMAT']} | the construction rule for the `fqtn` field
        globalMrURL | No | string |  |  | the URL of an outside MR instance
        lastMod | No | string | date-time |  | datestamp for last update to this object
        owner | No | string |  |  | a label used to identify who requested this `Topic` to be provisioned.  In the future this may be an AAF Identity.
        partitionCount | No | string |  |  | the kafka attribute for specifying the number of partitions
        publisherRole | No | string |  |  | a value generated by dbcapi, this AAF Role has permission to publish to this `Topic`
        replicationCase | No | string |  | {'enum': ['REPLICATION_NOT_SPECIFIED', 'REPLICATION_NONE', 'REPLICATION_EDGE_TO_CENTRAL', 'REPLICATION_EDGE_TO_CENTRAL_TO_GLOBAL', 'REPLICATION_CENTRAL_TO_EDGE', 'REPLICATION_CENTRAL_TO_GLOBAL', 'REPLICATION_GLOBAL_TO_CENTRAL', 'REPLICATION_GLOBAL_TO_CENTRAL_TO_EDGE', 'REPLICATION_EDGE_TO_FQDN', 'REPLICATION_FQDN_TO_EDGE', 'REPLICATION_FQDN_TO_GLOBAL', 'REPLICATION_GLOBAL_TO_FQDN', 'REPLICATION_EDGE_TO_FQDN_TO_GLOBAL', 'REPLICATION_GLOBAL_TO_FQDN_TO_EDGE']} | An indicator for how this `Topic` should be replicated when there are more than one `MR_Cluster` instances
        replicationCount | No | string |  |  | the kafka attribute for specifying replication within an `MR_Cluster` instance
        status | No | string |  | {'enum': ['EMPTY', 'NEW', 'STAGED', 'VALID', 'INVALID', 'DELETED']} |
        subscriberRole | No | string |  |  | a value generated by dbcapi, this AAF Role has permission to subscribe to this `Topic`
        tnxEnabled | No | string |  |  |
        topicDescription | No | string |  |  | a description of what this Topic is used for
        topicName | No | string |  |  | the short name used by humans, and utilized to construct the `FQTN`
        version | No | string |  |  | a hook for any versioning needed for managing a `Topic` over time

