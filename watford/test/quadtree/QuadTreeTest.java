/** Quad Tree Test Suite
 * PUBLIC DOMAIN - Christopher A. Watford (2005)
 */

package watford.test.quadtree;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import watford.util.quadtree.QuadTree;

/** Test suite for the quad tree
 * 
 * @author Christopher A. Watford
 *
 */
public class QuadTreeTest extends JFrame implements ActionListener, ItemListener {
	class QuadTreePanel extends JPanel implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 9133820596959964703L;
		public QuadTree kqt;
		public boolean follow = false;
		public Point followPoint;
		public QuadTreePanel( ) {
			setSize(700,700);
			kqt = new QuadTree(new Rectangle(700,700), TestConfig.QUADTREE_BUCKET);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		public void paint(Graphics g) {
			super.paint(g);
			kqt.paint(g, this.getVisibleRect());
			g.finalize();
		}
		
		public void mouseEntered(MouseEvent arg0) {
			this.follow = true;
		}

		public void mouseExited(MouseEvent arg0) {
			this.follow = false;
		}

		public void mouseClicked(MouseEvent arg0) { }
		public void mousePressed(MouseEvent arg0) { }
		public void mouseReleased(MouseEvent arg0) { }
		public void mouseDragged(MouseEvent arg0) { }

		public void mouseMoved(MouseEvent evt) {
			if(this.follow) {
				this.followPoint = new Point(evt.getX(),evt.getY());
			}
		}
	}
	
