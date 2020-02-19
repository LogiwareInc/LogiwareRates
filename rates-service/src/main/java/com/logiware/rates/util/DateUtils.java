package com.logiware.rates.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static String formatToString(Date date, String format) {
		if (date == null) {
			return null;
		}
		return new SimpleDateFormat(format).format(date);
	}

	public static Date parseToDate(String date, String format) throws ParseException {
		Date parsedDate = null;
		if (null != date && !date.trim().isEmpty()) {
			parsedDate = (Date) new SimpleDateFormat(format).parse(date.trim());
		}
		return parsedDate;
	}

	public static String addDay(String format, Date date, Integer day) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return formatToString(calendar.getTime(), format);
	}

	public static Date addDateBetween(Date now, int date, int hours, int minute, int sec) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, sec);
		return calendar.getTime();
	}

	public static void main(String[] args) throws ParseException {
		LocalDate date = LocalDate.parse("17/10/2019", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}
