/** Kinetic Hybrid PR/PMR Quad Tree
 * 
 * Copyright (c) 2005, Christopher A. Watford
 * All rights reserved. See LICENSE for more details.
 *  
 * Created on 26 September 2005
 * @author Christopher A. Watford
 * 
 * $Id: IPaintable.java,v 1.1 2005/10/31 05:02:01 caw Exp $
 */

package watford.util.quadtree;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface IPaintable {
	public void paint(Graphics g, Rectangle bounds, boolean debug);
}
