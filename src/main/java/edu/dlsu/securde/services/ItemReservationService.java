package edu.dlsu.securde.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.dlsu.securde.model.ItemReservation;
import edu.dlsu.securde.repositories.ItemReservationRepository;

@Service
public class ItemReservationService {

	@Autowired
	private ItemReservationRepository itemReservationRepository;

	public List<ItemReservation> findItemReservationByItemId(Integer id) {
		List<ItemReservation> itemReservation = itemReservationRepository.findItemReservationByItemId(id);
		return itemReservation;
	}
	
	public List<ItemReservation> findItemReservationByUserId(Integer id) {
		List<ItemReservation> itemReservation = itemReservationRepository.findItemReservationByUserId(id);
		return itemReservation;
	}
	
	public void saveItemReservation(ItemReservation itemReservation) {
		itemReservationRepository.save(itemReservation);
	}
	
	public void deleteItemReservation(Integer id){
		itemReservationRepository.delete(id);
	}

	public List<ItemReservation> findSameItemReservations(Integer item_id, Integer user_id) {
		List<ItemReservation> sameItemReservations = itemReservationRepository.findSameItemReservations(item_id,user_id);
		return sameItemReservations;
	}

	public List<ItemReservation> findItemReservationByStatus(Integer user_id, Integer status) {
		List<ItemReservation> status3ItemReservations = itemReservationRepository.findItemReservationByStatus(user_id,status);
		return status3ItemReservations;
	}
	
	public List<ItemReservation> findItemReservationByStatusONLY(Integer status) {
		List<ItemReservation> status3ItemReservations = itemReservationRepository.findItemReservationByStatusONLY(status);
		return status3ItemReservations;
	}
}