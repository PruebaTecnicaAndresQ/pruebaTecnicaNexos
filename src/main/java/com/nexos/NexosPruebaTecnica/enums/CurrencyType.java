package com.nexos.NexosPruebaTecnica.enums;

public enum CurrencyType {

	US("Dollar");

	private String desc;

	private CurrencyType(String description) {
		this.desc = description;
	}

	public String getDescription() {
		return this.desc;
	}
}
