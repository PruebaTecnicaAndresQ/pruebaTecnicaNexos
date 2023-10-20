package com.nexos.NexosPruebaTecnica.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


import com.nexos.NexosPruebaTecnica.enums.CurrencyType;
import com.nexos.NexosPruebaTecnica.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Table(name = "CARD_TRANSACTION")
@Data
@Builder
public class CardTransaction implements Serializable, Cloneable {

	private static final long serialVersionUID = 3925522214409724812L;

	@Tolerate
	CardTransaction() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionDate;
	private BigDecimal transactionAmount;
	@Enumerated(EnumType.STRING)
	private CurrencyType CurrencyType;
	@Enumerated(EnumType.ORDINAL)
	private TransactionType transactionType;		
	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	private CardsProduct CardsProduct;
}
