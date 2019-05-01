package edu.dlsu.securde.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.Item;
import edu.dlsu.securde.repositories.ItemRepository;

@Service
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public List<Item> getAllItems() {
		List<Item> items = new ArrayList<Item>();
		itemRepository.findAll().forEach(items::add);
		return items;
	}

	public void addItem(Item item) {
		itemRepository.save(item);
	}

	public List<Item> getAllBooks() {
		List<Item> items = itemRepository.findItemByItemType(0);
		return items;
	}

	public List<Item> getAllMagazines() {
		List<Item> items = itemRepository.findItemByItemType(1);
		return items;
	}

	public Item getItem(int id) {
		return itemRepository.findItemById(id).get(0);
	}

	public List<Item> getAllTheses() {
		List<Item> items = itemRepository.findItemByItemType(2);
		return items;
	}

	public List<Item> getAllAuthors() {
		List<Item> items = itemRepository.findAllAuthors();
		return items;
	}

	public List<Item> getAllPublishers() {
		List<Item> items = itemRepository.findAllPublishers();
		return items;
	}

	public List<Item> getPublisherWorks(String publisher) {
		List<Item> items = itemRepository.findPublisherWorks(publisher);
		return items;
	}

	public List<Item> getAuthorWorks(String author) {
		List<Item> items = itemRepository.findAuthorWorks(author);
		return items;
	}
	
	public List<Item> findKeyWord(String keyWord){
		List<Item> items = itemRepository.findKeyWord(keyWord);
		return items;
	}
	
	public void saveItem(Item item){
		itemRepository.save(item);
	}
	
	public void deleteItem(Integer id){
		itemRepository.delete(id);
	}
}
