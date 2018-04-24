package com.wumin.common.text;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.time.DateFormatUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {

	public static List<List<List<String>>> readXlsx(InputStream is) throws IOException {
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

		List<List<List<String>>> result = ListUtil.newArrayList();
		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			List<List<String>> sheet = readXlsx(xssfWorkbook, numSheet);
			result.add(sheet);
		}

		xssfWorkbook.close();

		return result;
	}

	public static List<List<String>> readXlsx(InputStream is, int sheet) throws IOException {
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
		List<List<String>> result = readXlsx(xssfWorkbook, sheet);
		xssfWorkbook.close();
		return result;
	}

	public static List<List<String>> readXlsx(XSSFWorkbook xssfWorkbook, int sheet) throws IOException {
		List<List<String>> result = ListUtil.newArrayList();

		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(sheet);
		if (xssfSheet != null) {
			// 循环行Row
			for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow == null) {
					continue;
				}

				List<String> row = ListUtil.newArrayList();
				// 循环列Cell
				for (int cellNum = 0; cellNum <= xssfRow.getLastCellNum(); cellNum++) {
					XSSFCell xssfCell = xssfRow.getCell(cellNum);
					String value = xssfCell == null ? null : getValue(xssfCell);

					row.add(value);
				}
				result.add(row);
			}
		}

		return result;
	}

	public static String getValue(XSSFCell xssfCell) {
		if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(xssfCell)) {
				return DateFormatUtil.formatStandardDate(xssfCell.getDateCellValue());
			} else {
				String value = String.valueOf(xssfCell.getNumericCellValue());
				if(StringUtils.contains(value, "E") ) {
					DecimalFormat df = new DecimalFormat("#");  
					return df.format(xssfCell.getNumericCellValue());
				}
				return value;
			}
		} else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
			return String.valueOf(xssfCell.getStringCellValue());
		} else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
			// nothing21
			return null;
		} else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(xssfCell.getBooleanCellValue());
		} else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
			return String.valueOf(xssfCell.getErrorCellValue());
		} else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
			try {
				return String.valueOf(xssfCell.getNumericCellValue());
			} catch (IllegalStateException e) {
				return String.valueOf(xssfCell.getRichStringCellValue());
			}
		} else { // nothing29
			return null;
		}
	}
	
	public static void copyRow(XSSFRow fromRow, XSSFRow toRow, boolean copyValueFlag) {
        for (Iterator iterator = fromRow.cellIterator(); iterator.hasNext();) {
            XSSFCell tmpCell = (XSSFCell) iterator.next();
            XSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(tmpCell, newCell, copyValueFlag);
        }
    }

    public static void copyCell(XSSFCell fromCell, XSSFCell toCell, boolean copyValueFlag) {
        // 样式
        toCell.setCellStyle(fromCell.getCellStyle());
        // 评论
        if (fromCell.getCellComment() != null) {
            toCell.setCellComment(fromCell.getCellComment());
        }
        // 不同数据类型处理
        int srcCellType = fromCell.getCellType();
        toCell.setCellType(srcCellType);
        if (copyValueFlag) {
            if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
                if (HSSFDateUtil.isCellDateFormatted(fromCell)) {
                    toCell.setCellValue(fromCell.getDateCellValue());
                } else {
                    toCell.setCellValue(fromCell.getNumericCellValue());
                }
            } else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
                toCell.setCellValue(fromCell.getRichStringCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
                // nothing21
            } else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                toCell.setCellValue(fromCell.getBooleanCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
                toCell.setCellErrorValue(fromCell.getErrorCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
                toCell.setCellFormula(fromCell.getCellFormula());
            } else { // nothing29
            }
        }
    }

    public static void copyCellStyle(XSSFCellStyle fromStyle, XSSFCellStyle toStyle) {
        toStyle.setAlignment(fromStyle.getAlignment());
        // 边框和边框颜色
        toStyle.setBorderBottom(fromStyle.getBorderBottom());
        toStyle.setBorderLeft(fromStyle.getBorderLeft());
        toStyle.setBorderRight(fromStyle.getBorderRight());
        toStyle.setBorderTop(fromStyle.getBorderTop());
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

        // 背景和前景
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

        toStyle.setDataFormat(fromStyle.getDataFormat());
        toStyle.setFillPattern(fromStyle.getFillPattern());
        // toStyle.setFont(fromStyle.getFont(null));
        toStyle.setHidden(fromStyle.getHidden());
        toStyle.setIndention(fromStyle.getIndention());// 首行缩进
        toStyle.setLocked(fromStyle.getLocked());
        toStyle.setRotation(fromStyle.getRotation());// 旋转
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
        toStyle.setWrapText(fromStyle.getWrapText());
    }

	public static List<List<List<String>>> readXls(InputStream is) throws IOException {
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

		List<List<List<String>>> result = ListUtil.newArrayList();
		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			List<List<String>> sheet = readXls(hssfWorkbook, numSheet);
			result.add(sheet);
		}

		hssfWorkbook.close();

		return result;
	}

	public static List<List<String>> readXls(InputStream is, int sheet) throws IOException {
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		List<List<String>> result = readXls(hssfWorkbook, sheet);
		hssfWorkbook.close();
		return result;
	}

	public static List<List<String>> readXls(HSSFWorkbook hssfWorkbook, int sheet) throws IOException {
		List<List<String>> result = ListUtil.newArrayList();

		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheet);
		if (hssfSheet != null) {
			// 循环行Row
			for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow == null) {
					continue;
				}

				List<String> row = ListUtil.newArrayList();
				// 循环列Cell
				for (int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++) {
					HSSFCell hssfCell = hssfRow.getCell(cellNum);
					String value = hssfCell == null ? null : getValue(hssfCell);

					row.add(value);
				}
				result.add(row);
			}
		}

		return result;
	}

	public static void copyRow(HSSFRow fromRow, HSSFRow toRow, boolean copyValueFlag) {
		for (Iterator iterator = fromRow.cellIterator(); iterator.hasNext();) {
			HSSFCell tmpCell = (HSSFCell) iterator.next();
			HSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
			copyCell(tmpCell, newCell, copyValueFlag);
		}
	}

	public static void copyCell(HSSFCell fromCell, HSSFCell toCell, boolean copyValueFlag) {
		// HSSFCellStyle cellStyle = hssfWorkbook.createCellStyle();
		// copyCellStyle(srcCell.getCellStyle(), cellStyle);
		// 样式
		toCell.setCellStyle(fromCell.getCellStyle());
		// 评论
		if (fromCell.getCellComment() != null) {
			toCell.setCellComment(fromCell.getCellComment());
		}
		// 不同数据类型处理
		int srcCellType = fromCell.getCellType();
		toCell.setCellType(srcCellType);
		if (copyValueFlag) {
			if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(fromCell)) {
					toCell.setCellValue(fromCell.getDateCellValue());
				} else {
					toCell.setCellValue(fromCell.getNumericCellValue());
				}
			} else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
				toCell.setCellValue(fromCell.getRichStringCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
				// nothing21
			} else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
				toCell.setCellValue(fromCell.getBooleanCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
				toCell.setCellErrorValue(fromCell.getErrorCellValue());
			} else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
				toCell.setCellFormula(fromCell.getCellFormula());
			} else { // nothing29
			}
		}
	}

	public static void copyCellStyle(HSSFCellStyle fromStyle, HSSFCellStyle toStyle) {
		toStyle.setAlignment(fromStyle.getAlignment());
		// 边框和边框颜色
		toStyle.setBorderBottom(fromStyle.getBorderBottom());
		toStyle.setBorderLeft(fromStyle.getBorderLeft());
		toStyle.setBorderRight(fromStyle.getBorderRight());
		toStyle.setBorderTop(fromStyle.getBorderTop());
		toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
		toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
		toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
		toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

		// 背景和前景
		toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
		toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

		toStyle.setDataFormat(fromStyle.getDataFormat());
		toStyle.setFillPattern(fromStyle.getFillPattern());
		// toStyle.setFont(fromStyle.getFont(null));
		toStyle.setHidden(fromStyle.getHidden());
		toStyle.setIndention(fromStyle.getIndention());// 首行缩进
		toStyle.setLocked(fromStyle.getLocked());
		toStyle.setRotation(fromStyle.getRotation());// 旋转
		toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
		toStyle.setWrapText(fromStyle.getWrapText());
	}

	public static String getValue(HSSFCell hssfCell) {
		if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
				return DateFormatUtil.formatStandardDate(hssfCell.getDateCellValue());
			} else {
				String value = String.valueOf(hssfCell.getNumericCellValue());
				if(StringUtils.contains(value, "E") ) {
					DecimalFormat df = new DecimalFormat("#");  
					return df.format(hssfCell.getNumericCellValue());
				}
				return value;
			}
		} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			return String.valueOf(hssfCell.getStringCellValue());
		} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			// nothing21
			return null;
		} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
			return String.valueOf(hssfCell.getErrorCellValue());
		} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
			try {
				return String.valueOf(hssfCell.getNumericCellValue());
			} catch (IllegalStateException e) {
				return String.valueOf(hssfCell.getRichStringCellValue());
			}
		} else { // nothing29
			return null;
		}
	}

}