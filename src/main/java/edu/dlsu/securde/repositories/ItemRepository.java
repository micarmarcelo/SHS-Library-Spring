package edu.dlsu.securde.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.dlsu.securde.model.Item;

public interface ItemRepository extends CrudRepository<Item, Integer> {

	@Query("SELECT i " + "FROM Item i " + "WHERE i.id = :id")
	ArrayList<Item> findItemById(@Param("id") Integer id);

	@Query("SELECT i " + "FROM Item i " + "WHERE i.itemType = :itemType")
	ArrayList<Item> findItemByItemType(@Param("itemType") Integer itemType);

	@Query("SELECT i " + "FROM Item i " + "GROUP BY(i.author)")
	ArrayList<Item> findAllAuthors();

	@Query("SELECT i " + "FROM Item i " + "GROUP BY(i.publisher)")
	ArrayList<Item> findAllPublishers();

	@Query("SELECT i " + "FROM Item i " + "WHERE i.publisher =:publisher")
	ArrayList<Item> findPublisherWorks(@Param("publisher") String publisher);

	@Query("SELECT i " + "FROM Item i " + "WHERE i.author =:author")
	ArrayList<Item> findAuthorWorks(@Param("author") String author);
	
	@Query("SELECT i " + "FROM Item i " + "WHERE i.author LIKE CONCAT('%',:keyWord,'%') OR i.itemName LIKE CONCAT('%',:keyWord,'%') "
			+ "OR i.publisher LIKE CONCAT('%',:keyWord,'%')")
	ArrayList<Item> findKeyWord(@Param("keyWord") String keyWord);
}
