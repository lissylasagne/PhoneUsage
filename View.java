package phoneUsage;

import infovis.debug.Debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.stream.Collectors;

public class View extends JPanel  {
	     private Model model = null;
	     private Graphics2D graphics;
	     private int counter = 0;
	     //visualisation mode
	     private int mode = 1;

	     //app category
	     //0=all, 1=entertainment, 2=communication, 3=organisation
	     private int category = 0; 
	     
	     private int days = 0;
	     
	     //for zoom
	     private double zoom = 1.0;
	     //private int zoomCounter = 0;
	     double zoomPercentage = 13.0;
	     private double percentage = zoomPercentage / 100;
	     
	     private Rectangle2D.Double R_all;
	     private Rectangle2D.Double R_enter;
	     private Rectangle2D.Double R_comm;
	     private Rectangle2D.Double R_orga;
	     
	     private ArrayList<App> apps = new ArrayList<App>();
	     private ArrayList<Usage> usage = new ArrayList<Usage>();
	     
	     private int numDays = 1;
	     
	     private int[] hourlyUsage;
	     private int[] hourlyUsageEnter;
	     private int[] hourlyUsageComm;
	     private int[] hourlyUsageOrga;
	     
	     //for mode 0
	     private Rectangle2D[] hourlyUsageRec;
	     
	     //for mode 1
	     private Rectangle2D.Double D_1;
	     private Rectangle2D.Double D_2;
	     private Rectangle2D.Double D_3;
	     private Rectangle2D.Double D_4;
	     private Rectangle2D.Double D_5;
	     private Rectangle2D.Double D_6;
	     private Rectangle2D.Double D_7;
	     private Rectangle2D.Double Zoom_1;
	     private Rectangle2D.Double Zoom_2;
	     private Rectangle2D.Double Zoom_3;
	     private Rectangle2D.Double Zoom_4;
	     private Rectangle2D.Double Zoom_5;
	     private Rectangle2D.Double Zoom_6;
	     
	     
	     //for mode 2
	     private Arc2D[] pieChart; 
	     private Arc2D chosenArc;
		 
