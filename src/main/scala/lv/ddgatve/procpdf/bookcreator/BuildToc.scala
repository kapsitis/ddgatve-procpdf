package lv.ddgatve.procpdf.bookcreator

import java.io.FileOutputStream
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.Rectangle
import com.itextpdf.text.Element
import com.itextpdf.text.PageSize
import com.itextpdf.text.Chunk
import com.itextpdf.text.pdf.BaseFont

class BuildToc(var pdfOutFile: String) {
  
  val isLargeFont = true
  
  val tocFontSize = if (isLargeFont) { 14 } else { 11 } 
  val tocFontSkip = if (isLargeFont) { 16 } else { 11 } 
  val tocMinHeight = if (isLargeFont) { 35.0f } else { 25.0f }
  val tocTableMinHeight = if (isLargeFont) { 50.0f } else { 25.0f }

  def scanLeft[a, b](xs: Iterable[a])(s: b)(f: (b, a) => b) =
    xs.foldLeft(List(s))((acc, x) => f(acc(0), x) :: acc).reverse

  val logger = org.apache.log4j.Logger
    .getLogger(classOf[BuildToc])

  def addDots(arg: String): String = {
    var dottedLine = arg
    for (i <- 0 to 100) {
      val ch = new Chunk(dottedLine, FontFactory
        .getFont(FontFactory.HELVETICA, tocFontSize,
          Font.NORMAL, BaseColor.BLACK))
      //        Console.out.println("widthPoint is " + ch.getWidthPoint())
      if (ch.getWidthPoint() > 355.0f) {
        return dottedLine
      }
      dottedLine += " .";
    }
    dottedLine
  }

  def processStatic(staticData: List[String], pageCounts: List[(Int,Boolean)], chapterStartPage: Int, title:String): Unit = {

    // TODO: chapterStartPageShift sometimes is 1 (cloud web) or 2
    val chapterStartPageShift = 0
    // Is there a preface chapter?
    val hasPreface = false
    // should we enumerate the TOC items
    val hasTocEnumeration = false

    val document = new Document();

    document.setPageSize(PageSize.LETTER);
    document.setMargins(36, 36, 54, 72)

    PdfWriter.getInstance(document,
      new FileOutputStream(pdfOutFile))

    document.open();

    val table = new PdfPTable(3)
    table.setWidthPercentage(81.0f)
    // TODO: first item should be 35

    if (hasTocEnumeration) {
      table.setWidths(Array(35, 520, 45))
    } else {
      table.setWidths(Array(0, 555, 45))
    }

    
    val p1 = new Paragraph(title,
      FontFactory
        .getFont(FontFactory.HELVETICA_BOLD, tocFontSkip,
          Font.NORMAL, BaseColor.BLACK))
    p1.setIndentationLeft(50.0f)
    p1.setLeading(20.0f, 1.0f)
    val cell1 = new PdfPCell(p1)
    cell1.setColspan(3)
    cell1.setBorderColor(new BaseColor(12, 33, 115))
    cell1.setBorderWidth(1.4f)
    cell1.setBorder(Rectangle.BOTTOM)
    cell1.setBorder(Rectangle.TOP)
    cell1.setHorizontalAlignment(Element.ALIGN_LEFT)
    cell1.setMinimumHeight(tocTableMinHeight)
    cell1.setPaddingBottom(10.0f)
    cell1.setVerticalAlignment(Element.ALIGN_BOTTOM)
   // table.addCell(cell1);
    table.addCell(cell1);

    val p0 = new Paragraph(" ")
    val c0 = new PdfPCell(p0)
    c0.setBorder(Rectangle.TOP)
    c0.setBorderWidth(1.4f)
    c0.setBorderColor(new BaseColor(12, 33, 115))
    c0.setMinimumHeight(20.0f)
    table.addCell(c0)
    table.addCell(c0)
    table.addCell(c0)

    // The first 6 pages are used for cover, copyright and TOC
    val barePageCounts = pageCounts filter { _._2 == false } map { _._1 }
    val pageNums = scanLeft(barePageCounts)(chapterStartPageShift + chapterStartPage)(_ + _)
    for (idx <- 0 until staticData.length) {

      val chNum = if (hasPreface) { idx } else { 1 + idx }

      
      val pIdx = if (hasTocEnumeration && (!hasPreface || idx > 0)) {
        new Paragraph("" + chNum + ".\u00A0", FontFactory
          .getFont(FontFactory.HELVETICA, tocFontSize,
            Font.NORMAL, BaseColor.BLACK))
      } else {
        new Paragraph("\u00A0", FontFactory
          .getFont(FontFactory.HELVETICA, tocFontSize,
            Font.NORMAL, BaseColor.BLACK))
      }

      val cellIdx = new PdfPCell(pIdx)
      cellIdx.setHorizontalAlignment(Element.ALIGN_RIGHT)

      cellIdx.setMinimumHeight(tocMinHeight)
      cellIdx.setBorder(Rectangle.NO_BORDER)
      table.addCell(cellIdx)

      val dottedLine = addDots(staticData(idx).trim.replaceAll("\\s+", " "))
            val NEWFONT = "resources/arial.ttf"            
      val bf = BaseFont.createFont(NEWFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

      
      
      val p2 = if (dottedLine.indexOf(" Intro") == -1 || isLargeFont) {
        new Paragraph(dottedLine, new Font(bf, tocFontSize, Font.NORMAL, BaseColor.BLACK))
      } else {
        new Paragraph(dottedLine, new Font(bf, tocFontSize, Font.BOLD, BaseColor.BLACK))
      }

      val cell2 = new PdfPCell(p2)
      cell2.setHorizontalAlignment(Element.ALIGN_JUSTIFIED_ALL)
      cell2.setMinimumHeight(tocMinHeight)
      cell2.setBorder(Rectangle.NO_BORDER)
      table.addCell(cell2)

      
      val p3 = new Paragraph("" + (pageNums(idx)),
        FontFactory
          .getFont(FontFactory.HELVETICA, tocFontSize,
            Font.NORMAL, BaseColor.BLACK))
      p3.setAlignment(Element.ALIGN_RIGHT)
      val cell3 = new PdfPCell(p3)
      cell3.setHorizontalAlignment(Element.ALIGN_RIGHT)
      cell3.setMinimumHeight(tocMinHeight)
      cell3.setBorder(Rectangle.NO_BORDER)

      table.addCell(cell3)
    }

    document.add(table)

    document.close();

  }

}