package lv.ddgatve.procpdf.bookcreator.pptx

class WikiRun(var text:String, var isBold:Boolean, var isItalic:Boolean) {
  val normText = text
  if (normText.matches("""\s+""")) {
    isBold = false
    isItalic = false
  }
  
  override def toString():String = {
    val prefix = if (normText.startsWith(" ")) " " else ""
    val suffix = if (normText.endsWith(" ")) " " else ""
    if (isBold) {
      return prefix + "**" + normText.trim() + "**" + suffix
    } else if (isItalic) {
      return prefix + "*" + normText.trim() + "*" + suffix
    } else {
      return normText
    }
  }
  

}    