		@Override
		public void paint(Graphics g) {	
			Graphics2D g2D = (Graphics2D) g;
			graphics = g2D;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.clearRect(0, 0, getWidth(), getHeight());
			// for Zoom
			g2D.scale(zoom, zoom);
		    
			
			//counter so it doesnt run twice
			if(counter == 0) {
				int labelCount = 0;
			       for (String l : model.getLabels()) {
			    	if(l.equals("Tag(1-2)")) {
						break;
					}
					labelCount++;
				}
			        
		        //get number of days
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
				
				//arrays with usage per hour: [i] is the hour
				//i = 1 is day 0 hour 1, i = 25 is day 1 hour 1...
				hourlyUsage = new int[numDays*24];
				
				for(int i = 0; i < hourlyUsage.length; i++) {
					hourlyUsage[i] = 0;
					for(Usage u : usage) {
						if(i == (u.getDay() * 24 + u.getHour())) {
							hourlyUsage[i] += u.getDuration();
						}
					}
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
				}
				
				hourlyUsageOrga = new int[numDays*24];
				
				for(int i = 0; i < hourlyUsageOrga.length; i++) {
					hourlyUsageOrga[i] = 0;
					for(Usage u : usage) {
						if(i == (u.getDay() * 24 + u.getHour())) {
							if(u.getApp().getCategory() == "Organisatorisches") {
								hourlyUsageOrga[i] += u.getDuration();
							}
						}
					}
				}
				counter++;
			}
				
			//start drawing
			//mode general hour overview
			if(mode == 0) {
				double size = getWidth()/28;
				
				g2D.setColor(Color.WHITE);
				g2D.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				
				//draw category buttons
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
				
				//for loops: days and hours
				for(int row = 0; row < hourlyUsage.length/24; row++) {
					for(int column = 0; column < 24; column++) {
						Rectangle2D rect = new Rectangle2D.Double(0, 0, size, size);
						
						//map duration from 0 to 60 to color from 255 to 0
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
						
						//day and night color
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
			
			//Koordinatensystem Tagesübersicht
			else if(mode == 1) {
				
				
				//Buttons Tageswechsel      // Überlappt sich noch mit Koordinatensystem
				double size2 = getWidth()/28;
				g2D.setColor(Color.WHITE);
				g2D.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				int fontSize = (int)(size2/3);
				Color red = new Color(255,109,76);
			    Color grey = new Color(240,240,240);
			    g2D.setColor(grey);
			    
			    D_1 = new Rectangle2D.Double((int)(1.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_1);
			    D_2 = new Rectangle2D.Double((int)(5.0*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_2);
			    D_3 = new Rectangle2D.Double((int)(8.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_3);
			    D_4 = new Rectangle2D.Double((int)(12.0*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_4);
			    D_5 = new Rectangle2D.Double((int)(15.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_5);
			    D_6 = new Rectangle2D.Double((int)(19.0*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_6);
			    D_7 = new Rectangle2D.Double((int)(22.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_7);
			    
			   
			    
			    g2D.setColor(red);
			    
			    if(days == 0) {
			    	g2D.fill(D_1);
			    } else if(days == 1) {
			    	g2D.fill(D_2);
			    } else if(days == 2) {
			    	g2D.fill(D_3);
			    } else if(days == 3) {
			    	g2D.fill(D_4);
			    } else if(days == 4) {
			    	g2D.fill(D_5);
			    } else if(days == 5) {
			    	g2D.fill(D_6);
			    } else if(days == 6) {
			    	g2D.fill(D_7);
			    }
				
				
				
				Font font = new Font("Sans", Font.PLAIN, fontSize);
			    g2D.setFont(font);
			    g2D.setColor(Color.BLACK);
				g2D.drawString("Tag 1", (int)(2*size2), (int)(size2)); 
				g2D.drawString("Tag 2", (int)(5.5*size2), (int)(size2));
			    g2D.drawString("Tag 3", (int)(9*size2), (int)(size2)); 
			    g2D.drawString("Tag 4", (int)(12.5*size2), (int)(size2)); 
			    g2D.drawString("Tag 5", (int)(16*size2), (int)(size2));
			    g2D.drawString("Tag 6", (int)(19.5*size2), (int)(size2));
			    g2D.drawString("Tag 7", (int)(23*size2), (int)(size2));
			    
			    //g2D.translate(2*size2, 2*size2);
				
				
				
				// X-Achse Koordinaten (konstant)
				//	Start (50,800) Ende (800,800)
				final int xAchse_x1 = 50;
				final int xAchse_x2 = 1300;
				final int xAchse_y = 800;
				 
				// Y-Achse Koordinaten (konstant)
				//	Start (50,800) Ende (50,50) 
				final int yAchse_y1 = 50;
				final int yAchse_y2 = 800;
				final int yAchse_x = 50;
				 
				//Achsenpfeile durch Hipotenusen von Dreieck
				 
				// setzen der Dreieckskatheten
				final int firstLenght = 10;
				final int secondLenght = 5;
				 
				// Größe Nulpunktanzeige
				final int originCoordinateLenght = 6;
				 
				// Abstand der Zahlen an Achse 
				final int axisLabelDistance = 40;
				 
				 
				
				  
				// X-Achse ((50,800) bis (800,800))
				g2D.drawLine(xAchse_x1, xAchse_y,
						xAchse_x2, xAchse_y);
				  
				// Y-Achse ((50,800) bis (50,50))
				g2D.drawLine(yAchse_x, yAchse_y1,
						yAchse_x, yAchse_y2);
				  
				// Pfeil X-Achse
				g2D.drawLine(xAchse_x2 - firstLenght,
						xAchse_y - secondLenght,
						xAchse_x2, xAchse_y);
				g2D.drawLine(xAchse_x2 - firstLenght,
						xAchse_y + secondLenght,
						xAchse_x2, xAchse_y);
				  
				//Pfeil Y-Achse
				g2D.drawLine(yAchse_x - secondLenght,
						yAchse_y1 + firstLenght,
						yAchse_x, yAchse_y1);
				g2D.drawLine(yAchse_x + secondLenght, 
						yAchse_y1 + firstLenght,
						yAchse_x, yAchse_y1);
				  
				// Zeichnen des Nullpunkts
				g2D.fillOval(
						xAchse_x1 - (originCoordinateLenght / 2), 
						yAchse_y2 - (originCoordinateLenght / 2),
						originCoordinateLenght, originCoordinateLenght);
				  
				// Zeichnen von "Urzeit" und "Dauer"
				g2D.drawString("Uhrzeit", xAchse_x2 - axisLabelDistance / 2,
						xAchse_y + axisLabelDistance);
				g2D.drawString("Dauer", yAchse_x - axisLabelDistance,
						yAchse_y1 + axisLabelDistance / 2);
				//g2.drawString("(0, 0)", X_AXIS_FIRST_X_COORD - AXIS_STRING_DISTANCE,
				     //Y_AXIS_SECOND_Y_COORD + AXIS_STRING_DISTANCE);
				  
				// Achsenbeschriftung
				int xCoordNumbers = 24; // in Stunden
				int yCoordNumbers = 61; // in Minuten
				int xLength = (xAchse_x2 - xAchse_x1)
						/ xCoordNumbers;
				int yLength = (yAchse_y2 - yAchse_y1)
						/ yCoordNumbers;
				  
				// Zeichnen der X-Achsenbeschriftung
				for(int i = 0; i < xCoordNumbers;) {
					g2D.drawLine(xAchse_x1 + (i * xLength),
							xAchse_y - secondLenght,
							xAchse_x1 + (i * xLength),
							xAchse_y + secondLenght);
					g2D.drawString(Integer.toString(i), 
							xAchse_x1 + (i * xLength) - 3,
							xAchse_y + axisLabelDistance);
					i+= 1;
				}
				  
				//Zeichnen der Y-Achsenbeschriftung
				for(int i = 0; i < yCoordNumbers;) {
					g2D.drawLine(yAchse_x - secondLenght,
							yAchse_y2 - (i * yLength), 
							yAchse_x + secondLenght,
							yAchse_y2 - (i * yLength));
					g2D.drawString(Integer.toString(i), 
							yAchse_x - axisLabelDistance, 
							yAchse_y2 - (i * yLength));
					i+=10;
				}
				
				//for zoom
				//zooming sections
				Color x = new Color(255,109,76,0); // invisible
				g2D.setColor(x);
				
			    Zoom_1 = new Rectangle2D.Double((int)(xAchse_x1 + (0 * xLength)), (int)(xAchse_x1 + (0.5 * xLength)), (int)(4.25*size2), (int)(16*size2));
				g2D.draw(Zoom_1);
				Zoom_2 = new Rectangle2D.Double((int)(xAchse_x1 + (4 * xLength)), (int)(xAchse_x1 + (0.5 * xLength)), (int)(4.25*size2), (int)(16*size2));
				g2D.draw(Zoom_2);
				Zoom_3 = new Rectangle2D.Double((int)(xAchse_x1 + (8 * xLength)), (int)(xAchse_x1 + (0.5 * xLength)), (int)(4.25*size2), (int)(16*size2));
				g2D.draw(Zoom_3);
				Zoom_4 = new Rectangle2D.Double((int)(xAchse_x1 + (12 * xLength)), (int)(xAchse_x1 + (0.5 * xLength)), (int)(4.25*size2), (int)(16*size2));
				g2D.draw(Zoom_4);
				Zoom_5 = new Rectangle2D.Double((int)(xAchse_x1 + (16 * xLength)), (int)(xAchse_x1 + (0.5 * xLength)), (int)(4.25*size2), (int)(16*size2));
				g2D.draw(Zoom_5);
				Zoom_6 = new Rectangle2D.Double((int)(xAchse_x1 + (20 * xLength)), (int)(xAchse_x1 + (0.5 * xLength)), (int)(4.25*size2), (int)(16*size2));
				g2D.draw(Zoom_6);
				
				
				
				
			    
			    
			    int[] hourlyUsageEnterDay = new int[numDays*24];
			    int[] hourlyUsageCommDay = new int[numDays*24];
			    int[] hourlyUsageOrgaDay = new int[numDays*24];
			    
			    
			    	
			    	// START ANSICHT
				    // Automatisches ändern des Koordinatensystems
			    	System.out.println(getDays());
				    int j;
				    if (days == 0) {
				    	j = 0;
				    } 
				    else if(days == 1) {
				    	j = 1;
				    }
				    else if(days == 2) {
				    	j = 2;
				    }
				    else if(days == 3) {
				    	j = 3;
				    }
				    else if (days == 4) {
				    	j = 4;
				    }
				    else if (days == 5) {
				    	j = 5;
				    }
				    else {
				    	j = 6;
				    }
				    
			    	System.out.print("j:" + j);
			    	
			    	//int j =1;
			    	
				    // Unterhaltungsstunden pro Tag
					
					
					for(int i = 0; i < hourlyUsageEnterDay.length; i++) {
						hourlyUsageEnterDay[i] = 0;
						for(Usage u : usage) {
							if (u.getDay()== j) {
								if(i == (u.getDay() * 24 + u.getHour())) {
									if(u.getApp().getCategory() == "Unterhaltung") {
										hourlyUsageEnterDay[i] += u.getDuration();
									}
								}
							}
						}
					}
					//System.out.println(hourlyUsageEnterDay[0]);
					
					
					//Kommunikationsstunden pro Tag
					
					for(int i = 0; i < hourlyUsageCommDay.length; i++) {
						hourlyUsageCommDay[i] = 0;
						for(Usage u : usage) {
							if (u.getDay()== j) {
								if(i == (u.getDay() * 24 + u.getHour())) {
									if(u.getApp().getCategory() == "Kommunikation") {
										hourlyUsageCommDay[i] += u.getDuration();
									}
								}
							}
						}
					}
					//System.out.println(hourlyUsageCommDay[0]);
					
					//Organisationsstunden pro Tag
					
					
					for(int i = 0; i < hourlyUsageOrgaDay.length; i++) {
						hourlyUsageOrgaDay[i] = 0;
						for(Usage u : usage) {
							if (u.getDay()== j) {
								if(i == (u.getDay() * 24 + u.getHour())) {
									if(u.getApp().getCategory() == "Organisatiorisches") {
										hourlyUsageOrgaDay[i] += u.getDuration();
									}
								}
							}
						}
					}
					//System.out.println(hourlyUsageOrgaDay[0]);
					
			    //}
				//g2D.drawOval(46,796,8,8); // Nullpunkt
				// Punkte an Achsenbeschriftung anpassen
				// Gibt immer doppelt aus? Wie break setzen????
				// Zeichnen Punkte Unterhaltung
					
				
				for (int i = 0; i<24; i++) {
					g2D.setColor(java.awt.Color.green);
					//drawOval(int x, int y, int width, int height)
					g2D.drawOval(xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageEnterDay[i] * yLength)-6,12,12);
					//System.out.println(i+","+hourlyUsageEnterDay[i]);
					g2D.fillOval(xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageEnterDay[i] * yLength)-6,12,12);
					//Dauert zu lange Testen auf anderem Rechner
					/*
					while(i>0) {
						g2D.drawLine(xAchse_x1 + ((i-1) * xLength)-6, yAchse_y2 - (hourlyUsageEnterDay[i-1] * yLength)-6, xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageEnterDay[i] * yLength)-6);
					}
					*/
				}
				
				//Zeichnen Punkte Kommunikation
				//g2D.setColor(java.awt.Color.red);
					//g2D.drawOval(xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageCommDay[i] * yLength)-6,12,12); 
					//g2D.fillOval(xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageCommDay[i] * yLength)-6,12,12); 
					//Dauert zu lange Testen auf anderem Rechner
					/*
					while(i>0) {
						g2D.drawLine(xAchse_x1 + ((i-1) * xLength)-6, yAchse_y2 - (hourlyUsageCommDay[i-1] * yLength)-6, xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageCommDay[i] * yLength)-6);
					}*/
				//}
				
				//Zeichnen Punkte Organisation
				//for (int i = 0; i<24; i++) {
					//g2D.setColor(java.awt.Color.blue);
					//g2D.drawOval(xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageOrgaDay[i] * yLength)-6,12,12);
					//g2D.fillOval(xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageOrgaDay[i] * yLength)-6,12,12); 
					//Dauert zu lange Testen auf anderem Rechner
					/*
					while(i>0) {
						g2D.drawLine(xAchse_x1 + ((i-1) * xLength)-6, yAchse_y2 - (hourlyUsageOrgaDay[i-1] * yLength)-6,xAchse_x1 + (i * xLength)-6,yAchse_y2 - (hourlyUsageOrgaDay[i] * yLength)-6);
					}*/
				//}
					
				
				//zoom
				//--> Koordinatensystem in Rechtecke unterteilen(nicht sichtbar) wenn klick zoomfunktion aufruf
				//Zoomfunktion: durch scaling --> Fenstergröße auf größe des Rechteckes
				
				
					
			}

			//pie chart
			else if(mode==2) {
				int size = (getWidth() + getHeight())/5;
				
				//entertainment
				Color yellow = new Color(255,205,3);
				//communication
				Color red = new Color(255,3,91);
				//orga
				Color blue = new Color(32,177,199);
				
				int numHours = hourlyUsage.length;
				
				int usage = 0;
				int enter = 0;
				int comm = 0;
				int orga = 0;
				
				for(int i = 0; i < numHours; i++) {
					usage += hourlyUsage[i];
					enter += hourlyUsageEnter[i];
					comm += hourlyUsageComm[i];
					orga += hourlyUsageOrga[i];
				}
				
				Rectangle2D bounds = new Rectangle2D.Double((getWidth()/2)-size/2, (getHeight()/2)-size/2, size, size);
				
				//Debug.println("Total: " + usage + " Enter: "+ enter + " Comm: " + comm + " Orga: " + orga);
				
				if(category == 0) {
					pieChart = new Arc2D[3];
					
					//map usage to 360 degree and paint diagramm
					int enter360 = (enter * 360)/usage;
					int comm360 = (comm * 360)/usage;
					int orga360 = (orga * 360)/usage;
					
					while((enter360+comm360+orga360) != 360) {
						int random = (int) (Math.random()*3);
						switch(random) {
							case 0:
								enter360++; 
								break;
							case 1: 
								comm360++;
								break;
							case 2: 
								orga360++;
								break;
						}
					}
					//Debug.println("Total: " + usage + " Enter: "+ enter + " Comm: " + comm + " Orga: " + orga);
					g2D.setColor(yellow);
					pieChart[0] = new Arc2D.Double(bounds, 0, enter360, Arc2D.PIE);
					g2D.fill(pieChart[0]);
							
					g2D.setColor(red);
					pieChart[1] = new Arc2D.Double(bounds, enter360, comm360, Arc2D.PIE);
					g2D.fill(pieChart[1]);
					
					g2D.setColor(blue);
					pieChart[2] = new Arc2D.Double(bounds, enter360+comm360, orga360, Arc2D.PIE);
					g2D.fill(pieChart[2]);
					
				} else if(category == 1 || category == 2 || category == 3) {
					String cat = "";
					int total = 0;
					switch(category) {
						case 1:
							cat = "Unterhaltung";
							g2D.setColor(yellow);
							total = enter;
							break;
						case 2:
							cat = "Kommunikation";
							g2D.setColor(red);
							total = comm;
							break;
						case 3:
							cat = "Organisatorisches";
							g2D.setColor(blue);
							total = orga;
							break;
					}
					
					g2D.fillOval((getWidth()/2)-size/2, (int)(getHeight()/2)-size/2, size, size);
										
					//get total 360 degree value
					int count = 0;
					
					for (App a : apps) {
						if(a.getCategory().equals(cat)) {
							count++;
						}
					}
					//Debug.println("total: " + total);
					
					//array with app duration; i = app
					int[] appDuration = new int[count];
					pieChart = new Arc2D[count];
					count = 0;
					
					for (App a : apps) {
						if(a.getCategory().equals(cat)) {
							appDuration[count] = a.getTotal();
							/*for(Usage u : a.getUsage()) {
								appDuration[count] += u.getDuration();
							}*/
							count++;
						}
					} 
					
					int totalArcSize = 0;
					for(int i = 0; i < appDuration.length; i++) {
						int arcSize = (appDuration[i] * 360)/total;
						//set Color
						if(i%2 == 0) {
							g2D.setColor(new Color(0,0,0,50));
						} else {
							g2D.setColor(new Color(255,255,255,50));
						}
						
						if(i == appDuration.length-1) {
							arcSize = 360 - totalArcSize;
						}
						
						pieChart[i] = new Arc2D.Double(bounds, totalArcSize, arcSize, Arc2D.PIE);
						g2D.fill(pieChart[i]);
						
						totalArcSize += arcSize;
					}
					if(chosenArc != null) {
						Point2D start = chosenArc.getStartPoint();
						Point2D end = chosenArc.getEndPoint();
						Point2D center = new Point2D.Double(chosenArc.getCenterX(), chosenArc.getCenterY());
						
						Point2D info = new Point2D.Double((start.getX() + end.getX() - center.getX()), 
															(start.getY() + end.getY() - center.getY()));
						if(chosenArc.getAngleExtent() >= 180) {
							info.setLocation(1.5*center.getX() - 0.8*info.getX(), 1.5*center.getY() - 0.8*info.getY());
						}
						
						String infoString = "";
						
						for(int i = 0; i < pieChart.length; i++) {
							if(pieChart[i].equals(chosenArc)) {
								int iter = 0;
								for (App a : apps) {
									if(a.getCategory().equals(cat)) {
										if(iter == i) {
											infoString = new String(a.getName() + " \r\n" + a.getTotal() + "min");
										}
										iter++;
									}
								}
							}
						}
						
						g2D.setColor(Color.BLACK);
						g2D.drawString(infoString, (int)info.getX(), (int)info.getY());
					}
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
		
		//mode == 1
		public Rectangle2D.Double getD_1() {
			return D_1;
		}
		
		public Rectangle2D.Double getD_2() {
			return D_2;
		}
		
		public Rectangle2D.Double getD_3() {
			return D_3;
		}
		
		public Rectangle2D.Double getD_4() {
			return D_4;
		}
		
		public Rectangle2D.Double getD_5() {
			return D_5;
		}
		
		public Rectangle2D.Double getD_6() {
			return D_6;
		}
		
		public Rectangle2D.Double getD_7() {
			return D_7;
		}
		
		public Rectangle2D.Double getZoom_1() {
			return Zoom_1;
		}
		
		public Rectangle2D.Double getZoom_2() {
			return Zoom_2;
		}
		
		public Rectangle2D.Double getZoom_3() {
			return Zoom_3;
		}
		
		public Rectangle2D.Double getZoom_4() {
			return Zoom_4;
		}
		
		public Rectangle2D.Double getZoom_5() {
			return Zoom_5;
		}
		
		public Rectangle2D.Double getZoom_6() {
			return Zoom_6;
		}
		
		
		public int getCategory() {
			return category;
		}
		
		public void setCategory(int cat) {
			this.category = cat;
		}
		
		public int getDays() {
			return days;
		}
		
		public void setDays(int d) {
			this.days = d;
		}
		
		public int getMode() {
			return mode;
		}
		
		public void setMode(int mode) {
			this.mode = mode;
		}
		
		//mode = 2
		public Arc2D[] getPieChart() {
			return pieChart;
		}
		
		public Arc2D getPieChart(int i) {
			return pieChart[i];
		}
		
		public ArrayList<App> getApps() {
			return apps;
		}
		
		public Arc2D getChosenArc() {
			return chosenArc;
		}
		
		public void setChosenArc(Arc2D arc) {
			this.chosenArc = arc;
		}
		
		// hier nochmal schauen
		// besser wenn man direkt Koordinaten des gewünschten Sichtfeldes eingeben kann
		
		public void setZoomPercentage(int zoomPercentage) {
		    percentage = ((double) zoomPercentage) / 100;
		  }

		  public void originalSize() {
		    zoom = 1;
		  }

		  public void zoomIn() {
		    zoom += percentage;
		  }

		  public void zoomOut() {
		    zoom -= percentage;

		    if (zoom < percentage) {
		      if (percentage > 1.0) {
		        zoom = 1.0;
		      } else {
		        zoomIn();
		      }
		    }
		  }
		  
		  
}
