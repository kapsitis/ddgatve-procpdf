package lv.ddgatve.procpdf.books

import _root_.java.io.{ FileOutputStream, IOException }
import _root_.com.itextpdf.text.{ Document, PageSize, Rectangle }
import _root_.com.itextpdf.text.pdf.{ PdfContentByte, PdfImportedPage, PdfReader, PdfWriter }

object BookWriter {

  /**
   * 
   */
  def main(args: Array[String]): Unit = {

    val document = new Document()

    println("LETTER.getHeight() = " + PageSize.LETTER.getHeight)
    println("leftMargin = " + document.leftMargin)
    println("rightMargin = " + document.rightMargin)
    println("topMargin = " + document.topMargin)
    println("bottomMargin = " + document.bottomMargin)

    val readerCover = new PdfReader("resources/cover.pdf")
    val readerCopyright = new PdfReader("resources/wsga-v76-201-exam-guide.pdf")
    val readerGuide = new PdfReader("resources/wsga-v77-201-exam-guide-v3.pdf")
    
    val numPages = readerGuide.getNumberOfPages + 4
    println("numpages = " + numPages)

    val writer = PdfWriter.getInstance(document, new FileOutputStream("resources/wsga-v77-201-exam-guide2.pdf"))
    document.setPageSize(PageSize.LETTER)
    //    document.setMargins(36f, 36f, 36f, 36f)
    document.open

    val cb: PdfContentByte = writer.getDirectContent

    val pageSeq = 0 until numPages

    pageSeq foreach { x =>
      {
        val imported = x match {
          case 0 => writer.getImportedPage(readerCover, 1)
          case 1 => writer.getImportedPage(readerCopyright, 4)
          case 2 => writer.getImportedPage(readerCopyright, 3)
          case 3 => writer.getImportedPage(readerCopyright, 4)
          case _ => writer.getImportedPage(readerGuide, x-3)
        }
//        val imported = writer.getImportedPage(reader, x)
        cb.addTemplate(imported, 1f, 0f, 0f, 1f, 0f, 0f)
        document.newPage
      }
    }

    document.close
  }
}