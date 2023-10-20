package com.nexos.NexosPruebaTecnica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.nexos.NexosPruebaTecnica.dtos.JwtResponse;
import com.nexos.NexosPruebaTecnica.exceptions.ApiUnauthorizedException;
import com.nexos.NexosPruebaTecnica.services.SecurityService;
import com.nexos.NexosPruebaTecnica.utils.Constants;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityServiceTest {

	


	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private MockMvc mvc;

	
	@BeforeEach
	public void setup() {
	}
	@Test
	void getTokenShouldByOk() throws Exception {
		JwtResponse token = securityService.getToken(Mocks.GET_PARAM(), "client_credentials");
		assertThat(token != null && token.getAccesstoken() != null).isTrue();
	}

	@Test
	void getTokenShouldByUnautorize() throws Exception {
		Exception exception = assertThrows(ApiUnauthorizedException.class, () -> {
			securityService.getToken(Mocks.GET_WORNG_PARAM(), "client_credentials");
		});
		assertThat(ApiUnauthorizedException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.USER_NOT_FOUND)).isTrue();
	}

	@Test
	void getTokenShouldByNotValidGrantType() throws Exception {
		Exception exception = assertThrows(ApiUnauthorizedException.class, () -> {
			securityService.getToken(Mocks.GET_WORNG_PARAM(), "client_crede");
		});
		assertThat(ApiUnauthorizedException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_GRANT_TYPE)).isTrue();
	}

	@Test
	void getTokenShouldByNotValidParam() throws Exception {
		Exception exception = assertThrows(ApiUnauthorizedException.class, () -> {
			securityService.getToken(Mocks.GET_WORNG_PARAM_IMPUT(), "client_credentials");
		});
		assertThat(ApiUnauthorizedException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_VALUES)).isTrue();
	}
	
	@Test
	void getTokenShouldByValidateToken() throws Exception {
		JwtResponse token = securityService.getToken(Mocks.GET_PARAM(), "client_credentials");
		MvcResult result = mvc.perform(get("/card/123/number?clientName=c&clientSurName=a&clientIdentification=1").contentType(MediaType.APPLICATION_JSON).header("authorization",token.getAccesstoken())				
				).andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}
}
