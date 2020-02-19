package com.logiware.rates.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 * @author Lakshmi Narayanan
 *
 */

public class ExcelUtils {

	private ExcelUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static String getBooleanValue(Cell cell) {
		return String.valueOf(cell.getBooleanCellValue());
	}

	private static String getNumericValue(Cell cell, String datePattern) {
		if (DateUtil.isCellDateFormatted(cell)) {
			return DateUtils.formatToString(cell.getDateCellValue(), datePattern);
		} else {
			return String.valueOf(cell.getNumericCellValue());
		}
	}

	private static String getFormulaValue(Cell cell) {
		return cell.getCellFormula();
	}

	private static String getStringValue(Cell cell) {
		return cell.getStringCellValue();
	}

	public static String getCellValue(Cell cell, FormulaEvaluator evaluator, String datePattern) {
		CellType cellType;
		if (cell.getCellType() == CellType.FORMULA) {
			cellType = evaluator.evaluateFormulaCell(cell);
		} else {
			cellType = cell.getCellType();
		}
		switch (cellType) {
		case BOOLEAN:
			return getBooleanValue(cell);
		case NUMERIC:
			return getNumericValue(cell, datePattern);
		case FORMULA:
			return getFormulaValue(cell);
		case BLANK:
			return "";
		case ERROR:
			return "";
		default:
			return getStringValue(cell);
		}
	}
}
