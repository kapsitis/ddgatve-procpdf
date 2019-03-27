package lv.ddgatve.procpdf.bookcreator.conf

class PptSectionData(npk:Int, title:String, numSlides:Int, overflow:Int, skip:Boolean) {
  
  def getNpk(): Int = npk
  def getTitle(): String = title
  def getNumSlides(): Int = numSlides
  def getOverflow(): Int = overflow 
  def getSkip(): Boolean = skip
  
  
}