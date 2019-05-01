package edu.dlsu.securde.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.dlsu.securde.model.Item;
import edu.dlsu.securde.model.ItemReservation;
import edu.dlsu.securde.model.Review;
import edu.dlsu.securde.model.RoomReservation;
import edu.dlsu.securde.model.Schedule;
import edu.dlsu.securde.model.Tag;
import edu.dlsu.securde.model.User;
import edu.dlsu.securde.services.ItemReservationService;
import edu.dlsu.securde.services.ItemService;
import edu.dlsu.securde.services.LoggingService;
import edu.dlsu.securde.services.LoginService;
import edu.dlsu.securde.services.MeetingRoomsService;
import edu.dlsu.securde.services.RegistrationService;
import edu.dlsu.securde.services.ReviewService;
import edu.dlsu.securde.services.RoomReservationService;
import edu.dlsu.securde.services.TagService;
import edu.dlsu.securde.services.UserService;

@Controller
public class WebController {

	@Autowired
	ItemService itemService;

	@Autowired
	LoginService loginService;

	@Autowired
	RegistrationService registrationService;

	@Autowired
	UserService userService;

	@Autowired
	ItemReservationService itemReservationService;

	@Autowired
	ReviewService reviewService;

	@Autowired
	LoggingService loggingService;

	@Autowired
	RoomReservationService roomReservationService;

	@Autowired
	MeetingRoomsService meetingRoomsService;

