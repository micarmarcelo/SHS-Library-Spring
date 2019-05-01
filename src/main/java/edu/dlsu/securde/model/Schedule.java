package edu.dlsu.securde.model;

public class Schedule {

	private String startTime;
	private String endTime;
	private Integer status;
	private String username;
	
	public Schedule(){
		
	}
	
	public Schedule(String startTime, String endTime, Integer status, String username) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.setUsername(username);
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}