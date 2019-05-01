package edu.dlsu.securde.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "room_reservations")
public class RoomReservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	Integer id;
	
	@Column(name="username")
	String username;
	
	@Column(name="user_id")
	Integer userId;
	
	@Column(name="date")
	String date;
	
	@Column(name="location")
	String location ;
	
	@Column(name="start_time")
	String startTime;
	
	@Column(name="end_time")
	String endTime;

	public RoomReservation(){
		
	}
	
	public RoomReservation(Integer id, String username, Integer userId, String date, String location, String startTime,
			String endTime) {
		super();
		this.id = id;
		this.username = username;
		this.userId = userId;
		this.date = date;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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
	
}
