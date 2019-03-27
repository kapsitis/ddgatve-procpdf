package lv.ddgatve.procpdf.books

import _root_.java.io.IOException
import _root_.com.itextpdf.text.pdf.PdfReader;
import _root_.com.itextpdf.text.pdf.parser.{
  PdfReaderContentParser,
  SimpleTextExtractionStrategy,
  TextExtractionStrategy
}

/**
 * Reads a PDF file as a plaintext
 */
object PdfAnalyzer {

  def main(args: Array[String]): Unit = {
    println("Start analyzing file " + args(0))
    println(loadText(args(0)))
    println("End analyzing file")
  }

  def loadText(pdf: String): String = {
    val reader = new PdfReader(pdf);
    val parser = new PdfReaderContentParser(reader);
    var strategy: TextExtractionStrategy = null;
    val result = new StringBuffer();
    strategy = parser.processContent(1, new SimpleTextExtractionStrategy());
    result.append(strategy.getResultantText());
    return result.toString();
  }
}
