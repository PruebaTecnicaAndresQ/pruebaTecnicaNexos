package com.nexos.NexosPruebaTecnica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import com.nexos.NexosPruebaTecnica.dtos.CardBalanceDto;
import com.nexos.NexosPruebaTecnica.dtos.CardOperationsDto;
import com.nexos.NexosPruebaTecnica.dtos.CardProductDto;
import com.nexos.NexosPruebaTecnica.dtos.JwtResponse;
import com.nexos.NexosPruebaTecnica.dtos.TransactionOperationsDto;
import com.nexos.NexosPruebaTecnica.enums.CardStatus;
import com.nexos.NexosPruebaTecnica.enums.CurrencyType;
import com.nexos.NexosPruebaTecnica.repositories.CardClientRepository;
import com.nexos.NexosPruebaTecnica.repositories.CardProductRepository;
import com.nexos.NexosPruebaTecnica.repositories.CardTransactionRepository;
import com.nexos.NexosPruebaTecnica.services.CardService;
import com.nexos.NexosPruebaTecnica.services.SecurityService;
import com.nexos.NexosPruebaTecnica.services.TransactionService;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllesTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private CardProductRepository cardProductRepository;

	@Autowired
	private CardClientRepository cardClientRepository;

	@Autowired
	private CardTransactionRepository cardTransactionRepository;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private CardService CardService;

	private CardProductDto testCard;

	private JwtResponse token;
	
	private TransactionOperationsDto tx;

	@BeforeAll
	public void setup() throws Exception {
		cardTransactionRepository.deleteAll();
		cardProductRepository.deleteAll();
		cardClientRepository.deleteAll();

		testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		token = securityService.getToken(Mocks.GET_PARAM(), "client_credentials");

	}

	@Test
	@Order(1)
	void createCardShouldByOK() throws Exception {
		MvcResult result = mvc
				.perform(get("/card/123456/number?clientName=c&clientSurName=a&clientIdentification=1")
						.contentType(MediaType.APPLICATION_JSON).header("authorization", token.getAccesstoken()))
				.andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	@Order(2)
	void activateCardProductShouldByOK() throws Exception {

		MvcResult result = mvc.perform(post("/card/enroll").contentType(MediaType.APPLICATION_JSON)
				.content("{\"cardId\":" + "\"" + testCard.getCardProductNumber() + "\"" + "}")
				.header("authorization", token.getAccesstoken())).andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	@Order(3)
	void rechargeProductShouldByOK() throws Exception {

		MvcResult result = mvc.perform(post("/card/balance")
				.content("{\"cardId\":" + "\"" + testCard.getCardProductNumber() + "\","
						+ "\"balance\":1000,\"currencyType\":\"US\"}")
				.contentType(MediaType.APPLICATION_JSON).header("authorization", token.getAccesstoken()))
				.andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	@Order(4)
	void consultAmountCardShouldByOK() throws Exception {

		MvcResult result = mvc.perform(get("/card/balance/" + testCard.getCardProductNumber())
				.contentType(MediaType.APPLICATION_JSON).header("authorization", token.getAccesstoken()))
				.andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	@Order(5)
	void purchaseShouldByOK() throws Exception {

		MvcResult result = mvc.perform(post("/transaction/purchase").content("{\"cardId\":\""+testCard.getCardProductNumber()+"\",\"price\":50,\"currencyType\":\"US\"}")
				.contentType(MediaType.APPLICATION_JSON).header("authorization", token.getAccesstoken()))
				.andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	@Order(6)
	void getTransactionShouldByOK() throws Exception {
		tx = transactionService.registerpurchase(TransactionOperationsDto.builder()
				.cardId(testCard.getCardProductNumber()).currencyType("US").price(BigDecimal.TEN).build());
		MvcResult result = mvc.perform(get("/transaction/" + tx.getTransactionId())
				.contentType(MediaType.APPLICATION_JSON).header("authorization", token.getAccesstoken()))
				.andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	@Order(7)
	void anulationTrasactionShouldByOK() throws Exception {

		MvcResult result = mvc.perform(post("/transaction/anulation").content("{\"cardId\":\""+testCard.getCardProductNumber()+"\",\"transactionId\":" +tx.getTransactionId() +"}")
				.contentType(MediaType.APPLICATION_JSON).header("authorization", token.getAccesstoken()))
				.andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	@Order(8)
	void blokCardProductShouldByOK() throws Exception {

		MvcResult result = mvc.perform(post("/card/" + testCard.getCardProductNumber())
				.contentType(MediaType.APPLICATION_JSON).header("authorization", token.getAccesstoken()))
				.andExpect(status().isOk()).andReturn();
		assertThat(result.getResponse().getStatus()).isEqualTo(200);
	}
}
