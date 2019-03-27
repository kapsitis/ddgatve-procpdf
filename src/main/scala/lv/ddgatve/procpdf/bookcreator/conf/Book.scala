package lv.ddgatve.procpdf.bookcreator.conf

import lv.ddgatve.procpdf.bookcreator.BuildDateUtil
import lv.ddgatve.procpdf.bookcreator.util.PathTemplate
import lv.ddgatve.procpdf.bookcreator.util.PdfProperties
import lv.ddgatve.procpdf.bookcreator.util.FileUtils
import com.itextpdf.text.pdf.PdfReader

class Book(papersize: String, result: String, sectionstart: Int, tocPages: Int, items: List[Map[String, String]]) {

  def getPapersize(): String = papersize
  def getResult(): String = result
  def getSectionstart(): Int = sectionstart
  def getTocPages(): Int = tocPages
  def getItems(): List[Map[String, String]] = items

  def getDeltaProperties(): (List[List[Int]], List[List[(Int, Int)]]) = {
    val deltaSeqNested = for (fName <- getFNamesOfType("powerpoint")) yield {
      val props = new PdfProperties(FileUtils.getPropFileName(fName))
      props.getPptSections() map (_.getOverflow())
    }

    val deltaCumsumNested = (deltaSeqNested map { x => x.scanLeft(0)(_ + _) })

    /**
     * Return a list of lists with raw overflow/delta values
     * (exactly replicating the property files of "powerpoint" type items)
     */
    val chapterDeltasNested = deltaCumsumNested map { ss =>
      ({
        (1 to (ss.length - 1)) map ({ i: Int => { (ss(i - 1), ss(i)) }
        })
      }).toList
    }

    val countFragmentPages = for (fName <- getFNamesOfType("powerpoint")) yield {
      val props = new PdfProperties(FileUtils.getPropFileName(fName))
      props.getPptSections() map (_.getOverflow())
    }

    (deltaSeqNested, chapterDeltasNested)
  }

  def getExpandedResult(dateString: String): String = {
    return PathTemplate.replaceAll(getResult(),
      List(("revision", BuildDateUtil.getDateString(dateString))))
  }

  def getFNamesOfType(theType: String): List[String] = {
    val items = for (item <- getItems()) yield {
      if (item("type").equals(theType)) { item("file") } else { "" }
    }
    items filter { !_.equals("") }
  }

  // TODO: merge with getFNamesOfType
  def getFNamesOfList(theTypes: List[String]): List[String] = {
    println("### getItems is " + getItems().size); 
    val items = for (item <- getItems()) yield {
      if (theTypes.contains(item("type"))) { item("file") } else { "" }
    }
    items filter { !_.equals("") }
  }

  def getFNamesPrefixesOfType(theType: String): List[(String, String)] = {
    val items = for (item <- getItems()) yield {
      if (item("type").equals(theType)) { (item("file"), item("prefix")) } else { ("", "") }
    }
    items filter { !_._1.equals("") }
  }

  def getItemsOfType(theType: String): List[Map[String, String]] = {
    val items = for (item <- getItems()) yield {
      if (item("type").equals(theType)) { item } else { null }
    }
    items filter { _ != null }
  }

  def getItemsOfTypes(types: List[String]): List[Map[String, String]] = {
    val items = for (item <- getItems()) yield {
      if (types.contains(item("type"))) { item } else { null }
    }
    items filter { _ != null }
  }

  /**
   * Return the number of pages in every bookmarked segment
   */
  def countPagesNested(): List[(String, List[(Int,Boolean)])] = {
    val stuff = getFNamesOfList(List("powerpoint", "multipage-pdf"))
    val pageNums = for (fName <- stuff) yield {
      if (fName.endsWith(".pptx")) {
        val props = new PdfProperties(FileUtils.getPropFileName(fName))
        val rr = props.getPptSections() map (x => { (x.getOverflow() + x.getNumSlides(), x.getSkip()) })
        //println("fr1(" + fName + ", " + rr + ")")
        (fName, rr)
      } else {
        val reader = new PdfReader(fName)
        //println("fr2(" + fName + ", " + List(reader.getNumberOfPages) + ")")
        (fName, List((reader.getNumberOfPages, false)))
      }
    }
    pageNums
  }

  /**
   * Return raw page ranges (without overflows)
   * TODO: Arguments are never used
   */
  def getFromTo(countPagesNested: List[(String, List[(Int,Boolean)])]): List[(Int, Int)] = {
    val result2 = countPagesNested map { sublist =>
      {
        val allPageCount = sublist._2 map { _._1 }
        val addedChunk = allPageCount.scanLeft(0)(_ + _)
        val subResult = (for (ii <- 0 until sublist._2.size) yield {
          (addedChunk(ii) + 1, addedChunk(ii + 1))
        }).toList
       subResult

      }
    }
    result2.flatten
  }

  def getSkipped(fName: String): List[Boolean] = {
    val props = new PdfProperties(FileUtils.getPropFileName(fName))
    props.getPptSections() map { pptSection =>
      {
        pptSection.getSkip()
      }
    }
  }
  
  

  // TODO: This is obsolete; it only takes into account PPT sections, 
  // /but not the standalone PDFs
//  def getSectionTitles(): List[List[String]] = {
//    //val pptFNames = getFNamesOfType("powerpoint")
//    val pptFNames = getFNamesOfType("powerpoint")
//
//    pptFNames map { pptFName =>
//      {
//        val props = new PdfProperties(FileUtils.getPropFileName(pptFName))
//        props.getPptSections() map { pptSection =>
//          {
//            pptSection.getTitle()
//          }
//        }
//      }
//    }
//  }

  def getBookmarkTree(): List[List[(String,Boolean)]] = {
    //val pptFNames = getFNamesOfType("powerpoint")

    for (item <- getItems()) yield {
      if (item("type").equals("powerpoint")) {
        val skipped = getSkipped(item("file"))
        val fName = item("file")
        val props = new PdfProperties(FileUtils.getPropFileName(fName))
        val pptSections = props.getPptSections().zip(skipped)
        pptSections filter {
          pptSection => pptSection._1.getNumSlides() > 0
        } map { pptSection =>
          {
            (pptSection._1.getTitle(), pptSection._2)
          }
        }
      } else if (item("type").equals("multipage-pdf")) {
        if (item("title").equals("T\u0113mas un uzdevumi")) {
//          List(("MURR",true),("AAAA", false),("BBBB", false),("CCCC", false),("DDDD", false))
//          List(("MURR",false))
          List((item("title"),false))
        } else {
          List((item("title"),false))
        }
      } else {
        List()
      }
    }
  }

}

object Book {
  def main(args: Array[String]): Unit = {
    val gConf = new GlobalConf("gilmore-global.xml")
    val theBatch = gConf.getCurrentBatch(1)
    val theBook = theBatch.getBooks()(0)
    println("booksize is " + theBook.getItems().size)
    val murr = theBook.getBookmarkTree()
    for (sublist <- murr) {
      if (sublist.size < 2) {
      println("sub is " + sublist)
      } else {
        print("sub is ")
        for (subsublist <- sublist) {
          println("    " + subsublist)
        }
      }
    }
  }

}
