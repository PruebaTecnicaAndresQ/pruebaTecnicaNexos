package com.nexos.NexosPruebaTecnica.enums;

public enum TransactionType {
	
     VALID_BUY("Compra exitosa"),
     ANNULLED_BUY("Compra anulada"),
     RECHARGE("Recarga");

	private String desc;

	private TransactionType(String description) {
		this.desc = description;
	}

	public String getDescription() {
		return this.desc;
	}
}
