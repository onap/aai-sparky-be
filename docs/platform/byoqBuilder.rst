.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Sparky - BYOQ Builder
=======================

*BYOQ Builder* Overview
~~~~~~~~~~~~~~~~~~~~~~~~~

*BYOQ Builder* provides a way to easily create complex queries through a visual builder

.. image:: images/aai-ui-byoq-builder.JPG
   :scale: 100 %
   :alt: alternate text
   :align: center

*BYOQ Builder* Features
~~~~~~~~~~~~~~~~~~~~~~~~~
With BYOQ Builder UI, users can:
  * Click on a tile for a configured node-type and view the instance data

Navigation to *BYOQ Builder*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The *BYOQ Builder* view can be reached by clicking on the BYOQ Builder hamburger menu item

.. image:: images/byoq-builder-hamburger.JPG
   :scale: 100 %
   :alt: alternate text
   :align: center

How to use *BYOQ Builder*
~~~~~~~~~~~~~~~~~~~~~~~~~~~
1. On the BYOQ Builder page click 'Start Building' to open the modal with all node types, to choose a starting node for your query

.. image:: images/byoq-builder-view-1.JPG
   :scale: 100 %
   :alt: alternate text
   :align: center

2. In this example I chose generic-vnf and decided to filter on one of it's properties by clicking options and entering a filter

.. image:: images/byoq-builder-view-2.JPG
   :scale: 100 %
   :alt: alternate text
   :align: center

The little blue + box allows for adding OR filter values (but I don't use it here)

.. image:: images/byoq-builder-view-3.JPG
   :scale: 100 %
   :alt: alternate text
   :align: center

3. Scroll the modal to the bottom and click submit, you will see the element you chose and a DSL query being built

.. image:: images/byoq-builder-view-4.JPG
   :scale: 100 %
   :alt: alternate text
   :align: center

4. From here you can add, remove, or edit via the icons on the node, in this case I choose + for add check a few more nodes in my query path

.. image:: images/byoq-builder-view-5.JPG
    :scale: 100 %
    :alt: alternate text
    :align: center

5. After submitting that I can see the query being built at the top and it showing the same visually

.. image:: images/byoq-builder-view-6.JPG
    :scale: 100 %
    :alt: alternate text
    :align: center

6. You can continue this process of adding/deleting/editing and adding filters until you have the final query for your desired output.

At this point you would click Run Query or Manually Edit and Run Query if you want to make manual edits.

After this is done, your qury will be ran and results returned.

.. image:: images/byoq-builder-view-7.JPG
    :scale: 100 %
    :alt: alternate text
    :align: center

.. image:: images/byoq-builder-view-8.JPG
    :scale: 100 %
    :alt: alternate text
    :align: center

