package phoneUsage;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import infovis.debug.Debug;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
	private int x,y = 0;

	public void mouseClicked(MouseEvent arg0) {
		if(view.getMode() == 0) {
			x = arg0.getX();
			y = arg0.getY();
			
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
			}
			
			view.repaint();
		
		else if(view.getMode() == 1) {
			x = arg0.getX();
			y = arg0.getY();
			
			Rectangle2D.Double R_all = view.getR_all();
			Rectangle2D.Double R_enter = view.getR_enter();
			Rectangle2D.Double R_comm = view.getR_comm();
			Rectangle2D.Double R_orga = view.getR_orga();
			Rectangle2D.Double D_1 = view.getD_1();
			Rectangle2D.Double D_2 = view.getD_2();
			Rectangle2D.Double D_3 = view.getD_3();
			Rectangle2D.Double D_4 = view.getD_4();
			Rectangle2D.Double D_5 = view.getD_5();
			Rectangle2D.Double D_6 = view.getD_6();
			Rectangle2D.Double D_7 = view.getD_7();
			
			if(R_all.contains(x,y)) {
				view.setCategory(0);
			} else if(R_enter.contains(x,y)) {
				view.setCategory(1);
			} else if(R_comm.contains(x,y)) {
				view.setCategory(2);
			} else if(R_orga.contains(x,y)) {
				view.setCategory(3);
			} else if(D_1.contains(x,y)) {
				view.setCategory(4);
			} else if(D_2.contains(x,y)) {
				view.setCategory(5);
			} else if(D_3.contains(x,y)) {
				view.setCategory(6);
			} else if(D_4.contains(x,y)) {
				view.setCategory(7);
			} else if(D_5.contains(x,y)) {
				view.setCategory(8);
			} else if(D_6.contains(x,y)) {
				view.setCategory(9);
			} else if(D_7.contains(x,y)) {
				view.setCategory(10);
			}
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

}
