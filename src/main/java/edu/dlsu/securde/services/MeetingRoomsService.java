package edu.dlsu.securde.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.Schedule;

@Service
public class MeetingRoomsService {

	@Autowired
	RoomReservationService roomReservationService;

	public List<Schedule> processSchedule(List<Schedule> schedules, String location) {

		Integer startTime = 9;
		Integer endTime = 10;
		String amPmS = " AM";
		String amPmE = " AM";

		LocalDate date = LocalDate.now();
		date = date.plus(1, ChronoUnit.DAYS);
		String tom = date + "";

		if (roomReservationService.getRoomReservationsByLocation(location).size() > 0) {

			for (int i = 0; i < 12; i++) {

				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setUsername("None");

				// If Reserved..
				System.out.println(tom + location + startTime + amPmS + endTime + amPmE);

				if (roomReservationService
						.getRoomReservationsByLocationAndTime(tom, location, startTime + amPmS, endTime + amPmE)
						.size() > 0) {
					System.out.println("TIME CHECK RESERVED:" + startTime + " " + endTime);
					schedule.setStatus(1);
					schedule.setUsername(roomReservationService
							.getRoomReservationsByLocationAndTime(tom, location, startTime + amPmS, endTime + amPmE)
							.get(0).getUsername());
				} else {
					schedule.setStatus(0);
				}

				schedules.add(schedule);
				// Adjust the time slots..
				startTime += 1;
				if (startTime == 13) {
					startTime = 1;
				}
				endTime += 1;
				if (endTime == 13) {
					endTime = 1;
				}
				if (startTime == 12) {
					amPmS = " PM";
				}
				if (endTime == 12) {
					amPmE = " PM";
				}
			}
		} else {

			// No Reservations..
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedules.add(schedule);

				// Adjust the time slots..
				startTime += 1;
				if (startTime == 13) {
					startTime = 1;
				}
				endTime += 1;
				if (endTime == 13) {
					endTime = 1;
				}
				if (startTime == 12) {
					amPmS = " PM";
				}
				if (endTime == 12) {
					amPmE = " PM";
				}
			}
		}

		return schedules;
	}
}
