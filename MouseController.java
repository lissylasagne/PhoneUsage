package infovis.phoneUsage;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
	private int x,y = 0;

	public void mouseClicked(MouseEvent arg0) {
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
