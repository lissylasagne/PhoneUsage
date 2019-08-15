package infovis.phoneUsage;

import infovis.debug.Debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import java.util.stream.Collectors;

public class View extends JPanel {
	     private Model model = null;
	     private Graphics2D graphics;
	     private int counter = 0;
	     //visualisation mode
	     private int mode = 0;
	     //app category
	     //1=all, 2=entertainment, 2=communication, 3=organisation
	     private int category = 3;
	     private Rectangle2D.Double R_all;
	     private Rectangle2D.Double R_enter;
	     private Rectangle2D.Double R_comm;
	     private Rectangle2D.Double R_orga;
	     
	     private ArrayList<App> apps = new ArrayList<App>();
	     private ArrayList<Usage> usage = new ArrayList<Usage>();
	     
	     private int numDays = 0;
	     
	     private int[] hourlyUsage;
	     private int[] hourlyUsageEnter;
	     private int[] hourlyUsageComm;
	     private int[] hourlyUsageOrga;
		 
		@Override
		public void paint(Graphics g) {	
			Graphics2D g2D = (Graphics2D) g;
			graphics = g2D;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.clearRect(0, 0, getWidth(), getHeight());
				
			if(counter == 0) {
				int labelCount = 0;
			       for (String l : model.getLabels()) {
			    	if(l.equals("Tag(1-2)")) {
						break;
					}
					labelCount++;
				}
			        
		        
		        int rangeCount = 0;
				for (Range range : model.getRanges()) {
					if(rangeCount == labelCount) {
						numDays = (int)(range.getMax()+1);
					}
					rangeCount++;
				}
					
				int dataCount = 0;
				for (Data d : model.getList()) {
					Usage u = new Usage(d.getLabel(), (int)(d.getValue(0)),  (int)(d.getValue(1)),  (int)(d.getValue(2)));
					usage.add(u);
					dataCount++;
				}
					
				//get list of all used apps without doubles
				for (Usage u : usage) {
					boolean isDouble = false;
					for(App a : apps) {
						if(u.getAppName().equals(a.getName()))	{
							isDouble = true;
							a.addToUsage(u);
							u.setApp(a);
							break;
						}
					}
					if(!isDouble) {
						App a = new App(numDays, u.getAppName());
						apps.add(a);
						a.addToUsage(u);
						u.setApp(a);
					}
				}
											
				for (App a : apps) {
					//Debug.println("AppName: " + a.getName());
					//Debug.println("Catergory: " + a.getCategory());
					//Debug.println("AppUsageSize: " + a.getUsage().size());
					for(Usage u : a.getUsage()) {
						//u.print();
					}
						
					for(int i = 0; i < numDays; i++) {
						//Debug.println("Day" + i + ":" + a.getEveryDay(i));
					}
					//Debug.println("Average:" + a.getAverage());
					//Debug.println("Total:" + a.getTotal());
				}
				
				hourlyUsage = new int[numDays*24];
				
				for(int i = 0; i < hourlyUsage.length; i++) {
					hourlyUsage[i] = 0;
					for(Usage u : usage) {
						if(i == (u.getDay() * 24 + u.getHour())) {
							hourlyUsage[i] += u.getDuration();
						}
					}
					//Debug.println("Hourly Usage" + i + ": " + hourlyUsage[i]);					
				}
				
				hourlyUsageEnter = new int[numDays*24];
				
				for(int i = 0; i < hourlyUsageEnter.length; i++) {
					hourlyUsageEnter[i] = 0;
					for(Usage u : usage) {
						if(i == (u.getDay() * 24 + u.getHour())) {
							if(u.getApp().getCategory() == "Unterhaltung") {
								hourlyUsageEnter[i] += u.getDuration();
							}
						}
					}
					//Debug.println("Hourly Usage" + i + ": " + hourlyUsage[i]);					
				}
				
				hourlyUsageComm = new int[numDays*24];
				
				for(int i = 0; i < hourlyUsageComm.length; i++) {
					hourlyUsageComm[i] = 0;
					for(Usage u : usage) {
						if(i == (u.getDay() * 24 + u.getHour())) {
							if(u.getApp().getCategory() == "Kommunikation") {
								hourlyUsageComm[i] += u.getDuration();
							}
						}
					}
					//Debug.println("Hourly Usage" + i + ": " + hourlyUsage[i]);					
				}
				
				hourlyUsageOrga = new int[numDays*24];
				
				for(int i = 0; i < hourlyUsageOrga.length; i++) {
					hourlyUsageOrga[i] = 0;
					for(Usage u : usage) {
						if(i == (u.getDay() * 24 + u.getHour())) {
							if(u.getApp().getCategory() == "Organisatiorisches") {
								hourlyUsageOrga[i] += u.getDuration();
							}
						}
					}
					//Debug.println("Hourly Usage" + i + ": " + hourlyUsage[i]);					
				}
				counter++;
			}
				
			//start drawing
			//mode general hour overview
			if(mode == 0) {
				double size = getWidth()/28;
				
				g2D.setColor(Color.WHITE);
				g2D.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				
				int fontSize = (int)(size/1.5);

			    Color red = new Color(255,109,76);
			    Color grey = new Color(240,240,240);
			    g2D.setColor(grey);
			    
			    R_all = new Rectangle2D.Double((int)(1.9*size), (int)(size)-fontSize, (int)(3.7*size), (int)(size));
			    g2D.fill(R_all);
			    
			    R_enter = new Rectangle2D.Double((int)(7.4*size), (int)(size)-fontSize, (int)(4.6*size), (int)(size));
			    g2D.fill(R_enter);
			    
			    R_comm = new Rectangle2D.Double((int)(13.9*size), (int)(size)-fontSize, (int)(5.5*size), (int)(size));
			    g2D.fill(R_comm);
			    
			    R_orga = new Rectangle2D.Double((int)(20.65*size), (int)(size)-fontSize, (int)(5.5*size), (int)(size));
			    g2D.fill(R_orga);
			    
			    g2D.setColor(red);
			    
			    if(category == 0) {
			    	g2D.fill(R_all);
			    } else if(category == 1) {
			    	g2D.fill(R_enter);
			    } else if(category == 2) {
			    	g2D.fill(R_comm);
			    } else if(category == 3) {
			    	g2D.fill(R_orga);
			    }
			    
			    
			    Font font = new Font("Sans", Font.PLAIN, fontSize);
			    g2D.setFont(font);
			    g2D.setColor(Color.BLACK);

			    g2D.drawString("Insgesamt", (int)(2*size), (int)(size)); 
			    g2D.drawString("Unterhaltung", (int)(7.5*size), (int)(size));
			    g2D.drawString("Kommunikation", (int)(14*size), (int)(size)); 
			    g2D.drawString("Organisatorisch", (int)(20.75*size), (int)(size)); 
				
				g2D.translate(2*size, 2*size);
					
				for(int row = 0; row < hourlyUsage.length/24; row++) {
					for(int column = 0; column < 24; column++) {
						Rectangle2D rect = new Rectangle2D.Double(0, 0, size, size);
						
						int color = 0; 
						
						if(category == 0) {
							color = 255-(int)(hourlyUsage[24*row+column]*4.25);
					    } else if(category == 1) {
					    	color = 255-(int)(hourlyUsageEnter[24*row+column]*4.25);
					    } else if(category == 2) {
					    	color = 255-(int)(hourlyUsageComm[24*row+column]*4.25);
					    } else if(category == 3) {
					    	color = 255-(int)(hourlyUsageOrga[24*row+column]*4.25);
					    }
						
						g2D.setColor(new Color(color, color, color, 255));
						g2D.fill(rect);
						
						if(column <= 8 || column >= 20) {
							g2D.setColor(new Color(35, 64, 153, 55));
						}
						else
						{
							g2D.setColor(new Color(255, 137, 3, 55));
						}
						
						g2D.fill(rect);
						g2D.translate(size, 0);
						}
					g2D.translate((-size*24), size);
				}
			}
		}
		
		public void setModel(Model model) {
			this.model = model;
		}
		
		public Rectangle2D.Double getR_all() {
			return R_all;
		}
		
		public Rectangle2D.Double getR_enter() {
			return R_enter;
		}
		
		public Rectangle2D.Double getR_comm() {
			return R_comm;
		}
		
		public Rectangle2D.Double getR_orga() {
			return R_orga;
		}
		
		public int getCategory() {
			return category;
		}
		
		public void setCategory(int cat) {
			this.category = cat;
		}
}
