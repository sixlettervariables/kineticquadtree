/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: QuadTreeNode.java,v 1.6 2005/11/10 07:31:16 caw Exp $
 */

package watford.util.quadtree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * QuadTreeNode has the meat and potatoes of the QuadTree class. The type of
 * node used by the Kinetic Hybrid PR/PMR Quad Tree is a bucket node which
 * contains all vertices and segments which pass through it, geometrically.
 * 
 * When the number of objects inside the node reaches the max split size
 * it divides into quadrants and populates its new child nodes.
 * 
 * The node recieves updates from the kinetic objects inside of it and notifies
 * its parent if an object moves outside its bounds so that the parent can
 * decide to balance itself.
 * 
 * @author Christopher A. Watford
 */
public class QuadTreeNode extends AbstractSpatialSet {
	private Rectangle bounds;
	private List leafMembers;
	private QuadTree tree;
	private QuadTreeNode parent;
	private QuadTreeNode[] children;
	private int splitAfter;
	private Color c;
	
	/**
	 * 
	 * @param tree
	 * @param parent
	 * @param bounds
	 * @param splitSize
	 */
	public QuadTreeNode(QuadTree tree, QuadTreeNode parent, Rectangle bounds, int splitSize) {
		this(tree, parent, bounds, splitSize, new ArrayList(splitSize));
	}
	
	/**
	 * 
	 * @param tree
	 * @param parent
	 * @param bounds
	 * @param splitSize
	 * @param members
	 */
	public QuadTreeNode(QuadTree tree, QuadTreeNode parent, Rectangle bounds, int splitSize, List members) {
		this.tree = tree;
		this.bounds = bounds;
		this.leafMembers = members;
		this.splitAfter = splitSize;
		this.children = null;
		this.parent = parent;
		this.c = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
	}

	public boolean isEmpty( ) {
		if(this.children == null)
			return this.leafMembers.isEmpty();
		
		synchronized(this.children) {
			for(int ii = 0; ii < this.children.length; ii++) {
				if(!this.children[ii].isEmpty())
					return false; 
			}
		}
		
		return true;
	}
	
	public boolean isLeaf( ) {
		return (this.children == null);
	}
	
	public int size( ) {
		if(this.children == null)
			return this.leafMembers.size();
		
		int size = 0;
		size += this.children[QuadTree.QUADTREENODE_NW].size();
		size += this.children[QuadTree.QUADTREENODE_NE].size();
		size += this.children[QuadTree.QUADTREENODE_SW].size();
		size += this.children[QuadTree.QUADTREENODE_SE].size();
		
		return size;
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
		if(this.children == null)
			return this.leafMembers.contains(o);
		
		synchronized(this.children) {
			for(int ii = 0; ii < this.children.length; ii++) {
				if(this.children[ii].contains(o))
					return true;
			}
		}
		
		return false;
	}

	public void toList(List ll) {
		if(ll == null)
			return;
		
		// recently rewritten to be tail recursize (kinda, no return)
		if(this.children == null) {
			ll.addAll(this.leafMembers);
		} else {
			synchronized(this.children) {
				this.children[QuadTree.QUADTREENODE_NW].toList(ll);
				this.children[QuadTree.QUADTREENODE_SW].toList(ll);
				this.children[QuadTree.QUADTREENODE_SE].toList(ll);
				this.children[QuadTree.QUADTREENODE_NE].toList(ll);
			}
		}
	}

