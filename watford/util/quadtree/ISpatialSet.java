/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: ISpatialSet.java,v 1.3 2005/11/08 15:39:27 caw Exp $
 */

package watford.util.quadtree;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * 
 * @author chriswatford
 *
 */
public interface ISpatialSet {
	/**
	 * Get the number of objects in the spatial set
	 * @return Object count
	 */
	public int size( );
	
	/**
	 * Get the bounds of the spatial set
	 * @return Rectangle representing the bounds of the spatial set
	 */
	public Rectangle getBounds();
	
	/**
	 * Does the spatial set contain any objects?
	 * @return Is the set empty?
	 */
	public boolean isEmpty( );
	
	/**
	 * Test for intersection of a point and any object inside
	 * the set.
	 * @param p Point of intersection
	 * @return Did we intersect?
	 */
	public boolean geometryIntersects(Point p);
	
	/**
	 * Test for intersection of a rectangle and any object inside
	 * the set.
	 * @param r Rectangle for intersection
	 * @return Did we intersect?
	 */
	public boolean geometryIntersects(Rectangle r);
	
	/**
	 * Test for intersection of a spatial object and any object
	 * inside the set.
	 * @param obj Object to test for intersection
	 * @return Did we intersect?
	 */
	public boolean geometryIntersects(ISpatialObject obj);
	
	/**
	 * Does the set contain, geometrically, the point
	 * @param p Point to test for containment.
	 * @return Could we contain this point?
	 */
	public boolean geometryContains(Point p);
	
	/**
	 * Does the set contain, geometrically, the rectangle
	 * @param r Rectangle to test for containment
	 * @return Could we contain this rectangle?
	 */
	public boolean geometryContains(Rectangle r);
	
	/**
	 * Does the set contain, geometrically, the spatial object
	 * @param obj Spatial object to test for containment
	 * @return Could we contain this object?
	 */
	public boolean geometryContains(ISpatialObject obj);
	
	/**
	 * Test if we currently contain the object
	 * @param o Object to test for containment
	 * @return Do we currently contain this object?
	 */
	public boolean contains(ISpatialObject o);
	
	/**
	 * Populates the list passed with all the objects inside the
	 * spatial set.
	 * 
	 * Written in tail recursive style to keep memory allocations down.
	 * @param objects List to fill with objects contained by the set
	 */
	public void toList(List objects);
	
	/**
	 * Populate the list with all the objects which fall inside
	 * the bounds (given the bounds fall inside the set).
	 * 
	 * If a null list is passed, objectsInside still runs, but will
	 * just return the count of objects inside the bounds.
	 * 
	 * Written in tail recursive style to keep memory allocations down.
	 * @param objects List passed in to contain the objects (may be null)
	 * @param bounds Bounds to return objects inside of
	 * @return Count of objects found within the bounds
	 */
	public int objectsInside(List objects, Rectangle bounds);
	
	/**
	 * Populate the list with all the objects which fall inside
	 * the bounds (given the bounds fall inside the set) which are of
	 * a specific class.
	 * 
	 * If a null list is passed, objectsInsideLike still runs, but will
	 * just return the count of objects inside the bounds that match
	 * the class.
	 * 
	 * Written in tail recursive style to keep memory allocations down.
	 * @param objects List passed in to contain the objects (may be null)
	 * @param bounds Bounds to return objects inside of
	 * @param cc Class the objects must be to be included
	 * @return Count of objects found within the bounds matching the given class
	 */
	public int objectsInsideLike(List objects, Rectangle bounds, Class cc);
	
	//XXX it'd be hard to define how a quad tree iterator
	// should behave, so I'm leaving it out.
	//public Iterator iterator();
	
	/**
	 * Add a spatial object to the set
	 * @param obj Object to add to the set
	 * @return True if the insert was successful
	 */
	public boolean add(ISpatialObject obj);
	
	/**
	 * Removes a spatial object from the set
	 * @param o Object to remove from the spatial set
	 * @return True if the object was removed
	 */
	public boolean remove(ISpatialObject o);

	/**
	 * Empty the spatial set of all objects
	 */
	public void clear();
}
