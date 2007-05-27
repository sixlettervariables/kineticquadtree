/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: AnimatedPolyline.java,v 1.3 2005/11/11 00:45:54 caw Exp $
 */
package watford.test.quadtree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import watford.util.quadtree.AbstractPolyline;
import watford.util.quadtree.AbstractSpatialSet;
import watford.util.quadtree.IPaintable;

public class AnimatedPolyline extends AbstractPolyline implements IPaintable {

	private GeneralPath line;
	private int lastX, lastY;
	private int lastCount;

	public AnimatedPolyline( ) {
		this.line = new GeneralPath();
		this.lastX = this.lastY = 0;
		this.lastCount = 0;
	}
	
	public AnimatedPolyline(int[] xx, int[] yy, int count) {
		this.lastX = xx[count-1];
		this.lastY = yy[count-1];
		this.lastCount = this.count;
		updateGeneralPath();
	}
	
	public AnimatedPolyline(AbstractSpatialSet parent) {
		super(parent);
		
		this.line = new GeneralPath();
		this.lastX = this.lastY = 0;
		this.lastCount = 0;
	}
	
	public AnimatedPolyline(AbstractSpatialSet parent, int[] xx, int[] yy, int count) {
		super(new AbstractSpatialSet[] { parent },xx,yy,count);

		this.lastX = xx[count-1];
		this.lastY = yy[count-1];
		this.lastCount = this.count;
		updateGeneralPath();
	}
	
	public void add(int xx, int yy) {
		if(lastCount > 0) {
			line.append(new Line2D.Float(lastX, lastY, (float)xx, (float)yy), false);
		}
		
		super.add(xx,yy);
		
		lastX = xx;
		lastY = yy;
		lastCount = this.count;
	}

	public void add(Point p) {
		this.add(p.x,p.y);
	}
	
	public void updateGeneralPath( ) {
		this.line = new GeneralPath();
		for(int ii = 1; ii < this.count; ii++) {
			float xx = x[ii-1],
				yy = y[ii-1];
			
			line.append(new Line2D.Float(xx, yy, (float)x[ii], (float)y[ii]), false);
		}
	}
	
	public boolean intersects(GeneralPath path) {
		return this.line.intersects(path.getBounds());
	}

	public boolean intersects(Rectangle rect) {
		return this.line.intersects(rect);
	}

	public boolean intersects(Point pnt) {
		return this.line.intersects((double)pnt.x, (double)pnt.y, 1, 1);
	}
	
	public boolean containedPartiallyBy(Rectangle rect) {
		return this.line.intersects(rect);
	}
	
	public boolean containedFullyBy(Rectangle rect) {
		return rect.contains(this.line.getBounds());
	}
	
	public Color getColor() {
		return Color.black;
	}

	public void paint(Graphics g, Rectangle area, boolean dontColor) {
		if(!dontColor)
			g.setColor(getColor());
		
		//XXX cheap trick to see if more than one QuadTreeNode
		//    contains us, which is the only instance we clip
		//    ourselves.
		if(dontColor)
			g.clipRect(area.x, area.y, area.width, area.height);
		
		if(g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D)g;
			g2.draw(this.line);
		} else {
			//g.drawPolyline(this.x, this.y, this.count);
		}
		
		if(dontColor)
			g.setClip(null);
	}

	public Rectangle getBounds() {
		return this.line.getBounds();
	}
	
	public GeneralPath points() {
		return this.line;
	}

	public void translate(Point p) {
		Point c = centroid();
		this.translate(p.x-c.x,p.y-c.y);
	}

	public void translate(int dx, int dy) {
		/*for(int ii = 0; ii < this.count; ii++) {
			this.x[ii] += dx;
			this.y[ii] += dy;
		}*/
		this.line.transform(AffineTransform.getTranslateInstance(dx,dy));
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
		//this
	}

	private static final long serialVersionUID = -6716900340192110826L;

	public void warpTo(int x, int y) {
		Point c = centroid();
		this.translate(x-c.x,y-c.y);
	}
}
