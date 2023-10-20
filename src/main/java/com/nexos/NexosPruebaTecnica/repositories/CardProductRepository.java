package com.nexos.NexosPruebaTecnica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nexos.NexosPruebaTecnica.entities.CardsProduct;

@Repository
public interface CardProductRepository extends JpaRepository<CardsProduct, Long>, CrudRepository<CardsProduct, Long> {

	public CardsProduct findByProductNumber(String productNumber);
}
