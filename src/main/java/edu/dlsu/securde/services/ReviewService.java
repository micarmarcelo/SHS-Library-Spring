package edu.dlsu.securde.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.Review;
import edu.dlsu.securde.repositories.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	public void addReview(Review newReview){
		reviewRepository.save(newReview);
	}
	
	public List<Review> findReviewsByItemId(Integer id){
		List<Review> reviews = reviewRepository.findReviewsByItemId(id);
		return reviews;
	}
}
