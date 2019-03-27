package lv.ddgatve.procpdf.postprocess

import java.io.{ FileOutputStream, IOException }
import com.itextpdf.text.{ Document, PageSize, Rectangle }
import com.itextpdf.text.pdf.{ PdfContentByte, PdfImportedPage, PdfReader, PdfWriter }
import com.itextpdf.text.BaseColor

/**
 * Attempt to crop each page by 25f.
 */
object WhiteMargins {

  def main(args: Array[String]): Unit = {
    if (args.size < 1) {
      Console.err.println("Usage: WhiteMargins <pdf-file>")
      System.exit(0)
    }
    val inFile = args(0)
    val outFile = if (inFile.endsWith(".pdf")) {
      inFile.substring(0, inFile.length - 4) + ".MARGINS.pdf"
    } else {
      inFile + ".MARGINS.pdf"
    }

    val yellowFile = "resources/a4-yellow.pdf"

    val document = new Document()
    val widthA4 = PageSize.A4.getWidth
    val heightA4 = PageSize.A4.getHeight
    val x0 = 25.0f
    val y0 = 25.0f
    val xN = widthA4 - x0
    val yN = heightA4 - y0
    val crop = new Rectangle(x0, y0, xN, yN)

    val reader = new PdfReader(inFile)
    val numPages = reader.getNumberOfPages
    println("numpages = " + numPages)
    println("widthA4 = " + widthA4 + ", heightA4 = " + heightA4)
    val writer = PdfWriter.getInstance(document, new FileOutputStream(outFile))
    document.setPageSize(PageSize.A4)
    document.setMargins(36f, 36f, 36f, 36f)
    document.open
    val cb: PdfContentByte = writer.getDirectContent

    for (x <- 1 to numPages) {

      val imported = writer.getImportedPage(reader, x)
      cb.addTemplate(imported, 1f, 0f, 0f, 1f, 0f, 0f)
      cb.saveState()
      cb.setColorStroke(BaseColor.WHITE)
      cb.setColorFill(BaseColor.WHITE)
      cb.rectangle(0f,0f,widthA4,25f)
      cb.fillStroke()
      cb.stroke()
      cb.rectangle(0f,0f,25f,heightA4)
      cb.fillStroke()
      cb.stroke()
      cb.rectangle(widthA4-25f,0f,widthA4,heightA4)
      cb.fillStroke()
      cb.stroke()
      cb.rectangle(0f,heightA4-25f,widthA4,heightA4)
      cb.fillStroke()
      cb.stroke()
      cb.restoreState()
      document.newPage
    }
    document.close
  }
}