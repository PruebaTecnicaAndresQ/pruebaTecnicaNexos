package com.nexos.NexosPruebaTecnica.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class CardOperationsDto implements Serializable {

	private static final long serialVersionUID = 2773817170075531777L;

	@Tolerate
	CardOperationsDto() {
	}

	private String cardId;
	private BigDecimal balance;
	private Long price;
	private Long ransactionId;
	private String currencyType;
}
