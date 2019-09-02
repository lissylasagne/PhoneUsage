package phoneUsage;

import infovis.debug.Debug;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.stream.Collectors;

public class View extends JPanel  {
	     private Model model = null;
	     private Graphics2D graphics;
	     
	     //counter for computing things just once
	     private int counter = 0;
	     
	     //visualisation mode
	     private int mode = 1;

	     //app category
	     //0=all, 1=entertainment, 2=communication, 3=organisation
	     private int category = 0; 
	     
	     private int days = 0;
	     
	    
	     
	     //buttons for choosing cateory
	     private Rectangle2D.Double R_all;
	     private Rectangle2D.Double R_enter;
	     private Rectangle2D.Double R_comm;
	     private Rectangle2D.Double R_orga;
	     
	     //all apps and usages
	     private ArrayList<App> apps = new ArrayList<App>();
	     private ArrayList<Usage> usage = new ArrayList<Usage>();
	     
	     private int numDays = 3;
	     
	     //hourly usage sorted by category
	     private int[] hourlyUsage;
	     private int[] hourlyUsageEnter;
	     private int[] hourlyUsageComm;
	     private int[] hourlyUsageOrga;
	     
	     //for mode 0 and 2
	     private Shape[] allShapes;
	     private Shape activeShape;
	     private int infoX = 100;
	     private int infoY = 100;
	     
	     //for mode 1
	     private Rectangle2D.Double D_1;
	     private Rectangle2D.Double D_2;
	     private Rectangle2D.Double D_3;
	     private Rectangle2D.Double D_4;
	     private Rectangle2D.Double D_5;
	     private Rectangle2D.Double D_6;
	     private Rectangle2D.Double D_7;
	     
	     //for zooming per Klick
	     private int zoom = 1;
	     private Point2D clickPoint;
	     private Point2D lastClick;
	     private int lastZoom;
	     
	     private int zoomIn = 2;
	     
		 
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
					
