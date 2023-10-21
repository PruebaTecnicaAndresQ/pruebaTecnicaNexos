package com.nexos.NexosPruebaTecnica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nexos.NexosPruebaTecnica.dtos.CardBalanceDto;
import com.nexos.NexosPruebaTecnica.dtos.CardOperationsDto;
import com.nexos.NexosPruebaTecnica.dtos.CardProductDto;
import com.nexos.NexosPruebaTecnica.dtos.TransactionOperationsDto;
import com.nexos.NexosPruebaTecnica.entities.CardTransaction;
import com.nexos.NexosPruebaTecnica.entities.CardsProduct;
import com.nexos.NexosPruebaTecnica.enums.CardStatus;
import com.nexos.NexosPruebaTecnica.enums.CurrencyType;
import com.nexos.NexosPruebaTecnica.enums.TransactionType;
import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;
import com.nexos.NexosPruebaTecnica.exceptions.ApiNotFountException;
import com.nexos.NexosPruebaTecnica.repositories.CardClientRepository;
import com.nexos.NexosPruebaTecnica.repositories.CardProductRepository;
import com.nexos.NexosPruebaTecnica.repositories.CardTransactionRepository;
import com.nexos.NexosPruebaTecnica.services.CardService;
import com.nexos.NexosPruebaTecnica.services.TransactionService;
import com.nexos.NexosPruebaTecnica.utils.Constants;

@SpringBootTest
public class TransactionServiceTest {
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

	@BeforeEach
	public void setup() {
		cardTransactionRepository.deleteAll();
		cardProductRepository.deleteAll();
		cardClientRepository.deleteAll();
	}

	@Test
	void registerpurchaseShouldByok() throws ApiErrorException, ApiNotFountException {
		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		testCard = CardService.changeStatusCard(
				CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
		CardBalanceDto cardBalance = CardService
				.rechargeBalance(CardOperationsDto.builder().cardId(testCard.getCardProductNumber())
						.currencyType(CurrencyType.US.name()).balance(new BigDecimal(1000)).build());
		TransactionOperationsDto tx = transactionService.registerpurchase(TransactionOperationsDto.builder()
				.cardId(testCard.getCardProductNumber()).currencyType("US").price(BigDecimal.TEN).build());
		assertThat(tx != null && tx.getTransactionId() != null).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokNotParam() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			transactionService.registerpurchase(null);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_DATA)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokNotNumericCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			transactionService.registerpurchase(TransactionOperationsDto.builder().cardId("a").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokNotValidPrice() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			transactionService.registerpurchase(TransactionOperationsDto.builder().cardId("123456").price(null).build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PRICE)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokNotValidCurrency() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			transactionService.registerpurchase(
					TransactionOperationsDto.builder().cardId("123456").price(BigDecimal.TEN).currencyType("COP").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_CURRENCY)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokNotFountcard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiNotFountException.class, () -> {
			transactionService.registerpurchase(
					TransactionOperationsDto.builder().cardId("123456").price(BigDecimal.TEN).currencyType("US").build());
		});
		assertThat(ApiNotFountException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_FOUND_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokInactiveCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			transactionService.registerpurchase(TransactionOperationsDto.builder()
					.cardId(testCard.getCardProductNumber()).price(BigDecimal.TEN).currencyType("US").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INACTIVE_CARD)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokBlokedCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			testCard = CardService.changeStatusCard(
					CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.BLOCKED);
			transactionService.registerpurchase(TransactionOperationsDto.builder()
					.cardId(testCard.getCardProductNumber()).price(BigDecimal.TEN).currencyType("US").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_ACTIVE_PRODUCT_NUMNBER)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByokExpiredCard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			testCard = CardService.changeStatusCard(
					CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -5);
			CardsProduct savedCard = cardProductRepository.findByProductNumber(testCard.getCardProductNumber());
			savedCard.setProductExpiration(cal.getTime());
			cardProductRepository.saveAndFlush(savedCard);
			transactionService.registerpurchase(TransactionOperationsDto.builder()
					.cardId(testCard.getCardProductNumber()).price(BigDecimal.TEN).currencyType("US").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.EXPIRED_CARD)).isTrue();
	}

