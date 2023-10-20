package com.nexos.NexosPruebaTecnica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexos.NexosPruebaTecnica.dtos.CardBalanceDto;
import com.nexos.NexosPruebaTecnica.dtos.CardOperationsDto;
import com.nexos.NexosPruebaTecnica.dtos.CardProductDto;
import com.nexos.NexosPruebaTecnica.enums.CardStatus;
import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;
import com.nexos.NexosPruebaTecnica.exceptions.ApiNotFountException;
import com.nexos.NexosPruebaTecnica.services.CardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/card")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Servicio de gestion de tarjetas", description = "Grupo de servicios para la administración de tarjetas")
public class CardController {

	@Autowired
	private CardService cardService;

	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se crea correctamente la tarjeta,Devuelve los datos de la tarjeta creada"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para la generación de tarjetas dentro del sistema")
	@GetMapping(path = "/{productId}/number", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CardProductDto> createCardProduct(@PathVariable("productId") String productId,
			@RequestParam("clientName") String clientName, @RequestParam("clientSurName") String clientSurName,
			@RequestParam("clientIdentification") String clientIdentification) throws ApiErrorException {
		return ResponseEntity.ok(cardService.createCard(productId, clientName, clientSurName, clientIdentification));
	}

	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se activa correctamente la tarjeta,Devuelve los datos de la tarjeta activada"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para la activación de tarjetas dentro del sistema,Campo cardId es obligatorio ")
	@PostMapping(path = "/enroll", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CardProductDto> activateCardProduct(@RequestBody CardOperationsDto request)
			throws ApiErrorException, ApiNotFountException {
		return ResponseEntity.ok(cardService.changeStatusCard(request, CardStatus.ACTIVE));
	}

	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se bloquea correctamente la tarjeta,Devuelve los datos de la tarjeta bloqueada"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para bloqueo de tarjetas dentro del sistema, NOTA: "
							+ "no se realiza con metodo DELETE, ya que en realidad no se elimina ningun recurso, unicamente cambia de "
							+ "estado, Esto para preservar la integridad y la trazabilidad de la base de datos")
	@PostMapping(path = "/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CardProductDto> blokCardProduct(@PathVariable("cardId") String cardId)
			throws ApiErrorException, ApiNotFountException {
		return ResponseEntity.ok(
				cardService.changeStatusCard(CardOperationsDto.builder().cardId(cardId).build(), CardStatus.BLOCKED));
	}

	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se recarga correctamente la tarjeta,Devuelve el nuevo saldo"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para realizar recargas de tarjetas dentro del sistema,campos cardId y balance son obligatorios")
	@PostMapping(path = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CardBalanceDto> rechargeProduct(@RequestBody CardOperationsDto request)
			throws ApiErrorException, ApiNotFountException {
		return ResponseEntity.ok(cardService.rechargeBalance(request));
	}

	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se consulta correctamente la tarjeta,Devuelve el  saldo"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para realizar consulta de tarjetas dentro del sistema")

	@GetMapping(path = "/balance/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CardBalanceDto> consultAmountCard(@PathVariable("cardId") String productId)
			throws ApiErrorException, ApiNotFountException {
		return ResponseEntity.ok(cardService.getProductBalance(productId));
	}
}
