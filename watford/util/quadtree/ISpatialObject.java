/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: ISpatialObject.java,v 1.2 2005/11/11 00:41:15 caw Exp $
 */

package watford.util.quadtree;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.Collection;

public interface ISpatialObject {
	public void translate(Point p);
	public void translate(int dx, int dy);
	
	public abstract Rectangle getBounds();

	public boolean containedFullyBy(Rectangle rect);
	public boolean containedPartiallyBy(Rectangle rect);
	public boolean intersects(GeneralPath path);
	public boolean intersects(Rectangle rect);
	public boolean intersects(Point pnt);
	
	public int pointCount();
	public Point centroid();
	public GeneralPath points();
	public void warpTo(int x, int y);
	
	public void deleteParent(AbstractSpatialSet set);
	public void addParent(AbstractSpatialSet set);
	public boolean hasParent(AbstractSpatialSet set);
	public Collection getParents();
	public AbstractSpatialSet getParent();
}
