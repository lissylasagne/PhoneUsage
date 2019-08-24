package phoneUsage;

import infovis.debug.Debug;

public class Usage {
	private String appName;
	private App app;
	private int day;
	private int hour;
	private int duration;
	
	public Usage(String appName, int duration, int hour, int day) {
		this.appName = appName;
		this.day = day;
		this.hour = hour;
		this.duration = duration;
	}
	
	public void setAppName(String name) {
		this.appName = name;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setApp(App app) {
		this.app = app;
	}
	
	public App getApp() {
		return app;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public int getDay() {
		return day;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void print() {
		Debug.println("App: " + getAppName());
		Debug.println("Day: " + getDay());
		Debug.println("Hour: " + getHour());
		Debug.println("Duration: " + getDuration());
	}
}
