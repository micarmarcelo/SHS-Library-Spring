package edu.dlsu.securde.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "item_reservations")
public class ItemReservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name = "item_id")
	private Integer item_id;
	@Column(name = "user_id")
	private Integer user_id;
	
	//1 - Available, 2 - Borrowed, 3 - In Cart
	@Column(name = "status")
	private Integer status;
	@Column(name = "borrow_date")
	private String borrowDate;
	@Column(name = "return_date")
	private String returnDate;
	@Column(name = "item_name")
	private String itemName;
	@Column(name = "item_type")
	private String itemType;
	
	//1 - Item can be reviewed by the user already..
	@Column(name = "can_review")
	private Integer can_review;
	
	public ItemReservation(){
		
	}
	
	public ItemReservation(Integer id, Integer item_id, Integer user_id, Integer status, String borrowDate,
			String returnDate, String itemName, String itemType, Integer can_review) {
		super();
		this.id = id;
		this.item_id = item_id;
		this.user_id = user_id;
		this.status = status;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
		this.itemName = itemName;
		this.itemType = itemType;
		this.can_review = can_review;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(String borrowDate) {
		this.borrowDate = borrowDate;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Integer getCan_review() {
		return can_review;
	}

	public void setCan_review(Integer can_review) {
		this.can_review = can_review;
	}
	
}