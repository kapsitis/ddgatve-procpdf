package lv.ddgatve.procpdf.bookcreator.conf

/**
 * One set of books/PDF-files to be generated; it is read from gilmore-global.xml
 */
class Batch(num:Int, dir:String, label:String, dateString:String, 
    covertext:List[PaperLoc], runningfooter:String, tocTitle:String, books:List[Book]) {
  def printBatch(): Unit = {
    println("" + num + ". " + label) 
  }
  
  def getDir(): String = dir
  def getLabel(): String = label
  def getCovertext(): List[PaperLoc] = covertext
  def getDateString(): String = dateString
  def getRunningfooter(): String = runningfooter
  def getTocTitle(): String = tocTitle
  def getBooks(): List[Book] = books
}