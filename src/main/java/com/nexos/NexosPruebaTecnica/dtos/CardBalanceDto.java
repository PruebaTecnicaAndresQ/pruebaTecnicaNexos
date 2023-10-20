package com.nexos.NexosPruebaTecnica.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardBalanceDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6750461278698008887L;

	@JsonProperty(value = "cardId")
	private String cardProductNumber;

	@JsonProperty(value = "cardBalance")
	private BigDecimal cardProductAmount;
}
