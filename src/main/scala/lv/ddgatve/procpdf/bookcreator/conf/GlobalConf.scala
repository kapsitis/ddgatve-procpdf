package lv.ddgatve.procpdf.bookcreator.conf

import scala.xml.XML

/**
 * Read global config file into a data structure
 */
class GlobalConf(confFileName: String) {

  val global = XML.loadFile("gilmore-global.xml")

  val comment = (global \ "comment").head.text 

  val resourceDir = "resources"

  var count = 0
  val batches = for (bb <- global \ "batch") yield {
    count = count + 1
    val theDir = bb.attribute("dir").head.text
    val theLabel = bb.attribute("label").head.text
    val theDateString = (bb \ "date").head.text 
    val covertext = for (btext <- bb \ "covertext" \ "text") yield {
      val paperLoc = btext.attribute("paperLoc").head.text
      val text = btext.head.text
      new PaperLoc(paperLoc, text)
    }
    val runningfooter = (bb \ "runningfooter").head.text.trim()

    val tocTitle = bb.attribute("tocTitle") match {
      case Some(x) => x.head.text.toString
      case None => "Table of Contents"
    }

    
    val bookList = for (theBook <- bb \ "book") yield {
      val paper = theBook.attribute("paper").head.text
      val result = theBook.attribute("result").head.text
      val sectionstart = theBook.attribute("sectionstart") match {
        case Some(x) => x.head.text.toInt
        case None => 7
      }
      val tocPages = theBook.attribute("tocPages") match {
        case Some(x) => x.head.text.toInt
        case None => 7
      }
      
      
      val items = for (theItem <- theBook \ "item") yield {
        val itemType = theItem.attribute("type").head.text
        
        //val itemFile = dataItems \ "@file" find { _.text }
        val itemFile = theItem.attribute("file") match {
          case Some(x) => x.head.text
          case None => ""
        }
        val itemBreak = theItem.attribute("break") match {
          case Some(x) => x.head.text
          case None => ""
        }
        val itemContent = theItem.attribute("content") match {
          case Some(x) => x.head.text
          case None => ""
        }
        val itemPdfFile = theItem.attribute("pdfFile") match {
          case Some(x) => x.head.text
          case None => ""
        }
        val itemPrefix = theItem.attribute("prefix") match {
          case Some(x) => x.head.text
          case None => ""
        }
        val itemTitle = theItem.attribute("title") match {
          case Some(x) => x.head.text
          case None => ""
        }
        val itemMyId = theItem.attribute("myId") match {
          case Some(x) => x.head.text
          case None => ""
        }
        val itemParentId = theItem.attribute("parentId") match {
          case Some(x) => x.head.text
          case None => ""
        }
        Map("type" -> itemType, 
            "file" -> itemFile, 
            "break" -> itemBreak, 
            "content" -> itemContent, 
            "pdfFile" -> itemPdfFile,
            "prefix" -> itemPrefix, 
            "title" -> itemTitle,
            "myId" -> itemMyId, 
            "parentId" -> itemParentId)

      }
      new Book(paper, result, sectionstart, tocPages, items.toList)
    }
    new Batch(count, theDir, theLabel, theDateString, covertext.toList, runningfooter, tocTitle, bookList.toList)
  }

  val entries =
    (for (entry <- global \\ "entry") yield {
      val theKey = entry.attribute("key").get.text
      val theVal = entry.head.text
      (theKey, theVal)
    }).toList

  def printBatches(): Unit = {
    for (batch <- batches) {
      batch.printBatch
    }
  }

  /**
   * Get list of labels/directories for all possible batches (to print out menu)
   */
  def getLabDir(): List[(String, String)] = {
    val result = for (batch <- batches) yield {
      (batch.getDir, batch.getLabel)
    }
    result.toList
  }
  
  def getCurrentBatch(n: Int): Batch = {
    return batches(n-1)
  }
  
  def getDateStr(n: Int): String = {
    return getCurrentBatch(n).getDateString()
  }
  
  def getRunningFooter(n: Int): String = {
    return getCurrentBatch(n).getRunningfooter()
  }
  
  def getTocTitle(n: Int): String = {
    return getCurrentBatch(n).getTocTitle()
  }

  /**
   * Return output directory for the n-th batch
   */
  def getBaseDir(n: Int): String = {
    getCurrentBatch(n).getDir()
  }

  /**
   * Get all PPT filepaths from the n-th batch and k-th book
   */
//  def getPptFiles(n: Int, k: Int): List[String] = {
//    val currentBook = batches(n - 1).getBooks()(k - 1)
//    val allItems = currentBook.getItems() map { item =>
//      {
//        val itemFile = item("file")
//        val itemType = item("type")
//        if (itemType.equals("powerpoint")) { itemFile } else { "" }        
//      }
//    }
//    val result = allItems filter {!_.equals("")} 
//    return result
//  }
}

