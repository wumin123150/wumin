package com.wumin.common.io;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.wumin.common.time.DateFormatUtil;

public class WebFileUtil {
	
	/**
	 * 日期格式化对象
	 */
	public static final String MONTH_FORMAT = "/yyyyMM/ddHHmmss";
	public static final String YEAR_MONTH_FORMAT = "yyyyMM";
	public static final String SHORT_FORMAT = "ddHHmmss";

	private static final char[] N36_CHARS = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		'x', 'y', 'z' };
	
	public static String generateFilename(String path, String ext) {
		return path + DateFormatUtil.formatCurrentDate(MONTH_FORMAT) + RandomStringUtils.random(4, N36_CHARS) + "." + ext;
	}
	
	public static String generateByFilename(String path, String fileName, String ext) {
		return path + fileName + "." + ext;
	}

	/**
	 * Sanitizes a filename from certain chars.<br />
	 * 
	 * This method enforces the <code>forceSingleExtension</code> property and
	 * then replaces all occurrences of \, /, |, :, ?, *, &quot;, &lt;, &gt;,
	 * control chars by _ (underscore).
	 * 
	 * @param filename a potentially 'malicious' filename
	 * @return sanitized filename
	 */
	public static String sanitizeFileName(final String filename) {
		if (StringUtils.isNotBlank(filename)) {
			String name = forceSingleExtension(filename);

			// Remove \ / | : ? * " < > 'Control Chars' with _
			return name.replaceAll("\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
		}
			
		return filename;
	}

	/**
	 * Sanitizes a folder name from certain chars.<br />
	 * 
	 * This method replaces all occurrences of \, /, |, :, ?, *, &quot;, &lt;,
	 * &gt;, control chars by _ (underscore).
	 * 
	 * @param folderName a potentially 'malicious' folder name
	 * @return sanitized folder name
	 */
	public static String sanitizeFolderName(final String folderName) {
		if (StringUtils.isNotBlank(folderName)) {
			return folderName.replaceAll("\\.|\\\\|/|\\||:|\\?|\\*|\"|<|>|\\p{Cntrl}", "_");
		}
		return folderName;
	}

	/**
	 * Replaces all dots in a filename with underscores except the last one.
	 * 
	 * @param filename
	 *            filename to sanitize
	 * @return string with a single dot only
	 */
	public static String forceSingleExtension(final String filename) {
		return filename.replaceAll("\\.(?![^.]+$)", "_");
	}

	/**
	 * Checks if a filename contains more than one dot.
	 * 
	 * @param filename
	 *            filename to check
	 * @return <code>true</code> if filename contains severals dots, else
	 *         <code>false</code>
	 */
	public static boolean isSingleExtension(final String filename) {
		return filename.matches("[^\\.]+\\.[^\\.]+");
	}

	/**
	 * Iterates over a base name and returns the first non-existent file.<br />
	 * This method extracts a file's base name, iterates over it until the first
	 * non-existent appearance with <code>basename(n).ext</code>. Where n is a
	 * positive integer starting from one.
	 * 
	 * @param file base file
	 * @return first non-existent file
	 */
	public static File getUniqueFile(final File file) {
		if (!file.exists())
			return file;

		File tmpFile = new File(file.getAbsolutePath());
		File parentDir = tmpFile.getParentFile();
		int count = 1;
		String extension = FilenameUtils.getExtension(tmpFile.getName());
		String baseName = FilenameUtils.getBaseName(tmpFile.getName());
		do {
			tmpFile = new File(parentDir, baseName + "(" + count++ + ")." + extension);
		} while (tmpFile.exists());
		return tmpFile;
	}

}
