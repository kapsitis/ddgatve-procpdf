package lv.ddgatve.procpdf.bookcreator.pdf

import java.io.FileInputStream
import java.io.FileOutputStream
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper;

object Compress {
  def main(args: Array[String]): Unit = {
    val reader = new PdfReader(new FileInputStream(
        "/Training/Role-Based-Curriculum/4-Administrator/Data/Books/Student-Editions/US/AP-DATA-v8.1-Admin-Student-BG00115-uncompressed.pdf"))
    val stamper = new PdfStamper(reader, new FileOutputStream(
        "/Training/Role-Based-Curriculum/4-Administrator/Data/Books/Student-Editions/US/AP-DATA-v8.1-Admin-Student-BG00115-compressed.pdf"))
    val total: Int = reader.getNumberOfPages() + 1;
    for (i <- 1 to total) {
      reader.setPageContent(i + 1, reader.getPageContent(i + 1));
    }
    stamper.setFullCompression();
    stamper.close();
  }
}