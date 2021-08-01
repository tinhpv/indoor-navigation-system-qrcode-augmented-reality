<img src="https://i.imgur.com/vsYs4Cg.png" width=140/>
# INDOOR NAVIGATION
Provide service for navigation inside buildings
This systems provides some following functions:
- Find way inside a building
- Navigate user in a building using QR-Code (offline) and Augmented Reality (online).

Systems consists: 
- A web-admin to manage maps (add static map, add location point)
- A mobile app (Android) using for navigation

### Mobile App Screenshots
<img src="https://i.imgur.com/rFQf562.png"/>   <img src="https://i.imgur.com/vSUTRQJ.png"/> <img src="https://i.imgur.com/RVKvF6r.png"/> <img src="https://i.imgur.com/zl5pvHA.png" /> <img src="https://i.imgur.com/OG1SHzL.png" width=250 /> 

### QR-Code Navigation Approach
Reference: [“A Novel Method for Indoor Navigation Using QR Codes”](https://www.researchgate.net/publication/295210900_A_Novel_Method_for_Indoor_Navigation_Using_QR_Codes "“A Novel Method for Indoor Navigation Using QR Codes”"), Sujith Suresh, P.M. Rubesh Anand, and D. Sahaya Lenin
<img src="https://i.imgur.com/H6q6Tn5.png" />

### Augmented Reality Navigation
Technologies:
- [ARCore by Google](https://developers.google.com/ar "ARCore by Google")
- [Azure Spatial Anchors](https://azure.microsoft.com/en-us/services/spatial-anchors/ "Azure Spatial Anchors")

This feature requires some setups before working. In detail, we must collect spatial data and setup *anchors* at locations inside the building.

