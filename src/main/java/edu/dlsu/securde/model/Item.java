package edu.dlsu.securde.model;

import javax.persistence.*;

@Entity
@Table(name = "itemsList")
public class Item {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="ItemName")
	private String itemName;
	@Column(name="Location")
	private String location;
	@Column(name="Author")
	private String author;
	@Column(name="Publisher")
	private String publisher;
	@Column(name="YearPublished")
	private Integer yearPublished;
	@Column(name="ItemStatus")
	private Integer itemStatus;
	@Column(name="DateOfAvailability")
	private String dateOfAvailability;
	@Column(name="ItemType")
	private Integer itemType;
	
	public Item() {
		
	}

	public Item(int itemid, String itemName, String location, String author, String publisher, int yearPublished, int itemStatus, String dateOfAvailability, int itemType) {
		super();
		this.id = itemid;
		this.itemName = itemName;
		this.location = location;
		this.author = author;
		this.publisher = publisher;
		this.yearPublished = yearPublished;
		this.itemStatus = itemStatus;
		this.dateOfAvailability = dateOfAvailability;
		this.itemType = itemType;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getItemName() {
		return itemName;
	}
	public String setItemName(String itemName) {
		this.itemName = itemName;
		
		return this.itemName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public int getYearPublished() {
		return yearPublished;
	}
	public void setYearPublished(int yearPublished) {
		this.yearPublished = yearPublished;
	}
	public int getItemStatus() {
		return itemStatus;
	}
	public void setItemStatus(int itemStatus) {
		this.itemStatus = itemStatus;
	}
	public String getDateOfAvailability() {
		return dateOfAvailability;
	}
	public void setDateOfAvailability(String dateOfAvailability) {
		this.dateOfAvailability = dateOfAvailability;
	}
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	
}