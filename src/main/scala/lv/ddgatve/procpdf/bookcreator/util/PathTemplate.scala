package lv.ddgatve.procpdf.bookcreator.util

/**
 * This object is responsible for placeholder string replacement
 */
object PathTemplate {
  
  
  // Replace same template with values from a list; return the resulting list
  def replaceAsList(path:String, key:String, vals:List[String]):List[String] = {
    for (theVal <- vals) yield {
      path.replaceAll("""\$\{""" + key + """\}""", theVal)
    }  
  }
  
  // Replace template with multiple key-value pairs; return single string
  def replaceAll(path:String, replacements:List[(String,String)]):String = {
    var result = path
    for (ref <- replacements) {
      result = result.replaceAll("""\$\{""" + ref._1 + """\}""", ref._2)
    } 
    result
  }
  
  def getEntryVal(globalEntries:List[(String,String)], key:String):String = {
    for ((a,b) <- globalEntries) {
      if (a == key) return b
    }
    return "NA"
  }
}