package com.wumin.core.utils;

import com.wumin.common.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DownloadUtil {

  private static final int DEFAULT_BUFFER_SIZE = 20480; // ..bytes = 20KB.
  private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 week.
  private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

  public static void download(File file, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    download(file, null, request, response);
  }

  public static void download(File file, String fileName, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    if (!file.exists() || !file.canRead()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String filename = StringUtils.isEmpty(fileName) ? file.getName() : fileName;
    String contentType = contentType(filename);
    long length = file.length();
    long lastModified = file.lastModified();
    String eTag = filename + "_" + length + "_" + lastModified;
    long expires = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME;

    // Validate request headers for caching ---------------------------------------------------

    // If-None-Match header should contain "*" or ETag. If so, then return 304.
    String ifNoneMatch = request.getHeader("If-None-Match");
    if (ifNoneMatch != null && matches(ifNoneMatch, fileName)) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      response.setHeader("ETag", eTag); // Required in 304.
      response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
      return;
    }

    // If-Modified-Since header should be greater than LastModified. If so, then return 304.
    // This header is ignored if any If-None-Match header is specified.
    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
    if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      response.setHeader("ETag", eTag); // Required in 304.
      response.setDateHeader("Expires", expires); // Postpone cache with 1 week.
      return;
    }

    // Validate request headers for resume ----------------------------------------------------

    // If-Match header should contain "*" or ETag. If not, then return 412.
    String ifMatch = request.getHeader("If-Match");
    if (ifMatch != null && !matches(ifMatch, fileName)) {
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return;
    }

    // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
    long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
    if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return;
    }

    // Validate and process range -------------------------------------------------------------

    // Prepare some variables. The full Range represents the complete file.
    Range full = new Range(0, length - 1, length);
    List<Range> ranges = new ArrayList<Range>();

    // Validate and process Range and If-Range headers.
    String range = request.getHeader("Range");
    if (range != null) {
      // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
      if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
        response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
        return;
      }

      String ifRange = request.getHeader("If-Range");
      if (ifRange != null && !ifRange.equals(eTag)) {
        try {
          long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
          if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
            ranges.add(full);
          }
        } catch (IllegalArgumentException ignore) {
          ranges.add(full);
        }
      }

      // If any valid If-Range header, then process each part of byte range.
      if (ranges.isEmpty()) {
        for (String part : range.substring(6).split(",")) {
          // Assuming a file with length of 100, the following examples returns bytes at:
          // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
          long start = sublong(part, 0, part.indexOf("-"));
          long end = sublong(part, part.indexOf("-") + 1, part.length());

          if (start == -1) {
            start = length - end;
            end = length - 1;
          } else if (end == -1 || end > length - 1) {
            end = length - 1;
          }

          // Check if Range is syntactically valid. If not, then return 416.
          if (start > end) {
            response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
          }

          // Add range.
          ranges.add(new Range(start, end, length));
        }
      }
    }

    // Prepare and initialize response --------------------------------------------------------

    // Get content type by file name and set content disposition.
    String disposition = "inline";

    // If content type is unknown, then set the default value.
    // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
    // To add new content types, add new mime-mapping entry in web.xml.
    if (contentType == null) {
      contentType = "application/octet-stream";
    } else if (!contentType.startsWith("image")) {
      // Else, expect for images, determine content disposition. If content type is supported by
      // the browser, then set to inline, else attachment which will pop a 'save as' dialogue.
      String accept = request.getHeader("Accept");
      disposition = accept != null && accepts(accept, contentType) ? "inline" : "attachment";
    }

    // Initialize response.
    response.reset();
    response.setBufferSize(DEFAULT_BUFFER_SIZE);
    response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
    response.setHeader("Accept-Ranges", "bytes");
    response.setHeader("ETag", eTag);
    response.setDateHeader("Last-Modified", lastModified);
    response.setDateHeader("Expires", expires);

    // Send requested file (part(s)) to client ------------------------------------------------

    // Prepare streams.
    RandomAccessFile input = null;
    OutputStream output = null;

    try {
      // Open streams.
      input = new RandomAccessFile(file, "r");
      output = response.getOutputStream();

      if (ranges.isEmpty() || ranges.get(0) == full) {
        // Return full file.
        Range r = full;
        response.setContentType(contentType);
        response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
        response.setHeader("Content-Length", String.valueOf(r.length));

        copy(input, output, r.start, r.length);

      } else if (ranges.size() == 1) {
        // Return single part of file.
        Range r = ranges.get(0);
        response.setContentType(contentType);
        response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
        response.setHeader("Content-Length", String.valueOf(r.length));
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

        // Copy single part range.
        copy(input, output, r.start, r.length);

      } else {
        // Return multiple parts of file.
        response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

        // Cast back to ServletOutputStream to get the easy println methods.
        ServletOutputStream sos = (ServletOutputStream) output;

        // Copy multi part range.
        for (Range r : ranges) {
          // Add multipart boundary and header fields for every range.
          sos.println();
          sos.println("--" + MULTIPART_BOUNDARY);
          sos.println("Content-Type: " + contentType);
          sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

          // Copy single part range of multi part range.
          copy(input, output, r.start, r.length);
        }

        // End with multipart boundary.
        sos.println();
        sos.println("--" + MULTIPART_BOUNDARY + "--");
      }
    } finally {
      close(output);
      close(input);
    }
  }

  // Helpers (can be refactored to public utility class) ----------------------------------------

  private static void close(Closeable resource) {
    if (resource != null) {
      try {
        resource.close();
      } catch (IOException ignore) {
      }
    }
  }

  private static void copy(RandomAccessFile input, OutputStream output, long start, long length) throws IOException {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int read;

    try {
      if (input.length() == length) {
        // Write full range.
        while ((read = input.read(buffer)) > 0) {
          output.write(buffer, 0, read);
        }
      } else {
        input.seek(start);
        long toRead = length;

        while ((read = input.read(buffer)) > 0) {
          if ((toRead -= read) > 0) {
            output.write(buffer, 0, read);
          } else {
            output.write(buffer, 0, (int) toRead + read);
            break;
          }
        }
      }
    } catch (IOException ignore) {
    }
  }

  private static long sublong(String value, int beginIndex, int endIndex) {
    String substring = value.substring(beginIndex, endIndex);
    return (substring.length() > 0) ? Long.parseLong(substring) : -1;
  }

  private static boolean accepts(String acceptHeader, String toAccept) {
    String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
    Arrays.sort(acceptValues);
    return Arrays.binarySearch(acceptValues, toAccept) > -1
      || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
      || Arrays.binarySearch(acceptValues, "*/*") > -1;
  }

  private static boolean matches(String matchHeader, String toMatch) {
    String[] matchValues = matchHeader.split("\\s*,\\s*");
    Arrays.sort(matchValues);
    return Arrays.binarySearch(matchValues, toMatch) > -1
      || Arrays.binarySearch(matchValues, "*") > -1;
  }

  public static String contentType(String filename) {
    String fileType = FileUtil.getFileExtension(filename).toLowerCase(Locale.ENGLISH);
    switch(fileType) {
      case "xls":
      case "xlsx":
        return "application/vnd.ms-excel";
      case "doc":
        return "application/msword";
      case "ppt":
        return "application/vnd.ms-powerpoint";
      case "pdf":
        return "application/pdf";
      case "gif":
        return "image/gif";
      case "jpg":
      case "jpeg":
        return "image/jpeg";
      case "png":
        return "image/png";
      case "bmp":
        return "image/x-xbitmap";
      case "svg":
        return "image/svg+xml";
      case "mp3":
        return "audio/mp3";
      case "mp4":
        return "audio/mp4";
      case "aac":
        return "video/mp4";
      case "zip":
        return "application/zip";
      case "apk":
        return "application/vnd.android.package-archive";
    }
    return null;
  }

  // Inner classes ------------------------------------------------------------------------------

  protected static class Range {
    long start;
    long end;
    long length;
    long total;

    public Range(long start, long end, long total) {
      this.start = start;
      this.end = end;
      this.length = end - start + 1;
      this.total = total;
    }
  }

}