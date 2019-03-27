package lv.ddgatve.procpdf.postprocess

import java.io.{ FileOutputStream, IOException }
import com.itextpdf.text.{ Document, PageSize, Rectangle }
import com.itextpdf.text.pdf.{ PdfContentByte, PdfImportedPage, PdfReader, PdfWriter }
import scala.xml._
import com.itextpdf.text.pdf.PdfTemplate

object PdfCorrectingWriter {

  /**
   * Utility to create a standardized version of some scanning result with
   * each page properly rotated.
   */
  def main(args: Array[String]): Unit = {
    if (args.size < 2) {
      Console.err.println("Usage: PdfCorrectingWriter <pdf-file> <transforms-xml>")
      System.exit(0)
    }
    val inFile = args(0)
    val outFile = inFile.substring(0, inFile.length - 4) + ".UL.pdf"

    val document = new Document()

    println("A4.getHeight() = " + PageSize.A4.getHeight)
    println("leftMargin = " + document.leftMargin)
    println("rightMargin = " + document.rightMargin)
    println("topMargin = " + document.topMargin)
    println("bottomMargin = " + document.bottomMargin)

    val reader = new PdfReader(inFile)
    val numPages = reader.getNumberOfPages
    println("numpages = " + numPages)
    val W = PageSize.A4.getWidth
    val H = PageSize.A4.getHeight

    val writer = PdfWriter.getInstance(document, new FileOutputStream(outFile))
    document.setPageSize(PageSize.A4)
    document.setMargins(36f, 36f, 36f, 36f)
    document.open

    val cb: PdfContentByte = writer.getDirectContent
    val scaleRatio = 1.1f

    val transformXml = XML.loadFile(args(1))

    val defaultNode = (transformXml \\ "page" find { _.attribute("id").get.text.equals("default") }).get
    val defaultRotate = defaultNode.attribute("rotate").get.text.toFloat
    val defaultX = defaultNode.attribute("x").get.text.toFloat
    val defaultY = defaultNode.attribute("y").get.text.toFloat

    val evenNode = (transformXml \\ "page" find { _.attribute("id").get.text.equals("even") }).get
    val evenRotate = evenNode.attribute("rotate").get.text.toFloat
    val evenX = evenNode.attribute("x").get.text.toFloat
    val evenY = evenNode.attribute("y").get.text.toFloat

    val oddNode = (transformXml \\ "page" find { _.attribute("id").get.text.equals("odd") }).get
    val oddRotate = oddNode.attribute("rotate").get.text.toFloat
    val oddX = oddNode.attribute("x").get.text.toFloat
    val oddY = oddNode.attribute("y").get.text.toFloat
    
    for (i <- 1 to numPages) {
      val imported: PdfTemplate = writer.getImportedPage(reader, i)
      val currentNodeOpt = transformXml \\ "page" find { _.attribute("id").get.text.equals("" + i) }
      val currentNode = currentNodeOpt match {
        case Some(node) => {
          println("custom node for i = " + i)
          node
        }
        case _ => {
          defaultNode
        }
      }
      val currentRotate = (if (i % 2 == 0) evenRotate else oddRotate)+
      (currentNode.attribute("rotate") match {
        case Some(node) => {
          println("custom rotate for i = " + i + " is " + node.text.toFloat)
          node.text.toFloat
        }
        case _ => {
          0
        }
      })
      val currentX = (if (i % 2 == 0) evenX else oddX)+
       (currentNode.attribute("x") match {
        case Some(node) => {
          node.text.toFloat
        }
        case _ => {
          defaultX
        }
      })
      val currentY = (if (i % 2 == 0) evenY else oddY) +
      (currentNode.attribute("y") match {
        case Some(node) => {
          node.text.toFloat
        }
        case _ => {
          defaultY
        }
      })

      //      val rect = new Rectangle(W/10,H/10,9*W/10,9*H/10)
      //      writer.setCropBoxSize(rect)
      System.out.println("for i = " + i + " rotate is " + currentRotate)
      System.out.println("for i = " + i + " rotate0 is " + (if (i % 2 == 0) evenRotate else oddRotate))
      
      val scaleFactor = 1.15F
      val a = scaleFactor * Math.cos(currentRotate).toFloat
      val b = scaleFactor * Math.sin(currentRotate).toFloat
      val c = -scaleFactor * Math.sin(currentRotate).toFloat
      val d = scaleFactor * Math.cos(currentRotate).toFloat

      cb.addTemplate(imported, a, b, c, d, currentX * scaleFactor, currentY * scaleFactor)
      document.newPage
    }
    document.close
  }
}