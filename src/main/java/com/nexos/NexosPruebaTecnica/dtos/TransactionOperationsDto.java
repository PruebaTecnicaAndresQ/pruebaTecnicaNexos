package com.nexos.NexosPruebaTecnica.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class TransactionOperationsDto implements Serializable {

	@Tolerate
	TransactionOperationsDto() {
	}
	private static final long serialVersionUID = -8001728171656349017L;
	private String cardId;
	private BigDecimal price;
	private Long transactionId;
	private String currencyType;
	private String transactionDate;
	private String transactionStatus;
}
