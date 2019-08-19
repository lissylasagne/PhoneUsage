package infovis.phoneUsage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import infovis.debug.Debug;

public class KeyController implements KeyListener{

	public void keyReleased(KeyEvent e) { 
	}
	
    public void keyTyped(KeyEvent e) { 
    	Debug.println("Key pressed");
    	int key = e.getKeyCode();
        Debug.println("Key pressed: " + key);
    }
	
	public void keyPressed(KeyEvent e) {
    }

}
