package edu.dlsu.securde.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.Tag;
import edu.dlsu.securde.repositories.TagRepository;

@Service
public class TagService {

	@Autowired
	TagRepository tagRepository;
	
	public List<Tag> findTagByItem(Integer item_id){
		return tagRepository.findTagByItemId(item_id);
	}
}