	@Test()
	void registerpurchaseShouldByoknotBalanceAviableard() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			testCard = CardService.changeStatusCard(
					CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
			transactionService.registerpurchase(TransactionOperationsDto.builder()
					.cardId(testCard.getCardProductNumber()).price(BigDecimal.TEN).currencyType("US").build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_BALANCE_AVIABLE)).isTrue();
	}

	@Test
	void AnulledTransactionShouldByOK() throws ApiErrorException, ApiNotFountException {

		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		testCard = CardService.changeStatusCard(
				CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
		CardBalanceDto cardBalance = CardService
				.rechargeBalance(CardOperationsDto.builder().cardId(testCard.getCardProductNumber())
						.currencyType(CurrencyType.US.name()).balance(new BigDecimal(1000)).build());
		TransactionOperationsDto tx = transactionService.registerpurchase(TransactionOperationsDto.builder()
				.cardId(testCard.getCardProductNumber()).currencyType("US").price(BigDecimal.TEN).build());
		tx = transactionService.anulationTrasaction(tx);
		assertThat(tx != null && tx.getTransactionStatus().equals(TransactionType.ANNULLED_BUY.getDescription()))
				.isTrue();
	}

	@Test
	void AnulledTransactionShouldByNotParam() throws ApiErrorException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			transactionService.anulationTrasaction(null);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_DATA)).isTrue();

	}

	@Test
	void AnulledTransactionShouldByNotFounTx() throws ApiNotFountException {
		Exception exception = assertThrows(ApiNotFountException.class, () -> {
			 transactionService.anulationTrasaction(TransactionOperationsDto.builder()
					.cardId("123456").currencyType("US").price(BigDecimal.TEN).transactionId(123l).build());
		});
		assertThat(ApiNotFountException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_FOUND_TRANSACTION)).isTrue();

	}
	
	@Test
	void AnulledTransactionShouldByNoyAnulledByTime() throws ApiErrorException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			testCard = CardService.changeStatusCard(
					CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
			CardService
					.rechargeBalance(CardOperationsDto.builder().cardId(testCard.getCardProductNumber())
							.currencyType(CurrencyType.US.name()).balance(new BigDecimal(1000)).build());
			TransactionOperationsDto tx = transactionService.registerpurchase(TransactionOperationsDto.builder()
					.cardId(testCard.getCardProductNumber()).currencyType("US").price(BigDecimal.TEN).build());
             
			Calendar cl = Calendar.getInstance();
			cl.add(Calendar.HOUR, -36);
			CardTransaction txSaved = cardTransactionRepository.findById(tx.getTransactionId()).get();
			txSaved.setTransactionDate(cl.getTime());
			cardTransactionRepository.saveAndFlush(txSaved);
			
			 transactionService.anulationTrasaction(TransactionOperationsDto.builder()
					.cardId(tx.getCardId()).transactionId(tx.getTransactionId()).build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_CANCEL_TRANSACTION_BY_DATE)).isTrue();

	}

	@Test
	void AnulledTransactionShouldByNotValidTx() throws ApiErrorException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
					Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
			testCard = CardService.changeStatusCard(
					CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
			CardBalanceDto cardBalance = CardService
					.rechargeBalance(CardOperationsDto.builder().cardId(testCard.getCardProductNumber())
							.currencyType(CurrencyType.US.name()).balance(new BigDecimal(1000)).build());
			TransactionOperationsDto tx = transactionService.registerpurchase(TransactionOperationsDto.builder()
					.cardId(testCard.getCardProductNumber()).currencyType("US").price(BigDecimal.TEN).build());

			tx = transactionService.anulationTrasaction(tx);
			// Se anula dos veces para forzar a estado tx no valida
			transactionService.anulationTrasaction(
					TransactionOperationsDto.builder().transactionId(tx.getTransactionId()).build());
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_VALID_TRANSACTION)).isTrue();

	}

	@Test
	void ConsultTransactionShouldBy() throws ApiErrorException, ApiNotFountException {

		CardProductDto testCard = CardService.createCard("123456", Mocks.MOCK_CLIENT.getClientName(),
				Mocks.MOCK_CLIENT.getClientSurName(), Mocks.MOCK_CLIENT.getClientIdentification());
		testCard = CardService.changeStatusCard(
				CardOperationsDto.builder().cardId(testCard.getCardProductNumber()).build(), CardStatus.ACTIVE);
		CardBalanceDto cardBalance = CardService
				.rechargeBalance(CardOperationsDto.builder().cardId(testCard.getCardProductNumber())
						.currencyType(CurrencyType.US.name()).balance(new BigDecimal(1000)).build());
		TransactionOperationsDto tx = transactionService.registerpurchase(TransactionOperationsDto.builder()
				.cardId(testCard.getCardProductNumber()).currencyType("US").price(BigDecimal.TEN).build());
		TransactionOperationsDto ConsultedTx = transactionService.getTransaction(tx.getTransactionId());
		assertThat(ConsultedTx != null && ConsultedTx.getTransactionId().equals(tx.getTransactionId())).isTrue();
	}

	@Test
	void ConsultTransactionShouldByNotFound() throws ApiErrorException, ApiNotFountException {
		Exception exception = assertThrows(ApiNotFountException.class, () -> {
			transactionService.getTransaction(1L);
		});
		assertThat(ApiNotFountException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.NOT_FOUND_TRANSACTION)).isTrue();

	}

	@Test
	void ConsultTransactionShouldByInvalidParam() throws ApiErrorException {
		Exception exception = assertThrows(ApiErrorException.class, () -> {
			transactionService.getTransaction(0L);
		});
		assertThat(ApiErrorException.class.equals(exception.getClass())
				&& exception.getMessage().equals(Constants.INVALID_PARAM_DATA)).isTrue();

	}
}
