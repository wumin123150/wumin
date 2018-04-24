package com.wumin.common.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ImageUtil {

  public static final String SCALE_SEPARATOR_STR = "_";
  public static final String EXTENSION_SEPARATOR_STR = ".";

  public static boolean isScale(File file) {
    String fileBaseName = FileUtil.getFileName(file.getName());
    return StringUtils.lastIndexOf(fileBaseName, SCALE_SEPARATOR_STR) < StringUtils.lastIndexOf(fileBaseName, EXTENSION_SEPARATOR_STR);
  }

  public static String getScaleSource(File file) {
    String filePath = file.getPath();
    int lastPos = filePath.lastIndexOf(SCALE_SEPARATOR_STR);
    return StringUtils.substring(filePath, 0, lastPos);
  }

  public static String getScaleRange(File file) {
    String fileBaseName = FileUtil.getFileName(file.getName());
    int lastScalePos = StringUtils.lastIndexOf(fileBaseName, SCALE_SEPARATOR_STR);
    int lastExtensionPos = StringUtils.lastIndexOf(fileBaseName, EXTENSION_SEPARATOR_STR);
    return lastScalePos < 0 ? "" : StringUtils.substring(fileBaseName, lastScalePos + 1, lastExtensionPos);
  }

  public static int getScaleWidth(File file) {
    String range = getScaleRange(file);
    return NumberUtils.toInt(StringUtils.substringBefore(range, "x"), 0);
  }

  public static int getScaleHeight(File file) {
    String range = getScaleRange(file);
    if (StringUtils.contains(range, "q")) {
      return NumberUtils.toInt(StringUtils.substringBetween(range, "x", "q"), 0);
    } else {
      return NumberUtils.toInt(StringUtils.substringAfter(range, "x"), 0);
    }
  }

  public static Integer getScaleQuality(File file) {
    String range = getScaleRange(file);
    return NumberUtils.createInteger(StringUtils.trimToNull(StringUtils.substringAfter(range, "q")));
  }

  public static void scale(File srcFile, String scaleRange) throws IOException {
    int boxWidth = NumberUtils.toInt(StringUtils.substringBefore(scaleRange, "x"), 0);
    int boxHeight = StringUtils.contains(scaleRange, "q") ? NumberUtils.toInt(
      StringUtils.substringBetween(scaleRange, "x", "q"), 0) : NumberUtils.toInt(
      StringUtils.substringAfter(scaleRange, "x"), 0);
    Integer boxQuality = NumberUtils.createInteger(StringUtils.trimToNull(StringUtils.substringAfter(scaleRange,
      "q")));
    scale(srcFile, boxWidth, boxHeight, boxQuality);
  }

  public static void scale(File srcFile, Integer boxWidth, Integer boxHeight) throws IOException {
    scale(srcFile, boxWidth, boxHeight, null);
  }

  public static void scale(File srcFile, Integer boxWidth, Integer boxHeight, Integer quality) throws IOException {
    scale(srcFile, boxWidth, boxHeight, quality, FileUtil.getFileExtension(srcFile.getName()));
  }

  public static void scale(File srcFile, Integer boxWidth, Integer boxHeight, Integer quality, String ext)
    throws IOException {
    String range = boxWidth + "x" + boxHeight;
    if (quality != null) {
      range += "q" + quality;
    }
    File tarFile = new File(srcFile.getPath() + SCALE_SEPARATOR_STR + range + EXTENSION_SEPARATOR_STR + ext);
    scale(srcFile, tarFile, boxWidth, boxHeight, quality);
  }

  public static void scale(File srcFile, File tarFile, String scaleRange) throws IOException {
    int boxWidth = NumberUtils.toInt(StringUtils.substringBefore(scaleRange, "x"), 0);
    int boxHeight = StringUtils.contains(scaleRange, "q") ? NumberUtils.toInt(
      StringUtils.substringBetween(scaleRange, "x", "q"), 0) : NumberUtils.toInt(
      StringUtils.substringAfter(scaleRange, "x"), 0);
    Integer boxQuality = NumberUtils.createInteger(StringUtils.trimToNull(StringUtils.substringAfter(scaleRange,
      "q")));
    scale(srcFile, tarFile, boxWidth, boxHeight, boxQuality);
  }

  public static void scale(File srcFile, File tarFile, Integer boxWidth, Integer boxHeight) throws IOException {
    scale(srcFile, tarFile, boxWidth, boxHeight, null);
  }

  public static void scale(File srcFile, File tarFile, int boxWidth, int boxHeight, Integer quality)
    throws IOException {
    OutputStream tarStream = new FileOutputStream(tarFile);
    scale(srcFile, tarStream, boxWidth, boxHeight, quality, FileUtil.getFileExtension(tarFile.getName()));
    tarStream.close();
  }

  private static void scale(File srcFile, OutputStream tarStream, int boxWidth, int boxHeight, Integer quality,
                            String format) throws IOException {
    Builder<File> builder = Thumbnails.of(srcFile).outputFormat(format);
    if (quality != null && StringUtils.equalsIgnoreCase("jpg", format)) {
      builder.outputQuality(quality / 100f);
    }

    BufferedImage srcImage = ImageIO.read(srcFile);
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    if (0 == boxWidth) {
      boxWidth = width;
    }
    if (0 == boxHeight) {
      boxHeight = height;
    }
    if (width > boxWidth || height > boxHeight) {
      if ((float) width / height > (float) boxWidth / boxHeight) {
        builder.width(boxWidth);
      } else {
        builder.height(boxHeight);
      }
    }

    builder.toOutputStream(tarStream);
  }

  public static void forceScale(File srcFile, String scaleRange) throws IOException {
    int boxWidth = NumberUtils.toInt(StringUtils.substringBefore(scaleRange, "x"), 0);
    int boxHeight = StringUtils.contains(scaleRange, "q") ? NumberUtils.toInt(
      StringUtils.substringBetween(scaleRange, "x", "q"), 0) : NumberUtils.toInt(
      StringUtils.substringAfter(scaleRange, "x"), 0);
    Integer boxQuality = NumberUtils.createInteger(StringUtils.trimToNull(StringUtils.substringAfter(scaleRange,
      "q")));
    forceScale(srcFile, boxWidth, boxHeight, boxQuality);
  }

  public static void forceScale(File srcFile, Integer boxWidth, Integer boxHeight) throws IOException {
    forceScale(srcFile, boxWidth, boxHeight, null);
  }

  public static void forceScale(File srcFile, Integer boxWidth, Integer boxHeight, Integer quality)
    throws IOException {
    forceScale(srcFile, boxWidth, boxHeight, quality, FileUtil.getFileExtension(srcFile.getName()));
  }

  public static void forceScale(File srcFile, Integer boxWidth, Integer boxHeight, Integer quality, String ext)
    throws IOException {
    String range = boxWidth + "x" + boxHeight;
    if (quality != null) {
      range += "q" + quality;
    }
    File tarFile = new File(srcFile.getPath() + SCALE_SEPARATOR_STR + range + EXTENSION_SEPARATOR_STR + ext);
    forceScale(srcFile, tarFile, boxWidth, boxHeight, quality);
  }

  public static void forceScale(File srcFile, File tarFile, String scaleRange) throws IOException {
    int boxWidth = NumberUtils.toInt(StringUtils.substringBefore(scaleRange, "x"), 0);
    int boxHeight = StringUtils.contains(scaleRange, "q") ? NumberUtils.toInt(
      StringUtils.substringBetween(scaleRange, "x", "q"), 0) : NumberUtils.toInt(
      StringUtils.substringAfter(scaleRange, "x"), 0);
    Integer boxQuality = NumberUtils.createInteger(StringUtils.trimToNull(StringUtils.substringAfter(scaleRange,
      "q")));
    forceScale(srcFile, tarFile, boxWidth, boxHeight, boxQuality);
  }

  public static void forceScale(File srcFile, File tarFile, Integer boxWidth, Integer boxHeight) throws IOException {
    forceScale(srcFile, tarFile, boxWidth, boxHeight, null);
  }

  public static void forceScale(File srcFile, File tarFile, int boxWidth, int boxHeight, Integer quality)
    throws IOException {
    OutputStream tarStream = new FileOutputStream(tarFile);
    forceScale(srcFile, tarStream, boxWidth, boxHeight, quality, FileUtil.getFileExtension(tarFile.getName()));
    tarStream.close();
  }

  private static void forceScale(File srcFile, OutputStream tarStream, int boxWidth, int boxHeight, Integer quality,
                                 String format) throws IOException {
    Builder<File> builder = Thumbnails.of(srcFile).outputFormat(format);
    if (quality != null && StringUtils.equalsIgnoreCase("jpg", format)) {
      builder.outputQuality(quality / 100f);
    }

    BufferedImage srcImage = ImageIO.read(srcFile);
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();
    if (0 == boxWidth) {
      boxWidth = width;
    }
    if (0 == boxHeight) {
      boxHeight = height;
    }
    if (width != boxWidth || height != boxHeight) {
      if ((float) width / height > (float) boxWidth / boxHeight) {
        builder.sourceRegion(Positions.CENTER, Math.round((float) height * boxWidth / boxHeight), height);
      } else {
        builder.sourceRegion(Positions.CENTER, width, Math.round((float) width * boxHeight / boxWidth));
      }
    }

    builder.forceSize(boxWidth, boxHeight).toOutputStream(tarStream);
  }

  public static void mark(File srcFile, File tarFile, Position position, Integer quality, File markFile) throws IOException {
    BufferedImage markImage = ImageIO.read(markFile);
    OutputStream tarStream = new FileOutputStream(tarFile);
    mark(srcFile, tarStream, position, markImage, quality, FileUtil.getFileExtension(srcFile.getName()));
  }

  protected static void mark(File srcFile, OutputStream tarStream, Position position, BufferedImage markImage, Integer quality, String format) throws IOException {
    Thumbnails.of(srcFile)
      .watermark(position, markImage, quality / 100f)
      .outputFormat(format)
      .toOutputStream(tarStream);
  }

}
