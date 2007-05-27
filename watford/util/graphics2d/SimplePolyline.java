/** Simple Polyline
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * < christopher.watford@gmail.com >
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The name of Christopher A. Watford may not be used to endorse or
 *       promote products derived from this software without specific prior
 *       written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * @author Christopher A. Watford
 */

package watford.util.graphics2d;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple representation of a Polyline to send to Graphics::drawPolyline().
 * Contains 2 arrays--X-coordinates and Y-coordinates--and a count of the
 * points to render. This allows the user to send in a vector with more
 * buckets than required for performance reasons.
 * 
 * @author Christopher A. Watford
 */
public class SimplePolyline implements Serializable {
	/* coordinates and number of valid points */
	private int[] xx, yy;
	private int points;
	private int size;
	
	private static final int DEFAULT_FILL = 15;
	
	/**
	 * Create a new SimplePolyline
	 */
	public SimplePolyline( ) {
		this.xx = new int[DEFAULT_FILL];
		this.yy = new int[DEFAULT_FILL];
		this.points = 0;
		this.size = DEFAULT_FILL;
	}

	/** A polyline of a known number of points
	 * @param x X-coordinates
	 * @param y Y-coordinates
	 * @param points Number of valid points, not necessarily the length of the arrays
	 */
	public SimplePolyline(int[] x, int[] y, int points) {
		this(x, y, points, points);
	}

	/** A polyline of a known number of points and a different max capacity
	 * @param x X-coordinates
	 * @param y Y-coordinates
	 * @param points Number of valid points, not necessarily the length of the arrays
	 * @param size Size of the arrays
	 */
	public SimplePolyline(int[] x, int[] y, int points, int size) {
		this.xx = x;
		this.yy = y;
		this.points = points;
		this.size = size;
	}
	
	/** A polyline of an absolute number of points,
	 * uses the minimum value of the lengths of the two arrays
	 * @param x X-coordinates
	 * @param y Y-coordinates
	 */
	public SimplePolyline(int[] x, int[] y) {
		this(x,y,Math.min(x.length,y.length));
	}
	
	/** A polyline of 1 point
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 */
	public SimplePolyline(int x, int y) {
		this();
		this.xx[0] = x;
		this.yy[0] = y;
		this.points = 1;
	}
	
	/**
	 * Add a new point onto the polyline
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 */
	public void add(int x, int y) {
		if(this.points == this.size)
			resize();
		
		this.xx[this.points] = x;
		this.yy[this.points] = y;
		this.points++;
	}
	
	/**
	 * Add the next line segment by translating from the last
	 * point
	 * @param dx Offset in the X direction
	 * @param dy Offset in the Y direction
	 */
	public void moveToOffset(int dx, int dy) {
		if(this.points > 0)
			add(this.xx[this.points-1] + dx, this.yy[this.points-1] + dy);
	}
	
	/**
	 * Remove the last point from the line.
	 */
	public void removeLast( ) {
		if(this.points > 0)
			this.points--;
	}
	
	private void resize( ) {
		int[] newX = new int[this.size * 2];
		int[] newY = new int[this.size * 2];
		
		System.arraycopy(xx, 0, newX, 0, this.size);
		System.arraycopy(yy, 0, newY, 0, this.size);
		
		this.size *= 2;
		this.xx = null; // get the GC to cough it up
		this.yy = null;
		this.xx = newX;
		this.yy = newY;
	}
	
	/**
	 * Get the X-coordinates and allocate it in a new array
	 * @return Newly alloced array containing the X-coordinates
	 */
	public int[] getX_alloc() {
		return (int[])this.xx.clone();
	}

	/**
	 * Get the Y-coordinates and allocate it in a new array
	 * @return Newly alloced array containing the Y-coordinates
	 */
	public int[] getY_alloc() {
		return (int[])this.yy.clone();
	}
	
	/**
	 * Get the X-coordinates
	 * @return Array containing the X-coordinates
	 */
	public int[] getX() {
		return this.xx;
	}

	/**
	 * Get the Y-coordinates
	 * @return Array containing the Y-coordinates
	 */
	public int[] getY() {
		return this.yy;
	}
	
	/**
	 * Line segment count
	 * @return Number of segments
	 */
	public int length() {
		return this.points-1;
	}

	/**
	 * Vertex count
	 * @return Number of vertices
	 */
	public int count() {
		return this.points;
	}
	
	/**
	 * Split this polyline into many polylines by clipping out
	 * a rectangular section.
	 * @param r Rectangular to clip from the current polyline
	 * @return An array of polylines resulting from the clipping
	 */
	public List clip(Rectangle r) {
		List polylines = new ArrayList();
		
		// temp arrays
		int[] XX = new int[points];
		int[] YY = new int[points];
		
		int newp = 0;
		for(int ii = 0; ii < points; ii++) {
			if(r.contains(xx[ii], yy[ii])) {
				if(newp > 0) {
					/* if we have more than one point in the previous
					 * line segment, add it to the return array
					 */
					polylines.add(new SimplePolyline(XX, YY, newp));
				}
				
				// reset xx and yy
				XX = new int[points - newp];
				YY = new int[points - newp];
				newp = 0;
			} else {
				// add a non-clipped point
				XX[newp] = xx[ii];
				YY[newp] = yy[ii];
				newp++;
			}
		}
		
		// get the trailing section added if there is one
		if(newp > 0)
			polylines.add(new SimplePolyline(XX, YY, newp));
		
		return polylines;
	}
	
	/** Draw a polyline to a canvas
	 * @param g Graphics canvas to draw the polyline to
	 */
	public void draw(Graphics g) {
		if(this.points > 1)
			g.drawPolyline(this.xx, this.yy, this.points);
	}
}