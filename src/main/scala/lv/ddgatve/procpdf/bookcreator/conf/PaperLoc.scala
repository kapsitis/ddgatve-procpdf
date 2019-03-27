package lv.ddgatve.procpdf.bookcreator.conf

import lv.ddgatve.procpdf.bookcreator.util.PathTemplate

/**
 * This class is responsible for remembering static text snippets with
 * their locations (that are relative to the paper size).
 */
class PaperLoc(template: String, text: String) {
  def getTemplate(): String = template
  def getText(): String = text

  def getQuadruple(gConf: GlobalConf, paper: String): List[Int] = {
    val theKey = PathTemplate.replaceAll(getTemplate(), List(("paper", paper)))
    val theValue = PathTemplate.getEntryVal(gConf.entries, theKey)
    theValue.split(",").toList map { x => x.toInt }
  }
}
