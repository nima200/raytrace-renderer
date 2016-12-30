package comp557.a4;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.*;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name = "";
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;

	public Mesh() {
		super();
		this.soup = null;
	}			
	
	@Override
	public void intersect(Ray ray, IntersectResult result) {
		double epsilon = 1e-9;
		for (Mesh mesh: meshMap.values()) {
			for (int[] face: mesh.soup.faceList) {
				Vector3d v0 = new Vector3d(mesh.soup.vertexList.get(face[0]).p.x, mesh.soup.vertexList.get(face[0]).p.y, mesh.soup.vertexList.get(face[0]).p.z);
				Vector3d v1 = new Vector3d(mesh.soup.vertexList.get(face[1]).p.x, mesh.soup.vertexList.get(face[1]).p.y, mesh.soup.vertexList.get(face[1]).p.z);
				Vector3d v2 = new Vector3d(mesh.soup.vertexList.get(face[2]).p.x, mesh.soup.vertexList.get(face[2]).p.y, mesh.soup.vertexList.get(face[2]).p.z);
				
				Vector3d rayOrigin = new Vector3d(ray.eyePoint.x, ray.eyePoint.y, ray.eyePoint.z);
				Vector3d rayDirection = new Vector3d(ray.viewDirection.x, ray.viewDirection.y, ray.viewDirection.z);
				
				Vector3d v0v1 = new Vector3d(v1); v0v1.sub(v0);
				Vector3d v0v2 = new Vector3d(v2); v0v2.sub(v0);
				Vector3d normal = new Vector3d();
				normal.cross(v0v1, v0v2);
				
				double nDotDir = normal.dot(rayDirection);
				// Checking to make sure the ray and the triangle are not parallel.
				if (Math.abs(nDotDir) > epsilon) {
					// distance from plane to origin
					Vector3d v0SubOrigin = new Vector3d(v0);
					v0SubOrigin.sub(rayOrigin);
					double t = (normal.dot(v0SubOrigin)) / nDotDir;
					// Continuing only if triangle is in front of the camera
					if (t > 0) {
						if (t < result.t) {
							// Computing the intersection point
							Vector3d p = new Vector3d(rayDirection); 
							p.scale(t); 
							p.add(rayOrigin);
							
							// Inside-Outside test
							Vector3d perp = new Vector3d();
							
							Vector3d edge0 = new Vector3d(v1);
							edge0.sub(v0);
							Vector3d boundary0 = new Vector3d(p);
							boundary0.sub(v0);
							perp.cross(edge0, boundary0);
							// Only continuing if it was on the inner boundary of the first edge checked
							if (normal.dot(perp) > 0) {
								Vector3d edge1 = new Vector3d(v2);
								edge1.sub(v1);
								Vector3d boundary1 = new Vector3d(p);
								boundary1.sub(v1);
								perp.cross(edge1, boundary1);
								// Only continuing if it was on the inner boundary of the second edge checked
								if (normal.dot(perp) > 0) {
									Vector3d edge2 = new Vector3d(v0);
									edge2.sub(v2);
									Vector3d boundary2 = new Vector3d(p);
									boundary2.sub(v2);
									perp.cross(edge2, boundary2);
									// Only set result to consider this triangle as his if p was on the inner 
									// boundary of all 3 edges.
									if(normal.dot(perp) > 0) {
										result.t = t;
										result.p.set(p);
										normal.normalize();
//										normal.scale(-1);
										result.n.set(normal);
										result.material = material;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
