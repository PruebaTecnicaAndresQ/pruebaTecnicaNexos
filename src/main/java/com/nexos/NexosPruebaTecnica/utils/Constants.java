package com.nexos.NexosPruebaTecnica.utils;

public final class Constants {

	public static final String CLIENT_CREDENTIALS = "client_credentials";
	public static final String INVALID_GRANT_TYPE = "El campo grant_type es invalido";
	public static final String INVALID_PARAM_VALUES = "Client_id y/o client_secret son invalidos";
	public static final String USER_NOT_FOUND = "Usuario y/o contraseña incorrectos";
	public static final String UNAUTHORIZED = "Usuario no autorizado";
	public static final String INVALID_PRODUCT_ID = "El número de prodúcto debe ser númerico";
	public static final String INVALID_CLIENT_ID = "El número de identificación del cliente debe ser númerico";
	public static final String INVALID_PARAM_DATA = "Uno o mas campos estan vacios,verifique e intente de nuevo";
	public static final String INVALID_PRODUCT_NUMNBER = "El número de tarjeta debe ser númerico";
	public static final String NOT_FOUND_PRODUCT_NUMNBER = "El número de tarjeta no existe";
	public static final String NOT_INACTIVE_PRODUCT_NUMNBER = "La tarjeta no se puede activar, ya esta activa y/o bloqueada";
	public static final String NOT_ACTIVE_PRODUCT_NUMNBER = "No se pueden realizar transacciones, ya que la tarjeta esta inactiva y/o bloqueada";
	public static final String INVALID_BALANCE = "El valor de la recarga debe ser mayor a 0";
	public static final String INVALID_PRICE = "El valor de la transacción debe ser mayor a 0";
	public static final String INVALID_CURRENCY = "Solo se pueden generar transacciones en dolar US";
	public static final String INVALID_BALANCE_AVIABLE = "Saldo insuficiente para la compra";
	public static final String EXPIRED_CARD = "Su tarjeta esta vencina y no puede realizar transacciones";
	public static final String INACTIVE_CARD = "Su tarjeta esta inactiva, favor activarla e intentar de nuevo";
	public static final String NOT_FOUND_TRANSACTION = "Transacción no encontrada";
	public static final String NOT_CANCEL_TRANSACTION_BY_DATE = "No se puede cancelar la transaccion despues de 24 horas de realizada";
	public static final String NOT_VALID_TRANSACTION = "La transaccion ingresada no es valida para cancelación";
}
