# Sparky - Inventory UI Service

### _View & Inspect_ Overview

_View & Inspect_ provides a graph based view of elements within AAI. A single entity is the entry point into each graph, and from that base element a graph is generated based off relationships. 

### Navigation to _View & Inspect_

The _View & Inspect_ view can be reached by two means:

1. Main navigation menu
2. Selecting a search result related to an entity instance (e.g. an entity called readme-entity)

### Using _View & Inspect_

_View & Inspect_ is driven by using the search bar at the top of the UI to find and select entity instances. Once an instance has been slected, a request is proccessed in _Sparky's_ backend component that generates a graph representation of the selected entity. The graph data is returned to _View & Inspect_ and rendered on screen.

#### Node Details

Upon node selection, the selected graph node details will appear in a panel to the right of the graph titled, _Node Details_.

### Interacting with the Graph

The graph can be panned by clicking and holding empty space amongst the graph and moving the mouse. This will pan the entire graph.
The graph can be zoomed in and out by using a mouse scroll wheel.
Nodes in the graph can be select by clicking on them.
Nodes in the graph can be moved by clicking, holding, and dragging them using the mouse.