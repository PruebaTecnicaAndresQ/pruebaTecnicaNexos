package com.nexos.NexosPruebaTecnica;

import com.nexos.NexosPruebaTecnica.dtos.TransactionOperationsDto;
import com.nexos.NexosPruebaTecnica.entities.CardClient;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public final class Mocks {

	public static CardClient MOCK_CLIENT = CardClient.builder().clientId(1l).clientIdentification("123").clientName("prueba").clientSurName("nexos").build();

	public static MultiValueMap<String, String> GET_PARAM () 
	{
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("client_id", "nexos");
	    params.add("client_secret", "nexos");
	    return params;
	}
	public static MultiValueMap<String, String> GET_WORNG_PARAM () 
	{
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("client_id", "usuario");
	    params.add("client_secret", "clave");
	    return params;
	}
	public static MultiValueMap<String, String> GET_WORNG_PARAM_IMPUT () 
	{
	    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add("client", "usuario");
	    params.add("secret", "clave");
	    return params;
	}
}
