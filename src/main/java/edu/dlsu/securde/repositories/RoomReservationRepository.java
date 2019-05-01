package edu.dlsu.securde.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.dlsu.securde.model.RoomReservation;

public interface RoomReservationRepository extends CrudRepository<RoomReservation, Integer> {

	@Query("SELECT rr " + "FROM RoomReservation rr " + "WHERE rr.date = :date")
	ArrayList<RoomReservation> findRoomReservationByDate(@Param("date") String date);

	@Query("SELECT rr " + "FROM RoomReservation rr " + "WHERE rr.location = :location")
	ArrayList<RoomReservation> findRoomReservationByLocation(@Param("location") String location);

	@Query("SELECT rr " + "FROM RoomReservation rr "
			+ "WHERE rr.location = :location AND rr.startTime = :startTime AND rr.endTime = :endTime AND rr.date = :date")
	ArrayList<RoomReservation> findRoomReservationByLocationAndTime(@Param("date") String date,@Param("location") String location,
			@Param("startTime") String startTime, @Param("endTime") String endTime);

	@Query("SELECT rr " + "FROM RoomReservation rr " + "WHERE rr.username = :username")
	ArrayList<RoomReservation> findRoomReservationByUsername(@Param("username") String username);
}
