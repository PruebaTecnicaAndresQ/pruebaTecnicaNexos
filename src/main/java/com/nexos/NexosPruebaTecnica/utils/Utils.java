package com.nexos.NexosPruebaTecnica.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nexos.NexosPruebaTecnica.exceptions.ApiErrorException;

public final class Utils {

	/***
	 * Utilitario para identificar si el valor de un string es numerico o no
	 * 
	 * @param value
	 * @return
	 * @throws ApiErrorException
	 */
	public static boolean isNumeric(String value) throws Exception {

		Pattern pattern = Pattern.compile("^\\d+$");
		Matcher matcher = pattern.matcher(value);
		return matcher.find();

	}

	/**
	 * Metodo para validar que un string no sea nullo o vacio
	 * 
	 * @param value
	 * @return
	 * @throws ApiErrorException
	 */
	public static boolean isNotNullOrEmpty(String value) throws ApiErrorException {
		return value != null && !value.isEmpty();
	}

	/**
	 * Metodo para generar un numero aleatorio de X caracteres de lognitud
	 * 
	 * @param maxDigit
	 * @return
	 * @throws ApiErrorException
	 */
	public static Long generateRandomNumber(int maxDigit) throws Exception {
		StringBuilder response = new StringBuilder();
		Random random = new Random();
		for (int i = 1; i <= maxDigit; i++) {
			int digit = random.nextInt(10);
			response.append(digit);
		}
		return Long.valueOf(response.toString());
	}

	/**
	 * Metodo para sumar años a la fecha actual, devuelve el ultimo dia del mes
	 * resultante
	 * 
	 * @param yearsCount
	 * @return
	 * @throws ApiErrorException
	 */
	public static Date addYears(int yearsCount) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 50);
		cal.add(Calendar.YEAR, yearsCount);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * Metodo para formatear la fecha de expiracion en formato mm/yyyy
	 * 
	 * @param inputDate
	 * @return
	 * @throws ApiErrorException
	 */
	public static String DateToStingFormat(Date inputDate) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("MM/yyyy");
		return df.format(inputDate);
	}

	/**
	 * Metodo para formatear la fecha de expiracion en formato dd/mm/yyyy hh:mm:ss
	 * 
	 * @param inputDate
	 * @return
	 * @throws ApiErrorException
	 */
	public static String DateToStingFullFormat(Date inputDate) throws Exception {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		return df.format(inputDate);

	}
}
