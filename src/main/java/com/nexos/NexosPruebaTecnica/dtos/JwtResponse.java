package com.nexos.NexosPruebaTecnica.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
	@JsonProperty(value = "token_type")
	private String tokentype;

	@JsonProperty(value = "access_token")
	private String accesstoken;

	@JsonProperty(value = "expires_in")
	private int expiresIn;

	@JsonProperty(value = "client_id")
	private String client_id;

	@JsonProperty(value = "issuedAt")
	private String issuedAt;
}
