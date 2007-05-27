/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: AbstractPolyline.java,v 1.2 2005/11/11 04:48:30 caw Exp $
 */

package watford.util.quadtree;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Observable;

/**
 * 
 * @author Christopher A. Watford
 *
 */
public abstract class AbstractPolyline extends Observable
	implements ISpatialObject, Serializable {

	private transient Hashtable parents;
	protected int[] x, y;
	private int size;
	protected int count;
	
	protected static final int POLYLINE_DEFAULT_FILL = 32;
	
	public AbstractPolyline( ) {
		this.count = 0;
		this.size = POLYLINE_DEFAULT_FILL;
		this.x = new int[this.size];
		this.y = new int[this.size];
		this.parents = new Hashtable();
	}

	public AbstractPolyline(int[] xx, int[] yy, int count) {
		if(xx.length < count || yy.length < count)
			throw new IllegalArgumentException("Arrays passed to constructor have less points than <count> requires!");
		
		this.count = count;
		this.size = (count * 3) / 2;
		this.x = new int[this.size];
		this.y = new int[this.size];
		this.parents = new Hashtable();

		System.arraycopy(xx, 0, this.x, 0, count);
		System.arraycopy(yy, 0, this.y, 0, count);
	}
	
	public AbstractPolyline(AbstractSpatialSet parent) {
		this();
		
		addParent(parent);
	}
	
	public AbstractPolyline(AbstractSpatialSet[] parent, int[] xx, int[] yy, int count) {
		this(xx, yy, count);
		
		for(int ii = 0; ii < parent.length; ii++)
			addParent(parent[ii]);
	}

	
	public void add(int x, int y) {
		this.x[this.count] = x;
		this.y[this.count] = y;
		
		if(++this.count == this.size) {
			int[] xx = new int[this.size*2],
				yy = new int[this.size*2];
			
			System.arraycopy(this.x, 0, xx, 0, this.count-1);
			System.arraycopy(this.y, 0, yy, 0, this.count-1);
			
			this.x = xx;
			this.y = yy;
			this.size *= 2;
		}
	}

	public void add(Point p) {
		this.add(p.x,p.y);
	}
	
	public abstract void updateGeneralPath();
	
	public abstract boolean intersects(GeneralPath path);
	public abstract boolean intersects(Rectangle rect);
	public abstract boolean intersects(Point pnt);
	public abstract boolean containedPartiallyBy(Rectangle rect);
	public abstract Rectangle getBounds();
	public abstract void translate(Point p);
	public abstract void translate(int dx, int dy);
	
	public final void deleteParent(AbstractSpatialSet p) {
		this.parents.remove(p);
		this.deleteObserver(p);
	}

	public final void addParent(AbstractSpatialSet p) {
		this.parents.put(p,p);
		this.addObserver(p);
	}
	
	public final boolean hasParent(AbstractSpatialSet p) {
		if(parents == null)
			parents = new Hashtable();
		return parents.containsKey(p);
	}
	
	public final Collection getParents() {
		return parents.values();
	}
	
	public AbstractSpatialSet getParent() {
		return null;
	}

	public int pointCount() {
		return this.count;
	}

	public Point centroid() {
		return new Point(x[count-1],y[count-1]);
	}
}
