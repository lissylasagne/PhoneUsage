package infovis.phoneUsage;

import infovis.debug.Debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	     
	     //counter for computing things just once
	     private int counter = 0;
	     
	     //visualisation mode
	     private int mode = 2;

	     //app category
	     //0=all, 1=entertainment, 2=communication, 3=organisation
	     private int category = 0; 
	     
	     private int day = 0;
	     
	     //buttons for choosing cateory
	     private Rectangle2D.Double R_all;
	     private Rectangle2D.Double R_enter;
	     private Rectangle2D.Double R_comm;
	     private Rectangle2D.Double R_orga;
	     
	     //all apps and usages
	     private ArrayList<App> apps = new ArrayList<App>();
	     private ArrayList<Usage> usage = new ArrayList<Usage>();
	     
	     //number of listed days
	     private int numDays = 0;
	     
	     //hourly usage sorted by category
	     private int[] hourlyUsage;
	     private int[] hourlyUsageEnter;
	     private int[] hourlyUsageComm;
	     private int[] hourlyUsageOrga;
	     
	     //for mode 0 and 2
	     private Shape[] allShapes;
	     private Shape activeShape;
	     private int infoX = 200;
	     private int infoY = 200;
	     
	     //for mode 1
	     private Rectangle2D.Double D_1;
	     private Rectangle2D.Double D_2;
	     private Rectangle2D.Double D_3;
	     private Rectangle2D.Double D_4;
	     private Rectangle2D.Double D_5;
	     private Rectangle2D.Double D_6;
	     private Rectangle2D.Double D_7;
		 
		@Override
		public void paint(Graphics g) {	
			Graphics2D g2D = (Graphics2D) g;
			graphics = g2D;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.clearRect(0, 0, getWidth(), getHeight());
			
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
				
				
				allShapes = new Shape[hourlyUsage.length];
				
				//for loops: days and hours
				for(int row = 0; row < hourlyUsage.length/24; row++) {
					for(int column = 0; column < 24; column++) {
						Rectangle2D rect = new Rectangle2D.Double(2*size+column*size, 2*size+row*size, size, size);
						allShapes[24*row+column] = rect;						
						
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
						} else {
							g2D.setColor(new Color(255, 137, 3, 55));
						}
						g2D.fill(rect);
					}
				}
				
				if(activeShape != null) {
					g2D.setColor(new Color(200,0,0,255));
					g2D.fill(activeShape);
					
					int i = getActiveShapeFromAll(activeShape, allShapes);
					int usage = 0;
					
					String cat = "";
					if(category == 0) {
						usage = hourlyUsage[i];
					} else if(category == 1) {
						usage = hourlyUsageEnter[i];
						cat = "Unterhaltung";
					} else if(category == 2) {
						usage = hourlyUsageComm[i];
						cat = "Kommunikation";
					} else if(category == 3) {
						usage = hourlyUsageOrga[i];
						cat = "Organisatorisches";
					}
					
					String infoString = new String("Dauer: " + usage + "min");
					
					int appCount = 0;
					ArrayList<String> infoStrings = new ArrayList<String>(); 
					
					for (App a : apps) {
						if(category == 0) {
							for(Usage u : a.getUsage()) {
								if((u.getDay()*24) + u.getHour() == i) {
									appCount++;
									infoStrings.add(new String(a.getName() + ": " + u.getDuration() + "min"));
								}
							}
						} else {
							if(a.getCategory().contentEquals(cat)) {
								for(Usage u : a.getUsage()) {
									if((u.getDay()*24) + u.getHour() == i) {
										appCount++;
										infoStrings.add(new String(a.getName() + ": " + u.getDuration() + "min"));
									}
								}
							}
						}
					}
					
					int x = (getWidth() / (int)((Rectangle2D)activeShape).getX());
					if(x > 1) {
						x = (int) (4*(getWidth()/6));
					} else {
						x = (int) ((getWidth()/6));
					}
					int y = (getHeight() / (int)((Rectangle2D)activeShape).getY());
					
					if(y > 1) {
						y = (int) (4*(getHeight()/6));
					} else {
						y = (int) ((getHeight()/6));
					}
					
					Rectangle2D infoRect = new Rectangle2D.Double(x,y,(getWidth()/6), size*appCount);
					g2D.setColor(Color.WHITE);
					g2D.fill(infoRect);
					
					g2D.setColor(Color.BLACK);
					g2D.drawString(infoString, x, y);
					
					int iter = 1;
					for(String s : infoStrings) {
						g2D.drawString(s, x, (int)(y+iter*size));
						iter++;
					}
					
				}
			} 
			
			//Koordinatensystem Tagesübersicht
			else if(mode == 1) {
								
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
				
				//Buttons Tageswechsel      // Überlappt sich noch mit Koordinatensystem
				double size2 = getWidth()/28;
				//g2D.setColor(Color.WHITE);
				//g2D.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				int fontSize = (int)(size2/3);
				//Color red = new Color(255,109,76);
			    //Color grey = new Color(240,240,240);
			    //g2D.setColor(grey);
			    
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
			    
			    //g2D.setColor(red);
			    
			    if(day == 0) {
			    	g2D.fill(D_1);
			    } else if(day == 1) {
			    	g2D.fill(D_2);
			    } else if(day == 2) {
			    	g2D.fill(D_3);
			    } else if(day == 3) {
			    	g2D.fill(D_4);
			    } else if(day == 4) {
			    	g2D.fill(D_5);
			    } else if(day == 5) {
			    	g2D.fill(D_6);
			    } else if(day == 6) {
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
				
				
				
				// START ANSICHT
			    // Automatisches ändern des Koordinatensystems
			    int j;
			    if (day == 0) {
			    	j = 0;
			    } 
			    else if(day == 1) {
			    	j = 1;
			    }
			    else if(day == 2) {
			    	j = 2;
			    }
			    else if(day == 3) {
			    	j = 3;
			    }
			    else if (day == 4) {
			    	j = 4;
			    }
			    else if (day == 5) {
			    	j = 5;
			    }
			    else {
			    	j = 6;
			    }
			    
				/*int j = 0;
				// Unterhaltungsstunden pro Tag
				int[] hourlyUsageEnterDay = new int[numDays*24];
				
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
				}*/
				//System.out.println(hourlyUsageEnterDay[0]);
				
				
				//Kommunikationsstunden pro Tag
				int[] hourlyUsageCommDay = new int[numDays*24];
				
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
				int[] hourlyUsageOrgaDay = new int[numDays*24];
				
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
				//g2D.drawOval(46,796,8,8); // Nullpunkt
				// Punkte an Achsenbeschriftung anpassen
				// Gibt immer doppelt aus? Wie break setzen????
				// Zeichnen Punkte Unterhaltung
				
				/*for (int i = 0; i<24; i++) {
					g2D.setColor(java.awt.Color.green);
					//drawOval(int x, int y, int width, int height)
					g2D.drawOval(46+i,796+hourlyUsageEnterDay[i],12,12);
					//System.out.println(i+","+hourlyUsageEnterDay[i]);
					//g2D.fillOval(46+i,796+hourlyUsageEnterDay[i],12,12);
					//Dauert zu lange Testen auf anderem Rechner
					/*
					while(i>0) {
						g2D.drawLine(i-1, hourlyUsageEnterDay[i-1], i,hourlyUsageEnterDay[i]);
					}
				}*/
				
				//Zeichnen Punkte Kommunikation
				//for (int i = 0; i<24; i++) {
					//g2D.setColor(java.awt.Color.red);
					//g2D.drawOval(46+i,796+hourlyUsageCommDay[i],12,12); 
					//g2D.fillOval(46+i,796+hourlyUsageCommDay[i],12,12); 
					//Dauert zu lange Testen auf anderem Rechner
					/*
					while(i>0) {
						g2D.drawLine(i-1, hourlyUsageCommDay[i-1], i,hourlyUsageCommDay[i]);
					}*/
				//}
				
				//Zeichnen Punkte Organisation
				//for (int i = 0; i<24; i++) {
					//g2D.setColor(java.awt.Color.blue);
					//g2D.drawOval(46+0,796+hourlyUsageOrgaDay[0],12,12);
					//g2D.fillOval(46+0,796+hourlyUsageOrgaDay[0],12,12); 
					//Dauert zu lange Testen auf anderem Rechner
					/*
					while(i>0) {
						g2D.drawLine(i-1, hourlyUsageOrgaDay[i-1], i,hourlyUsageOrgaDay[i]);
					}*/
				//}
				
					
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
					allShapes = new Shape[3];
					
					//map usage to 360 degree and paint diagramm
					int enter360 = (enter * 360)/usage;
					int comm360 = (comm * 360)/usage;
					int orga360 = (orga * 360)/usage;
					
					int i = 0;
					while((enter360+comm360+orga360) != 360) {
						if(i % 3 == 0) {
							enter360++; 
						} else if(i % 3 == 1) {
							comm360++; 
						} else if(i % 3 == 2) {
							orga360++; 
						}
					}
					
					//Debug.println("Total: " + usage + " Enter: "+ enter + " Comm: " + comm + " Orga: " + orga);
					g2D.setColor(yellow);
					allShapes[0] = new Arc2D.Double(bounds, 0, enter360, Arc2D.PIE);
					g2D.fill(allShapes[0]);
							
					g2D.setColor(red);
					allShapes[1] = new Arc2D.Double(bounds, enter360, comm360, Arc2D.PIE);
					g2D.fill(allShapes[1]);
					
					g2D.setColor(blue);
					allShapes[2] = new Arc2D.Double(bounds, enter360+comm360, orga360, Arc2D.PIE);
					g2D.fill(allShapes[2]);
					
					//draw info for clicked arc
					for(int j = 0; j < 3; j++) {
						Point2D start = ((Arc2D)allShapes[j]).getStartPoint();
						Point2D end = ((Arc2D)allShapes[j]).getEndPoint();
						Point2D center = new Point2D.Double(((Arc2D)allShapes[j]).getCenterX(), ((Arc2D)allShapes[j]).getCenterY());
						
						Point2D info = new Point2D.Double((start.getX() + end.getX() - 1.2*center.getX()), 
															(start.getY() + end.getY() - 1.2*center.getY()));
						if(((Arc2D)allShapes[j]).getAngleExtent() >= 180) {
							info.setLocation(1.8*center.getX() - info.getX(), 1.8*center.getY() - info.getY());
						}
						
						String infoString = "";
						
						if(j == 0) {
							infoString = new String("Unterhaltung " + printTimeInHours(enter));
						} 
						else if(j == 1) {
							infoString = new String("Kommunikation " + printTimeInHours(comm));
						} 
						else if(j == 2) {
							infoString = new String("Organisatorisches " + printTimeInHours(orga));
						}
						
						g2D.setColor(Color.BLACK);
						g2D.drawString(infoString, (int)info.getX(), (int)info.getY());
					}
					
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
					allShapes = new Shape[count];
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
						
						allShapes[i] = new Arc2D.Double(bounds, totalArcSize, arcSize, Arc2D.PIE);
						g2D.fill(allShapes[i]);
						
						totalArcSize += arcSize;
					}
					
					/*
					//draw info for clicked arc
					if(activeShape != null) {
						Point2D start = ((Arc2D)activeShape).getStartPoint();
						Point2D end = ((Arc2D)activeShape).getEndPoint();
						Point2D center = new Point2D.Double(((Arc2D)activeShape).getCenterX(), ((Arc2D)activeShape).getCenterY());
						
						Point2D info = new Point2D.Double((start.getX() + end.getX() - 1.2*center.getX()), 
															(start.getY() + end.getY() - 1.2*center.getY()));
						if(((Arc2D)activeShape).getAngleExtent() >= 180) {
							info.setLocation(1.8*center.getX() - info.getX(), 1.8*center.getY() - info.getY());
						}
												
						int i = getActiveShapeFromAll(activeShape, allShapes);
						
						String infoString = "";			
						
						int iter = 0;
						for (App a : apps) {
							if(a.getCategory().equals(cat)) {
								if(iter == i) {
									infoString = new String(a.getName() + " \r\n" + printTimeInHours(a.getTotal()));
								}
								iter++;
							}
						}
						
						g2D.setColor(Color.BLACK);
						g2D.drawString(infoString, (int)info.getX(), (int)info.getY());
					}
					*/
					
					int numApps = allShapes.length;
					
					String[] infoStrings = new String[numApps];
					
					for(int i = 0; i < numApps; i++) {
						int iter = 0;
						for (App a : apps) {
							if(a.getCategory().equals(cat)) {								
								if(iter == i) {
									infoStrings[i] = new String(a.getName() + " \r\n" + printTimeInHours(a.getTotal()));
								}
								iter++;
							}
						}
					}
					
					int fontSize = 14;
					
					g2D.setColor(Color.white);
					
					Rectangle2D rect = new Rectangle2D.Double(infoX, infoY, fontSize*10, numApps*(fontSize) + 2);
					activeShape = rect;
					g2D.fill(rect);
					
					g2D.setColor(Color.black);
					g2D.draw(rect);
					
					Font font = new Font("Sans", Font.PLAIN, fontSize);
				    g2D.setFont(font);
					
					for(int i = 0; i < numApps; i++) {
						g2D.drawString(infoStrings[i], infoX, infoY + fontSize* (i+1));
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
		
		//mode == 0
		public Shape[] getAllShapes() {
			return allShapes;
		}
		
		public Shape getAllShapes(int i) {
			return allShapes[i];
		}
		
		public void setAllShapes(Shape[] shapes) {
			this.allShapes = shapes;
		}
		
		public void setAllShapes(Shape shape, int i) {
			this.allShapes[i] = shape;
		}
		
		public Shape getActiveShape() {
			return activeShape;
		}
		
		public void setActiveShape(Shape shape) {
			this.activeShape = shape;
		}
		
		public int getActiveShapeFromAll(Shape activeShape, Shape[] allShapes) {
			for(int i = 0; i < allShapes.length; i++) {
				if(allShapes[i].equals(activeShape)) {
					return i;
				}
			}
			Debug.println("No shape found");
			return 0;
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
		
		public int getCategory() {
			return category;
		}
		
		public void setCategory(int cat) {
			this.category = cat;
		}
		
		public int getMode() {
			return mode;
		}
		
		public void setMode(int mode) {
			this.mode = mode;
		}
		
		public ArrayList<App> getApps() {
			return apps;
		}
		
		public String printTimeInHours(int min) {
			String output;
			if(min >= 60) {
				output = new String((min/60) + "h " + (min%60) + "min");
			} else {
				output = new String((min%60) + "min");
			}
			return output;
		}
		
		//mode 2
		
		public void setInfoX(int x) {
			this.infoX = x;
		}
		
		public int getInfoX() {
			return infoX;
		}
		
		public void setInfoY(int y) {
			this.infoY = y;
		}
		
		public int getInfoY() {
			return infoY;
		}
}
