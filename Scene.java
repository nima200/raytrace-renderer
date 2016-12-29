package comp557.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.*;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /** List of surfaces in the scene */
    public List<Intersectable> surfaceList = new ArrayList<Intersectable>();
	
	/** All scene lights */
	public Map<String,Light> lights = new HashMap<String,Light>();

    /** Contains information about how to render the scene */
    public Render render;
    
    /** The ambient light colour */
    public Color3f ambient = new Color3f();

    public int refRecursion = 0;
    /** 
     * Default constructor.
     */
    public Scene() {
    	this.render = new Render();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel) {
 
        Camera cam = render.camera; 
        int width = cam.imageSize.width;
        int height = cam.imageSize.height;
        
        render.init(width, height, showPanel);
        
        for ( int i = 0; i < height && !render.isDone(); i++ ) {
            for ( int j = 0; j < width && !render.isDone(); j++ ) {
            	Color3f[][] sampleColors = new Color3f[render.samples][render.samples];
            	for (int p = 0; p < render.samples; p++) {
            		for (int q = 0; q < render.samples; q++) {
            			double[] offset = makeUniformOffset(render.samples, p, q);
                		Ray ray = new Ray();
                    	generateRay(j, i, offset, cam, ray);
                    	IntersectResult result = new IntersectResult();
                    	for (Intersectable surface : surfaceList) {
                    		surface.intersect(ray, result);
                    	}
                    	Color3f color = new Color3f(render.bgcolor);
                        if (result.t<Double.POSITIVE_INFINITY) {
                        	// TODO: Shading
//                        	System.out.println(this.refRecursion);
                        	color.set(calculateShading(ray, this.refRecursion, result));
                        }
                        sampleColors[p][q] = color;
            		}  
            		
            	}
            	Color3f averageColor = averageColors(sampleColors);
            	averageColor.clampMax(1);
            	int r = (int)(255*averageColor.x);
                int g = (int)(255*averageColor.y);
                int b = (int)(255*averageColor.z);
                int a = 255;
                int argb = (a<<24 | r<<16 | g<<8 | b);  
                render.setPixel(j, i, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray) {
		 
		Vector3d basisU = new Vector3d();
		Vector3d basisV = new Vector3d();
		Vector3d basisW = new Vector3d();
		
		double l = -cam.imageSize.width/2.0;
		double r = cam.imageSize.width/2.0;
		double b = cam.imageSize.height/2.0;
		double t = -cam.imageSize.height/2.0;
		double distance = cam.imageSize.getHeight()/(2*Math.tan(Math.toRadians(cam.fovy/2)));
		
		basisW.set(cam.from.x - cam.to.x, cam.from.y - cam.to.y, cam.from.z - cam.to.z);
		basisW.normalize();
		basisU.cross(cam.up, basisW);
		basisU.normalize();
		basisV.cross(basisW, basisU);
		basisV.normalize();
		
		Point3d e = cam.from;
		Point3d s = new Point3d();
		double u = l + (r-l)*(i+offset[0])/cam.imageSize.getWidth();
		double v = b + (t-b)*(j+offset[1])/cam.imageSize.getHeight();
		
		s.x = e.x + u*basisU.x + v*basisV.x - distance*basisW.x;
		s.y = e.y + u*basisU.y + v*basisV.y - distance*basisW.y;
		s.z = e.z + u*basisU.z + v*basisV.z - distance*basisW.z;

		Point3d p = e;
		Vector3d d = new Vector3d(s.x - e.x, s.y - e.y, s.z - e.z);
		ray.set(p, d);
	}

	/**
	 * Shoot a shadow ray in the scene and get the result.
	 * 
	 * @param result Intersection result from raytracing. 
	 * @param light The light to check for visibility.
	 * @param root The scene node.
	 * @param shadowResult Contains the result of a shadow ray test.
	 * @param shadowRay Contains the shadow ray used to test for visibility.
	 * 
	 * @return True if a point is in shadow, false otherwise. 
	 */
	public static boolean inShadow(final IntersectResult result, final Light light, final List<Intersectable> surfaceList, IntersectResult shadowResult, Ray shadowRay) {
		
		// TODO: Objective 5: finish this method and use it in your lighting computation
		shadowRay.viewDirection.set(light.from.x - result.p.x, light.from.y - result.p.y, light.from.z - result.p.z);
//		shadowRay.viewDirection.normalize();
		shadowRay.eyePoint.set(shadowRay.viewDirection);
		shadowRay.eyePoint.scale(1e-9);
		shadowRay.eyePoint.add(result.p);
		for (Intersectable surface: surfaceList) {
			surface.intersect(shadowRay, shadowResult);
		}
		if (shadowResult.t < 1) {
			return true;
		} else {
			return false;
		}
	}
	public static Color3f averageColors(Color3f[][] colors) {
		Color3f color = new Color3f();
		float red = 0.0f;
		float green = 0.0f;
		float blue = 0.0f;
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < colors.length; j++) {
				red += colors[i][j].x;
				green += colors[i][j].y;
				blue += colors[i][j].z;
			}
		}
		red = (float) (red / (Math.pow(colors.length, 2)));
		green = (float) (green / (Math.pow(colors.length, 2)));
		blue = (float) (blue / (Math.pow(colors.length, 2)));
		color.set(red, green, blue);
		return color;
	}
	public static double[] makeUniformOffset(int samples, int p, int q) {
		double[] offsetPair = new double[2];
		offsetPair[0] = (p + 0.5) / samples;
		offsetPair[1] = (q + 0.5) / samples;
		return offsetPair;
	}
	public Color3f calculateShading (Ray ray, int recursionLevel, IntersectResult result) {
		Color3f color = new Color3f();
		color.set(this.ambient.x*result.material.diffuse.x, this.ambient.y*result.material.diffuse.y, this.ambient.z*result.material.diffuse.z);
    	result.n.normalize();
    	for (Light light : lights.values()) {
    		
    		IntersectResult shadowResult = new IntersectResult();
    		Ray shadowRay = new Ray();
    		if (!inShadow(result, light, surfaceList, shadowResult, shadowRay)) {
    			Vector3d l = new Vector3d(light.from.x - result.p.x, light.from.y - result.p.y, light.from.z - result.p.z);
        		l.normalize();
        		// Lambertian Shading
        		double nDotL = result.n.dot(l);
        		double I = light.power;
        		Color3f diffuse = new Color3f();
        		diffuse.x = (float) (light.color.x * result.material.diffuse.x * I * Math.max(0.0f, nDotL));
        		diffuse.y = (float) (light.color.y * result.material.diffuse.y * I * Math.max(0.0f, nDotL));
        		diffuse.z = (float) (light.color.z * result.material.diffuse.z * I * Math.max(0.0f, nDotL));
        		//Blinn-Phong Shading
        		Vector3d v = new Vector3d(ray.eyePoint.x - result.p.x, ray.eyePoint.y - result.p.y, ray.eyePoint.z - result.p.z);
        		v.normalize();
        		Vector3d h = new Vector3d(v.x + l.x, v.y + l.y, v.z + l.z);
        		h.normalize();
        		double nDotH = result.n.dot(h);
        		Color3f specular = new Color3f();
        		specular.x = (float) (result.material.specular.x * I * Math.pow(Math.max(0.0f, nDotH), result.material.shinyness));
        		specular.y = (float) (result.material.specular.y * I * Math.pow(Math.max(0.0f, nDotH), result.material.shinyness));
        		specular.z = (float) (result.material.specular.z * I * Math.pow(Math.max(0.0f, nDotH), result.material.shinyness));
        		color.add(diffuse);
        		color.add(specular);
        		
        		
        		if (recursionLevel > 0) {
        			Color3f reflectedColor = new Color3f(render.bgcolor);
        			Ray reflectiveRay = new Ray();
        			reflectiveRay.eyePoint = result.p;
        			reflectiveRay.viewDirection.set(ray.viewDirection.x-(2*ray.viewDirection.dot(result.n))*result.n.x, 
        					ray.viewDirection.y-(2*ray.viewDirection.dot(result.n))*result.n.y,
        					ray.viewDirection.z-(2*ray.viewDirection.dot(result.n))*result.n.z);
        			IntersectResult reflectionResult = new IntersectResult();
        			for (Intersectable surface : surfaceList) {
                		surface.intersect(reflectiveRay, reflectionResult);
                	}
        			if (reflectionResult.t<Double.POSITIVE_INFINITY) {
        				reflectedColor.set(result.material.reflective.x*calculateShading(reflectiveRay, recursionLevel-1, reflectionResult).x,
        				result.material.reflective.y*calculateShading(reflectiveRay, recursionLevel-1, reflectionResult).y,
        				result.material.reflective.z*calculateShading(reflectiveRay, recursionLevel-1, reflectionResult).z);
        				color.add(reflectedColor);
        			}
        		}
    		}
    	}
		return color;
	}
}
