# Sparky - Inventory UI Service

### _VNFs_ Overview

_VNFs_ is an aggregation based view that provides aggregate counts of VNFs based off of provsioning status and orchestration status.  There is slide out filtering panel that will allow filtering on different values from the data set.  The filters can be selected and cleared, and will have an immediate impact on the charts and graphs in this view.

### Navigation to _VNFs_

1. Main navigation menu
2. Selecting a search result related to an aggregation result (e.g. <orch-status> and <prov-status> VNFs)

### Using _VNFs_

_VNFs_ is driven by using the search bar at the top of the UI to find and select aggregation queries. Once selected, the aggregation queries will be sent to the _Sparky_ backend component for processing. When a result set has been determined _VNFs_ will render the data.