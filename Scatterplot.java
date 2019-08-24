package phoneUsage;

import infovis.gui.GUI;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Scatterplot {
	private View view;
	private Model model;
	private MouseController controller ;
	//private KeyController key;
	
	public JPanel getView(){
		if (view == null) generateScatterplot();
		return view;
	}

	private void generateScatterplot() {
		view = new View();
		model = new Model();
		controller = new MouseController();
		//key = new KeyController();
		view.setModel(model);
		controller.setModel(model);
		controller.setView(view);
		view.addMouseListener(controller);
		view.addMouseMotionListener(controller);
		view.addMouseWheelListener(controller);
		//view.addKeyListener(key);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI application = new GUI();
				application.setView(new Scatterplot().getView());
				application.getJFrame().setVisible(true);
			}
		});
	}

}
