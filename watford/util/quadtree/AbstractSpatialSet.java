/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: AbstractSpatialSet.java,v 1.1 2005/10/31 05:02:01 caw Exp $
 */

package watford.util.quadtree;

import java.util.Observer;

/** AbstractSpatialSet is a helper class to show we need nodes that both
 * implement ISpatialSet and Observer. Polymorphism would help me out a
 * lot with this!
 * 
 * @author Christopher A. Watford
 */
public abstract class AbstractSpatialSet implements ISpatialSet, Observer {

}
