#Project description
Hello

This program is a basic raytracer rendering engine which was made using Java and the API provided by the OpenGL (JOGL) library.
Most of the work is done on implementing several intersection algorithms that add support for intersecting a ray with different types of geometry such as spheres, boxes, planes, and triangles.
The implementation of triangle intersection introduced support for any standard .obj file to be fed into the rendering engine and results could be seen once rendered. However, this implementation was significantly inefficient compared to object-order rendering. This is due to the inefficiency of image-order rendering.
Decrease in performance can be seen significantly once antialiasing is turned on on any scene.
The scenes are given to the program using xml files. This is useful due to the fact that a parser created in java can easily search for
tags in an xml file and interprete them accordingly.
#Supported geometry
* Spheres
* Planes
* Boxes
* Triangles (polygons)

#Features
* Supersampling (antialiasing)
* Fresnel reflection
* Lambertian Shading
* Blinn-Phong shading
* Shadows
* Perhaps more features will be added later!

#Credits
The basic structure of this program was prepared by Mr. Paul Kry (the instructor of the course) as well as the ideas behind the tasks to be completed.