	@Autowired
	TagService tagService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Username" + auth.getName());
		if (auth.getName().equals("anonymousUser")) {
			User user = new User();
			modelAndView.addObject("user", user);
			loggingService.logInfo("Anonymous is accessing the login page");
			modelAndView.setViewName("login");
		} else {
			modelAndView.setViewName("redirect:/startUp");
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/registration" }, method = RequestMethod.GET)
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("registration");
		loggingService.logInfo("Anonymous is accessing the registration page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/processForgetPassword" }, method = RequestMethod.POST)
	public ModelAndView processForgetPassword(@RequestParam String username) {
		ModelAndView modelAndView = new ModelAndView();
		if (userService.getUser(username).size() > 0) {
			if (userService.getUser(username).get(0).getUserType() == 4
					&& userService.getUser(username).get(0).getLocked() != 1) {
				modelAndView.addObject("user", userService.getUser(username).get(0));
				modelAndView.setViewName("step2ForgetPassword");
				loggingService.logInfo(
						"Anonymous with username: " + username + " is correct at step1 of forget password process.");
			} else {
				modelAndView.setViewName("login");
				System.out.println("Username not a user type");
				loggingService.logInfo("Anonymous user failed at step2 of forget password process.");
			}
		} else {
			modelAndView.setViewName("login");
			loggingService.logInfo("Anonymous committed an error in step1 of forget password process.");
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/forgetPassword" }, method = RequestMethod.GET)
	public ModelAndView forgetPassword() {
		ModelAndView modelAndView = new ModelAndView();
		loggingService.logInfo("Anonymous user is now accessing the forget password page.");
		modelAndView.setViewName("forgetPassword");
		return modelAndView;
	}

	@RequestMapping(value = { "/step2ProcessForgetPassword" }, method = RequestMethod.POST)
	public ModelAndView step2ProcessForgetPassword(@RequestParam String secretAnswer, @RequestParam String username) {
		ModelAndView modelAndView = new ModelAndView();
		if (userService.getUser(username).get(0) != null) {
			// if user..
			if (userService.getUser(username).get(0).getUserType() == 4
					&& userService.getUser(username).get(0).getLocked() != 1) {
				if (bCryptPasswordEncoder.matches(secretAnswer,
						userService.getUser(username).get(0).getSecretAnswer())) {
					modelAndView.addObject("user", userService.getUser(username).get(0));
					modelAndView.setViewName("finalforgetPassword");
					loggingService.logInfo("Anonymous with username: " + username
							+ " is correct at step2 of forget password process.");
				} else {
					modelAndView.setViewName("login");
					loggingService.logInfo("Anonymous user failed at step2 of forget password process.");
					System.out.println("Username plus 1 wrong attempt..");
					User user = userService.getUser(username).get(0);
					user.setWrongAttempts(user.getWrongAttempts() + 1);
					if (user.getWrongAttempts() == 5) {
						user.setLocked(1);
						user.setWrongAttempts(0);
						System.out.println("Username is locked");
					}
					userService.updateUser(user);
				}
			} else {
				modelAndView.setViewName("login");
				System.out.println("Username not a user type or Account is locked");
				loggingService.logInfo("Anonymous user failed at step2 of forget password process.");
			}
		} else {
			modelAndView.setViewName("login");
			loggingService.logInfo("Anonymous user failed at step2 of forget password process.");
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/changePasswordForget" }, method = RequestMethod.POST)
	public ModelAndView changePasswordForget(@RequestParam String password, @RequestParam String checkPassword,
			@RequestParam String username) {
		ModelAndView modelAndView = new ModelAndView();
		User user = userService.getUser(username).get(0);

		if (user != null) {
			System.out.println("Attempting to change password.");
			System.out.println("NewPassword: " + password);
			System.out.println("ConfirmPassword: " + checkPassword);
			if (isPasswordValid(password, userService.getUser(username).get(0).getUsername(),
					userService.getUser(username).get(0).getFirstName(),
					userService.getUser(username).get(0).getLastName()) && password.equals(checkPassword) && 
					!bCryptPasswordEncoder.matches(password, user.getPassword())) {
				modelAndView.setViewName("/login");
				loggingService
						.logInfo("Anonymous with username: " + user.getUsername() + " successfully changed password.");
			} else {
				modelAndView.setViewName("/login");
				loggingService.logInfo("Change password failed due to invalid password input.");
			}
		} else {
			modelAndView.setViewName("/login");
			loggingService.logInfo("Change password failed due to some data error.");
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/registration" }, method = RequestMethod.POST)
	public ModelAndView registrationDone(@Valid User user) {

		ModelAndView modelAndView = new ModelAndView();

		loggingService.logInfo("Checking if username: " + user.getUsername() + " is existing.");
		// Existing username not found..
		if (registrationService.checkExistingUser(user.getUsername()).size() == 0) {


			// Process input..
			System.out.println("User name is: " + user.getUsername());

			// Parsing date
			String[] date = user.getBirthday().split(",");
//			user.setBirthday(date[2] + "/" + date[0] + "/" + date[1]);
			user.setBirthday("1998-01-02");

			// Set the lock status and number of attempts to 0..
			user.setLocked(0);
			user.setWrongAttempts(0);

			// Add the new user to the database..
			user.setUserType(4);

			//user.setUserType(1);
			registrationService.addUser(user);
			System.out.println("New user created!");
			modelAndView.setViewName("login");
			loggingService.logInfo("Username not existing.");
			loggingService.logInfo("Anonymous has successfully created an account. Redirecting to login page.");
		} else {
			System.out.println("EXISTING USER ALREADY!");
			loggingService.logInfo("Account creation failed because the username: " + user.getUsername()
					+ " is already existing. Redirecting back to registration.");
			modelAndView.setViewName("registration");
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/loginLocked" }, method = RequestMethod.GET)
	public ModelAndView loginLocked() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("loginLocked");
		loggingService.logInfo(
				"Account login credentials are now correct but is locked due to past numerous invalid password inputs.");
		return modelAndView;
	}

	// One time run only after login..
	@RequestMapping(value = { "/startUp" }, method = RequestMethod.GET)
	public ModelAndView startUp() {
		ModelAndView modelAndView = new ModelAndView();

		loggingService.logInfo("Getting the server files ready.");
		// Used for Resetting the DB Item_List..
		// List<Item> items = itemService.getAllItems();
		// for (int i = 0; i < items.size(); i++) {
		// items.get(i).setItemStatus(0);
		// items.get(i).setDateOfAvailability(null);
		// itemService.saveItem(items.get(i));
		// }

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());
		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		loggingService.logInfo("Current user authenticated: " + user.getUsername());

		loggingService.logInfo("Updating item statuses if necessary.");
		// Update status if borrowed items return date are already before
		List<ItemReservation> checkItemReservations = itemReservationService.findItemReservationByStatusONLY(2);
		for (int i = 0; i < checkItemReservations.size(); i++) {

			// Date today
			LocalDate today = LocalDate.now();
			String dateToday = today + "";

			// // Increment by one on the returnDate..
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// String dt = checkItemReservations.get(i).getReturnDate(); //
			// Start
			// // date
			// Calendar c = Calendar.getInstance();
			// try {
			// c.setTime(sdf.parse(dt));
			// } catch (ParseException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			// c.add(Calendar.DATE, 1); // number of days to add
			// dt = sdf.format(c.getTime()); // dt is now the new date

			// Check returnDate if before Today's date..
			try {
				// If returnDate+1 == todayDate, then update status..
				// System.out.println("Return Date+1: " + dt + " comparing to
				// Today Date: " + dateToday);
				if (sdf.parse(checkItemReservations.get(i).getReturnDate()).before(sdf.parse(dateToday))) {
					// Change to available status..
					Item updateItem = itemService.getItem(checkItemReservations.get(i).getItem_id());
					updateItem.setItemStatus(0);
					updateItem.setDateOfAvailability(null);
					itemService.saveItem(updateItem);
				} else {
					// User is still borrowing item..
					Item updateItem = itemService.getItem(checkItemReservations.get(i).getItem_id());
					updateItem.setItemStatus(1);
					updateItem.setDateOfAvailability(checkItemReservations.get(i).getReturnDate());
					itemService.saveItem(updateItem);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (user.getUserType() == 4) {

			// Set cart for student/faculty..
			loggingService.logInfo("Adjusting reserve cart items if necessary.");
			// Adjust borrow dates and return dates if needed..3-IN CART
			List<ItemReservation> itemReservations = itemReservationService.findItemReservationByStatus(user.getId(),
					3);
			// Date for today..
			LocalDate today = LocalDate.now();
			String dateToday = today + "";
			for (int i = 0; i < itemReservations.size(); i++) {
				if (!(itemReservations.get(i).getBorrowDate().equals(dateToday))) {

					// Adjust Borrow Date..
					itemReservations.get(i).setBorrowDate(dateToday);

					LocalDate nextWeek = null;
					String dateNextWeek = null;
					// Check User Type First..
					// If Faculty..
					if (userService.findUserById(itemReservations.get(i).getUser_id()).get(0).getAccountType() == 1) {
						// Add 1 months to the current date
						nextWeek = today.plus(1, ChronoUnit.MONTHS);
						dateNextWeek = nextWeek + "";
					}
					// If student, 1 week..
					else if (userService.findUserById(itemReservations.get(i).getUser_id()).get(0)
							.getAccountType() == 2) {
						// Add 1 week to the current date
						nextWeek = today.plus(1, ChronoUnit.WEEKS);
						dateNextWeek = nextWeek + "";
					}

					// Adjust Return Date..
					itemReservations.get(i).setReturnDate(dateNextWeek);

					// Update To DB..
					itemReservationService.saveItemReservation(itemReservations.get(i));
				}
			}

			loggingService.logInfo("Redirecting user: " + user.getUsername() + " to home page.");
			modelAndView.setViewName("user/home");
		} else if (user.getUserType() == 1) {
			// set needed objects for admin..
			// Students/Faculty..
			List<User> users = userService.findByUserType(4);
			modelAndView.addObject("users", users);
			modelAndView.addObject("user", new User());

			// Staff and Managers..
			modelAndView.addObject("managers", userService.findByUserType(2));
			modelAndView.addObject("staffs", userService.findByUserType(3));

			loggingService.logInfo("Redirecting user: " + user.getUsername() + " to admin page.");
			modelAndView.setViewName("admin/admin");
		} else if (user.getUserType() == 2 || user.getUserType() == 3) {

			// Set needed objects for manager/staff..
			modelAndView.addObject("items", itemService.getAllItems());
			modelAndView.addObject("item", new Item());

			// Set the meeting rooms schedule..
			// Only show schedule for tomorrow..

			// Get Today's date..
			LocalDate today1 = LocalDate.now();
			today1 = today1.plus(1, ChronoUnit.DAYS);
			String tomorrow = today1 + "";

			// Check if there are roomReservations for tomorrow..
			if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

				// Check room by room.. 5 Rooms total..
				System.out.println("Checking Rooms.");

				// Room1
				List<Schedule> schedules1 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
				// Room2
				List<Schedule> schedules2 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
				// Room3
				List<Schedule> schedules3 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
				// Room4
				List<Schedule> schedules4 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
				// Room5
				List<Schedule> schedules5 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

			}
			// All rooms available
			else {

				System.out.println("All rooms are available.");

				List<Schedule> schedule1 = new ArrayList<Schedule>();
				List<Schedule> schedule2 = new ArrayList<Schedule>();
				List<Schedule> schedule3 = new ArrayList<Schedule>();
				List<Schedule> schedule4 = new ArrayList<Schedule>();
				List<Schedule> schedule5 = new ArrayList<Schedule>();
				// Set to available from 9AM to 8PM..
				Integer startTime = 9;
				Integer endTime = 10;
				String amPmS = " AM";
				String amPmE = " AM";
				for (int i = 0; i < 12; i++) {
					Schedule schedule = new Schedule();
					schedule.setStartTime(startTime + amPmS);
					schedule.setEndTime(endTime + amPmE);
					schedule.setStatus(0);
					schedule.setUsername("None");
					schedule1.add(schedule);
					schedule2.add(schedule);
					schedule3.add(schedule);
					schedule4.add(schedule);
					schedule5.add(schedule);

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
				modelAndView.addObject("schedule1", schedule1);
				modelAndView.addObject("schedule2", schedule2);
				modelAndView.addObject("schedule3", schedule3);
				modelAndView.addObject("schedule4", schedule4);
				modelAndView.addObject("schedule5", schedule5);
			}

			modelAndView.addObject("schedule", new Schedule());
			if (user.getUserType() == 2) {
				loggingService.logInfo("Redirecting user: " + user.getUsername() + " to manager page.");
				modelAndView.setViewName("manager/libraryManager");
			} else {
				loggingService.logInfo("Redirecting user: " + user.getUsername() + " to staff page.");
				modelAndView.setViewName("staff/libraryStaff");
			}
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/user/home" }, method = RequestMethod.GET)
	public ModelAndView displayHome() {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("user/home");

		loggingService.logInfo("User: " + user.getUsername() + " currently in the home page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/displayBooks" }, method = RequestMethod.GET)
	public ModelAndView displayBooks() {
		ModelAndView modelAndView = new ModelAndView();
		List<Item> itemTest = itemService.getAllBooks();

		modelAndView.addObject("item", new Item());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.addObject("books", itemTest);
		modelAndView.setViewName("user/displayBooks");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing books.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/displayMagazines" }, method = RequestMethod.GET)
	public ModelAndView displayMagazines() {
		ModelAndView modelAndView = new ModelAndView();
		List<Item> itemTest = itemService.getAllMagazines();

		modelAndView.addObject("item", new Item());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.addObject("magazines", itemTest);
		modelAndView.setViewName("user/displayMagazines");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing magazines.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/displayMeetingRooms" }, method = RequestMethod.GET)
	public ModelAndView displayMeetingRooms() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime = 9;
			Integer endTime = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

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
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());
		modelAndView.setViewName("user/displayMeetingRooms");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing meeting rooms.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/displayTheses" }, method = RequestMethod.GET)
	public ModelAndView displayTheses() {
		ModelAndView modelAndView = new ModelAndView();
		List<Item> itemTest = itemService.getAllTheses();

		modelAndView.addObject("item", new Item());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.addObject("theses", itemTest);
		modelAndView.setViewName("user/displayTheses");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing theses.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/displayAuthors" }, method = RequestMethod.GET)
	public ModelAndView displayAuthors() {
		ModelAndView modelAndView = new ModelAndView();
		List<Item> itemTest = itemService.getAllAuthors();

		modelAndView.addObject("item", new Item());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.addObject("authors", itemTest);
		modelAndView.setViewName("user/displayAuthors");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing authors.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/displayPublishers" }, method = RequestMethod.GET)
	public ModelAndView displayPublishers() {
		ModelAndView modelAndView = new ModelAndView();
		List<Item> itemTest = itemService.getAllPublishers();

		modelAndView.addObject("item", new Item());
		modelAndView.addObject("publishers", itemTest);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("user/displayPublishers");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing publishers.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/viewAccount" }, method = RequestMethod.GET)
	public ModelAndView viewAccount() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		loggingService.logInfo("Loading account details.");

		// Transaction History..
		// Only Show Borrowed Items and Reserved Meeting Rooms..
		List<ItemReservation> cartItems = itemReservationService.findItemReservationByStatus(user.getId(), 2);

		// Check the returnDate, if returnDate is before Today's date, then the
		// user can make a review on that Item..
		for (int i = 0; i < cartItems.size(); i++) {

			// Get Today's date..
			LocalDate today = LocalDate.now();
			String dateToday = today + "";

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			// Check returnDate if before Today's date..
			try {
				if (sdf.parse(cartItems.get(i).getReturnDate()).before(sdf.parse(dateToday))) {
					cartItems.get(i).setCan_review(1);
				} else {
					cartItems.get(i).setCan_review(0);
				}
				// Update ItemReservation..
				itemReservationService.saveItemReservation(cartItems.get(i));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		modelAndView.addObject("cartItems", cartItems);
		modelAndView.addObject("itemReservation", new ItemReservation());

		modelAndView.addObject("roomTransactions",
				roomReservationService.getRoomReservationsByUsername(user.getUsername()));
		modelAndView.addObject("room", new ItemReservation());

		modelAndView.setViewName("user/viewAccount");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing account page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/editAccount" }, method = RequestMethod.GET)
	public ModelAndView editAccount() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("user/editAccount");
		loggingService.logInfo("Username " + user.getUsername() + " currently in edit account page");
		return modelAndView;
	}

	@RequestMapping(value = "/user/viewItem", method = RequestMethod.POST)
	public ModelAndView viewItemPOST(@RequestParam Integer id) {
		ModelAndView modelAndView = new ModelAndView();

		loggingService.logInfo("Loading item details.");

		// User Info
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());
		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		System.out.println("User ID: " + user.getId());

		// Item Reservation Info
		System.out.println("Item ID is: " + id);

		// Tag..
		if (tagService.findTagByItem(id) != null) {
			modelAndView.addObject("tag", tagService.findTagByItem(id).get(0));
		} else {
			Tag tag = new Tag();
			tag.setTag("None");
			modelAndView.addObject("tag", tag);
		}

		// Check Item Status
		Item checkItem = itemService.getItem(id);
		if (checkItem.getItemStatus() == 0) {

			// Date Info..
			LocalDate today = LocalDate.now();
			System.out.println("Current date: " + today);

			String dateToday = today + "";
			modelAndView.addObject("dateNow", dateToday);
			LocalDate nextWeek = null;
			if (user.getAccountType() == 2) {
				// add 1 week to the current date if student..
				nextWeek = today.plus(1, ChronoUnit.WEEKS);
				System.out.println("Next week: " + nextWeek);
			} else { // add 1 month to the current date
				nextWeek = today.plus(1, ChronoUnit.MONTHS);
				System.out.println("Next week: " + nextWeek);
			}

			String dateNextWeek = nextWeek + "";
			modelAndView.addObject("dateNextWeek", dateNextWeek);

			// More Cases..
			if (itemReservationService.findItemReservationByItemId(id).size() > 0) {
				System.out.println("More Cases");
				List<ItemReservation> checkItemReservation = itemReservationService.findItemReservationByItemId(id);
				for (int i = 0; i < checkItemReservation.size(); i++) {
					if (checkItemReservation.get(i).getStatus() == 3
							&& checkItemReservation.get(i).getUser_id() != user.getId()) {
						// Already In Someone's Cart
						modelAndView.addObject("status", 3);
					} else if (checkItemReservation.get(i).getStatus() == 3
							&& checkItemReservation.get(i).getUser_id() == user.getId()) {
						// Already In Cart
						modelAndView.addObject("status", 4);
						break;
					} else {
						// Available (Consider case: User already borrowed this
						// item before, so it will be present in the Item
						// Reservations Table)..
						modelAndView.addObject("status", 1);
					}
				}
			}
			// Item Id not found in the item reservations..
			else {
				// Available
				modelAndView.addObject("status", 1);
			}
		} else {
			if (itemReservationService.findItemReservationByItemId(id).size() > 0) {
				List<ItemReservation> checkItemReservation = itemReservationService.findItemReservationByItemId(id);
				for (int i = 0; i < checkItemReservation.size(); i++) {
					if (checkItemReservation.get(i).getStatus() == 2
							&& checkItemReservation.get(i).getUser_id() == user.getId()) {
						// Case if User was the one who borrowed..
						modelAndView.addObject("status", 5);
						modelAndView.addObject("returnDate", checkItem.getDateOfAvailability());
						break;
					} else {
						// Not Available
						modelAndView.addObject("status", 2);
						modelAndView.addObject("returnDate", checkItem.getDateOfAvailability());
					}
				}
			}
		}

		// Item Info
		Item newItem = itemService.getItem(id);
		modelAndView.addObject("item", newItem);

		// Item Reviews
		modelAndView.addObject("reviews", reviewService.findReviewsByItemId(id));
		modelAndView.addObject("review", new Review());

		modelAndView.setViewName("user/viewItem");
		loggingService.logInfo("Username " + user.getUsername() + " currently viewing item: " + newItem.getItemName());
		return modelAndView;
	}

	@RequestMapping(value = { "/user/authorPublisherResults" }, method = RequestMethod.POST)
	public ModelAndView authorPublisherResults(@RequestParam String authPub) {
		ModelAndView modelAndView = new ModelAndView();

		// Check if author or publisher
		if (itemService.getAuthorWorks(authPub).size() > 0) {
			// Author Search Result
			System.out.println("Author is: " + authPub);

			List<Item> items = itemService.getAuthorWorks(authPub);
			modelAndView.addObject("items", items);
		} else {
			// Publisher Search Result

			System.out.println("Publisher is: " + authPub);

			List<Item> items = itemService.getPublisherWorks(authPub);
			modelAndView.addObject("items", items);
		}

		modelAndView.addObject("item", new Item());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("user/authorPublisherResults");
		loggingService
				.logInfo("Username " + user.getUsername() + " currently viewing authors and publishers results page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/resultsPage" }, method = RequestMethod.POST)
	public ModelAndView resultsPage(@RequestParam String keyWord) {
		ModelAndView modelAndView = new ModelAndView();

		System.out.println("Searching Keyword: " + keyWord);

		modelAndView.addObject("item", new Item());

		List<Item> items = itemService.findKeyWord(keyWord);
		modelAndView.addObject("results", items);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("user/resultsPage");
		loggingService.logInfo(
				"Username " + user.getUsername() + " searched for: " + keyWord + ". Currently in the results page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/reserveCart" }, method = RequestMethod.GET)
	public ModelAndView reserveCart() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		List<ItemReservation> cartItems = itemReservationService.findItemReservationByUserId(user.getId());
		modelAndView.addObject("cartItems", cartItems);

		loggingService.logInfo("Loading username " + user.getUsername() + " cart items.");

		// Checking cart size for basis in disabling/enabling confirm
		// reservation button.
		if (cartItems.size() > 0) {
			modelAndView.addObject("size", 1);
			System.out.println("There are cart items..");
		} else {
			modelAndView.addObject("size", 0);
			System.out.println("There are no cart items..");
		}
		modelAndView.addObject("userID", user.getId());

		modelAndView.addObject("itemReservation", new ItemReservation());
		modelAndView.setViewName("user/reserveCart");
		loggingService.logInfo("Username " + user.getUsername() + " currently in reserve cart.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/processCart" }, method = RequestMethod.POST)
	public ModelAndView processCart(@Valid ItemReservation itemReservation) {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		// Checking ItemReservation..
		System.out.println("Item ID: " + itemReservation.getItem_id() + "User ID: " + itemReservation.getUser_id()
				+ "Borrow Date: " + itemReservation.getBorrowDate() + "Return Date: "
				+ itemReservation.getReturnDate());

		// Find the Item Name..
		Item item = itemService.getItem(itemReservation.getItem_id());
		// Set the Item Name..
		itemReservation.setItemName(item.getItemName());
		// Set the Item Type..
		if (item.getItemType() == 0) {
			itemReservation.setItemType("Book");
		} else if (item.getItemType() == 1) {
			itemReservation.setItemType("Magazine");
		} else if (item.getItemType() == 2) {
			itemReservation.setItemType("Thesis");
		}

		// Add ItemReservation..
		itemReservationService.saveItemReservation(itemReservation);
		System.out.println("Saved Item Reservation!");

		List<ItemReservation> cartItems = itemReservationService.findItemReservationByUserId(user.getId());
		modelAndView.addObject("cartItems", cartItems);
		modelAndView.addObject("itemReservation", new ItemReservation());

		// Checking cart size for basis in disabling/enabling confirm
		// reservation button.
		if (cartItems.size() > 0) {
			modelAndView.addObject("size", 1);
			System.out.println("There are cart items..");
		} else {
			modelAndView.addObject("size", 0);
			System.out.println("There are no cart items..");
		}
		modelAndView.addObject("userID", user.getId());

		modelAndView.setViewName("user/reserveCart");
		loggingService.logInfo("Username " + user.getUsername() + " added item: " + itemReservation.getItemName()
				+ " to reserve cart.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/confirmReservationCart" }, method = RequestMethod.POST)
	public ModelAndView confirmItemReservation(@RequestParam int id) {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		// Return All IN CART Item Reservations..
		List<ItemReservation> itemReservations = itemReservationService.findItemReservationByUserId(id);

		for (int i = 0; i < itemReservations.size(); i++) {
			// Set to Borrowed Status..
			itemReservations.get(i).setStatus(2);
			itemReservations.get(i).setCan_review(0);
			// Delete other users' reservations on the same items..
			List<ItemReservation> sameItemReservations = itemReservationService
					.findSameItemReservations(itemReservations.get(i).getItem_id(), user.getId());
			for (int j = 0; j < sameItemReservations.size(); j++) {
				itemReservationService.deleteItemReservation(sameItemReservations.get(i).getId());
			}
			// Update Status..
			itemReservationService.saveItemReservation(itemReservations.get(i));

			// Update Item Status..
			Item updateItem = itemService.getItem(itemReservations.get(i).getItem_id());
			updateItem.setItemStatus(1);
			updateItem.setDateOfAvailability(itemReservations.get(i).getReturnDate());
			itemService.saveItem(updateItem);
		}

		System.out.println("Confirmed Item Reservations!");

		List<ItemReservation> cartItems = itemReservationService.findItemReservationByUserId(user.getId());
		modelAndView.addObject("cartItems", cartItems);
		modelAndView.addObject("itemReservation", new ItemReservation());

		// modelAndView.setViewName("user/reserveCart");
		modelAndView.setViewName("user/home");
		loggingService.logInfo("Username " + user.getUsername()
				+ " confirmed all item reservations in cart. Redirecting user to the home page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/deleteItemReservation" }, method = RequestMethod.POST)
	public ModelAndView deleteItemReservation(@RequestParam int itemid) {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		itemReservationService.deleteItemReservation(itemid);
		System.out.println("Deleted Item Reservation!");

		List<ItemReservation> cartItems = itemReservationService.findItemReservationByUserId(user.getId());
		modelAndView.addObject("cartItems", cartItems);
		modelAndView.addObject("itemReservation", new ItemReservation());

		// Checking cart size for basis in disabling/enabling confirm
		// reservation button.
		if (cartItems.size() > 0) {
			modelAndView.addObject("size", 1);
			System.out.println("There are cart items..");
		} else {
			modelAndView.addObject("size", 0);
			System.out.println("There are no cart items..");
		}
		modelAndView.addObject("userID", user.getId());

		modelAndView.setViewName("user/reserveCart");
		loggingService.logInfo("Username " + user.getUsername() + " deleted item reservation id: " + itemid + ".");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/processMeetingRooms" }, method = RequestMethod.POST)
	public ModelAndView processRooms(@RequestParam String location, @RequestParam String startTime,
			@RequestParam String endTime) {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		// Adding the Meeting Reservation..
		RoomReservation roomReservation = new RoomReservation();

		// Date for tomorrow..
		LocalDate tom = LocalDate.now();
		tom = tom.plus(1, ChronoUnit.DAYS);
		roomReservation.setDate(tom + "");
		roomReservation.setLocation(location);
		roomReservation.setStartTime(startTime);
		roomReservation.setEndTime(endTime);
		roomReservation.setUserId(user.getId());
		roomReservation.setUsername(user.getUsername());

		System.out.print(endTime);
		roomReservationService.addRoomReservation(roomReservation);

		loggingService.logInfo("Username: " + user.getUsername() + " is reserving a meeting room at " + location + ", "
				+ startTime + " up to" + endTime + ".");

		// Transaction History..
		// Only Show Borrowed Items and Reserved Meeting Rooms..
		List<ItemReservation> cartItems = itemReservationService.findItemReservationByStatus(user.getId(), 2);

		// Check the returnDate, if returnDate is before Today's date, then the
		// user can make a review on that Item..
		for (int i = 0; i < cartItems.size(); i++) {

			// Get Today's date..
			LocalDate today = LocalDate.now();
			String dateToday = today + "";

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			// Check returnDate if before Today's date..
			try {
				if (sdf.parse(cartItems.get(i).getReturnDate()).before(sdf.parse(dateToday))) {
					cartItems.get(i).setCan_review(1);
				} else {
					cartItems.get(i).setCan_review(0);
				}
				// Update ItemReservation..
				itemReservationService.saveItemReservation(cartItems.get(i));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		modelAndView.addObject("cartItems", cartItems);
		modelAndView.addObject("itemReservation", new ItemReservation());

		modelAndView.addObject("roomTransactions",
				roomReservationService.getRoomReservationsByUsername(user.getUsername()));
		modelAndView.addObject("room", new ItemReservation());

		modelAndView.setViewName("user/viewAccount");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/processReview" }, method = RequestMethod.POST)
	public ModelAndView processReview(@RequestParam Integer item_id, @RequestParam String review) {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("user", user);

		// Testing..
		System.out.println("Review Message: " + review + " Item ID: " + item_id);
		// Set/Create the review..
		Review newReview = new Review();
		newReview.setUser_id(user.getId());
		newReview.setItem_id(item_id);
		newReview.setReview(review);
		newReview.setReviewer(user.getUsername());
		// Save Review in DB..
		reviewService.addReview(newReview);

		// Reload Necessary Data for ViewAccount..
		// Transaction History..
		// Only Show Borrowed Items and Reserved Meeting Rooms..
		List<ItemReservation> cartItems = itemReservationService.findItemReservationByStatus(user.getId(), 2);
		modelAndView.addObject("cartItems", cartItems);
		modelAndView.addObject("itemReservation", new ItemReservation());

		modelAndView.addObject("roomTransactions",
				roomReservationService.getRoomReservationsByUsername(user.getUsername()));
		modelAndView.addObject("room", new ItemReservation());
		modelAndView.setViewName("user/viewAccount");
		loggingService.logInfo("Username " + user.getUsername() + " added a review for item id: " + item_id);
		return modelAndView;
	}

	@RequestMapping(value = { "/manager/libraryManager" }, method = RequestMethod.GET)
	public ModelAndView libraryManager() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("items", itemService.getAllItems());
		modelAndView.addObject("item", new Item());

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime = 9;
			Integer endTime = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

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
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());

		modelAndView.setViewName("manager/libraryManager");
		loggingService.logInfo("Username " + user.getUsername() + " currently in manager page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/staff/libraryStaff" }, method = RequestMethod.GET)
	public ModelAndView libraryStaff() {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("items", itemService.getAllItems());
		modelAndView.addObject("item", new Item());

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime = 9;
			Integer endTime = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

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
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());
		modelAndView.setViewName("staff/libraryStaff");
		loggingService.logInfo("Username " + user.getUsername() + " currently in staff page.");
		return modelAndView;
	}

	@RequestMapping(value = { "/ViewItem" }, method = RequestMethod.POST)
	public ModelAndView viewItem(@RequestParam Integer item_id, @RequestParam Integer staff) {
		ModelAndView modelAndView = new ModelAndView();

		// Check Item Status
		Item checkItem = itemService.getItem(item_id);
		if (checkItem.getItemStatus() == 0) {
			// Available
			modelAndView.addObject("status", 1);
		} else {
			// Not Available
			modelAndView.addObject("status", 2);
			modelAndView.addObject("returnDate", checkItem.getDateOfAvailability());
		}

		// Tag..
		if (tagService.findTagByItem(item_id) != null) {
			modelAndView.addObject("tag", tagService.findTagByItem(item_id).get(0));
		} else {
			Tag tag = new Tag();
			tag.setTag("None");
			modelAndView.addObject("tag", tag);
		}

		// Item Info
		Item newItem = itemService.getItem(item_id);
		modelAndView.addObject("item", newItem);

		// Item Reviews
		modelAndView.addObject("reviews", reviewService.findReviewsByItemId(item_id));
		modelAndView.addObject("review", new Review());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime = 9;
			Integer endTime = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

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
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());

		User user = userService.getUser(auth.getName()).get(0);

		if (staff == 1) {
			loggingService
					.logInfo("Staff username " + user.getUsername() + " viewing item: " + newItem.getItemName() + ".");
			modelAndView.setViewName("staff/viewItem");
		} else if (staff == 0) {
			loggingService.logInfo(
					"Manager username " + user.getUsername() + " viewing item: " + newItem.getItemName() + ".");
			modelAndView.setViewName("manager/viewItem");
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/addItem" }, method = RequestMethod.POST)
	public ModelAndView addItem(@RequestParam String itemName, @RequestParam String author,
								@RequestParam String publisher, @RequestParam String location, @RequestParam Integer yearPublished,
								@RequestParam Integer itemType, @RequestParam Integer staff) {
		ModelAndView modelAndView = new ModelAndView();

		// Setting the new Item..
		Item item = new Item();
		item.setItemName(itemName);
		item.setAuthor(author);
		item.setPublisher(publisher);
		item.setLocation(location);
		item.setYearPublished(yearPublished);
		item.setItemType(itemType);
		item.setItemStatus(0);
		item.setDateOfAvailability(null);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		loggingService.logInfo(
				"Username: " + user.getUsername() + " adding new item, Name: " + item.getItemName() + " Author: "
						+ item.getAuthor() + " Publisher: " + item.getPublisher() + " Location: " + item.getLocation()
						+ " Item Type: " + item.getItemType() + " Year Published: " + item.getYearPublished());

		// Add the new Item..
		itemService.addItem(item);

		modelAndView.addObject("items", itemService.getAllItems());
		modelAndView.addObject("item", new Item());

		if (staff == 1) {
			modelAndView.setViewName("/staff/libraryStaff");
		} else if (staff == 0) {
			modelAndView.setViewName("/manager/libraryManager");
		}

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime = 9;
			Integer endTime = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

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
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());

		return modelAndView;
	}

	@RequestMapping(value = { "/deleteItem" }, method = RequestMethod.POST)
	public ModelAndView deleteItem(@RequestParam Integer delete_id, @RequestParam Integer staff) {
		ModelAndView modelAndView = new ModelAndView();

		itemService.deleteItem(delete_id);

		System.out.println("Deleting Item");

		modelAndView.addObject("items", itemService.getAllItems());
		modelAndView.addObject("item", new Item());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);

		loggingService.logInfo("Username: " + user.getUsername() + " deleting item id: " + delete_id);

		if (staff == 1) {
			modelAndView.setViewName("staff/libraryStaff");
		} else if (staff == 0) {
			modelAndView.setViewName("manager/libraryManager");
		}

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime = 9;
			Integer endTime = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

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
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());

		return modelAndView;
	}

	@RequestMapping(value = { "/editItem" }, method = RequestMethod.POST)
	public ModelAndView editItem(@RequestParam Integer id, @RequestParam String itemName, @RequestParam String author,
			@RequestParam String publisher, @RequestParam String location, @RequestParam Integer yearPublished,
			@RequestParam String itemType, @RequestParam Integer staff) {
		ModelAndView modelAndView = new ModelAndView();

		// Setting the new Item..
		Item item = new Item();
		item.setId(id);
		item.setItemName(itemName);
		item.setAuthor(author);
		item.setPublisher(publisher);
		item.setLocation(location);
		item.setYearPublished(yearPublished);

		if (itemType.equals("Book")) {
			item.setItemType(0);
		} else if (itemType.equals("Magazine")) {
			item.setItemType(1);
		} else if (itemType.equals("Thesis")) {
			item.setItemType(2);
		}
		item.setItemStatus(0);
		item.setDateOfAvailability(null);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);

		loggingService
				.logInfo("Username: " + user.getUsername() + " updating item, Name: " + item.getItemName() + " Author: "
						+ item.getAuthor() + " Publisher: " + item.getPublisher() + " Location: " + item.getLocation()
						+ " Item Type: " + item.getItemType() + " Year Published: " + item.getYearPublished());

		// Update Item..
		itemService.addItem(item);

		modelAndView.addObject("items", itemService.getAllItems());
		modelAndView.addObject("item", new Item());

		if (staff == 1) {
			modelAndView.setViewName("staff/libraryStaff");
		} else if (staff == 0) {
			modelAndView.setViewName("manager/libraryManager");
		}

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime = 9;
			Integer endTime = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime + amPmS);
				schedule.setEndTime(endTime + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

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
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());

		return modelAndView;
	}

	@RequestMapping(value = { "/admin/admin" }, method = RequestMethod.GET)
	public ModelAndView admin() {
		ModelAndView modelAndView = new ModelAndView();

		// Students/Faculty..
		List<User> users = userService.findByUserType(4);
		modelAndView.addObject("users", users);
		modelAndView.addObject("user", new User());

		// Staff and Managers..
		modelAndView.addObject("managers", userService.findByUserType(2));
		modelAndView.addObject("staffs", userService.findByUserType(3));

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);

		loggingService.logInfo("Username: " + user.getUsername() + " currently in the admin page.");

		modelAndView.setViewName("admin/admin");
		return modelAndView;
	}

	@RequestMapping(value = { "/admin/addStaff" }, method = RequestMethod.POST)
	public ModelAndView adminAddStaff(@RequestParam String username, @RequestParam String password,
			@RequestParam Integer userType, @RequestParam String email) {
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User adminUser = userService.getUser(auth.getName()).get(0);

		loggingService.logInfo(
				"Username: " + adminUser.getUsername() + " attempted adding new staff with username:" + username + ".");

		// Existing username not found..
		if (registrationService.checkExistingUser(username).size() == 0) {

			// Process input..
			System.out.println("User name is: " + username);

			// check inputs first..
			boolean accept = true;


			if (accept) {
				// Add the new user to the database..
				// Form the user..
				User user = new User();
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail(email);
				user.setUserType(userType);
				user.setLocked(0);
				user.setWrongAttempts(0);
				registrationService.addUser(user);
				System.out.println("New user created!");
			} else {
				loggingService.logInfo("Staff/Manager account creation failed. Admin input invalid account details.");
			}
		} else {
			System.out.println("EXISTING USER ALREADY!");
			loggingService.logInfo("Staff/Manager account creation failed. Username already exists.");
		}

		// Students/Faculty..
		List<User> users = userService.findByUserType(4);
		modelAndView.addObject("users", users);
		modelAndView.addObject("user", new User());

		// Staff and Managers..
		modelAndView.addObject("managers", userService.findByUserType(2));
		modelAndView.addObject("staffs", userService.findByUserType(3));
		modelAndView.setViewName("admin/admin");

		return modelAndView;
	}

	@RequestMapping(value = { "/admin/unlockAccount" }, method = RequestMethod.POST)
	public ModelAndView unlockAccount(@RequestParam Integer id) {
		ModelAndView modelAndView = new ModelAndView();

		// Unlock the userid..
		User user = userService.findUserById(id).get(0);
		user.setLocked(0);
		userService.updateUser(user);

		// Students/Faculty..
		List<User> users = userService.findByUserType(4);
		modelAndView.addObject("users", users);
		modelAndView.addObject("user", new User());

		// Staff and Managers..
		modelAndView.addObject("managers", userService.findByUserType(2));
		modelAndView.addObject("staffs", userService.findByUserType(3));

		modelAndView.setViewName("admin/admin");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User adminUser = userService.getUser(auth.getName()).get(0);
		loggingService
				.logInfo("Username: " + adminUser.getUsername() + " unlocking account: " + user.getUsername() + ".");

		return modelAndView;
	}

	@RequestMapping(value = { "/manager/override" }, method = RequestMethod.POST)
	public ModelAndView override(@RequestParam String startTime, @RequestParam String endTime,
								 @RequestParam String location) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		modelAndView.addObject("items", itemService.getAllItems());
		modelAndView.addObject("item", new Item());

		// Get Date..
		LocalDate date = LocalDate.now();
		date = date.plus(1, ChronoUnit.DAYS);

		// Remove Room Reservation..
		RoomReservation roomReservation = roomReservationService
				.getRoomReservationsByLocationAndTime(date + "", location, startTime, endTime).get(0);
		roomReservationService.deleteRoomReservation(roomReservation.getId());

		loggingService.logInfo("Username: " + user.getUsername() + " overriding room reservation made by "
				+ roomReservation.getUsername() + ".");

		// Set the meeting rooms schedule..
		// Only show schedule for tomorrow..

		// Get Today's date..
		LocalDate today = LocalDate.now();
		today = today.plus(1, ChronoUnit.DAYS);
		String tomorrow = today + "";

		// Check if there are roomReservations for tomorrow..
		if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

			// Check room by room.. 5 Rooms total..
			System.out.println("Checking Rooms.");

			// Room1
			List<Schedule> schedules1 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
			// Room2
			List<Schedule> schedules2 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
			// Room3
			List<Schedule> schedules3 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
			// Room4
			List<Schedule> schedules4 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
			// Room5
			List<Schedule> schedules5 = new ArrayList<Schedule>();
			modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

		}
		// All rooms available
		else {

			System.out.println("All rooms are available.");

			List<Schedule> schedule1 = new ArrayList<Schedule>();
			List<Schedule> schedule2 = new ArrayList<Schedule>();
			List<Schedule> schedule3 = new ArrayList<Schedule>();
			List<Schedule> schedule4 = new ArrayList<Schedule>();
			List<Schedule> schedule5 = new ArrayList<Schedule>();
			// Set to available from 9AM to 8PM..
			Integer startTime1 = 9;
			Integer endTime1 = 10;
			String amPmS = " AM";
			String amPmE = " AM";
			for (int i = 0; i < 12; i++) {
				Schedule schedule = new Schedule();
				schedule.setStartTime(startTime1 + amPmS);
				schedule.setEndTime(endTime1 + amPmE);
				schedule.setStatus(0);
				schedule.setUsername("None");
				schedule1.add(schedule);
				schedule2.add(schedule);
				schedule3.add(schedule);
				schedule4.add(schedule);
				schedule5.add(schedule);

				// Adjust the time slots..
				startTime1 += 1;
				if (startTime1 == 13) {
					startTime1 = 1;
				}
				endTime1 += 1;
				if (endTime1 == 13) {
					endTime1 = 1;
				}
				if (startTime1 == 12) {
					amPmS = " PM";
				}
				if (endTime1 == 12) {
					amPmE = " PM";
				}
			}
			modelAndView.addObject("schedule1", schedule1);
			modelAndView.addObject("schedule2", schedule2);
			modelAndView.addObject("schedule3", schedule3);
			modelAndView.addObject("schedule4", schedule4);
			modelAndView.addObject("schedule5", schedule5);
		}

		modelAndView.addObject("schedule", new Schedule());

		modelAndView.setViewName("manager/libraryManager");
		return modelAndView;
	}

	// @RequestMapping(value = { "/logout" }, method = RequestMethod.GET)
	// public ModelAndView logout() {
	// ModelAndView modelAndView = new ModelAndView();
	//
	// Authentication auth =
	// SecurityContextHolder.getContext().getAuthentication();
	// System.out.println(auth.getName());
	//
	// User user = userService.getUser(auth.getName()).get(0);
	// loggingService.logInfo("Username: " + user.getUsername() + "logging
	// out.");
	//
	// return modelAndView;
	// }

	@RequestMapping(value = { "/access-denied" }, method = RequestMethod.GET)
	public ModelAndView accessDenied() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);

		loggingService.logInfo("Username: " + user.getUsername()
				+ " tried to access unauthorized webpage/content. User is blocked from accessing the content.");

		modelAndView.setViewName("access-denied");
		return modelAndView;
	}

	@RequestMapping(value = { "/user/changePassword" }, method = RequestMethod.POST)
	public ModelAndView changePassword(@RequestParam String password, @RequestParam String checkPassword) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);

		System.out.println("Attempting to change password.");
		System.out.println("OLDPassword: " + checkPassword);
		System.out.println("NewPassword: " + password);
		// Last checking..
		if (bCryptPasswordEncoder.matches(checkPassword, user.getPassword()) && !checkPassword.equals(password)) {
			user.setPassword(bCryptPasswordEncoder.encode(password));
			// Update password..
			userService.updateUser(user);

			loggingService.logInfo("Username: " + user.getUsername()
					+ " successfully changed password. Redirecting the user back to the login page for re-authentication.");
			modelAndView.setViewName("redirect:/logout");
		} else {
			System.out.println("Old Password invalid.");

			loggingService.logInfo("Username: " + user.getUsername()
					+ " change password unsuccessful due to invalid inputs. Redirecting the user back to the account page.");
			modelAndView.addObject("user", user);

			loggingService.logInfo("Loading account details.");

			// Transaction History..
			// Only Show Borrowed Items and Reserved Meeting Rooms..
			List<ItemReservation> cartItems = itemReservationService.findItemReservationByStatus(user.getId(), 2);

			// Check the returnDate, if returnDate is before Today's date, then
			// the
			// user can make a review on that Item..
			for (int i = 0; i < cartItems.size(); i++) {

				// Get Today's date..
				LocalDate today = LocalDate.now();
				String dateToday = today + "";

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				// Check returnDate if before Today's date..
				try {
					if (sdf.parse(cartItems.get(i).getReturnDate()).before(sdf.parse(dateToday))) {
						cartItems.get(i).setCan_review(1);
					} else {
						cartItems.get(i).setCan_review(0);
					}
					// Update ItemReservation..
					itemReservationService.saveItemReservation(cartItems.get(i));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			modelAndView.addObject("cartItems", cartItems);
			modelAndView.addObject("itemReservation", new ItemReservation());

			modelAndView.addObject("roomTransactions",
					roomReservationService.getRoomReservationsByUsername(user.getUsername()));
			modelAndView.addObject("room", new ItemReservation());

			modelAndView.setViewName("user/viewAccount");
		}
		return modelAndView;
	}

	@RequestMapping(value = { "/goBack" }, method = RequestMethod.GET)
	public ModelAndView goBack() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);

		loggingService.logInfo("Username: " + user.getUsername()
				+ " tried to access unauthorized/error webpage. User is blocked/stopped from accessing the content.");

		if (user.getUserType() == 1) {
			// Students/Faculty..
			List<User> users = userService.findByUserType(4);
			modelAndView.addObject("users", users);
			modelAndView.addObject("user", new User());

			// Staff and Managers..
			modelAndView.addObject("managers", userService.findByUserType(2));
			modelAndView.addObject("staffs", userService.findByUserType(3));
			modelAndView.setViewName("admin/admin");
			loggingService.logInfo("Redirecting user back to the admin page.");
		} else if (user.getUserType() == 2 || user.getUserType() == 3) {
			modelAndView.addObject("items", itemService.getAllItems());
			modelAndView.addObject("item", new Item());

			// Set the meeting rooms schedule..
			// Only show schedule for tomorrow..

			// Get Today's date..
			LocalDate today = LocalDate.now();
			today = today.plus(1, ChronoUnit.DAYS);
			String tomorrow = today + "";

			// Check if there are roomReservations for tomorrow..
			if (roomReservationService.getRoomReservationsByDate(tomorrow).size() > 0) {

				// Check room by room.. 5 Rooms total..
				System.out.println("Checking Rooms.");

				// Room1
				List<Schedule> schedules1 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule1", meetingRoomsService.processSchedule(schedules1, "RoomNumber1"));
				// Room2
				List<Schedule> schedules2 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule2", meetingRoomsService.processSchedule(schedules2, "RoomNumber2"));
				// Room3
				List<Schedule> schedules3 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule3", meetingRoomsService.processSchedule(schedules3, "RoomNumber3"));
				// Room4
				List<Schedule> schedules4 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule4", meetingRoomsService.processSchedule(schedules4, "RoomNumber4"));
				// Room5
				List<Schedule> schedules5 = new ArrayList<Schedule>();
				modelAndView.addObject("schedule5", meetingRoomsService.processSchedule(schedules5, "RoomNumber5"));

			}
			// All rooms available
			else {

				System.out.println("All rooms are available.");

				List<Schedule> schedule1 = new ArrayList<Schedule>();
				List<Schedule> schedule2 = new ArrayList<Schedule>();
				List<Schedule> schedule3 = new ArrayList<Schedule>();
				List<Schedule> schedule4 = new ArrayList<Schedule>();
				List<Schedule> schedule5 = new ArrayList<Schedule>();
				// Set to available from 9AM to 8PM..
				Integer startTime = 9;
				Integer endTime = 10;
				String amPmS = " AM";
				String amPmE = " AM";
				for (int i = 0; i < 12; i++) {
					Schedule schedule = new Schedule();
					schedule.setStartTime(startTime + amPmS);
					schedule.setEndTime(endTime + amPmE);
					schedule.setStatus(0);
					schedule.setUsername("None");
					schedule1.add(schedule);
					schedule2.add(schedule);
					schedule3.add(schedule);
					schedule4.add(schedule);
					schedule5.add(schedule);

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
				modelAndView.addObject("schedule1", schedule1);
				modelAndView.addObject("schedule2", schedule2);
				modelAndView.addObject("schedule3", schedule3);
				modelAndView.addObject("schedule4", schedule4);
				modelAndView.addObject("schedule5", schedule5);
			}

			modelAndView.addObject("schedule", new Schedule());
			if (user.getUserType() == 2) {
				modelAndView.setViewName("manager/libraryManager");

				loggingService.logInfo("Redirecting user back to the manager page.");
			} else {
				modelAndView.setViewName("staff/libraryStaff");
				loggingService.logInfo("Redirecting user back to the staff page.");
			}
		} else if (user.getUserType() == 4) {
			modelAndView.addObject("user", user);
			modelAndView.setViewName("user/home");
			loggingService.logInfo("Redirecting user back to the home page.");
		}
		return modelAndView;
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest req, Exception ex) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());

		User user = userService.getUser(auth.getName()).get(0);
		loggingService.logInfo("Username: " + user.getUsername()
				+ " experienced webpage crashing. Either server side or page not found or others. User is redirected to the error page.");

		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", ex);
		mav.addObject("url", req.getRequestURL());
		mav.setViewName("error");
		return mav;
	}

	// Check if input contains only alphabet letters..
	public boolean isInputAlphabet(String input) {
		char[] chars = input.toCharArray();

		for (char c : chars) {
			if (!Character.isLetter(c)) {
				return false;
			}
		}
		return true;
	}

	// Check password input..
	private boolean isPasswordValid(String password, String username, String firstName, String lastName) {
		/*
		 * Passwords must have at least six characters.Passwords can’t contain
		 * the user name or parts of the user’s full name, such as his first
		 * name. Passwords must use at least three of the four available
		 * character types: lowercase letters, uppercase letters, numbers, and
		 * symbols.
		 */

		System.out.println("Password: " + password);
		int count = 0;

		if (password.length() < 6) {
			return false;
		} else {
			if (password.contains(username)) {
				return false;
			}
			if (password.contains(firstName)) {
				return false;
			}
			if (password.contains(lastName)) {
				return false;
			}

			if (password.matches(".+[A-Z].+") || password.matches("[A-Z].+") || password.matches(".+[A-Z]")) {
				System.out.println("A-Z");
				count++;
			}
			if (password.matches(".+[a-z].+") || password.matches("[a-z].+") || password.matches(".+[a-z]")) {
				System.out.println("a-z");
				count++;
			}
			if (password.matches(".+[1-9].+") || password.matches("[1-9].+") || password.matches(".+[1-9]")) {
				System.out.println("1-9");
				count++;
			}
			Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
			Matcher matcher = pattern.matcher(password);

			if (!matcher.matches()) {
				count++;
				System.out.println("Special Symbols");
			}
			if (count < 3) {
				return false;
			}
		}

		return true;
	}

	public boolean isValidUsername(String username) {
		int aCount = 0;
		int nCount = 0;
		if (username.matches(".+[A-Z].+") || username.matches("[A-Z].+") || username.matches(".+[A-Z]")) {
			System.out.println("A-Z");
			aCount++;
		}
		if (username.matches(".+[a-z].+") || username.matches("[a-z].+") || username.matches(".+[a-z]")) {
			System.out.println("a-z");
			aCount++;
		}
		if (username.matches(".+[1-9].+") || username.matches("[1-9].+") || username.matches(".+[1-9]")) {
			System.out.println("1-9");
			nCount++;
		}
		return nCount > 0 && aCount > 0;

	}

}