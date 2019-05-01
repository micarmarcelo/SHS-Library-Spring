package edu.dlsu.securde.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.dlsu.securde.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Integer> {

	@Query("SELECT r " + "FROM Review r " + "WHERE r.item_id = :id")
	ArrayList<Review> findReviewsByItemId(@Param("id") Integer id);
}
