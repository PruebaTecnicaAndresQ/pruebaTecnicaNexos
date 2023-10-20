package com.nexos.NexosPruebaTecnica.repositories;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nexos.NexosPruebaTecnica.entities.CardTransaction;



@Repository
public interface CardTransactionRepository
extends JpaRepository<CardTransaction, Long>, CrudRepository<CardTransaction, Long>{

	@Query(value = "select * from card_transaction where product_id = ?1 ", nativeQuery = true)
	public List<CardTransaction> findByCardsProduct(Long product_id);
}
