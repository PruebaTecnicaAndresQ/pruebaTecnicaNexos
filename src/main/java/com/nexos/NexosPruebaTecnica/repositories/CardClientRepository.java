package com.nexos.NexosPruebaTecnica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nexos.NexosPruebaTecnica.entities.CardClient;



@Repository
public interface CardClientRepository
extends JpaRepository<CardClient, Long>, CrudRepository<CardClient, Long>{
	
	public CardClient findByClientIdentification(String clientIdentification);

}
