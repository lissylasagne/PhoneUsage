package phoneUsage;

import java.util.ArrayList;

import infovis.debug.Debug;

public class App {
	private String name = "";
	private String category = "";
	private ArrayList<Usage> everyUsage = new ArrayList<Usage>();
	private int[] everyDay;
	private int total = 0;
	private int average = 0;

	public App(int numDays, String appName) {
		everyDay = new int[numDays];
		name = appName;
		if(name.equals("Telegram")) {
			category = "Kommunikation";
		} 
		else if(name.equals("WhatsApp")) {
			category = "Kommunikation";
		} 
		else if(name.equals("Mail")) {
			category = "Kommunikation";
		} 
		else if(name.equals("Telefon")) {
			category = "Kommunikation";
		} 
		else if(name.equals("Reddit")) {
			category = "Unterhaltung";
		} 
		else if(name.equals("Youtube")) {
			category = "Unterhaltung";
		} 
		else if(name.equals("Internet")) {
			category = "Unterhaltung";
		}
		else if(name.equals("Instagram")) {
			category = "Unterhaltung";
		}
		else if(name.equals("Spotify")) {
			category = "Unterhaltung";
		}
		else if(name.equals("SoundCloud")) {
			category = "Unterhaltung";
		}
		else if(name.equals("Soundcoreset Stimmgerät & Metronom")) {
			category = "Unterhaltung";
		}
		else if(name.equals("UniNow")) {
			category = "Organisatorisches";
		}
		else if(name.equals("QualityTime")) {
			category = "Organisatorisches";
		}
		else if(name.equals("Uhr")) {
			category = "Organisatorisches";
		}
		else if(name.equals("DB Navigator")) {
			category = "Organisatorisches";
		}
		else if(name.equals("Airbnb")) {
			category = "Organisatorisches";
		}
		else if(name.equals("WG-Gesucht")) {
			category = "Organisatorisches";
		}
		else if(name.equals("VMT")) {
			category = "Organisatorisches";
		}
		else if(name.equals("Maps")) {
			category = "Organisatorisches";
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addToUsage(Usage use) {
		everyUsage.add(use);
		setEveryDay(use.getDay());
	}
	
	public ArrayList<Usage> getUsage() {
		return everyUsage;
	}
	
	public void setEveryDay(int i) {
		int total = 0;
		for (Usage u : everyUsage) {
			if(i == u.getDay()) {
				total += u.getDuration();
			}
		}
		everyDay[i] = total;
		setTotal();
		setAverage();
	}
	
	public int[] getEveryDay() {
		return everyDay;
	}
	
	public int getEveryDay(int i) {
		return everyDay[i];
	}
	
	public void setTotal() {
		total = 0;
		for(int i = 0; i < everyDay.length; i++) {
			total += everyDay[i];
		}
	}
	
	public int getTotal() {
		return total;
	}
	
	public void setAverage() {
		average = total/everyDay.length;
	}
	
	public int getAverage() {
		return average;
	}
	
	public String getCategory() {
		return category;
	}
}
