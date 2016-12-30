package comp557.a4;

import java.awt.geom.Point2D;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d( 0, 1, 0 );
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult result ) {
    
        // TODO: Objective 4: finish this class   
    	double t;
    	Point3d e = ray.eyePoint;
    	Vector3d direction = ray.viewDirection;
    	double a = this.n.x;
    	double b = this.n.y;
    	double c = this.n.z;
    	double d = 0.0;
    	t = (-1.0) * (a*e.x + b * e.y + c * e.z + d) / (a*direction.x + b*direction.y + c*direction.z);
    	if (t > 0) {
    		if (t < result.t) {
    			result.t = t;
    			result.n = new Vector3d(0,1,0);

    			Point3d p = new Point3d(direction);
    			p.scale(t);
    			p.add(e);
    			result.p = p;
    			if (material2 != null) {
    				if (Math.abs(Math.floor(result.p.x)) % 2 == 0 && Math.abs(Math.floor(result.p.z)) % 2 == 0) {
        				result.material = material;
        			} else if (Math.abs(Math.floor(result.p.x)) % 2 == 0 && Math.abs(Math.floor(result.p.z)) % 2 == 1) {
        				result.material = material2;
        			} else if (Math.abs(Math.floor(result.p.x)) % 2 == 1 && Math.abs(Math.floor(result.p.z)) % 2 == 1) {
        				result.material = material;
        			} else if (Math.abs(Math.floor(result.p.x)) % 2 == 1 && Math.abs(Math.floor(result.p.z)) % 2 == 0) {
        				result.material = material2;
        			} else {
        				result.material = material;
        			}
    			} else {
    				result.material = material;
    			}
    			
    		}
    	}
    	
		
    }
    
}
