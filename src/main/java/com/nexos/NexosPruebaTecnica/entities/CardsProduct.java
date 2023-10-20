package com.nexos.NexosPruebaTecnica.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


import com.nexos.NexosPruebaTecnica.enums.CardStatus;
import com.nexos.NexosPruebaTecnica.enums.CurrencyType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Table(name = "CARD_PRODUCT")
@Data
@Builder
public class CardsProduct implements Serializable, Cloneable {

	private static final long serialVersionUID = -7525573878054468724L;

	@Tolerate
	CardsProduct() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	private String productBin;
	private String productNumber;
	@Temporal(TemporalType.DATE)
	private Date productExpiration;
	@Enumerated(EnumType.STRING)
	private CardStatus productStatus;
	@Enumerated(EnumType.STRING)
	private CurrencyType CurrencyType;
	@ManyToOne
	@JoinColumn(name = "CardClient", referencedColumnName = "clientId", nullable = false)
	private CardClient CardClient;
	
	@OneToMany(mappedBy = "CardsProduct" ,cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<CardTransaction> transactions;
}
