package edu.dlsu.securde.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.dlsu.securde.model.ItemReservation;

@Repository
public interface ItemReservationRepository extends CrudRepository<ItemReservation, Integer> {

	// Used For Checking Item Status..
	@Query("SELECT ir " + "FROM ItemReservation ir " + "WHERE ir.item_id = :id")
	ArrayList<ItemReservation> findItemReservationByItemId(@Param("id") Integer id);

	// Return All IN CART Item Reservations..
	@Query("SELECT ir " + "FROM ItemReservation ir " + "WHERE ir.user_id = :id AND ir.status != 2")
	ArrayList<ItemReservation> findItemReservationByUserId(@Param("id") Integer id);

	// Return All Same Item Reservations By Different Users Based On The Given
	// Item ID..
	@Query("SELECT ir " + "FROM ItemReservation ir " + "WHERE ir.user_id != :user_id AND ir.item_id =:item_id")
	ArrayList<ItemReservation> findSameItemReservations(@Param("item_id") Integer item_id,
			@Param("user_id") Integer user_id);

	// Return Item Reservations Based On Item Status and User ID..
	@Query("SELECT ir " + "FROM ItemReservation ir " + "WHERE ir.user_id = :user_id AND ir.status =:status")
	List<ItemReservation> findItemReservationByStatus(@Param("user_id") Integer user_id,
			@Param("status") Integer status);

	// Return Item Reservations Based On Item Status ONLY..
	@Query("SELECT ir " + "FROM ItemReservation ir " + "WHERE ir.status =:status")
	List<ItemReservation> findItemReservationByStatusONLY(@Param("status") Integer status);
}
