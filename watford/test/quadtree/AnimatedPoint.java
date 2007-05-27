/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: AnimatedPoint.java,v 1.2 2005/11/11 00:45:54 caw Exp $
 */

package watford.test.quadtree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import watford.util.quadtree.AbstractKineticObject;
import watford.util.quadtree.AbstractSpatialSet;
import watford.util.quadtree.IPaintable;

public class AnimatedPoint extends AbstractKineticObject implements IPaintable {
	private Point loc;
	
	public AnimatedPoint(Point loc) {
		this(loc, null);
	}

	public AnimatedPoint(Point loc, AbstractSpatialSet parent) {
		super(parent);
		this.loc = loc;
	}
	
	public int getX( ) {
		return this.loc.x;
	}
	public int getY( ) {
		return this.loc.y;	
	}
	public Point getPoint( ) {
		return new Point(this.loc);
	}
	public void setPoint(Point p) {
		this.translate(p);
	}
	public void translate(Point p) {
		this.loc = p;
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
	}
	public void translate(int dx, int dy) {
		this.loc.translate(dx,dy);
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
	}

	public boolean intersects(GeneralPath path) {
		return path.intersects(new Rectangle(this.loc.x,this.loc.y,1,1));
	}
	
	public boolean intersects(Rectangle rect) {
		return rect.contains(this.loc);
	}
	
	public boolean intersects(Point pnt) {
		return (this.loc.distance(pnt) == 0.0);
	}
	
	public boolean containedFullyBy(Rectangle rect) {
		return rect.contains(this.loc);
	}

	public boolean containedPartiallyBy(Rectangle rect) {
		return rect.contains(this.loc);
	}
	
	public Color getColor() {
		return Color.red;
	}

	public void paint(Graphics g, Rectangle area, boolean dontColor) {
		if(!dontColor)
			g.setColor(getColor());
		
		if(area.contains(this.loc))
			g.fillOval(this.loc.x, this.loc.y, 2, 2);
	}
	
	public Rectangle getBounds( ) {
		return new Rectangle(this.loc.x, this.loc.y, 0, 0);
	}

	public int pointCount() {
		return 1;
	}

	public Point centroid() {
		return this.loc;
	}
	
	public GeneralPath points() {
		return null;
	}

	public void warpTo(int x, int y) {
		this.loc.x = x;
		this.loc.y = y;
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
	}
}
