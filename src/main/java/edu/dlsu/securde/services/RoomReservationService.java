package edu.dlsu.securde.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.RoomReservation;
import edu.dlsu.securde.repositories.RoomReservationRepository;

@Service
public class RoomReservationService {

	@Autowired
	private RoomReservationRepository roomReservationRepository;

	public List<RoomReservation> getRoomReservationsByDate(String date){
		List<RoomReservation> roomReservations = roomReservationRepository.findRoomReservationByDate(date);
		return roomReservations;
	}
	
	public List<RoomReservation> getRoomReservationsByLocation(String location){
		List<RoomReservation> roomReservations = roomReservationRepository.findRoomReservationByLocation(location);
		return roomReservations;
	}

	public List<RoomReservation> getRoomReservationsByLocationAndTime(String date,String location, String startTime, String endTime) {
		List<RoomReservation> roomReservations = roomReservationRepository.findRoomReservationByLocationAndTime(date,location,startTime,endTime);
		return roomReservations;
	}
	
	public List<RoomReservation> getRoomReservationsByUsername(String username) {
		List<RoomReservation> roomReservations = roomReservationRepository.findRoomReservationByUsername(username);
		return roomReservations;
	}
	
	public void addRoomReservation(RoomReservation roomReservation){
		roomReservationRepository.save(roomReservation);
	}
	
	public void deleteRoomReservation(Integer id){
		roomReservationRepository.delete(id);
	}
}