	QuadTreePanel qtp;
	AnimatedPoint[] points;
	AnimatedPolyline[] plines;
	javax.swing.Timer timer;
	long first = System.currentTimeMillis(); 
	long last;
	public QuadTreeTest() {
		super("Quad Tree Test");
		
		setSize(750, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(null);
		
		JMenuBar menubar = new JMenuBar();
		
		JMenu mnuFile = new JMenu("File");
		JMenuItem mnuitmNew = new JMenuItem("New");
		mnuitmNew.addActionListener(this);
		JMenuItem mnuitmRun = new JMenuItem("Run");
		mnuitmRun.addActionListener(this);
		JMenuItem mnuitmPause = new JMenuItem("Pause");
		mnuitmPause.addActionListener(this);
		JCheckBoxMenuItem mnuchkDebugLines = new JCheckBoxMenuItem("Show Debug Lines?");
		mnuchkDebugLines.addItemListener(this);
		JMenuItem mnuitmExit = new JMenuItem("Exit");
		mnuitmExit.addActionListener(this);
		
		mnuFile.add(mnuitmNew);
		mnuFile.addSeparator();
		mnuFile.add(mnuitmRun);
		mnuFile.add(mnuitmPause);
		mnuFile.addSeparator();
		mnuFile.add(mnuchkDebugLines);
		mnuFile.addSeparator();
		mnuFile.add(mnuitmExit);
		
		menubar.add(mnuFile);
		setJMenuBar(menubar);
		
		qtp = new QuadTreePanel();
		contentPane.add(qtp);
		qtp.setLocation(2,2);
		int boardSize = TestConfig.BOARD_SIZE+TestConfig.BOARD_PADDING;
		qtp.setSize(new Dimension(boardSize,boardSize));

		last = first;
		
		final Action movePoints = new AbstractAction( ) {
			private static final long serialVersionUID = -2127608122449546993L;
			public void actionPerformed(ActionEvent e) {
				//long now = System.currentTimeMillis();
				//if((now - last - TestConfig.TIME_SLICE) > TestConfig.TIME_SLICE)
				//	System.out.println(Long.toString(now - first) + ": Missed deadline by " + Long.toString(now - last));
				//last = now;
				
				int moveQuota = points.length/2;
				Hashtable moved = new Hashtable();
				for(int tt = 0; tt < moveQuota; tt++) {
					int pp = (int)Math.floor(Math.random()*points.length);
					if(moved.containsKey(points[pp])) {
						tt--;
						continue;
					}
					
					Point tp = null;
					if((Math.random() < TestConfig.ANT_VARIANCE_FROM_TASK)
							|| !qtp.follow || qtp.followPoint == null) {
						double vector = (Math.random()*2.0*Math.PI)-Math.PI;
						
						int xDrift = (int)(Math.cos(vector)/Math.abs(Math.cos(vector)));
						int yDrift = (int)(Math.sin(vector)/Math.abs(Math.sin(vector)));
						
						int x = points[pp].getX() + xDrift;
						int y = points[pp].getY() + yDrift;
						
						tp = new Point(x,y);
					} else {
						int xDrift = qtp.followPoint.x - points[pp].getX();
						int yDrift = qtp.followPoint.y - points[pp].getY();
						
						xDrift = xDrift / (xDrift != 0 ? (int)Math.abs(xDrift) : 1);
						yDrift = yDrift / (yDrift != 0 ? (int)Math.abs(yDrift) : 1);
						
						int x = points[pp].getX() + xDrift;
						int y = points[pp].getY() + yDrift;
						
						tp = new Point(x,y);
					}
					
					if(qtp.kqt.geometryContains(tp) &&
							!qtp.kqt.geometryIntersects(tp) ) {
								points[pp].translate(tp);
								moved.put(points[pp],points[pp]);
					}
				}
				repaint();
			}
		};
		
		final Action moveLines = new AbstractAction( ) {
			private static final long serialVersionUID = -2127608122449546993L;
			public void actionPerformed(ActionEvent e) {
				//long now = System.currentTimeMillis();
				//if((now - last - TestConfig.TIME_SLICE) > TestConfig.TIME_SLICE)
				//	System.out.println(Long.toString(now - first) + ": Missed deadline by " + Long.toString(now - last));
				//last = now;
				
				int moveQuota = plines.length/2;
				Hashtable moved = new Hashtable();
				for(int tt = 0; tt < moveQuota; tt++) {
					int pp = (int)Math.floor(Math.random()*plines.length);
					if(moved.containsKey(plines[pp])) {
						tt--;
						continue;
					}
					
					int xDrift, yDrift;
					//Point tp = null;
					if((Math.random() < TestConfig.ANT_VARIANCE_FROM_TASK)
							|| !qtp.follow || qtp.followPoint == null) {
						double vector = (Math.random()*2.0*Math.PI)-Math.PI;
						
						xDrift = (int)(Math.cos(vector)/Math.abs(Math.cos(vector)));
						yDrift = (int)(Math.sin(vector)/Math.abs(Math.sin(vector)));
						
						//int x = points[pp].getX() + xDrift;
						//int y = points[pp].getY() + yDrift;
						
						//tp = new Point(x,y);
					} else {
						Point c = plines[pp].centroid();
						xDrift = qtp.followPoint.x - c.x;
						yDrift = qtp.followPoint.y - c.y;
						
						xDrift = xDrift / (xDrift != 0 ? (int)Math.abs(xDrift) : 1);
						yDrift = yDrift / (yDrift != 0 ? (int)Math.abs(yDrift) : 1);
						
						//int x = points[pp].getX() + xDrift;
						//int y = points[pp].getY() + yDrift;
						
						//tp = new Point(x,y);
					}
					
					//if(qtp.kqt.geometryContains(tp) &&
					//		!qtp.kqt.geometryIntersects(tp) ) {
								plines[pp].translate(xDrift,yDrift);
								moved.put(plines[pp],plines[pp]);
					//}
				}
				//repaint();
				
				movePoints.actionPerformed(e);
			}
		};
		
		this.timer = new javax.swing.Timer(TestConfig.TIME_SLICE, moveLines);
		
		newMap();
	}
	
	public void newMap( ) {
		if(this.timer.isRunning())
			this.timer.stop();
		
		qtp.kqt.clear();
		plines = new AnimatedPolyline[TestConfig.POLYLINE_COUNT];
		for(int ll = 0; ll < plines.length; ll++) {
			plines[ll] = new AnimatedPolyline();
			int x = (int)Math.floor(Math.random()*TestConfig.BOARD_SIZE);
			int y = (int)Math.floor(Math.random()*TestConfig.BOARD_SIZE);
			plines[ll].add(x,y);
			
			for(int pp = 0; pp < TestConfig.POLYLINE_MAX_SEGMENT_COUNT; pp++) {
				int dx = (int)Math.floor(Math.random()*TestConfig.POLYLINE_MAX_SEGMENT_SIZE);
				int dy = (int)Math.floor(Math.random()*TestConfig.POLYLINE_MAX_SEGMENT_SIZE);
				
				int tx = x + (int)(dx * (Math.random() - Math.random()));
				int ty = y + (int)(dy * (Math.random() - Math.random()));
				
				Point p = new Point(tx,ty);
				while(!qtp.kqt.geometryContains(p)) {
					dx = (int)Math.floor(Math.random()*TestConfig.POLYLINE_MAX_SEGMENT_SIZE);
					dy = (int)Math.floor(Math.random()*TestConfig.POLYLINE_MAX_SEGMENT_SIZE);					
					tx = x + (int)(dx * (Math.random() - Math.random()));
					ty = y + (int)(dy * (Math.random() - Math.random()));
					
					p = new Point(tx,ty);
				}
				
				plines[ll].add(tx,ty);
				x = tx;
				y = ty;
			}
			
			qtp.kqt.add(plines[ll]);
		}
		
		points = new AnimatedPoint[TestConfig.POINT_COUNT];
		for(int pp = 0; pp < points.length; pp++) {
			int x = (int)Math.floor(Math.random()*TestConfig.BOARD_SIZE);
			int y = (int)Math.floor(Math.random()*TestConfig.BOARD_SIZE);
			Point p = new Point(x,y);
			
			while(qtp.kqt.geometryIntersects(p)) {
				p.x = (int)Math.floor(Math.random()*TestConfig.BOARD_SIZE);
				p.y = (int)Math.floor(Math.random()*TestConfig.BOARD_SIZE);
			}
			
			points[pp] = new AnimatedPoint(p); 
			qtp.kqt.add(points[pp]);
		}
		this.timer.start();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuadTreeTest qt = new QuadTreeTest();
		qt.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("new")) {
			newMap();
			this.repaint();
		} else if(e.getActionCommand().equalsIgnoreCase("run")) {
			if(!this.timer.isRunning())
				this.timer.start();
		} else if(e.getActionCommand().equalsIgnoreCase("pause")) {
			if(this.timer.isRunning())
				this.timer.stop();
		} else if(e.getActionCommand().equalsIgnoreCase("exit")) {
			System.exit(0);
		}
		
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() instanceof JCheckBoxMenuItem) {
			JCheckBoxMenuItem cb = (JCheckBoxMenuItem)e.getSource();
			qtp.kqt.setDrawNodes(cb.isSelected());
			this.repaint();
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7663173873468517938L;
}
