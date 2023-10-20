package com.nexos.NexosPruebaTecnica.entities;

import java.io.Serializable;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Entity
@Table(name = "CARD_CLIENT")
@Data
@Builder
public class CardClient implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3772904596841120363L;

	@Tolerate
	CardClient() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long clientId;
	private String clientName;
	private String clientSurName;
	private String clientIdentification;
}
