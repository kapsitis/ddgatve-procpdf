package lv.ddgatve.procpdf.bookcreator

import java.io._
import lv.ddgatve.procpdf.bookcreator.util.PptSections

/**
 * This object is responsible for creating [file].properties.txt
 * files in the respective directories (if it does not exist already).
 * If there is a "properties.txt" file for a given PPT, this object
 * does nothing.
 */
object MakePptProperties {

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }
  

  def makePropertiesFile(pptFile: String): Unit = {
    val sections = PptSections.getSections(pptFile)

    val data = Array("Five", "strings", "in", "a", "file!")
    printToFile(new File("example.txt")) { p =>
      data.foreach(p.println)
    }

  }

}