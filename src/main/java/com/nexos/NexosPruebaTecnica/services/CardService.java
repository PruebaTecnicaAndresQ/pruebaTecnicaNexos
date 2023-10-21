package com.nexos.NexosPruebaTecnica.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexos.NexosPruebaTecnica.dtos.CardBalanceDto;
import com.nexos.NexosPruebaTecnica.dtos.CardOperationsDto;
import com.nexos.NexosPruebaTecnica.dtos.CardProductDto;
import com.nexos.NexosPruebaTecnica.entities.CardClient;
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
import com.nexos.NexosPruebaTecnica.utils.Constants;
import com.nexos.NexosPruebaTecnica.utils.Utils;

import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Service
public class CardService {

	@Autowired
	private CardProductRepository cardProductRepository;

	@Autowired
	private CardClientRepository cardClientRepository;

	@Autowired
	private CardTransactionRepository cardTransactionRepository;

	/***
	 * Metodo para crear una tarjeta en el sistema, si el cliente no existe lo crea
	 * si no asocia la tarjeta al cliente que ingresa por parametro
	 * 
	 * @param String productId
	 * @param String clientName
	 * @param String clientSurname
	 * @param String clientIdentification
	 * @return CardProductDto
	 * @throws ApiErrorException
	 */
	public CardProductDto createCard(String productId, String clientName, String clientSurname,
			String clientIdentification) throws ApiErrorException {
		try {
			boolean isValidCardNumbner = false;
			CardsProduct card = null;
			CardClient client = null;
			if (Utils.isNotNullOrEmpty(productId) && Utils.isNotNullOrEmpty(clientName)
					&& Utils.isNotNullOrEmpty(clientSurname) && Utils.isNotNullOrEmpty(clientIdentification)) {

				if (!Utils.isNumeric(productId)) {
					throw new ApiErrorException(Constants.INVALID_PRODUCT_ID);
				}
				if (!Utils.isNumeric(clientIdentification)) {
					throw new ApiErrorException(Constants.INVALID_CLIENT_ID);
				}
				
				if(productId.length()!=6) 
				{
					throw new ApiErrorException(Constants.INVALID_PRODUCT_LENGTH);
				}
				String cardNumber = "";
				// Se realiza iteracion para garantizar que el numero de tarjeta generado no
				// exista en BD
				while (!isValidCardNumbner) {
					cardNumber = productId + Utils.generateRandomNumber(10).toString();
					card = cardProductRepository.findByProductNumber(cardNumber);
					if (card == null) {
						isValidCardNumbner = true;
						card = null;
					}
				}

				client = cardClientRepository.findByClientIdentification(clientIdentification);
				if (client == null) {
					client = CardClient.builder().clientIdentification(clientIdentification).clientName(clientName)
							.clientSurName(clientSurname).build();
					cardClientRepository.saveAndFlush(client);
				}
				card = CardsProduct.builder().CardClient(client).CurrencyType(CurrencyType.US).productBin(productId)
						.productNumber(cardNumber).productStatus(CardStatus.INACTIVE)
						.productExpiration(Utils.addYears(3)).build();
				cardProductRepository.saveAndFlush(card);

				return CardProductDto.builder().cardProductAmount(BigDecimal.ZERO)
						.cardProductExpiration(Utils.DateToStingFormat(card.getProductExpiration()))
						.cardProductNumber(card.getProductNumber()).cardProductStatus(card.getProductStatus().name())
						.build();

			} else {
				throw new ApiErrorException(Constants.INVALID_PARAM_DATA);
			}

		} catch (ApiErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

	/***
	 * Metodo para realizar la activacion de una tarjeta, esta solo puede estar en
	 * estado inactiva para poder realizar su activacion
	 * 
	 * @param String cardNumber
	 * @return CardProductDto
	 * @throws ApiErrorException
	 * @throws ApiNotFountException
	 */
	public CardProductDto changeStatusCard(CardOperationsDto request, CardStatus newStatus)
			throws ApiErrorException, ApiNotFountException {

		try {
			if (request == null || !Utils.isNotNullOrEmpty(request.getCardId())) {
				throw new ApiErrorException(Constants.INVALID_PARAM_DATA);
			}
			if (!Utils.isNumeric(request.getCardId())) {
				throw new ApiErrorException(Constants.INVALID_PRODUCT_NUMNBER);
			}

			CardsProduct card = cardProductRepository.findByProductNumber(request.getCardId());
			if (card == null) {
				throw new ApiNotFountException(Constants.NOT_FOUND_PRODUCT_NUMNBER);
			} else {
				if (CardStatus.ACTIVE.equals(newStatus) && !CardStatus.INACTIVE.equals(card.getProductStatus())) {
					throw new ApiErrorException(Constants.NOT_INACTIVE_PRODUCT_NUMNBER);
				} else {
					card.setProductStatus(newStatus);
					cardProductRepository.saveAndFlush(card);

					return CardProductDto.builder().cardProductAmount(BigDecimal.ZERO)
							.cardProductExpiration(Utils.DateToStingFormat(card.getProductExpiration()))
							.cardProductNumber(card.getProductNumber())
							.cardProductStatus(card.getProductStatus().name()).build();

				}
			}

		} catch (ApiNotFountException e) {
			throw e;
		} catch (ApiErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

	/**
	 * Metodo para realizar la recarga de la tarjeta
	 * 
	 * @param CardOperationsDto request
	 * @return CardProductDto
	 * @throws ApiErrorException
	 * @throws ApiNotFountException
	 */
	public CardBalanceDto rechargeBalance(CardOperationsDto request) throws ApiErrorException, ApiNotFountException {

		try {
			if (request == null || !Utils.isNotNullOrEmpty(request.getCardId())
					|| !Utils.isNotNullOrEmpty(request.getCurrencyType())) {
				throw new ApiErrorException(Constants.INVALID_PARAM_DATA);
			}
			if (!Utils.isNumeric(request.getCardId())) {
				throw new ApiErrorException(Constants.INVALID_PRODUCT_NUMNBER);
			}
			if (null == request.getBalance() || request.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
				throw new ApiErrorException(Constants.INVALID_BALANCE);
			}
			if (!CurrencyType.US.name().equals(request.getCurrencyType())) {
				throw new ApiErrorException(Constants.INVALID_CURRENCY);
			}
			CardsProduct card = cardProductRepository.findByProductNumber(request.getCardId());
			if (card == null) {
				throw new ApiNotFountException(Constants.NOT_FOUND_PRODUCT_NUMNBER);
			} else {
				if (!CardStatus.ACTIVE.equals(card.getProductStatus())) {
					throw new ApiErrorException(Constants.NOT_ACTIVE_PRODUCT_NUMNBER);
				}

				CardTransaction transaction = CardTransaction.builder().CardsProduct(card)
						.CurrencyType(CurrencyType.valueOf(request.getCurrencyType()))
						.transactionAmount(request.getBalance()).transactionDate(new Date())
						.transactionType(TransactionType.RECHARGE).build();
				cardTransactionRepository.saveAndFlush(transaction);
				
				return CardBalanceDto.builder().cardProductAmount(consultBalance(card.getProductNumber()))
						.cardProductNumber(card.getProductNumber()).build();
			}
		} catch (ApiNotFountException e) {
			throw e;
		} catch (ApiErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

	/**
	 * Metodo para consultar el saldo de una tarjeta
	 * 
	 * @param String cardId
	 * @return
	 * @throws ApiErrorException
	 * @throws ApiNotFountException
	 */
	public CardBalanceDto getProductBalance(String cardId) throws ApiErrorException, ApiNotFountException {

		try {
			if (!Utils.isNotNullOrEmpty(cardId)) {
				throw new ApiErrorException(Constants.INVALID_PARAM_DATA);
			}
			if (!Utils.isNumeric(cardId)) {
				throw new ApiErrorException(Constants.INVALID_PRODUCT_NUMNBER);
			}

			CardsProduct card = cardProductRepository.findByProductNumber(cardId);
			if (card == null) {
				throw new ApiNotFountException(Constants.NOT_FOUND_PRODUCT_NUMNBER);
			}

			return CardBalanceDto.builder().cardProductAmount(consultBalance(card.getProductNumber()))
					.cardProductNumber(card.getProductNumber()).build();
		} catch (ApiNotFountException e) {
			throw e;
		} catch (ApiErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

	/**
	 * Metodo para consultar el saldo de una tarjeta
	 * 
	 * @param card
	 * @return
	 * @throws ApiErrorException
	 */
	public BigDecimal consultBalance(String cardNumber) throws ApiErrorException {
		try {

			CardsProduct card = cardProductRepository.findByProductNumber(cardNumber);
			List<CardTransaction> transactions = cardTransactionRepository.findByCardsProduct(card.getProductId());
			if (transactions != null && !transactions.isEmpty()) {
				BigDecimal recharges = transactions.stream()
						.filter(tx -> TransactionType.RECHARGE.equals(tx.getTransactionType()))
						.map(z -> z.getTransactionAmount()).reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

				BigDecimal shopping = transactions.stream()
						.filter(tx -> TransactionType.VALID_BUY.equals(tx.getTransactionType()))
						.map(z -> z.getTransactionAmount()).reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

				return recharges.subtract(shopping);
			} else {
				return BigDecimal.ZERO;
			}

		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

}
