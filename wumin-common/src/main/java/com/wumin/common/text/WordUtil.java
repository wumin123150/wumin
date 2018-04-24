package com.wumin.common.text;

import com.wumin.common.base.ExceptionUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordUtil {

  public static void vreplaceText(WordprocessingMLPackage word, Map<String, String> mappings) throws Docx4JException {
    MainDocumentPart documentPart = word.getMainDocumentPart();
    try {
      documentPart.variableReplace(Maps.newHashMap(mappings));
    } catch (JAXBException ex) {
      ExceptionUtil.unchecked(ex);
    }
  }

  public static WordprocessingMLPackage getTemplate(String path) throws Docx4JException {
    return WordprocessingMLPackage.load(new File(path));
  }

  public static WordprocessingMLPackage getTemplate(InputStream in) throws Docx4JException {
    return WordprocessingMLPackage.load(in);
  }

  private static <T> List<T> getAllElementFromObject(Object object, Class<T> toSearch) {
    List<T> result = new ArrayList<>();

    if (object instanceof JAXBElement)
      object = ((JAXBElement<?>) object).getValue();

    if (object.getClass().equals(toSearch)) {
      result.add((T) object);
    } else if (object instanceof ContentAccessor) {
      List<?> children = ((ContentAccessor) object).getContent();
      for (Object child : children) {
        result.addAll(getAllElementFromObject(child, toSearch));
      }
    }

    return result;
  }

  private static P getTemplateParagraph(List<P> paragraphs, String placeholder) {
    for (P p : paragraphs) {
      List<Text> texts = getAllElementFromObject(p, Text.class);
      for (Text text : texts) {
        if (StringUtils.equals(text.getValue(), placeholder)) {
          return p;
        }
      }
    }
    return null;
  }

  private static Tbl getTemplateTable(List<Tbl> tables, String placeholder) {
    for (Tbl table : tables) {
      List<Text> texts = getAllElementFromObject(table, Text.class);
      for (Text text : texts) {
        if (StringUtils.equals(text.getValue(), placeholder)) {
          return table;
        }
      }
    }
    return null;
  }

  private static void replaceText(WordprocessingMLPackage word, String placeholder, String value) {
    List<Text> texts = getAllElementFromObject(word.getMainDocumentPart(), Text.class);

    for (Text text : texts) {
      if (StringUtils.equals(text.getValue(), placeholder)) {
        text.setValue(value);
      }
    }
  }

  private static void replaceParagraph(WordprocessingMLPackage word, String placeholder, List<String> values) {
    // 1. get the paragraph
    List<P> paragraphs = getAllElementFromObject(word.getMainDocumentPart(), P.class);

    // 2. find the paragraph
    P templateParagraph = getTemplateParagraph(paragraphs, "${" + placeholder + "}");
    if (templateParagraph == null)
      templateParagraph = getTemplateParagraph(paragraphs, placeholder);

    if (templateParagraph != null) {
      for (String value : values) {
        // 3. copy the found paragraph to keep styling correct
        P workingParagraph = XmlUtils.deepCopy(templateParagraph);

        // replace the text elements from the copy
        List<Text> texts = getAllElementFromObject(workingParagraph, Text.class);
        if (texts.size() > 0) {
          Text copyText = texts.get(0);
          copyText.setValue(value);
        }

        // add the paragraph to the document
        ((ContentAccessor) templateParagraph.getParent()).getContent().add(workingParagraph);
      }

      // 5. remove the original one
      ((ContentAccessor) templateParagraph.getParent()).getContent().remove(templateParagraph);
    }
  }

  private static void replaceTable(WordprocessingMLPackage word, String keyword, List<Map<String, String>> mapTables) {
    // get the table
    List<Tbl> tables = getAllElementFromObject(word.getMainDocumentPart(), Tbl.class);

    // find the table
    Tbl tempTable = getTemplateTable(tables, "${" + keyword + "}");
    if (tempTable == null)
      tempTable = getTemplateTable(tables, keyword);

    if (tempTable != null) {
      List<Tr> rows = getAllElementFromObject(tempTable, Tr.class);

      // first row is header, second row is content
      if (rows.size() > 1) {
        // this is our template row
        Tr templateRow = rows.get(1);

        for (int i = 0; i < mapTables.size(); i++) {
          Map<String, String> mappings = mapTables.get(i);

          Tr workingRow = XmlUtils.deepCopy(templateRow);
          List<Text> texts = getAllElementFromObject(workingRow, Text.class);
          for (Text text : texts) {
            String key = text.getValue();
            String value = mappings.get(key);
            if (value == null && StringUtils.startsWith(key, "${") && StringUtils.endsWith(key, "}")) {
              value = mappings.get(StringUtils.substring(key, 2, key.length() - 1));
            }

            if (value != null)
              text.setValue(value);
          }

          tempTable.getContent().add(i + 1, workingRow);
        }

        // remove the template row
        tempTable.getContent().remove(templateRow);
      }
    }
  }

  public static void replace(WordprocessingMLPackage word, Map<String, String> mappings,
                             Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException {
    // 段落替换
    if (mapParagraphs != null) {
      for (Map.Entry<String, List<String>> entry : mapParagraphs.entrySet()) {
        replaceParagraph(word, entry.getKey(), entry.getValue());
      }
    }
    // 表格替换
    if (mapTables != null) {
      for (Map.Entry<String, List<Map<String, String>>> entry : mapTables.entrySet()) {
        replaceTable(word, entry.getKey(), entry.getValue());
      }
    }
    // 文本占位符替换
    if (mappings != null) {
      vreplaceText(word, mappings);
      for (Map.Entry<String, String> entry : mappings.entrySet()) {
        replaceText(word, entry.getKey(), entry.getValue());
      }
    }
  }

  public static void saveToWord(String template, OutputStream out, Map<String, String> mappings,
                                Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    word.save(out);
  }

  public static void saveToWord(String template, String tarPath, Map<String, String> mappings,
                                Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    word.save(new File(tarPath));
  }

  public static void saveToPdf(String template, OutputStream out, Map<String, String> mappings,
                               Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    Docx4J.toPDF(word, out);
  }

  public static void saveToPdf(String template, String tarPath, Map<String, String> mappings,
                               Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException, FileNotFoundException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    Docx4J.toPDF(word, new FileOutputStream(new File(tarPath)));
  }

  public static void saveToWord(InputStream template, OutputStream out, Map<String, String> mappings,
                                Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    word.save(out);
  }

  public static void saveToWord(InputStream template, String tarPath, Map<String, String> mappings,
                                Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    word.save(new File(tarPath));
  }

  public static void saveToPdf(InputStream template, OutputStream out, Map<String, String> mappings,
                               Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    Docx4J.toPDF(word, out);
  }

  public static void saveToPdf(InputStream template, String tarPath, Map<String, String> mappings,
                               Map<String, List<String>> mapParagraphs, Map<String, List<Map<String, String>>> mapTables)
    throws Docx4JException, FileNotFoundException {
    WordprocessingMLPackage word = getTemplate(template);
    replace(word, mappings, mapParagraphs, mapTables);
    Docx4J.toPDF(word, new FileOutputStream(new File(tarPath)));
  }

  /**
   * 由于linux环境无中文字体，需要在params中传递fontFamily和fontFile
   */
  public static void saveUrlToWord(String url, OutputStream out, Map<String, String> params) throws Docx4JException, IOException {
    WordprocessingMLPackage word = urlToWord(url, params);
    word.save(out);
  }

  /**
   * 由于linux环境无中文字体，需要在params中传递fontFamily和fontFile
   */
  public static void saveUrlToWord(String url, String tarPath, Map<String, String> params) throws Docx4JException, IOException {
    WordprocessingMLPackage word = urlToWord(url, params);
    word.save(new File(tarPath));
  }

  /**
   * 由于linux环境无中文字体，需要在params中传递fontFamily和fontFile
   */
  public static void saveUrlToPdf(String url, OutputStream out, Map<String, String> params) throws Docx4JException, IOException {
    WordprocessingMLPackage word = urlToWord(url, params);
    Docx4J.toPDF(word, out);
  }

  /**
   * 由于linux环境无中文字体，需要在params中传递fontFamily和fontFile
   */
  public static void saveUrlToPdf(String url, String tarPath, Map<String, String> params) throws Docx4JException, IOException {
    WordprocessingMLPackage word = urlToWord(url, params);
    Docx4J.toPDF(word, new FileOutputStream(new File(tarPath)));
  }

  private static WordprocessingMLPackage urlToWord(String url, Map<String, String> params) throws Docx4JException, IOException {
    return xhtmlToWord(urlToXhtml(url), params);
  }

  private static WordprocessingMLPackage xhtmlToWord(Document doc, Map<String, String> params) throws Docx4JException {
    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
      .createPackage(PageSizePaper.valueOf("A4"), true); // A4纸横版

    if (params != null) {
      String fontFamily = params.get("fontFamily");
      String fontFile = params.get("fontFile");
      if (StringUtils.isNotEmpty(fontFamily) && StringUtils.isNotEmpty(fontFile)) {
        try {
          URL fontUrl = URI.create(fontFile).toURL();
          configFont(wordMLPackage, fontFamily, fontUrl); // 配置中文字体
        } catch (Exception ex) {
          ExceptionUtil.unchecked(ex);
        }
      }
    }

    XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
    wordMLPackage.getMainDocumentPart().getContent().addAll(
      xhtmlImporter.convert(doc.html(), doc.baseUri()));

    return wordMLPackage;
  }

  private static Document urlToXhtml(String url) throws IOException {
    Document doc = Jsoup.connect(url).get();

    for (Element script : doc.getElementsByTag("script")) { // 除去所有script
      script.remove();
    }

    for (Element a : doc.getElementsByTag("a")) { // 除去a的onclick，href属性
      a.removeAttr("onclick");
      a.removeAttr("href");
    }

    Elements links = doc.getElementsByTag("link"); // 将link中的地址替换为绝对地址
    for (Element element : links) {
      String href = element.absUrl("href");
      element.attr("href", href);
    }

    // 转为xhtml格式
    doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml);
    return doc;
  }

  // 加载字体文件（解决linux环境下无中文字体问题）
  private static void configFont(WordprocessingMLPackage wordMLPackage, String fontFamily, URL fontUrl) {
    try {
      Mapper fontMapper = new IdentityPlusMapper();
      wordMLPackage.setFontMapper(fontMapper);
      PhysicalFonts.addPhysicalFonts(fontFamily, fontUrl);
      PhysicalFont simsunFont = PhysicalFonts.get(fontFamily);
      fontMapper.put(fontFamily, simsunFont);

      RFonts rfonts = Context.getWmlObjectFactory().createRFonts(); // 设置文件默认字体
      rfonts.setAsciiTheme(null);
      rfonts.setAscii(fontFamily);
      wordMLPackage.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr().setRFonts(rfonts);
    } catch (Exception ex) {
      ExceptionUtil.unchecked(ex);
    }
  }

}