	public synchronized boolean add(ISpatialObject obj) {
		if(!obj.containedPartiallyBy(this.bounds))
			return false;
		
		if(this.children == null)
		{
			// shortcut
			if(obj.hasParent(this))
				return true;
			
			if(this.leafMembers.size() == this.splitAfter) {
				// add the point to ourselves before we split
				// the algo will add the point to the correct child afterwards
				this.leafMembers.add(obj);
				
				// split the leaf
				int halfWidth = this.bounds.width / 2;
				int halfHeight = this.bounds.height / 2;
				int x = this.bounds.x, y = this.bounds.y;
				
				int fudgeWidth = this.bounds.width - (halfWidth * 2);
				int fudgeHeight = this.bounds.height - (halfHeight * 2);
				
				Rectangle nw = new Rectangle(x, y, halfWidth, halfHeight),
					ne = new Rectangle(x + halfWidth, y, halfWidth + fudgeWidth, halfHeight),
					se = new Rectangle(x + halfWidth, y + halfHeight, halfWidth + fudgeWidth, halfHeight + fudgeHeight),
					sw = new Rectangle(x, y + halfHeight, halfWidth, halfHeight + fudgeHeight);
				
				// don't set up this.children immediately
				QuadTreeNode[] childNodes = new QuadTreeNode[4];
				childNodes[QuadTree.QUADTREENODE_NW] =
					new QuadTreeNode(this.tree, this, nw, this.splitAfter);
				childNodes[QuadTree.QUADTREENODE_NE] = 
					new QuadTreeNode(this.tree, this, ne, this.splitAfter);
				childNodes[QuadTree.QUADTREENODE_SE] = 
					new QuadTreeNode(this.tree, this, se, this.splitAfter);
				childNodes[QuadTree.QUADTREENODE_SW] =
					new QuadTreeNode(this.tree, this, sw, this.splitAfter);
				
				int sz = this.leafMembers.size();
				for(int ii = 0; ii < sz; ii++) {
					ISpatialObject oo = (ISpatialObject)this.leafMembers.get(ii);
					
					if(oo.containedPartiallyBy(nw))
						childNodes[QuadTree.QUADTREENODE_NW].add(oo);
										
					if(oo.containedPartiallyBy(ne))
						childNodes[QuadTree.QUADTREENODE_NE].add(oo);
					
					if(oo.containedPartiallyBy(se))
						childNodes[QuadTree.QUADTREENODE_SE].add(oo);
					
					if(oo.containedPartiallyBy(sw))
						childNodes[QuadTree.QUADTREENODE_SW].add(oo);
				}
				
				this.leafMembers.clear();
				this.children = childNodes;
				
				return true;
			} else {
				obj.addParent(this);
				boolean added = this.leafMembers.add(obj); 
				return added;
			}
		} else {
			if(obj.pointCount() == 1) {
				Point p = obj.centroid();
				if(p.x < this.bounds.x + this.bounds.width/2) {
					if(p.y < this.bounds.y + this.bounds.height/2) {
						return this.children[QuadTree.QUADTREENODE_NW].add(obj);
					} else {
						return this.children[QuadTree.QUADTREENODE_SW].add(obj);
					}
				} else {
					if(p.y < this.bounds.y + this.bounds.height/2) {
						return this.children[QuadTree.QUADTREENODE_NE].add(obj);
					} else {
						return this.children[QuadTree.QUADTREENODE_SE].add(obj);
					}				
				}				
			} else {
				for(int ii = 0; ii < this.children.length; ii++) {
					if(obj.containedPartiallyBy(this.children[ii].getBounds()))
						if(!this.children[ii].add(obj))
							return false;
				}
			}
			
			return true;
		}
	}
	
	public int objectsInside(List objects, Rectangle rect) {
		if(!this.bounds.contains(rect) && !this.bounds.intersects(rect) ) {
			return 0;
		}
		
		//XXX PROFILE!! (40us/call)
		
		if(this.children == null) {
			int cnt = 0;
			synchronized(this.leafMembers) {
				int sz = this.leafMembers.size();
				for(int ii = 0; ii < sz; ii++) {
					ISpatialObject o = (ISpatialObject)this.leafMembers.get(ii);
					if(o.containedPartiallyBy(rect)) {
						if(objects != null)
							objects.add(o);
						cnt++;
					}
				}
			}
			
			return cnt;
		} else {
			int cnt = 0;
			synchronized(this.children) {
				for(int ii = 0; ii < this.children.length; ii++) {
					cnt += this.children[ii].objectsInside(objects, rect);
				}
			}
			
			return cnt;
		}
	}
	
