package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d( 1, 1, 1 );
    	this.min = new Point3d( -1, -1, -1 );
    }	

	@Override
	public void intersect(Ray ray, IntersectResult result) {
		// TODO: Objective 6: Finish this class
		double txmin, txmax, tymin, tymax, tzmin, tzmax, tmin, tmax;
		double xmin, xmax, ymin, ymax, zmin, zmax;
		Point3d e = ray.eyePoint;
		Vector3d direction = ray.viewDirection;
		xmin = this.min.x; xmax = this.max.x; ymin = this.min.y; ymax = this.max.y; zmin = this.min.z; zmax = this.max.z;
		if (direction.x < 0) {
			txmin = (xmax - e.x) / direction.x;
			txmax = (xmin - e.x) / direction.x;
		} else {
			txmin = (xmin - e.x) / direction.x;
			txmax = (xmax - e.x) / direction.x;
		}
		
		if (direction.y < 0) {
			tymin = (ymax - e.y) / direction.y;
			tymax = (ymin - e.y) / direction.y;
		} else {
			tymin = (ymin - e.y) / direction.y;
			tymax = (ymax - e.y) / direction.y;
		}
		
		if (direction.z < 0) {
			tzmin = (zmax - e.z) / direction.z;
			tzmax = (zmin - e.z) / direction.z;
		} else {
			tzmin = (zmin - e.z) / direction.z;
			tzmax = (zmax - e.z) / direction.z;
		}
		
		tmin = Math.max(txmin, tymin);
		tmin = Math.max(tmin, tzmin);
		tmax = Math.min(txmax, tymax);
		tmax = Math.min(tmax, tzmax);
		if (tmin < tmax) {
			if (tmin > 0) {
				if (tmin < result.t) {
					result.t = tmin;
					Point3d p = new Point3d();
					ray.getPoint(tmin, p);
					result.p.set(p);
					double epsilon = 1e-9;
					if (Math.abs(result.p.x - min.x) < epsilon) {
						result.n.set(-1,0,0);
					} else if (Math.abs(result.p.x - max.x) < epsilon) {
						result.n.set(1,0,0);
					} else if (Math.abs(result.p.y - min.y) < epsilon) {
						result.n.set(0,-1,0);
					} else if (Math.abs(result.p.y - max.y) < epsilon) {
						result.n.set(0,1,0);
					} else if (Math.abs(result.p.z - min.z) < epsilon) {
						result.n.set(0,0,-1);
					} else if (Math.abs(result.p.z - max.z) < epsilon) {
						result.n.set(0,0,1);
					}
					result.material = material;
					
				}
			}
		}
	}	

}
