package edu.dlsu.securde.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import edu.dlsu.securde.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Integer>{

	@Query ("SELECT t " +
			"FROM Tag t " +
			"WHERE t.item_id = :item_id")
	ArrayList<Tag> findTagByItemId(@Param("item_id") Integer item_id);
}
