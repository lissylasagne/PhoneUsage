package infovis.phoneUsage;

import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import infovis.debug.Debug;

public class MouseController implements MouseListener, MouseMotionListener,MouseWheelListener {

	private Model model = null;
	private View view = null;
	private int x,y = 0;

	private int difX, difY;
	
	private int zoomCounter = 0;
	private boolean zoomIn = true;
	
	
	public void mouseClicked(MouseEvent arg0) {
		x = arg0.getX();
		y = arg0.getY();
		
		
		
		if(view.getMode() == 0) {
			//category buttons
			Rectangle2D.Double R_all = view.getR_all();
			Rectangle2D.Double R_enter = view.getR_enter();
			Rectangle2D.Double R_comm = view.getR_comm();
			Rectangle2D.Double R_orga = view.getR_orga();
			
			if(R_all.contains(x,y)) {
				view.setCategory(0);
			} else if(R_enter.contains(x,y)) {
				view.setCategory(1);
			} else if(R_comm.contains(x,y)) {
				view.setCategory(2);
			} else if(R_orga.contains(x,y)) {
				view.setCategory(3);
			} else {
			//hour rectangles
				for(int i = 0; i < view.getAllShapes().length; i++) {
					if(view.getAllShapes(i).contains(x,y)) {
						view.setActiveShape(view.getAllShapes(i));
						view.repaint();
						break;
					} else if(i == view.getAllShapes().length -1){
						view.setActiveShape(null);
					}
				}
			}
			
			view.repaint();
		
		} else if(view.getMode() == 1) {		
			Rectangle2D.Double D_1 = view.getD_1();
			Rectangle2D.Double D_2 = view.getD_2();
			Rectangle2D.Double D_3 = view.getD_3();
			Rectangle2D.Double D_4 = view.getD_4();
			Rectangle2D.Double D_5 = view.getD_5();
			Rectangle2D.Double D_6 = view.getD_6();
			Rectangle2D.Double D_7 = view.getD_7();
			
			if(D_1.contains(x,y)) {
				view.setDays(0);
			} else if(D_2.contains(x,y)) {
				view.setDays(1);
			} else if(D_3.contains(x,y)) {
				view.setDays(2);
			} else if(D_4.contains(x,y)) {
				view.setDays(3);
			} else if(D_5.contains(x,y)) {
				view.setDays(4);
			} else if(D_6.contains(x,y)) {
				view.setDays(5);
			} else if(D_7.contains(x,y)) {
				view.setDays(6);
			} else {
			//zooming per klick
			//Koordinatensystem unterteilt in rechtecke a 4 stunden zoomen falls klick
				if(zoomIn) {
					view.zoomIn();
				} else {
					view.zoomOut();
				}
				Point2D p = new Point2D.Double(x,y);
				view.setClickPoint(p);
				zoomCounter++;
				if(zoomCounter == 2) {
					zoomIn = !zoomIn;
					zoomCounter = 0;
				}
			}
				
			
			 
			view.repaint();
		
		} else if(view.getMode() == 2) {
			if(view.getCategory() == 0) {
				for(int i = 0; i < view.getAllShapes().length; i++) {
					if(view.getAllShapes(i).contains(x,y)) {
						view.setCategory(i+1);
						view.repaint();
					}
				}
			} else if(view.getCategory() == 1 || view.getCategory() == 2 || view.getCategory() == 3) {
				for(int i = 0; i < view.getAllShapes().length; i++) {
					if(view.getAllShapes(i).contains(x,y)) {
						break;
					} else {
						if(i == view.getAllShapes().length-1) {
							view.setCategory(0);
							view.setActiveShape(null);
							view.repaint();
						}
					}
				}
			}
		} else if(view.getMode() == 3) {
			//category buttons
			Rectangle2D.Double R_all = view.getR_all();
			Rectangle2D.Double R_enter = view.getR_enter();
			Rectangle2D.Double R_comm = view.getR_comm();
			Rectangle2D.Double R_orga = view.getR_orga();
			
			if(R_all.contains(x,y)) {
				view.setCategory(0);
			} else if(R_enter.contains(x,y)) {
				view.setCategory(1);
			} else if(R_comm.contains(x,y)) {
				view.setCategory(2);
			} else if(R_orga.contains(x,y)) {
				view.setCategory(3);
			} else {
				for(int i = view.getAllShapes().length-1; i >= 0; i--) {
					if(view.getAllShapes(i).contains(x,y)) { 
						view.setActiveShape(view.getAllShapes(i));
						view.repaint();
						break;
					} else if(i == view.getAllShapes().length -1){
						view.setActiveShape(null);
					}
				}
			}
			
			view.repaint();
		
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}
	//for zooming per mausrad 
	public void mouseDragged(MouseEvent arg0) {
					
	}

	public void mouseMoved(MouseEvent arg0) {		
	}

	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}

	@Override // zooming per mausrad
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		/*if(arg0.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			
			// make it a reasonable amount of zoom
			// .1 gives a nice slow transition
			view.scale += (.1 * arg0.getWheelRotation());
			// don't cross negative threshold.
			// also, setting scale to 0 has bad effects
			view.scale = Math.max(0.00001, view.scale); 
			view.repaint();
		}
	*/	
	}

}
