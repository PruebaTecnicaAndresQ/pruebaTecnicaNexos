package com.nexos.NexosPruebaTecnica.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardProductDto implements Serializable {

	private static final long serialVersionUID = -3681717397019924083L;

	@JsonProperty(value = "cardId")
	private String cardProductNumber;

	@JsonProperty(value = "cardExpiration")
	private String cardProductExpiration;

	@JsonProperty(value = "cardBalance")
	private BigDecimal cardProductAmount;

	@JsonProperty(value = "cardStatus")
	private String cardProductStatus;

}
