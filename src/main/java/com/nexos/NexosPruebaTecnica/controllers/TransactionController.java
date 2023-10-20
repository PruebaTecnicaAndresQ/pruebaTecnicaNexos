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
import org.springframework.web.bind.annotation.RestController;

import com.nexos.NexosPruebaTecnica.dtos.TransactionOperationsDto;
import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;
import com.nexos.NexosPruebaTecnica.exceptions.ApiNotFountException;
import com.nexos.NexosPruebaTecnica.services.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/transaction")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Servicio de gestion de transacciones", description = "Grupo de servicios para la administración de transacciones")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se realiza la transaccion de compra correctamente,Devuelve los datos de la compra"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para la generación de transacciones de comra dentro del sistema, Solo admite compras en US dolar")
	@PostMapping(path = "/purchase", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransactionOperationsDto> purchase(@RequestBody TransactionOperationsDto request)
			throws ApiErrorException, ApiNotFountException {
		return ResponseEntity.ok(transactionService.registerpurchase(request));
	}
	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se realiza la consulta de la transaccion de compra correctamente,Devuelve los datos de la compra"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para la generación de transacciones de comra dentro del sistema")
	@GetMapping(path = "/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransactionOperationsDto> getTransaction(@PathVariable("transactionId") Long transactionId)
			throws ApiErrorException, ApiNotFountException {
		return ResponseEntity.ok(transactionService.getTransaction(transactionId));
	}
	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando se realiza la anulacion de la transaccion de compra correctamente ,Devuelve los datos de la compra"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para la generación de transacciones de comra dentro del sistema")
	@PostMapping(path = "/anulation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransactionOperationsDto> anulationTrasaction(@RequestBody TransactionOperationsDto request)
			throws ApiErrorException, ApiNotFountException {
		return ResponseEntity.ok(transactionService.anulationTrasaction(request));
	}

}
