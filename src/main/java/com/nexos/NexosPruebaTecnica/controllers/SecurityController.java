package com.nexos.NexosPruebaTecnica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexos.NexosPruebaTecnica.dtos.JwtResponse;
import com.nexos.NexosPruebaTecnica.services.SecurityService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/security")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Servicio de autenticación", description = "Grupo de servicios para generar el token de autización del sistema")
public class SecurityController {

	@Autowired
	private SecurityService securityService;

	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Responde 200 cuando la autenticación en valida"),
			@ApiResponse(responseCode = "500", description = "Responde 500 si presenta algun error de ejecución", content = {
					@Content(examples = {}) }) }, description = "Servicio para la generación del token de seguridad sel sistema")

	@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Se debe enviar en el multiparametro dos etiquetas obligatorias client_id (usuario) y client_secret (contraseña)", required = true)
	@PostMapping(path = "/getToken", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JwtResponse> getToken(@RequestBody MultiValueMap<String, String> paramMap,
			@RequestParam("grant_type") String grantType) throws Exception {
		return ResponseEntity.ok(securityService.getToken(paramMap, grantType));
	}
}
