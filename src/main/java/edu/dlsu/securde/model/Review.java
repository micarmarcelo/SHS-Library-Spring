package edu.dlsu.securde.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "item_id")
	private Integer item_id;
	@Column(name = "user_id")
	private Integer user_id;
	@Column(name = "review")
	private String review;
	@Column(name = "reviewer")
	private String reviewer;
	
	public Review(){
		
	}

	public Review(Integer id, Integer item_id, Integer user_id, String review, String reviewer) {
		super();
		this.id = id;
		this.item_id = item_id;
		this.user_id = user_id;
		this.review = review;
		this.reviewer = reviewer;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getItem_id() {
		return item_id;
	}

	public void setItem_id(Integer item_id) {
		this.item_id = item_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
}