package com.nexos.NexosPruebaTecnica.enums;

public enum CardStatus {

	ACTIVE("ACTIVA"), INACTIVE("INACTIVA"), BLOCKED("BLOQUEADA");

	private String desc;

	private CardStatus(String description) {
		this.desc = description;
	}

	public String getDescription() {
		return this.desc;
	}
}
