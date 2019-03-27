package lv.ddgatve.procpdf.books

import java.lang.{ Object => JavaObject }
import java.io.FileOutputStream
import java.io.IOException
import java.sql.SQLException
import java.util.{ ArrayList => JavaArrayList }
import java.util.{ HashMap => JavaHashMap }
import java.util.{ List => JavaList }
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.SimpleBookmark
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.PdfDestination
import com.itextpdf.text.pdf.PdfStamper

object ConcatenateBookmarks {

  /**
   * Concatenates array of PDF files 'src' with the file 'dest' as result.
   * Tries to preserve bookmarks by merging them.
   */
  def manipulatePdf(src: Array[String], dest: String): Unit = {
    var document: Document = new Document()
    var copy: PdfCopy = new PdfCopy(document, new FileOutputStream(dest))
    document.open()
    var reader: PdfReader = null
    var page_offset: Int = 0
    var n: Int = 0
    // Create a list for the bookmarks
    val bookmarks: JavaArrayList[JavaHashMap[String, Object]] = new JavaArrayList
    var tmp: JavaList[JavaHashMap[String, Object]] = new JavaArrayList
    for (i <- 0 until src.length) {
      reader = new PdfReader(src(i))
      reader.consolidateNamedDestinations()
      n = reader.getNumberOfPages
      Console.out.println("numPages is " + n)
      for (page <- 0 until n) {
        copy.addPage(copy.getImportedPage(reader, page + 1));
      }
      copy.freeReader(reader);
      tmp = SimpleBookmark.getBookmark(reader)

      if (tmp != null) {
        //        SimpleBookmark.exportToXML(tmp, new FileOutputStream("resources/T"+i+".xml"), "UTF-8", true)
        Console.out.println("tmp.size is " + tmp.size())
        for (i <- 0 until tmp.size()) {
          Console.out.println("   " + tmp.get(i))
        }

        SimpleBookmark.shiftPageNumbers(tmp, page_offset, null)
        bookmarks.addAll(tmp);
      }
      page_offset += n;
    }
    // set the concatenated bookmarks
    copy.setOutlines(bookmarks);
    // by default opens the bookmarks in the left navbar
    copy.setViewerPreferences(PdfWriter.PageModeUseOutlines)
    document.close();

    val destReader: PdfReader = new PdfReader(dest)
    destReader.makeRemoteNamedDestinationsLocal()
    val stamper: PdfStamper = new PdfStamper(destReader, new FileOutputStream(dest + ".pdf"))
    stamper.close()
  }

  def main(args: Array[String]): Unit = {
    manipulatePdf(
      Array(
        "resources/dss-v780-201-student-guide-cover.pdf",
        "resources/empty-page.pdf",
        "resources/copyright2013.pdf",
        "resources/empty-page.pdf",
        "resources/dss-v780-201-student-guide.p1.pdf",
        "resources/dss-v780-201-student-guide.p2.pdf"),

      "resources/dss-v780-201-student-guide.c.pdf");
  }
}