	public int objectsInsideLike(List objects, Rectangle rect, Class cl) {
		if(!this.bounds.contains(rect) && !this.bounds.intersects(rect)) {
			return 0;
		}
		
		//XXX PROFILE!! (40us/call)
		
		if(this.children == null) {
			int cnt = 0;
			synchronized(this.leafMembers) {
				int sz = this.leafMembers.size();
				for(int ii = 0; ii < sz; ii++) {
					ISpatialObject o = (ISpatialObject)this.leafMembers.get(ii);
					if((o.getClass() == cl) && o.containedPartiallyBy(rect)) {
						if(objects != null)
							objects.add(o);
						cnt++;
					}
				}
			}
			
			return cnt;
		} else {
			int cnt = 0;
			synchronized(this.children) {
				for(int ii = 0; ii < this.children.length; ii++) {
					cnt += this.children[ii].objectsInsideLike(objects, rect, cl);
				}
			}
			
			return cnt;
		}
	}


	public boolean remove(ISpatialObject o) {
		if(this.children == null) {
			if(this.leafMembers.remove(o)) {
				o.deleteParent(this);
				return true;
			} else {
				return false;
			}
		}
		
		synchronized(this.children) {
			boolean contains = false;
			int sz = this.children.length;
			for(int ii = 0; ii < sz; ii++) {
				contains |= this.children[ii].contains(o);
				if(contains) {
					this.children[ii].remove(o);
					if(o.pointCount() == 1) {
						// shortcut for single point data
						o.deleteParent(this);
						return true;
					}
				}
			}
			
			if(contains)
				o.deleteParent(this);
			
			return contains;
		}
	}

	public synchronized void clear() {
		if(this.children == null) {
			this.leafMembers.clear();
		} else {
			for(int ii = 0; ii < this.children.length; ii++)
				this.children[ii].clear();
		}
	}

