/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: INoClipping.java,v 1.2 2005/10/31 05:21:58 caw Exp $
 */
package watford.util.quadtree;

/**
 * Have an object implement INoClipping to be ignored in the
 * Quad Tree's intersection routines. Allows for things like
 * pheramone trails, overlays, flying objects, etc.
 * 
 * @author Christopher A. Watford
 */
public interface INoClipping {

}