				for (Data d : model.getList()) {
					Usage u = new Usage(d.getLabel(), (int)(d.getValue(0)),  (int)(d.getValue(1)),  (int)(d.getValue(2)));
					usage.add(u);
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
			    Color yellow = new Color(255, 137, 3, 55);
			    Color blue = new Color(35, 64, 153, 55);
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
							g2D.setColor(blue);
						} else {
							g2D.setColor(yellow);
						}
						g2D.fill(rect);
					}
				}
				
				//if any box is clicked it turn into active shape
				if(activeShape != null && activeShape instanceof Rectangle2D) {
					//g2D.setColor(new Color(0,0,0,255));
					//g2D.fill(activeShape);
					
					int i = getActiveShapeFromAll(activeShape, allShapes);

					String cat = "";
					/*if(category == 0) {
						
					} else */
					if(category == 1) {
						cat = "Unterhaltung";
					} else if(category == 2) {
						cat = "Kommunikation";
					} else if(category == 3) {
						cat = "Organisatorisches";
					}
					/*
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
					*/
					
					int appCount = 0;
					ArrayList<String> appNames = new ArrayList<String>();
					ArrayList<Integer> appDuration = new ArrayList<Integer>();
					
					//get names and usage duration from apps that are used during chosen hour
					for (App a : apps) {
						if(category == 0) {
							for(Usage u : a.getUsage()) {
								if((u.getDay()*24) + u.getHour() == i) {
									appCount++;
									appNames.add(new String(a.getName() + ": " + u.getDuration() + "min"));
									appDuration.add(u.getDuration());
								}
							}
						} else {
							if(a.getCategory().contentEquals(cat)) {
								for(Usage u : a.getUsage()) {
									if((u.getDay()*24) + u.getHour() == i) {
										appCount++;
										appNames.add(new String(a.getName() + ": " + u.getDuration() + "min"));
										appDuration.add(u.getDuration());
									}
								}
							}
						}
					}
					
					//get position of infobox depending on position of clicked box
					double activeX = ((Rectangle2D)activeShape).getX();
					double activeY = ((Rectangle2D)activeShape).getY();
					
					int x = (getWidth() / (int)activeX);
					int y = (getWidth() / (int)activeY);
					
					//pos for writing strings later
					int xString = 0;
					
					//get points of small box that are closer to big box for drawing "3d"
					Point2D poly1 = new Point2D.Double(0,0);
					Point2D poly2 = new Point2D.Double(0,0);
					Point2D poly3 = new Point2D.Double(0,0);
					
					Point2D poly4 = new Point2D.Double(0,0);
					Point2D poly5 = new Point2D.Double(0,0);
					Point2D poly6 = new Point2D.Double(0,0);
					
					if(x > 1) {
						x = (int)(activeX + 2.5*size);
						xString = (int)(x + size*6);
						if(y > 1) {
							y = (int)(activeY + 2.5*size);
							
							poly1.setLocation(activeX + size, activeY);
							poly2.setLocation(activeX, activeY);
							poly3.setLocation(activeX, activeY + size);
							
							poly4.setLocation(x, y + size*4);
							poly5.setLocation(x, y);
							poly6.setLocation(x + size*4, y);
							
						} else {
							y = (int)((int)activeY - 5.5*size);
							
							poly1.setLocation(activeX, activeY);
							poly2.setLocation(activeX, activeY + size);
							poly3.setLocation(activeX + size, activeY + size);
							
							poly4.setLocation(x + size*4, y + size*4);
							poly5.setLocation(x, y + size*4);
							poly6.setLocation(x, y);
						}
					
					} else {
						x = (int)((int)activeX - 5.5*size);
						xString = (int)(x - size*6);
						if(y > 1) {
							y = (int)((int)activeY + 2.5*size);
							
							poly1.setLocation(activeX, activeY);
							poly2.setLocation(activeX + size, activeY);
							poly3.setLocation(activeX + size, activeY + size);
							
							poly4.setLocation(x + size*4, y + size*4);
							poly5.setLocation(x + size*4, y);
							poly6.setLocation(x, y);
						
						} else {
							y = (int)((int)activeY - 5.5*size);
							
							poly1.setLocation(activeX + size, activeY);
							poly2.setLocation(activeX + size, activeY + size);
							poly3.setLocation(activeX, activeY + size);
							
							poly4.setLocation(x + size*4, y);
							poly5.setLocation(x + size*4, y + size*4);
							poly6.setLocation(x, y + size*4);
						}
					}
					
					//draw boxes and map duration of each usage on box size
					Rectangle2D infoRect = new Rectangle2D.Double(x,y, size*4, size*4);
					int fullDuration = 0;
					for(int d : appDuration) {
						fullDuration += d;
					}
					
					double currentHeight = 0;
					double[] heights = new double[appCount];
					double multiplier = (size*4)/fullDuration;
					int iter = 0;
					for(int d : appDuration) {
						double height = d * multiplier;
						heights[iter] = height;
						Rectangle2D appRect = new Rectangle2D.Double(x,y+currentHeight, size*4, height);
						if(iter%2 == 0) {
							g2D.setColor(Color.gray);
						} else {
							g2D.setColor(Color.white);
						}
						g2D.fill(appRect);
						currentHeight += height;
						iter++;
					}
					
					if(iter > 0) {
						Polygon polyBottom = new Polygon();
						
						polyBottom.addPoint((int)poly2.getX(), (int)poly2.getY());
						polyBottom.addPoint((int)poly3.getX(), (int)poly3.getY());
						polyBottom.addPoint((int)poly4.getX(), (int)poly4.getY());
						polyBottom.addPoint((int)poly5.getX(), (int)poly5.getY());
						
						g2D.setColor(Color.gray);
						g2D.fill(polyBottom);
						
						Polygon polyTop = new Polygon();
						
						polyTop.addPoint((int)poly5.getX(), (int)poly5.getY());
						polyTop.addPoint((int)poly2.getX(), (int)poly2.getY());
						polyTop.addPoint((int)poly1.getX(), (int)poly1.getY());
						polyTop.addPoint((int)poly6.getX(), (int)poly6.getY());
						
						g2D.setColor(Color.white);
						g2D.fill(polyTop);
						
						if(i%24 <= 8 || i%24 >= 20) {
							g2D.setColor(blue);
						} else {
							g2D.setColor(yellow);
						}
						
						g2D.fill(infoRect);
						
						Polygon polyFull = new Polygon();
						polyFull.addPoint((int)poly1.getX(), (int)poly1.getY());
						polyFull.addPoint((int)poly2.getX(), (int)poly2.getY());
						polyFull.addPoint((int)poly3.getX(), (int)poly3.getY());
						polyFull.addPoint((int)poly4.getX(), (int)poly4.getY());
						polyFull.addPoint((int)poly5.getX(), (int)poly5.getY());
						polyFull.addPoint((int)poly6.getX(), (int)poly6.getY());
						
						g2D.fill(polyFull);
					}
					
					//write app names and connect with line to square
					fontSize = (int)(size/2);
					font = new Font("Sans", Font.PLAIN, fontSize);
				    g2D.setFont(font);
				    g2D.setColor(Color.BLACK);
				    int yString = y;
					int iter2 = 0;
					currentHeight = y;
					for(String n : appNames) {
						if(heights[iter2] >= fontSize) {
							yString += (heights[iter2]/2);
						} else {
							yString += fontSize/2;
						}
						
						if(xString == (int)(x + size*6)) {
							
							g2D.drawString(n, xString, yString);
							g2D.drawLine(xString, yString+2, (int)(xString + size*4), yString+2);
							g2D.drawLine(xString, yString+2, (int)(x +size*4), (int)(currentHeight + (heights[iter2]/2)));
						
						} else if(xString == (int)(x - size*6)) {
						
							g2D.drawString(n, xString, yString);
							g2D.drawLine(xString, yString+2, (int)(xString + size*4), yString+2);
							g2D.drawLine((int)(xString + size*4), yString+2, x, (int)(currentHeight + (heights[iter2]/2)));
						}
						
						if(heights[iter2] >= fontSize) {
							yString += (heights[iter2])/2;
						} else {
							yString += fontSize/2;
						}
						currentHeight += (heights[iter2]);
						iter2++;
					}
				}
			} 
			
			//coordinate system day view
			else if(mode == 1) {
				// for zooming per click
				if(clickPoint != null) {
					double x = -clickPoint.getX() * (zoom/2);
					double y = -clickPoint.getY() * (zoom/2);
					
					if(lastClick != null) {
						if(zoom > lastZoom) {
							x += lastClick.getX() * (zoom/2-1);
							y += lastClick.getY() * (zoom/2-1);
						} else if(zoom != 1){
							x += lastClick.getX() * (zoom/2-1);
							y += lastClick.getY() * (zoom/2-1);
						}
					}
					
					lastClick = new Point2D.Double(x,y);
					
					Debug.println("X: " + x + " Y: " + y);

					g2D.translate(x,y);
				}
				g2D.scale(zoom, zoom);
			    		
				//Buttons day changes
				double size2 = getWidth()/28;
				g2D.setColor(Color.WHITE);
				g2D.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				int fontSize = (int)(size2/3);
				Color red = new Color(255,109,76);
			    Color grey = new Color(240,240,240);
			    g2D.setColor(grey);
			    
			    D_1 = new Rectangle2D.Double((int)(2.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_1);
			    D_2 = new Rectangle2D.Double((int)(6.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_2);
			    D_3 = new Rectangle2D.Double((int)(10.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_3);
			    D_4 = new Rectangle2D.Double((int)(14.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_4);
			   /* D_5 = new Rectangle2D.Double((int)(15.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_5);
			    D_6 = new Rectangle2D.Double((int)(19.0*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_6);
			    D_7 = new Rectangle2D.Double((int)(22.6*size2), (int)(size2)-fontSize, (int)(2.0*size2), (int)(size2));
			    g2D.fill(D_7);
			    */
			   
			    
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
				g2D.drawString("Tag 1", (int)(3*size2), (int)(size2)); 
				g2D.drawString("Tag 2", (int)(7*size2), (int)(size2));
			    g2D.drawString("Tag 3", (int)(11*size2), (int)(size2)); 
			    g2D.drawString("Tag 4", (int)(15*size2), (int)(size2)); 
			    //g2D.drawString("Tag 5", (int)(16*size2), (int)(size2));
			    //g2D.drawString("Tag 6", (int)(19.5*size2), (int)(size2));
			    //g2D.drawString("Tag 7", (int)(23*size2), (int)(size2));
			    
			    //g2D.translate(2*size2, 2*size2);
				
				
				
				// X-axis coordinates (constant)
				//	Start (50,800) Ende (800,800)
				final int xAchse_x1 = 50;
				final int xAchse_x2 = 1300;
				final int xAchse_y = 800;
				 
				// Y-axis coordinates (constant)
				//	Start (50,800) Ende (50,50) 
				final int yAchse_y1 = 50;
				final int yAchse_y2 = 800;
				final int yAchse_x = 50;
				 
				//axis arrows through hipotenusis of triangle
				 
				// sets triangle catethis
				final int firstLenght = 10;
				final int secondLenght = 5;
				 
				// size 0 point advertisment
				final int originCoordinateLenght = 6;
				 
				// distance numbers axis
				final int axisLabelDistance = 40;
				 
				 
				
				  
				// X-axis ((50,800) to (800,800))
				g2D.drawLine(xAchse_x1, xAchse_y,
						xAchse_x2, xAchse_y);
				  
				// Y-axis ((50,800) to (50,50))
				g2D.drawLine(yAchse_x, yAchse_y1,
						yAchse_x, yAchse_y2);
				  
				// arrow X-axis
				g2D.drawLine(xAchse_x2 - firstLenght,
						xAchse_y - secondLenght,
						xAchse_x2, xAchse_y);
				g2D.drawLine(xAchse_x2 - firstLenght,
						xAchse_y + secondLenght,
						xAchse_x2, xAchse_y);
				  
				//arrow Y-axis
				g2D.drawLine(yAchse_x - secondLenght,
						yAchse_y1 + firstLenght,
						yAchse_x, yAchse_y1);
				g2D.drawLine(yAchse_x + secondLenght, 
						yAchse_y1 + firstLenght,
						yAchse_x, yAchse_y1);
				  
				// draws 0point
				g2D.fillOval(
						xAchse_x1 - (originCoordinateLenght / 2), 
						yAchse_y2 - (originCoordinateLenght / 2),
						originCoordinateLenght, originCoordinateLenght);
				  
				// draws "Urzeit" and "Dauer"
				g2D.drawString("Uhrzeit", xAchse_x2 - axisLabelDistance / 2,
						xAchse_y + axisLabelDistance);
				g2D.drawString("Dauer", yAchse_x - axisLabelDistance,
						yAchse_y1 + axisLabelDistance / 2);
				//g2.drawString("(0, 0)", X_AXIS_FIRST_X_COORD - AXIS_STRING_DISTANCE,
				     //Y_AXIS_SECOND_Y_COORD + AXIS_STRING_DISTANCE);
				  
				// axis inscription
				int xCoordNumbers = 24; // in Stunden
				int yCoordNumbers = 61; // in Minuten
				int xLength = (xAchse_x2 - xAchse_x1)
						/ xCoordNumbers;
				int yLength = (yAchse_y2 - yAchse_y1)
						/ yCoordNumbers;
				  
				// draws X-axis insciption
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
				  
				// draws Y-axis insciption
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
				
//------------------------------------poibt drawing  entertainment-------------------------------------------------------------------			
				if(days == 0) {
					for(int i = 0;i<24;i++) {
						g2D.setColor(java.awt.Color.green);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageEnter[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//Dauert zu lange Testen auf anderem Rechner
						
						if(i>0) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageEnter[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageEnter[i] * yLength));
						}
					}
				}
				else if(days == 1) {
					for(int i = 24;i<48;i++) {
						g2D.setColor(java.awt.Color.green);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageEnter[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//Dauert zu lange Testen auf anderem Rechner
						
						if(i>24) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageEnter[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageEnter[i] * yLength));
						}
					}
				}
				else if(days == 2) {
					for(int i = 48;i<72;i++) {
						g2D.setColor(java.awt.Color.green);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageEnter[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//Dauert zu lange Testen auf anderem Rechner
						
						if(i>48) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageEnter[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageEnter[i] * yLength));
						}
					}
				}
				else if(days == 3) {
					for(int i = 72;i<96;i++) {
						g2D.setColor(java.awt.Color.green);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageEnter[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//Dauert zu lange Testen auf anderem Rechner
						
						if(i>72) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageEnter[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageEnter[i] * yLength));
						}
					}
				}
				else if(days == 4) {
					for(int i = 96;i<120;i++) {
						g2D.setColor(java.awt.Color.green);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageEnter[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//Dauert zu lange Testen auf anderem Rechner
						
						if(i>96) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageEnter[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageEnter[i] * yLength));
						}
					}
				}
				else if(days == 5) {
					for(int i = 120;i<144;i++) {
						g2D.setColor(java.awt.Color.green);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageEnter[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//Dauert zu lange Testen auf anderem Rechner
						
						if(i>120) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageEnter[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageEnter[i] * yLength));
						}
					}
				}
				else if(days == 6) {
					for(int i = 144;i<168;i++) {
						g2D.setColor(java.awt.Color.green);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageEnter[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageEnter[i] * yLength)-6,12,12);
						//Dauert zu lange Testen auf anderem Rechner
						
						if(i>144) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageEnter[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageEnter[i] * yLength));
						}
					}
				}
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------			
//------------------------------------point drawing communication----------------------------------------------------------------------------------------------------			
				if(days == 0) {
					for(int i = 0;i<24;i++) {
						Color c = new Color(0,255,255,250);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageComm[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						
						if(i>0) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageComm[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageComm[i] * yLength));
						}
					}
				}
				else if(days == 1) {
					for(int i = 24;i<48;i++) {
						Color c = new Color(0,255,255,250);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageComm[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						
						if(i>24) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageComm[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageComm[i] * yLength));
						}
					}
				}
				else if(days == 2) {
					for(int i = 48;i<72;i++) {
						Color c = new Color(0,255,255,250);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageComm[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						
						if(i>48) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageComm[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageComm[i] * yLength));
						}
					}
				}
				else if(days == 3) {
					for(int i = 72;i<96;i++) {
						Color c = new Color(0,255,255,250);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageComm[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						
						if(i>72) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageComm[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageComm[i] * yLength));
						}
					}
				}
				else if(days == 4) {
					for(int i = 96;i<120;i++) {
						Color c = new Color(0,255,255,250);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageComm[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						
						if(i>96) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageComm[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageComm[i] * yLength));
						}
					}
				}
				else if(days == 5) {
					for(int i = 120;i<144;i++) {
						Color c = new Color(0,255,255,250);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageComm[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						
						if(i>120) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageComm[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageComm[i] * yLength));
						}
					}
				}
				else if(days == 6) {
					for(int i = 144;i<168;i++) {
						Color c = new Color(0,255,255,250);//türkis
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageComm[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageComm[i] * yLength)-6,12,12);
						
						if(i>144) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageComm[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageComm[i] * yLength));
						}
					}
				}
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------			
//------------------------------------point drawing organisation----------------------------------------------------------------------------------------------------			
				if(days == 0) {
					for(int i = 0;i<24;i++) {
						Color c = new Color(255, 0, 0,150);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						////System.out.println((i%24)+","+hourlyUsageOrga[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						
						if(i>0) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageOrga[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageOrga[i] * yLength));
						}
					}
				}
				else if(days == 1) {
					for(int i = 24;i<48;i++) {
						Color c = new Color(255, 0, 0,150);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageOrga[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						
						if(i>24) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageOrga[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageOrga[i] * yLength));
						}
					}
				}
				else if(days == 2) {
					for(int i = 48;i<72;i++) {
						Color c = new Color(255, 0, 0,150);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageOrga[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						
						if(i>48) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageOrga[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageOrga[i] * yLength));
						}
					}
				}
				else if(days == 3) {
					for(int i = 72;i<96;i++) {
						Color c = new Color(255, 0, 0,150);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageOrga[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						
						if(i>72) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageOrga[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageOrga[i] * yLength));
						}
					}
				}
				else if(days == 4) {
					for(int i = 96;i<120;i++) {
						Color c = new Color(255, 0, 0,150);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageOrga[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						
						if(i>96) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageOrga[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageOrga[i] * yLength));
						}
					}
				}
				else if(days == 5) {
					for(int i = 120;i<144;i++) {
						Color c = new Color(255, 0, 0,150);
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageOrga[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						
						if(i>120) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageOrga[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageOrga[i] * yLength));
						}
					}
				}
				else if(days == 6) {
					for(int i = 144;i<168;i++) {
						Color c = new Color(255, 0, 0,150);//pink
						g2D.setColor(c);
						//drawOval(int x, int y, int width, int height)
						g2D.drawOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						//System.out.println((i%24)+","+hourlyUsageOrga[i]);
						g2D.fillOval(xAchse_x1 + ((i%24) * xLength)-6,yAchse_y2 - (hourlyUsageOrga[i] * yLength)-6,12,12);
						
						if(i>144) {
							//System.out.println(i);
							g2D.drawLine(xAchse_x1 + (((i%24)-1) * xLength), yAchse_y2 - (hourlyUsageOrga[i-1] * yLength), xAchse_x1 + ((i%24) * xLength),yAchse_y2 - (hourlyUsageOrga[i] * yLength));
						}
					}
				}
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------			
				
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
					
					//map usage to 360 degree
					int enter360 = (enter * 360)/usage;
					int comm360 = (comm * 360)/usage;
					int orga360 = (orga * 360)/usage;
					
					int i = 0;
					//make sure pie chart is 360 degrees
					while((enter360+comm360+orga360) != 360) {
						if(i % 3 == 0) {
							enter360++; 
						} else if(i % 3 == 1) {
							comm360++; 
						} else if(i % 3 == 2) {
							orga360++; 
						}
					}
					
					//set color and draw each arc
					g2D.setColor(yellow);
					allShapes[0] = new Arc2D.Double(bounds, 0, enter360, Arc2D.PIE);
					g2D.fill(allShapes[0]);
							
					g2D.setColor(red);
					allShapes[1] = new Arc2D.Double(bounds, enter360, comm360, Arc2D.PIE);
					g2D.fill(allShapes[1]);
					
					g2D.setColor(blue);
					allShapes[2] = new Arc2D.Double(bounds, enter360+comm360, orga360, Arc2D.PIE);
					g2D.fill(allShapes[2]);
					
					
					//get points for drawing the strings and line to each arc
					Point2D[] infoXY = new Point2D[allShapes.length];
					int fontSize = 14;
					
					for(int j = 0; j < allShapes.length; j++) {
						Arc2D half = new Arc2D.Double(bounds, ((Arc2D)allShapes[j]).getAngleStart(), ((Arc2D)allShapes[j]).getAngleExtent(), Arc2D.PIE);
						half.setAngleExtent(half.getAngleExtent()/2);
						Point2D lineEnd = new Point2D.Double(half.getEndPoint().getX(), half.getEndPoint().getY());
						
						Point2D center = new Point2D.Double(((Arc2D)allShapes[j]).getCenterX(), ((Arc2D)allShapes[j]).getCenterY());
						double x = center.getX() + 1.4*(lineEnd.getX() - center.getX());
						double y = center.getY() + 1.4*(lineEnd.getY() - center.getY());
						Point2D info = new Point2D.Double(x,y);
						
						infoXY[j] = info;
						
						for(int k = 0; k < j; k++) {
							if((info.getY() < (infoXY[k].getY()+fontSize)) && (info.getY() > (infoXY[k].getY()-fontSize))) {
								if(info.getY() < infoXY[k].getY()) {
									info.setLocation(info.getX(), info.getY() + 2*fontSize);
								} else {
									info.setLocation(info.getX(), info.getY() - 2*fontSize);
								}
							}
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
						
						g2D.drawLine((int)info.getX()-8, (int)info.getY()+2, (int)(info.getX()+(fontSize*infoString.length()*0.5)), (int)info.getY()+2);
						
						Point2D lineStart = new Point2D.Double(0,0);
						
						if(info.getX() > getWidth()/2 - (getWidth()/12)) {
							lineStart.setLocation(info.getX()-8, info.getY()+2);
						} else {
							lineStart.setLocation(info.getX()+(fontSize*infoString.length()*0.5), info.getY()+2);
						}
							
						g2D.drawLine((int)lineStart.getX(), (int)lineStart.getY(), (int)lineEnd.getX(), (int)lineEnd.getY());
					}
					
				} else if(category == 1 || category == 2 || category == 3) {
					//same as category == 0, but this time dynamic number of arcs
					String cat = "";
					int total = 0;
					Color color = yellow;
					switch(category) {
						case 1:
							cat = "Unterhaltung";
							color = yellow;
							total = enter;
							break;
						case 2:
							cat = "Kommunikation";
							color = red;
							total = comm;
							break;
						case 3:
							cat = "Organisatorisches";
							color = blue;
							total = orga;
							break;
					}
					
					g2D.setColor(color);
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
						
						if(i == appDuration.length-1) {
							arcSize = 360 - totalArcSize;
						}
						
						allShapes[i] = new Arc2D.Double(bounds, totalArcSize, arcSize, Arc2D.PIE);
						if(i%2 == 0) {
							g2D.setColor(new Color(0,0,0,50));
						} else {
							g2D.setColor(new Color(255,255,255,50));
						}
						
						g2D.fill(allShapes[i]);
						
						totalArcSize += arcSize;
					}
					
					Point2D[] infoXY = new Point2D[allShapes.length];
					int fontSize = 14;
					
					for(int i = 0; i < allShapes.length; i++) {
						Arc2D half = new Arc2D.Double(bounds, ((Arc2D)allShapes[i]).getAngleStart(), ((Arc2D)allShapes[i]).getAngleExtent(), Arc2D.PIE);
						half.setAngleExtent(half.getAngleExtent()/2);
						Point2D lineEnd = new Point2D.Double(half.getEndPoint().getX(), half.getEndPoint().getY());
						
						Point2D center = new Point2D.Double(((Arc2D)allShapes[i]).getCenterX(), ((Arc2D)allShapes[i]).getCenterY());
						double x = center.getX() + 1.4*(lineEnd.getX() - center.getX());
						double y = center.getY() + 1.4*(lineEnd.getY() - center.getY());
						Point2D info = new Point2D.Double(x,y);
						
						infoXY[i] = info;
						
						for(int j = 0; j < i; j++) {
							if((info.getY() < (infoXY[j].getY()+fontSize)) && (info.getY() > (infoXY[j].getY()-fontSize))) {
								if(info.getY() < infoXY[j].getY()) {
									info.setLocation(info.getX(), info.getY() + 2*fontSize);
								} else {
									info.setLocation(info.getX(), info.getY() - 2*fontSize);
								}
							}
						}
												
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
						
						g2D.drawLine((int)info.getX()-8, (int)info.getY()+2, (int)(info.getX()+(fontSize*infoString.length()*0.5)), (int)info.getY()+2);
						
						Point2D lineStart = new Point2D.Double(0,0);
						
						if(info.getX() > getWidth()/2 - (getWidth()/12)) {
							lineStart.setLocation(info.getX()-8, info.getY()+2);
						} else {
							lineStart.setLocation(info.getX()+(fontSize*infoString.length()*0.5), info.getY()+2);
						}
							
						g2D.drawLine((int)lineStart.getX(), (int)lineStart.getY(), (int)lineEnd.getX(), (int)lineEnd.getY());
					}
				}
			}
			//mix between mode 0 and 2
			else if(mode == 3) {
				double size = getWidth()/28;
				
				g2D.setColor(Color.WHITE);
				g2D.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				
				//draw category buttons
				int fontSize = (int)(size/1.5);

			    Color red = new Color(255,109,76);
			    Color grey = new Color(240,240,240);
			    Color yellow = new Color(255, 137, 3, 25);
			    Color blue = new Color(35, 64, 153, 25);
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
				float angle = -15;
				size = getWidth()/48;
				
				//for loops: days and hours
				for(int row = 0; row < hourlyUsage.length/24; row++) {
					Rectangle2D bounds = new Rectangle2D.Double(getWidth()/2 - size*(7-row+1), 
							getHeight()/2 - size*(7-row+1), size*(7-row+1)*2, size*(7-row+1)*2);
					float currentAngle = 90;
					for(int column = 0; column < 24; column++) {
						Arc2D arc = new Arc2D.Double(bounds, currentAngle, angle, Arc2D.PIE);
						currentAngle += angle;
						
						allShapes[24*row+column] = arc;						
						
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
						g2D.fill(arc);
					}
				}
				
				//if any box is clicked it turn into active shape
				if(activeShape != null && activeShape instanceof Arc2D) {
					//g2D.setColor(new Color(0,0,0,255));
					//g2D.fill(activeShape);
					
					int i = getActiveShapeFromAll(activeShape, allShapes);

					String cat = "";
					/*if(category == 0) {
						
					} else */
					if(category == 1) {
						cat = "Unterhaltung";
					} else if(category == 2) {
						cat = "Kommunikation";
					} else if(category == 3) {
						cat = "Organisatorisches";
					}
					
					int appCount = 0;
					ArrayList<String> appNames = new ArrayList<String>();
					ArrayList<Integer> appDuration = new ArrayList<Integer>();
					
					//get names and usage duration from apps that are used during chosen hour
					for (App a : apps) {
						if(category == 0) {
							for(Usage u : a.getUsage()) {
								if((u.getDay()*24) + u.getHour() == i) {
									appCount++;
									appNames.add(new String(a.getName() + ": " + u.getDuration() + "min"));
									appDuration.add(u.getDuration());
								}
							}
						} else {
							if(a.getCategory().contentEquals(cat)) {
								for(Usage u : a.getUsage()) {
									if((u.getDay()*24) + u.getHour() == i) {
										appCount++;
										appNames.add(new String(a.getName() + ": " + u.getDuration() + "min"));
										appDuration.add(u.getDuration());
									}
								}
							}
						}
					}
					
					double activeStart = ((Arc2D)activeShape).getAngleStart();
					double activeExtend = ((Arc2D)activeShape).getAngleExtent();

					int row = i%7;
					Rectangle2D activeBounds = new Rectangle2D.Double(getWidth()/2 - size*16, 
							getHeight()/2 - size*16, size*32, size*32);

					Arc2D infoArc = new Arc2D.Double(activeBounds, activeStart, activeExtend, Arc2D.PIE);
					int color = 255-(int)(hourlyUsage[i]*4.25);
					g2D.setColor(new Color(color, color, color, 255));
					g2D.fill(infoArc);
					
					int fullDuration = 0;
					for(int d : appDuration) {
						fullDuration += d;
					}
					
					double currentAngle = activeStart;
					
					//int fontSize = (int)(size/2);
					Point2D[] infoXY = new Point2D[appCount];
					double[] angles = new double[appCount];
					double multiplier = (activeExtend)/fullDuration;
					int iter = 0;
					//get points for drawing line to infostring
					for(int d : appDuration) {
						double anglePart = d * multiplier;
						angles[iter] = anglePart;
						Arc2D appArc = new Arc2D.Double(activeBounds, currentAngle, anglePart, Arc2D.PIE);
						
						if(iter%2 == 0) {
							g2D.setColor(new Color(0,0,0,50));
						} else {
							g2D.setColor(new Color(255,255,255,150));
						}
						g2D.fill(appArc);
						currentAngle += anglePart;
						iter++;
						//g2D.setColor(Color.black);
						//g2D.drawLine((int)lineStart.getX(), (int)lineStart.getY(), (int)lineEnd.getX(), (int)lineEnd.getY());
					}
						
					int iterator = 0;
					
					//draw middle again
					for(row = 0; row < hourlyUsage.length/24; row++) {
						Rectangle2D bounds = new Rectangle2D.Double(getWidth()/2 - size*(7-row+1), 
								getHeight()/2 - size*(7-row+1), size*(7-row+1)*2, size*(7-row+1)*2);
						currentAngle = 90;
						for(int column = 0; column < 24; column++) {
							if(iterator > i) {
								Arc2D arc = new Arc2D.Double(bounds, currentAngle, angle, Arc2D.PIE);
								
								allShapes[24*row+column] = arc;						
								
								//map duration from 0 to 60 to color from 255 to 0
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
								g2D.fill(arc);
							}
							currentAngle += angle;
							iterator++;
						}
					}
				}
				
				Rectangle2D b = new Rectangle2D.Double(getWidth()/2-size*16, getHeight()/2-size*16, size*32, size*32);
				
				Arc2D night = new Arc2D.Double(b, -30, 180, Arc2D.PIE);
				g2D.setColor(blue);
				g2D.fill(night);
				
				Arc2D day = new Arc2D.Double(b, 150, 180, Arc2D.PIE);
				g2D.setColor(yellow);
				g2D.fill(day);
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


		public void zoomIn() {
			lastZoom = zoom;
			zoom = zoom * zoomIn;
		}

		public void zoomOut() {
			lastZoom = zoom;
		    zoom = zoom / zoomIn;
		}
		  
		 public void setClickPoint(Point2D p) {
			 this.clickPoint = p;
		 }
		 
		 public Point2D getClickPoint(Point2D p) {
			 return clickPoint;
		 }
		  
		  
}
