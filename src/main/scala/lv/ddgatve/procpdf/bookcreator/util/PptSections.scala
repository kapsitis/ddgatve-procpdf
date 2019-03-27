package lv.ddgatve.procpdf.bookcreator.util

import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Extract section info from PPTX file (using its ZIP structure
 * and regex patterns on "ppt/presentation.xml"
 */
object PptSections {

  /**
   * Return list of section titles and the number of pages in every section
   */
  def extractFromZip(filePath: String, sub: String): String = {
//    println("extractFromZip is " + filePath)
    val rootzip = new java.util.zip.ZipFile(filePath)
    import collection.JavaConverters._
    val entries = rootzip.entries.asScala
    entries foreach { e =>
      if (e.getName.endsWith(sub)) {
        val entryName = e.getName
        val content = scala.io.Source.
          fromInputStream(rootzip.getInputStream(e)).getLines().mkString("\n")
        return content
      }
    }
    return ""
  }

  def getSections(filePath: String): List[(String, Int)] = {
    val content = extractFromZip(filePath, "ppt/presentation.xml")
    if (content.equals("")) {
      return List(("", 0))
    } else {
      val sectPattern = """<p14:section name="([^"]+)"[^<>]*>""".r
      val slidePattern = """<p14:sldId id="[0-9]+"/>""".r
      val allSections = 
        (for (m <- sectPattern findAllMatchIn content) yield {
          (m.group(1).toString(),m.start(0).toInt)
        }).toList
      
      val ourSlides = (for (ii <- 0 until allSections.length) yield {
        //println("sect is " + allSections(ii)._1)
        val startIdx = allSections(ii)._2
        val endIdx = if (ii < allSections.length - 1) {
          allSections(ii + 1)._2
        } else {
          content.length()
        }
        val snippet  = content.substring(startIdx, endIdx)
        slidePattern.findAllMatchIn(snippet).size
      }).toList
      
      return (allSections map {x => x._1}).zip(ourSlides)
    }
    return List()
  }
}  

