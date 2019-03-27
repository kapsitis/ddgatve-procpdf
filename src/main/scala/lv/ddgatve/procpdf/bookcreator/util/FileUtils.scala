package lv.ddgatve.procpdf.bookcreator.util

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import lv.ddgatve.procpdf.bookcreator.pdf.ExtractImages

/**
 * This is responsible for checking file existence, finding propfile names, etc.
 * 
 * TODO: Consider moving code to "Book"
 */
object FileUtils {

  def printToFile(f: File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }

  def deleteFile(fName: String): Unit = {
    val fileTemp = new File(fName)
    if (fileTemp.exists) {
      fileTemp.delete()
    }
  }

  def fileExists(fName: String): Boolean = {   
    val fileTemp = new File(fName)
    return fileTemp.exists()
  }

  /**
   * Method always writes to a file
   */
  def stringToFile(fName: String, s: String): Unit = {
    val data = Array(s)
    printToFile(new File(fName)) { p =>
      data.foreach(p.println)
    }

  }

  /**
   * Appends ".properties.txt"
   */
  def getPropFileName(pptFile: String): String = {
    pptFile.replaceFirst("""\.pptx$""", ".properties.txt")
  }

  def getPdfFileName(pptFile: String): String = {
    pptFile.replaceFirst("""\.pptx$""", ".pdf")
  }

  def getOverflows(pptFile: String): List[Int] = {
    val pdfFile = getPdfFileName(pptFile)
    val startingSlides = ExtractImages.extractImages(pdfFile).toList
    
    val theSum = startingSlides.foldLeft(0)(_ + _)
        
    val sections = PptSections.getSections(pptFile)
    val pageRanges = sections map {x => x._2}
    val cumSums = pageRanges.scanLeft(0)(_+_)
    val oo = Array.fill[Int](pageRanges.size)(0)
    var theCounter = 0
    for (i <- 1 to startingSlides.size) {
      for (sect <- 1 to pageRanges.size) {
        if (theCounter >= cumSums(sect-1) && theCounter < cumSums(sect)) {
          oo(sect - 1) = oo(sect - 1) + (1 - startingSlides(i-1))
        }        
      }
      theCounter = theCounter + startingSlides(i-1)
    }    
    return oo.toList
  }

  def getPropString(pptFile: String): String = {
    val sections = PptSections.getSections(pptFile)
    val overflows = getOverflows(pptFile)
    val lines = for (ii <- 0 until sections.size) yield {
      val seqNum = ii + 1
      val sTitle = sections(ii)._1
      val sNumSlides = sections(ii)._2
      val sOverflow = overflows(ii)
      val line1 = f"section$seqNum%02d.title=$sTitle%s"
      val line2 = f"section$seqNum%02d.slides=$sNumSlides%s"
      val line3 = f"section$seqNum%02d.overflow=$sOverflow%s"
      (line1 + "\r\n" + line2 + "\r\n" + line3)
    }
    val longLine = lines.foldLeft("")(_ + "\r\n" + _)
    val result = if (longLine.startsWith("\r\n")) { longLine.substring(2) } else { longLine }
    return result
  }

  /**
   * Return 'true', if the file was actually created
   */
  def createPropFile(pptFile: String): Boolean = {
    val propFileName = getPropFileName(pptFile)
    val propString = getPropString(pptFile)
    val result = !fileExists(propFileName)
    if (result) {
      stringToFile(propFileName, propString)
    }
    return result
  }
}

