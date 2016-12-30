package comp557.a4;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable {
    
	/** Radius of the sphere. */
	public double radius = 1;
    
	/** Location of the sphere center. */
	public Point3d center = new Point3d( 0, 0, 0 );
    
    /**
     * Default constructor
     */
    public Sphere() {
    	super();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere( double radius, Point3d center, Material material ) {
    	super();
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    	
        // TODO: Objective 2: finish this class
    	double t1, t2;
    	Point3d e = ray.eyePoint;
    	Vector3d direction = new Vector3d(ray.viewDirection);
    	double a = direction.dot(direction);
    	double b = 2 * (((e.x - this.center.x)* direction.x) + ((e.y - this.center.y) * direction.y) + ((e.z - this.center.z) * direction.z));
    	double c = ((e.x - this.center.x) * (e.x - this.center.x)) + ((e.y - this.center.y) * (e.y - this.center.y)) + ((e.z - this.center.z) * (e.z - this.center.z)) - (this.radius * this.radius);
    	double discriminant = b * b - 4 * a * c;
    	if (a != 0 && discriminant >= 0) {
    		t1 = - 0.5 * (b + Math.sqrt(discriminant)) / a;
        	t2 = - 0.5 * (b - Math.sqrt(discriminant)) / a;
        	if (t1 > t2) {
        		t1 = t2;
        	}
        	if (t1 > 0) {
        		if (t1 < result.t) {
        			result.t = t1;
        			Vector3d normal = new Vector3d(direction);
        			normal.scale(t1);
        			normal.add(e);
        			result.p = new Point3d(normal);
        			normal.sub(this.center);
        			normal.scale(1.0/this.radius);
        			result.n = normal;
        			result.material = material;
        		}
        	}
    	}  	
    }
}
