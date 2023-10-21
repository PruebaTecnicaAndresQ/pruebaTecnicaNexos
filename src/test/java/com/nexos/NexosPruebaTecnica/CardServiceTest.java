package com.nexos.NexosPruebaTecnica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nexos.NexosPruebaTecnica.dtos.CardBalanceDto;
import com.nexos.NexosPruebaTecnica.dtos.CardOperationsDto;
import com.nexos.NexosPruebaTecnica.dtos.CardProductDto;
import com.nexos.NexosPruebaTecnica.entities.CardsProduct;
import com.nexos.NexosPruebaTecnica.enums.CardStatus;
import com.nexos.NexosPruebaTecnica.enums.CurrencyType;
import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;
import com.nexos.NexosPruebaTecnica.exceptions.ApiNotFountException;
import com.nexos.NexosPruebaTecnica.repositories.CardClientRepository;
import com.nexos.NexosPruebaTecnica.repositories.CardProductRepository;
import com.nexos.NexosPruebaTecnica.repositories.CardTransactionRepository;
import com.nexos.NexosPruebaTecnica.services.CardService;
import com.nexos.NexosPruebaTecnica.utils.Constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

@SpringBootTest
public class CardServiceTest {

	@Autowired
	private CardProductRepository cardProductRepository;

	@Autowired
	private CardClientRepository cardClientRepository;

	@Autowired
	private CardTransactionRepository cardTransactionRepository;

	@Autowired
	private CardService CardService;

	@BeforeEach
	public void setup() {
		cardTransactionRepository.deleteAll();
		cardProductRepository.deleteAll();
		cardClientRepository.deleteAll();
	}

	@Test()
	void createCardShouldByNullData() {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.createCard(null, null, null, null);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_DATA)).isTrue();
	}

	@Test()
	void createCardShouldByNotNumberProductId() {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.createCard("a", "Carlos", "quinones", "123");
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PRODUCT_ID)).isTrue();
	}

	@Test()
	void createCardShouldByNotNumberIdentification() {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.createCard("123", "Carlos", "quinones", "a");
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_CLIENT_ID)).isTrue();
	}

	@Test()
	void createCardShouldByCreateCardExitsClient() throws ApiErrorException {
		cardClientRepository.saveAndFlush(Mocks.MOCK_CLIENT);
		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		CardsProduct savedCard = cardProductRepository.findByProductNumber(testCard.getCardProductNumber());
		assertThat(savedCard.getProductId() != null).isTrue();
	}

	@Test()
	void createCardShouldByCreateCardNotExitsClient() throws ApiErrorException {
		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		CardsProduct savedCard = cardProductRepository.findByProductNumber(testCard.getCardProductNumber());
		assertThat(savedCard.getProductId() != null).isTrue();
	}

	@Test()
	void createCardShouldByChangeActiveStatusCard() throws ApiErrorException, ApiNotFountException {
		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		testCard = CardService.changeStatusCard(
				CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
		assertThat(testCard.getCardProductStatus().equals(CardStatus.ACTIVE.name())).isTrue();
	}

	@Test()
	void createCardShouldByChangeStatusActiveNotExistCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiNotFountException.class, () -> {
			CardService.changeStatusCard(CardOperationsDto.builder().cardId("456").build(), CardStatus.ACTIVE);
		});
		assertThat(ApiNotFountException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_FOUND_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void createCardShouldByChangeStatusActiveExistCardNotInactive() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			CardsProduct savedCard = cardProductRepository.findByProductNumber(testCard.getCardProductNumber());
			savedCard.setProductStatus(CardStatus.BLOCKED);
			cardProductRepository.saveAndFlush(savedCard);
			CardService.changeStatusCard(CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(),
					CardStatus.ACTIVE);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_INACTIVE_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void createCardShouldByChangeStatusBlokedNotExistCard() throws ApiErrorException, ApiNotFountException {
		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		testCard = CardService.changeStatusCard(
				CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.BLOCKED);
		assertThat(testCard.getCardProductStatus().equals(CardStatus.BLOCKED.name())).isTrue();
	}

	@Test()
	void createCardShouldByChangeStatusActiveNotData() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.changeStatusCard(null, CardStatus.ACTIVE);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_DATA)).isTrue();
	}

	@Test()
	void createCardShouldByChangeStatusActiveNotNumericCardid() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.changeStatusCard(CardOperationsDto.builder().cardId("a").build(), CardStatus.ACTIVE);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void createCardShouldByRechargeCard() throws ApiErrorException, ApiNotFountException {
		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		CardService.changeStatusCard(CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(),
				CardStatus.ACTIVE);
		CardBalanceDto cardBalance = CardService
				.rechargeBalance(CardOperationsDto.builder().cardId(testCard.getCardProductNumber())
						.currencyType(CurrencyType.US.name()).balance(new BigDecimal(1000)).build());
		assertThat(new BigDecimal(1000).setScale(2).equals(cardBalance.getCardProductAmount())).isTrue();
	}

	@Test()
	void createCardShouldByRechargeCardNotData() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.rechargeBalance(null);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_DATA)).isTrue();
	}

	@Test()
	void createCardShouldByRechargeCardNotNumericCardid() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.rechargeBalance(CardOperationsDto.builder().cardId("a").currencyType("US").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void createCardShouldByRechargeCardNotBalance() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.rechargeBalance(
					CardOperationsDto.builder().cardId("123").balance(null).currencyType("US").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_BALANCE)).isTrue();
	}

	@Test()
	void createCardShouldByRechargeCardWorngCurreny() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.rechargeBalance(
					CardOperationsDto.builder().currencyType("COP").cardId("123").balance(BigDecimal.ONE).build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_CURRENCY)).isTrue();
	}

	@Test()
	void createCardShouldByRechargeCardNotFoundCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiNotFountException.class, () -> {
			CardService.rechargeBalance(
					CardOperationsDto.builder().currencyType("US").cardId("123").balance(BigDecimal.ONE).build());
		});
		assertThat(ApiNotFountException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_FOUND_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void createCardShouldByRechargeCardNotActiveCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			CardService.rechargeBalance(CardOperationsDto.builder().currencyType("US")
					.cardId(testCard.getCardProductNumber()).balance(BigDecimal.ONE).build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_ACTIVE_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void createCardShouldByConsultBalanceCard() throws ApiErrorException, ApiNotFountException {
		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		CardService.changeStatusCard(CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(),
				CardStatus.ACTIVE);
		CardBalanceDto cardBalance = CardService.getProductBalance(testCard.getCardProductNumber());
		assertThat(BigDecimal.ZERO.equals(cardBalance.getCardProductAmount())).isTrue();
	}

	@Test()
	void createCardShouldByConsultBalanceCardNotData() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.getProductBalance(null);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_DATA)).isTrue();
	}

	@Test()
	void createCardShouldByConsultBalanceCardNumberid() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardService.getProductBalance("a");
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void createCardShouldByConsultBalanceNotFoudCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiNotFountException.class, () -> {
			CardService.getProductBalance("456");
		});
		assertThat(ApiNotFountException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_FOUND_PRODUCT_NUMNBER)).isTrue();
	}
}
