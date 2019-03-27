package lv.ddgatve.procpdf.bookcreator.pdf

import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

object ExtractImages {
//  val RESULT = "Cloud_Web_Admin_Training_Session_1.pdf";
  var currentPage = 0
  var slideStarts = Array(0)

  def extractImages(filename: String): Array[Int] = {
    val reader: PdfReader = new PdfReader(filename)
    val parser: PdfReaderContentParser = new PdfReaderContentParser(reader)
    val listener: MyImageRenderListener = new MyImageRenderListener()
    slideStarts = Array.fill[Int](reader.getNumberOfPages())(0)
    listener.setPath("murr.pdf")
    for (i <- 1 to reader.getNumberOfPages()) {
      currentPage = i
      parser.processContent(i, listener)
    }
    reader.close()
    return slideStarts 
  }

//  def main(args: Array[String]): Unit = {
//    extractImages("Cloud_Web_Admin_Training_Session_1.pdf")
//  }
}

