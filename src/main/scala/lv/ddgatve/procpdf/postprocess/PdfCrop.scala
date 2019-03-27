package lv.ddgatve.procpdf.postprocess
import com.itextpdf.text.PageSize
import com.itextpdf.text.Rectangle
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfTemplate
import java.io.FileReader
import com.itextpdf.text.pdf.PdfReader

/**
 * Magnify by Q, and crop.
 */
object PdfCrop {
  val Q = 1.15f

  //  val RESOURCE = "file.png"
  //
  //  def createTemplate(content: PdfContentByte, rect: Rectangle,
  //    factor: Int): PdfTemplate = {
  //    val template: PdfTemplate = content.createTemplate(
  //      rect.getWidth(), rect.getHeight());
  //    template.concatCTM(factor, 0, 0, factor, 0, 0);
  //    val reader: FileReader = new FileReader(RESOURCE);
  //    var c: Int = 0;
  //    while (c > -1) {
  //      c = reader.read
  //      template.setLiteral(c.toChar);
  //    }
  //    return template;
  //  }

  def main(args: Array[String]): Unit = {
    if (args.size < 2) {
      Console.err.println("Usage: PdfCrop <old-pdf> <new-pdf>")
      System.exit(0)
    }

    val w = PageSize.A4.getWidth
    val h = PageSize.A4.getHeight
    val marginX = 36f
    val marginY = marginX * h / w
    val rect = new Rectangle(0, 0, w, h)
    //    val crop = new Rectangle(marginX, h * (1 - 1 / Q), w / Q - marginX, h)
    val crop = new Rectangle(marginX, marginY, w - marginX, h - marginY)
    val reader = new PdfReader(args(0))
    val numPages = reader.getNumberOfPages

    val document = new Document
    val writer = PdfWriter.getInstance(document, new FileOutputStream(args(1)))
    document.open

    val content = writer.getDirectContent

    for (i <- 1 to numPages) {
      val template: PdfTemplate = writer.getImportedPage(reader, i)
      template.setBoundingBox(crop)
      content.addTemplate(template, 1f, 0f, 0f, 1f, 0f, 0f)
      document.newPage
    }
    document.close
  }

}