	/**
	 * 
	 */
	public synchronized void update(Observable o, Object arg) {
		ISpatialObject obj = (ISpatialObject)o;

		//XXX PROFILE! (60us/call)
		//XXX now 20us/call!!
		if(obj.pointCount() == 1) {
			// check if the node moved outside our bounds
			if(!obj.containedPartiallyBy(this.bounds)) {
				this.remove(obj);
				
				// walk up the tree until we find a node which
				// encompasses this node
				QuadTreeNode node = this.parent;
				while(node != null) {
					if(obj.containedPartiallyBy(node.bounds)) {
						node.add(obj);
						this.parent.balance();
						return;
					}
					
					node = node.parent;
				}
				
				// this is an error, node moved outside the quadtree bounds!!
				throw new IllegalStateException("ISpatialObject moved outside the bounds of the QuadTree!");
			}
		} else {
			QuadTreeNode node = this;
			while(!obj.containedFullyBy(node.bounds)) {
				node = node.parent;
				if(node == null) {
					tree.remove(obj);
					return;
//					throw new IllegalStateException("ISpatialObject not fully contained by anyone!");
				}
			}
			
			node.add(obj);
			
			// are we no longer part of the node?
			if(!obj.containedPartiallyBy(this.bounds)) {
				this.remove(obj);
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	public synchronized void balance( ) {
		if(this.children != null) {
			int sz = this.size();
			if(sz < this.splitAfter) {				
				// reclaim children
				List objects = new ArrayList(sz);
				for(int ii = 0; ii < this.children.length; ii++) {
					this.children[ii].toList(objects);
				}
				
				this.leafMembers = objects;
				this.children = null;
				
				sz = objects.size();
				for(int ii = 0; ii < sz; ii++) {
					((ISpatialObject)objects.get(ii)).addParent(this);
				}
			}
		}
	}
	
	public void paint(Graphics g, Rectangle viewport, boolean drawNodes) {
		if(viewport.contains(this.bounds) || viewport.intersects(this.bounds)) {
			if(this.children == null) {
				if(drawNodes) {
					g.setColor(Color.red);
					g.drawRect(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height);
					g.setColor(this.c);
				}
				
				// don't use iterator, slows us down
				int sz = this.leafMembers.size();
				for(int ii = 0; ii < sz; ii++) {
					Object obj = this.leafMembers.get(ii);
					if(obj instanceof IPaintable)
						((IPaintable)obj).paint(g, this.bounds, drawNodes);
				}
			} else {
				this.children[QuadTree.QUADTREENODE_NW].paint(g, viewport, drawNodes);
				this.children[QuadTree.QUADTREENODE_SW].paint(g, viewport, drawNodes);
				this.children[QuadTree.QUADTREENODE_NE].paint(g, viewport, drawNodes);
				this.children[QuadTree.QUADTREENODE_SE].paint(g, viewport, drawNodes);
			}
		}
	}

	// collision detection
	public boolean geometryIntersects(Point p) {
		if(this.bounds.contains(p)) {
			if(this.children == null) {
				int sz = this.leafMembers.size();
				for(int ii = 0; ii < sz; ii++) {
					ISpatialObject obj = (ISpatialObject)this.leafMembers.get(ii);
					
					if(obj instanceof INoClipping)
						continue;
					
					if(obj.intersects(p)) {
						return true;
					}
				}
			} else {
				if(p.x < this.bounds.x + this.bounds.width/2) {
					if(p.y < this.bounds.y + this.bounds.height/2) {
						return this.children[QuadTree.QUADTREENODE_NW].geometryIntersects(p);
					} else {
						return this.children[QuadTree.QUADTREENODE_SW].geometryIntersects(p);
					}
				} else {
					if(p.y < this.bounds.y + this.bounds.height/2) {
						return this.children[QuadTree.QUADTREENODE_NE].geometryIntersects(p);
					} else {
						return this.children[QuadTree.QUADTREENODE_SE].geometryIntersects(p);
					}				
				}
			}
		}
		
		return false;
	}
	
	public boolean geometryIntersects(Rectangle r) {
		if(this.bounds.contains(r) || this.bounds.intersects(r)) {
			if(this.children == null) {
				int sz = this.leafMembers.size();
				for(int ii = 0; ii < sz; ii++) {
					ISpatialObject obj = (ISpatialObject)this.leafMembers.get(ii);
					
					if(obj instanceof INoClipping)
						continue;
					
					if(obj.intersects(r)) {
						return true;
					}
				}
			} else {
				for(int ii = 0; ii < this.children.length; ii++) {
					if(this.children[ii].geometryIntersects(r))
						return true;
				}
			}
		}
		
		return false;
	}
	
	// collision detection
	public boolean geometryIntersects(ISpatialObject obj) {
		Rectangle objBounds = obj.getBounds();
		if(this.bounds.contains(objBounds) || this.bounds.intersects(objBounds)) {
			if(this.children == null) {
				int points = obj.pointCount();
				int sz = this.leafMembers.size();
				for(int ii = 0; ii < sz; ii++) {
					ISpatialObject oo = (ISpatialObject)this.leafMembers.get(ii);
					
					// can't intersect ourself
					if(oo == obj)
						continue;
					
					if(oo instanceof INoClipping)
						continue;
					
					// work with the single point when possible
					if(points == 1) {
						if(oo.intersects(objBounds)) {
							return true;
						}
					} else {
						if(oo.intersects(obj.points())) {
							return true;
						}
					}
				}
			} else {
				return
					this.children[QuadTree.QUADTREENODE_NW].geometryIntersects(obj) ||
					this.children[QuadTree.QUADTREENODE_NE].geometryIntersects(obj) ||
					this.children[QuadTree.QUADTREENODE_SE].geometryIntersects(obj) ||
					this.children[QuadTree.QUADTREENODE_SW].geometryIntersects(obj) ;
			}
		}
		
		return false;
	}

	public Rectangle getBounds() {
		return this.bounds;
	}
}