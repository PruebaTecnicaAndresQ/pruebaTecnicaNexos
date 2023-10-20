package com.nexos.NexosPruebaTecnica.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexos.NexosPruebaTecnica.dtos.TransactionOperationsDto;
import com.nexos.NexosPruebaTecnica.entities.CardTransaction;
import com.nexos.NexosPruebaTecnica.entities.CardsProduct;
import com.nexos.NexosPruebaTecnica.enums.CardStatus;
import com.nexos.NexosPruebaTecnica.enums.CurrencyType;
import com.nexos.NexosPruebaTecnica.enums.TransactionType;
import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;
import com.nexos.NexosPruebaTecnica.exceptions.ApiNotFountException;
import com.nexos.NexosPruebaTecnica.repositories.CardProductRepository;
import com.nexos.NexosPruebaTecnica.repositories.CardTransactionRepository;
import com.nexos.NexosPruebaTecnica.utils.Constants;
import com.nexos.NexosPruebaTecnica.utils.Utils;

@Service
public class TransactionService {

	@Autowired
	private CardProductRepository cardProductRepository;

	@Autowired
	private CardTransactionRepository cardTransactionRepository;

	@Autowired
	private CardService cardService;

	/**
	 * Metodo para registrar una compra dentro del sistema
	 * 
	 * @param TransactionOperationsDto request
	 * @return TransactionOperationsDto
	 * @throws ApiErrorException
	 * @throws ApiNotFountException
	 */
	public TransactionOperationsDto registerpurchase(TransactionOperationsDto request)
			throws ApiErrorException, ApiNotFountException {
		try {
			if (request == null || !Utils.isNotNullOrEmpty(request.getCardId())) {
				throw new ApiErrorException(Constants.INVALID_PARAM_DATA);
			}
			if (!Utils.isNumeric(request.getCardId())) {
				throw new ApiErrorException(Constants.INVALID_PRODUCT_NUMNBER);
			}
			if (null == request.getPrice() || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
				throw new ApiErrorException(Constants.INVALID_PRICE);
			}
			if (!CurrencyType.US.name().equals(request.getCurrencyType())) {
				throw new ApiErrorException(Constants.INVALID_CURRENCY);
			}
			CardsProduct card = cardProductRepository.findByProductNumber(request.getCardId());
			if (card == null) {
				throw new ApiNotFountException(Constants.NOT_FOUND_PRODUCT_NUMNBER);
			}
			if (CardStatus.INACTIVE.equals(card.getProductStatus())) {
				throw new ApiErrorException(Constants.INACTIVE_CARD);
			}
			if (CardStatus.BLOCKED.equals(card.getProductStatus())) {
				throw new ApiErrorException(Constants.NOT_ACTIVE_PRODUCT_NUMNBER);
			}

			if (new Date().after(card.getProductExpiration())) {
				throw new ApiErrorException(Constants.EXPIRED_CARD);
			}

			BigDecimal balance = cardService.consultBalance(card.getProductNumber());
			if (balance.subtract(request.getPrice()).compareTo(BigDecimal.ZERO) < 0) {
				throw new ApiErrorException(Constants.INVALID_BALANCE_AVIABLE);
			}

			CardTransaction transaction = CardTransaction.builder().CardsProduct(card)
					.CurrencyType(CurrencyType.valueOf(request.getCurrencyType())).transactionAmount(request.getPrice())
					.transactionDate(new Date()).transactionType(TransactionType.VALID_BUY).build();
			cardTransactionRepository.saveAndFlush(transaction);

			return TransactionOperationsDto.builder().cardId(card.getProductNumber())
					.transactionId(transaction.getTransactionId()).price(request.getPrice())
					.currencyType(request.getCurrencyType()).build();
		} catch (ApiNotFountException e) {
			throw e;
		} catch (ApiErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

	/**
	 * Metodo para consulat una transaccion por id
	 * 
	 * @param transactionId
	 * @return
	 * @throws ApiNotFountException
	 * @throws ApiErrorException
	 */
	public TransactionOperationsDto getTransaction(Long transactionId) throws ApiNotFountException, ApiErrorException {
		try {
			if (transactionId != null && transactionId.compareTo(0l) > 0) {
				CardTransaction transaction = cardTransactionRepository.findById(transactionId).get();

				return TransactionOperationsDto.builder().cardId(transaction.getCardsProduct().getProductNumber())
						.transactionId(transaction.getTransactionId()).price(transaction.getTransactionAmount())
						.currencyType(transaction.getCurrencyType().name())
						.transactionStatus(transaction.getTransactionType().getDescription())
						.transactionDate(Utils.DateToStingFullFormat(transaction.getTransactionDate())).build();

			} else {
				throw new ApiErrorException(Constants.INVALID_PARAM_DATA);
			}

		} catch (NoSuchElementException e) {
			throw new ApiNotFountException(Constants.NOT_FOUND_TRANSACTION);
		} catch (ApiErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

	/**
	 * Metodo para realizar la anulación de una transacción
	 * 
	 * @param TransactionOperationsDto request
	 * @return TransactionOperationsDto
	 * @throws ApiNotFountException
	 * @throws ApiErrorException
	 */
	public TransactionOperationsDto anulationTrasaction(TransactionOperationsDto request)
			throws ApiNotFountException, ApiErrorException {
		try {
			if (request != null && request.getTransactionId() != null && request.getTransactionId().compareTo(0l) > 0) {
				CardTransaction transaction = cardTransactionRepository.findById(request.getTransactionId()).get();
				if(!TransactionType.VALID_BUY.equals(transaction.getTransactionType())) 
				{
					throw new ApiErrorException(Constants.NOT_VALID_TRANSACTION);
				}
				
				Calendar txDate = Calendar.getInstance();
				txDate.setTime(transaction.getTransactionDate());
				txDate.add(Calendar.HOUR, 24);
				if (new Date().after(txDate.getTime())) {
					throw new ApiErrorException(Constants.NOT_CANCEL_TRANSACTION_BY_DATE);
				}

				transaction.setTransactionType(TransactionType.ANNULLED_BUY);
				cardTransactionRepository.saveAndFlush(transaction);

				return TransactionOperationsDto.builder().cardId(transaction.getCardsProduct().getProductNumber())
						.transactionId(transaction.getTransactionId()).price(transaction.getTransactionAmount())
						.currencyType(transaction.getCurrencyType().name())
						.transactionStatus(transaction.getTransactionType().getDescription())
						.transactionDate(Utils.DateToStingFullFormat(transaction.getTransactionDate())).build();

			} else {
				throw new ApiErrorException(Constants.INVALID_PARAM_DATA);
			}

		} catch (NoSuchElementException e) {
			throw new ApiNotFountException(Constants.NOT_FOUND_TRANSACTION);
		} catch (ApiErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ApiErrorException(e.getMessage());
		}
	}

}
