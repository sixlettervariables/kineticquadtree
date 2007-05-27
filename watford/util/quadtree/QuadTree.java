/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: QuadTree.java,v 1.3 2005/11/08 15:39:27 caw Exp $
 */

package watford.util.quadtree;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/** Kinetic Hybrid PR (Point-Region) and PMR (Polygon-Map-Random) Quad Tree
 * This class is a working framework for a Kinetic PR/PMR Quad tree.
 * Basically your standard Quad tree is static and filled once, or
 * filled continuously without any removals. The Kinetic PR/PMR Quad tree
 * takes into account objects in motion, along with static objects.
 * Each object in the quad tree extends the Observable abstract class,
 * which allows the quad tree nodes to be notified when an object
 * moves in the coordinate space.
 * 
 * The PMR portion of the Quad tree stores a pointer to a polygon into
 * every node the polygon reports it crosses. Currently this allows for
 * efficient lookups of what lines are in what bounding boxes. However,
 * collision detection is not quite up to par as the entire polyline is
 * checked during collision detection rather than just the segment inside
 * the bounding area. Detaching and reattaching segments with respect to
 * the kinetic nature of the tree would be cumbersome and slow. The main
 * issue is with overly complex polylines which double back on themselves
 * multiple times over.
 * 
 * The kinetic nature of this quad tree only takes action when the update notice
 * informs a node that an object has moved outside the node's boundaries.
 * When this happens a search up the tree is taken for a parent node
 * which now bounds the object. This node is then given ownership and
 * the node is added as if it was freshly inserted. This approach allows
 * for fast inserts as nodes move between adjacent nodes a majority of
 * the time, so in the average case the search only needs to look up to
 * it's parent and at most one more parent higher.
 * 
 * After an object moves from one node to another, the parent added
 * the object then calls balance, which takes a look at the new sub-structure.
 * If the new sub-structure is too large for the number of nodes it
 * contains, it removes all of the children and becomes a leaf. If
 * it contains too few, it looks to every leaf or child containing a leaf
 * and applies balance, until all nodes report too-few objects. Initially
 * I was worried about thrashing in the data structure, i.e. balance
 * occuring too often, however, empirical tests of 4000 objects contained
 * in a 500x500 area showed that there was no slow down in performance
 * (for a machine that could reasonably be expected to render 4000 objects
 * moving in random directions or in guided directions). Even when
 * restrictions on collisions were introduced, the quad tree algorithm
 * allowed for efficient lookups and detection, making this additional
 * step a non-issue.
 * 
 * My reference for this data structure was a paper and demo by
 * Ransom Kershaw Winder (2 Dec 2000) entitled "The Kinetic PR Quadtree".
 * His implementation makes use of prio-queues and MVCC tags to ensure
 * data is updated in order, along with the structure of the tree. His
 * implementation provides for a thread safe version, allowing the
 * object update thread or threads to operate independently of the
 * quadtree balancing thread. With MVCC-tagging the problem of out-of-order
 * or invalid updates are resolved with a simple age parameter. The
 * prio-queue ignores all entries who's objects have a larger age than the
 * key of the entry. With this setup, the tree can effectively schedule
 * its balances en masse and at its own discresion. While I initially
 * attempted to implement this idea, I found that context switching between
 * two threads and the addition of multiple schedules along with synchronization
 * to be far more of an issue than performance in both terms of time and
 * in necessity.
 * 
 * Winder, Ransom Kershaw. "The Kinetic Quadtree." 2 Dec. 2000.
 * 	<http://www.cs.umd.edu/~mount/Indep/Ransom/>
 * 
 * @author Christopher A. Watford
 *
 */
public class QuadTree implements ISpatialSet {
	public final static int QUADTREE_DEFAULT_SPLIT = 4;
	
	public final static int QUADTREENODE_NW = 0;
	public final static int QUADTREENODE_NE = 1;
	public final static int QUADTREENODE_SE = 2;
	public final static int QUADTREENODE_SW = 3;

	private Rectangle bounds;
	private QuadTreeNode root;
	private int splitSize;
	private boolean drawNodes;
	
	public QuadTree(Rectangle bounds) {
		this(bounds, QUADTREE_DEFAULT_SPLIT);
	}
	
	public QuadTree(Rectangle bounds, int splitSize) {
		this.drawNodes = false;
		this.bounds = bounds;
		this.splitSize = splitSize;
		this.root = new QuadTreeNode(this, null, bounds, splitSize);		
	}

	public int size( ) {
		return this.root.size();
	}
	
	public boolean isEmpty( ) {
		return this.root.isEmpty();
	}
	
	public int getSplitSize( ) {
		return this.splitSize;
	}
	
	public Rectangle getBounds( ) {
		return this.bounds;
	}
	
	public boolean geometryIntersects(Point p) {
		return this.root.geometryIntersects(p);
	}

	public boolean geometryIntersects(Rectangle r) {
		return this.root.geometryIntersects(r);
	}
	
	public boolean geometryIntersects(ISpatialObject obj) {
		return this.root.geometryIntersects(obj);
	}
	
	public boolean geometryContains(Point p) {
		return this.bounds.contains(p);
	}

	public boolean geometryContains(Rectangle r) {
		return this.bounds.contains(r);
	}

	public boolean geometryContains(ISpatialObject obj) {
		return this.bounds.contains(obj.getBounds());
	}
	
	public boolean contains(ISpatialObject o) {
		return this.root.contains(o);
	}
	
	public int objectsInside(List objects, Rectangle rect) {
		return this.root.objectsInside(objects, rect);
	}

	public int objectsInsideLike(List objects, Rectangle rect, Class cc) {
		return this.root.objectsInsideLike(objects, rect, cc);
	}
	
	public boolean add(ISpatialObject obj) {
		return this.root.add(obj);
	}
	
	public boolean remove(ISpatialObject o) {
		return this.root.remove(o);
	}

	public void clear( ) {
		this.root.clear();
	}
	
	public void paint(Graphics g, Rectangle viewport) {
		this.root.paint(g, viewport, drawNodes);
	}

	public boolean isDrawNodes() {
		return drawNodes;
	}

	public void setDrawNodes(boolean drawNodes) {
		this.drawNodes = drawNodes;
	}
	
	public void toList(List objects) {
		this.root.toList(objects);
	}
}
