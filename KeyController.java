package infovis.phoneUsage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import infovis.debug.Debug;

public class KeyController implements KeyListener{

	private Model model = null;
	private View view = null;
	
	public void keyReleased(KeyEvent e) { 
	}
	
    public void keyTyped(KeyEvent e) { 
    	
    }
	
    public void keyPressed(KeyEvent e) {
    	char key = e.getKeyChar();
    	
    	if(key == '0' || key == '1' || key == '2' || key == '3')
    	{
    		view.setMode(Character.getNumericValue(key));
    	}
    	
    	view.repaint();
    	
    }
    
	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}
}
