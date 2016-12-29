#sdaoject description
Hellsod
There were several key features, including the implementation of code for ray intersection with
  a) Different primitives like spheres, boxes, and planes.
  b) Polygons (which are composed of triangles).
The implementation of triangle intersection allowed for any standard .obj file to be fed into the rendering engine and results could
be seen once rendered. However, this implementation was significantly inefficient compared to object-order rendering. This is due tosdd
the inefficiency of image-order rendering.s
Decrease in performance can be seen significantly once antialiasing is turned on on any scene.
The scenes are given to the program using xml files. This is useful due to the fact that a parser created in java can easily search for
tags in an xml file and interprete them accordingly. ds
sadaasd
