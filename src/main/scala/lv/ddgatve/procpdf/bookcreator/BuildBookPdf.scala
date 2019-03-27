package lv.ddgatve.procpdf.bookcreator

import java.io.FileOutputStream
import java.util.{ ArrayList => JavaArrayList }
import java.util.{ HashMap => JavaHashMap }
import java.util.{ List => JavaList }
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.SimpleBookmark
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.PdfStamper
import scala.xml.XML
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfOutline
import com.itextpdf.text.pdf.PdfAction
import com.itextpdf.text.Chunk
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.Element
import com.itextpdf.text.Phrase
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Font
import com.itextpdf.text.BaseColor
import java.util.{ ArrayList => JavaArrayList }
import java.util.{ HashMap => JavaHashMap }
import java.util.{ List => JavaList }
import scala.xml.NodeSeq.seqToNodeSeq
import scala.xml.Node
import scala.xml.Elem
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import lv.ddgatve.procpdf.bookcreator.util.PathTemplate
import lv.ddgatve.procpdf.bookcreator.conf.GlobalConf
import lv.ddgatve.procpdf.bookcreator.conf.Book
import lv.ddgatve.procpdf.bookcreator.util.FileUtils
import lv.ddgatve.procpdf.bookcreator.util.PdfProperties
import lv.ddgatve.procpdf.bookcreator.pdf.BuildTextParagraphs



object BuildBookPdf {

  val PAGE_NUM_FONT = 9
  var pdfInFile = ""
  var pdfOutFile = ""

  def scanLeft[a, b](xs: Iterable[a])(s: b)(f: (b, a) => b) =
    xs.foldLeft(List(s))((acc, x) => f(acc(0), x) :: acc).reverse

  def mkList(arg: String, globalConf: GlobalConf): List[Int] = {
    val newArg = PathTemplate.replaceAll(arg, globalConf.entries)
    newArg.split(",").toList map { x => x.toInt }
  }

  def makeCover(texts: List[String], locations: List[List[Int]]): Unit = {
    val buildStamper = new BuildStamper(pdfInFile, pdfOutFile)
    buildStamper.stampList(texts, locations, List.fill(texts.size)(BaseColor.WHITE))
  }

  /**
   * Concatenates array of PDF files 'src' with the file 'dest' as result.
   * Tries to preserve bookmarks by merging them.
   */
  def manipulatePdf(src: Array[String],
    pages: List[(Int, Int)],
    theBook: Book,
    dest: String,
    marks: List[List[(String, Boolean)]],
    runningFooter: String,
//    tocTitle: String,
    prefixSize: Int): Unit = {

    println("** src.length = " + src.length)
    println("** pages.length = " + pages.size)
    println("** marks.length = " + marks.flatten.length)

    // TODO: Avoid manual config of page numbers
    val startpageMap = Map("cover" -> "1",
      "copyright" -> "3",
      "table-of-contents" -> "5",
      "chapters" -> ("" + theBook.getSectionstart()))
//    val startpageMap = Map("cover" -> "1",
//      "copyright" -> "1",
//      "table-of-contents" -> "100",
//      "chapters" -> ("" + theBook.getSectionstart()))

      
      
    var document: Document = new Document()

    var copy: PdfCopy = new PdfCopy(document, new FileOutputStream(dest))

    document.open()

    // attempt to make new bookmarks
    val cb: PdfContentByte = copy.getDirectContent()
    val rootOutline: PdfOutline = cb.getRootOutline()

    // TODO: Outline should not be hardcoded; should follow actual document!
    
    
    val oline1: PdfOutline = new PdfOutline(rootOutline,
      PdfAction.gotoLocalPage(startpageMap("cover"), false), "Cover")
    val oline2: PdfOutline = new PdfOutline(oline1,
      PdfAction.gotoLocalPage(startpageMap("copyright"), false), "Statement")
    val oline3: PdfOutline = new PdfOutline(oline1,
      PdfAction.gotoLocalPage(startpageMap("table-of-contents"), false),
      "Table of Contents")


    // TODO: get rid of "tocTitles"
//          val tocTitles = List(
//          "Overview and Getting Started",
//          "Asset and Endpoint Management", 
//          "Managing Audit & Protect Policies",
//          "Managing Compliance and Governance Policies",
//          "Forensic Analysis in Forcepoint CASB",
//          "Configuring Notifications and Generating Reports"
//          )
    
    var bkmkCount = 0
    var totalCount = 0
    var currentRoot: PdfOutline = null
    for (markIdx <- 0 until marks.length) {
      for (subIdx <- 0 until marks(markIdx).size) {
        if (marks(markIdx)(subIdx)._2 == false) {
          if (!theBook.getItems()(markIdx)("myId").equals("NA") && subIdx == 0) {
            
            println("********* (" + markIdx + "," + subIdx + ") *********")
            val oline1: PdfOutline = new PdfOutline(rootOutline,
              PdfAction.gotoLocalPage("" +
                (startpageMap("chapters").toInt +
                  bkmkCount - theBook.getTocPages()), false), /* tocTitles(markIdx-4) */ marks(markIdx)(subIdx)._1 )
            // TODO - make a properly configurable bookmark hierarchy - up to 3 levels
            currentRoot = oline1
            //currentRoot = rootOutline
          } else {
            val oline1: PdfOutline = new PdfOutline(currentRoot,
              PdfAction.gotoLocalPage("" +
                (startpageMap("chapters").toInt +
                  bkmkCount + 1 - theBook.getTocPages()), false), marks(markIdx)(subIdx)._1)
          }

          bkmkCount = bkmkCount + 1
        }
        totalCount = totalCount + 1
      }
    }


    var reader: PdfReader = null
    var page_offset: Int = 0
    var n: Int = 0
    // Create a list for the bookmarks
    val bookmarks: JavaArrayList[JavaHashMap[String, Object]] = new JavaArrayList
    var tmp: JavaList[JavaHashMap[String, Object]] = new JavaArrayList
    var globalPageCount = 0
    var bookmarkTargets = 0
    for (i <- 0 until src.length) {
      //println("READER IS " + src(i))
      reader = new PdfReader(src(i))
      reader.consolidateNamedDestinations()
      n = reader.getNumberOfPages

      val pageRange = pages(i)
      val adjustedRange = if (pageRange == (0, 0)) {
        (1, n)
      } else {
        (pageRange._1, pageRange._2)
      }

      // TODO: pad the "marks" with the preamble stuff
      val marksFlat = marks.flatten
//      if (i - prefixSize < 19) {
      if (i < prefixSize || i - prefixSize >= marksFlat.length || marksFlat(i - prefixSize)._2 == false) {
        //        println("(i+1) = " + (i + 1) + ", (bmt + 1) = " + (bookmarkTargets + 1))
        for (page <- adjustedRange._1 to math.min(n, adjustedRange._2)) {
          globalPageCount = globalPageCount + 1
          val pdfImpPage = copy.getImportedPage(reader, page)
          if (page == adjustedRange._1) {
            // ********************************************
            // ********* BOOKMAR TARGETS
            // ********************************************
            val stamp: PdfCopy.PageStamp = copy.createPageStamp(pdfImpPage)
            val chunk00 = new Chunk("" + (bookmarkTargets + 1),
              FontFactory
                .getFont(FontFactory.HELVETICA, 6,
                  Font.NORMAL, BaseColor.WHITE))
            
           
            chunk00.setLocalDestination("" + (bookmarkTargets + 1))
            val phrase00 = new Phrase(chunk00)
            ColumnText.showTextAligned(
              stamp.getUnderContent(), Element.ALIGN_LEFT,
              phrase00,
              36.0f, 792.0f, 0);
            stamp.alterContents();
          }

          if (globalPageCount >= startpageMap("table-of-contents").toInt) {
            val stamp: PdfCopy.PageStamp = copy.createPageStamp(pdfImpPage)
            val pb2 = stamp.getOverContent()

            pb2.saveState()
            
            /* START */
            // TODO: Two white rectangles not needed?
            /*
            pb2.setColorStroke(BaseColor.WHITE)
            pb2.setColorFill(BaseColor.WHITE)

            val EvenX0 = 290f
            val EvenY0 = 16f
            val OddX0 = 320f
            val OddY0 = 16f
            val RectHeight = 20f
            val RectWidth = 30f
            //          if (globalPageCount % 2 == 1) {
            pb2.moveTo(EvenX0, EvenY0)
            pb2.lineTo(EvenX0, EvenY0 + RectHeight)
            pb2.lineTo(EvenX0 + RectWidth, EvenY0 + RectHeight)
            pb2.lineTo(EvenX0 + RectWidth, EvenY0)
            pb2.closePathFillStroke()
            pb2.restoreState()

            pb2.saveState()
            pb2.setColorStroke(BaseColor.WHITE)
            pb2.setColorFill(BaseColor.WHITE)
            //          } else {
            pb2.moveTo(OddX0, OddY0)
            pb2.lineTo(OddX0, OddY0 + RectHeight)
            pb2.lineTo(OddX0 + RectWidth, OddY0 + RectHeight)
            pb2.lineTo(OddX0 + RectWidth, OddY0)
            //          }
            pb2.closePathFillStroke()
            
           */
            
            /* END */
            
            
            pb2.restoreState()

            
            
            val stampText = if (globalPageCount % 2 == 1) {
              "" + globalPageCount + "\u00A0\u00A0\u00A0\u00A0\u00A0" + runningFooter
            } else {
              runningFooter + "\u00A0\u00A0\u00A0\u00A0\u00A0" + globalPageCount
            }

            val chunk00 = new Chunk(stampText,
              FontFactory
                .getFont(FontFactory.HELVETICA, PAGE_NUM_FONT,
                  Font.NORMAL, BaseColor.BLACK))
            val phrase00 = new Phrase(chunk00)

           // TODO; remove next line
//           if (globalPageCount != 48 && globalPageCount != 107 && globalPageCount != 114) {
            if (globalPageCount % 2 == 1) {
              ColumnText.showTextAligned(
                stamp.getOverContent(), Element.ALIGN_LEFT,
                phrase00,
                91.0f, 41.0f, 0)
            } else {
              ColumnText.showTextAligned(
                stamp.getOverContent(), Element.ALIGN_RIGHT,
                phrase00,
                521.0f, 41.0f, 0)
            }
            
            
            
            
            
            //val pb2 = stamp.getOverContent()
            pb2.saveState()
            pb2.setColorStroke(new BaseColor(12, 33, 115))
            pb2.setColorFill(new BaseColor(12, 33, 115))

            val TriangleSide = 6f
            val TriangleHeight = 3 * Math.sqrt(3).toFloat
            val numWidth = new Chunk("" + globalPageCount,
              FontFactory
                .getFont(FontFactory.HELVETICA, PAGE_NUM_FONT,
                  Font.NORMAL, BaseColor.BLACK)).getWidthPoint()
            val RightX0 = 512f
            // KAP 2017-04-06
            val RightY0 = 41f
            val LeftX0 = 101f
            // KAP 2017-04-06
            val LeftY0 = 41f
            

            if (globalPageCount % 2 == 1) {
              pb2.moveTo(LeftX0 + numWidth, LeftY0)
              pb2.lineTo(LeftX0 + numWidth, LeftY0 + TriangleSide)
              pb2.lineTo(LeftX0 + numWidth - TriangleHeight, LeftY0 + TriangleSide / 2)
            } else {
              pb2.moveTo(RightX0 - numWidth, RightY0)
              pb2.lineTo(RightX0 - numWidth, RightY0 + TriangleSide)
              pb2.lineTo(RightX0 - numWidth + TriangleHeight, RightY0 + TriangleSide / 2)
            }
            
            pb2.closePathFillStroke()
            pb2.restoreState()

            stamp.alterContents()
            
            // TODO remove brace
           //}
          }
          copy.addPage(pdfImpPage)
        }
        bookmarkTargets = bookmarkTargets + 1
      }
      
//      } //TODO: KAP QUICKFIX
      copy.freeReader(reader)
      tmp = SimpleBookmark.getBookmark(reader)

      if (tmp != null) {
        //        SimpleBookmark.exportToXML(tmp, new FileOutputStream("resources/T"+i+".xml"), "UTF-8", true)
        Console.out.println("tmp.size is " + tmp.size())
        for (i <- 0 until tmp.size()) {
          Console.out.println("   " + tmp.get(i))
        }

        SimpleBookmark.shiftPageNumbers(tmp, page_offset, null)
        bookmarks.addAll(tmp);
      }
      page_offset += adjustedRange._2 - adjustedRange._1 + 1

    }
    // by default opens the bookmarks in the left navbar
    // TODO KAP (should not hide bookmarks)
    copy.setViewerPreferences(PdfWriter.PageModeUseOutlines)
    
    document.close();

    val destReader: PdfReader = new PdfReader(dest)
    destReader.makeRemoteNamedDestinationsLocal()
    val stamper: PdfStamper = new PdfStamper(destReader, new FileOutputStream(dest + ".pdf"))
    stamper.close()
  }

  def getStartpageMap(data: Node): Map[String, String] = {
    val subElements = List("cover", "copyright", "table-of-contents", "chapters")
    val startPages = subElements map { x =>
      (data \\ x)(0).attribute("startpage").get(0).text
    }
    (subElements zip startPages).toMap
  }

  def procPdf( //data: scala.xml.Node,
    dateString: String,
    globalConf: GlobalConf,
    selectedN: Int,
    currentBook: Int): Unit = {

    //val pptList = globalConf.getPptFiles(selectedN, currentBook)
    val theBatch = globalConf.getCurrentBatch(selectedN)
    val theBook = theBatch.getBooks()(currentBook - 1)
    val pptList = theBook.getFNamesOfType("powerpoint")
    //val pdfList = theBook.getFNamesOfType("multipage-pdf")
    val pdfList = theBook.getFNamesPrefixesOfType("multipage-pdf")

    val pptAndPdfList = theBook.getFNamesOfList(List("powerpoint", "multipage-pdf"))
    //val pptAndPdfList = theBook.getFNamesOfType("powerpoint")
    
    println("pptAndPdfList.toArray (len=" + pptAndPdfList.toArray.size + ") is " + pptAndPdfList)
    
    
    for (pptFile <- pptList) {
      val propFileName = FileUtils.getPropFileName(pptFile)
      // TODO: normally - should not delete prop file (just during testing)
      //FileUtils.deleteFile(propFileName)
      FileUtils.createPropFile(pptFile)
    }

    val coverFile = if (theBook.getFNamesOfType("cover").size > 0) {
      theBook.getFNamesOfType("cover").head
    } else { "" }

    val theTexts = for (paperLoc <- theBatch.getCovertext()) yield {
      paperLoc.getText()
    }
    val theExpandedTexts = theTexts map (x =>
      PathTemplate.replaceAll(x, List(("revision", BuildDateUtil.getDateString(dateString)))))
    val theLocations = for (paperLoc <- theBatch.getCovertext()) yield {
      paperLoc.getQuadruple(globalConf, theBook.getPapersize())
    }

    pdfInFile = coverFile
    pdfOutFile = globalConf.getBaseDir(selectedN) + "/my-cover.pdf"

     if (theBook.getItemsOfType("cover").size > 0) {
    makeCover(theExpandedTexts, theLocations)
     }

    var chList = List[String]()
    var chTitles = List[List[(String, Boolean)]]()
    var startpageMap = Map[String, String]()

    var rangeSeq = List[(Int, Int)]()

    // Check, if there is "message" item
    if (theBook.getItemsOfType("message").size > 0) {
      // Build CopyRight file
      val messageItem = theBook.getItemsOfType("message").head
      val paragraphs = messageItem("content").split(",").toList
      val btp = new BuildTextParagraphs(null,
        globalConf.getBaseDir(selectedN) + "/copyright.pdf")
      val copyrightLines = paragraphs map { x =>
        PathTemplate.getEntryVal(globalConf.entries, x)
      }
      btp.processStatic(copyrightLines.map(_.replaceAll("""(?m)\s+""", " ")))

    }

    val preambleItems = theBook.getItemsOfTypes(List("cover", "empty", "message", "table-of-contents"))
    val listPrefixSeq = for (item <- preambleItems) yield {
      item("type") match {
        case "cover" => globalConf.getBaseDir(selectedN) + "/my-cover.pdf"
        case "empty" => globalConf.resourceDir + "/empty-page.pdf"
        case "message" => globalConf.getBaseDir(selectedN) + "/copyright.pdf"
        case "table-of-contents" => globalConf.getBaseDir(selectedN) + "/toc_tmp.pdf"
      }
    }

    val deltaProperties = theBook.getDeltaProperties()
    val deltaSeq = deltaProperties._1.flatten
    val chapterDeltas = deltaProperties._2.flatten
    val cumulativeDeltas = List.fill(listPrefixSeq.size)((0, 0)) ++ chapterDeltas

    val listPrefix = listPrefixSeq.toList
    // default value for chapterstartPage
    var chapterstartPage = 7

    if (theBook.getFNamesOfType("powerpoint").size > 0 ||
      theBook.getFNamesOfType("multipage-pdf").size > 0) {
      //chTitles = theBook.getSectionTitles()
      chTitles = theBook.getBookmarkTree()

      //      val nestedChapterList = for (pptFile <- pptList) yield {
      //        val propFileName = FileUtils.getPropFileName(pptFile)
      //        val pdfFileName = FileUtils.getPdfFileName(pptFile)
      //        val npp = new PdfProperties(propFileName)
      //        val pptSectionData = npp.getPptSections()
      //        List.fill(pptSectionData.size)(pdfFileName)
      //      }
      //      chList = nestedChapterList.flatten

      val nestedChapterList = for (ppFile <- pptAndPdfList) yield {
        if (ppFile.endsWith(".pptx") || ppFile.endsWith(".ppt")) {
          val propFileName = FileUtils.getPropFileName(ppFile)
          val pdfFileName = FileUtils.getPdfFileName(ppFile)
          val npp = new PdfProperties(propFileName)
          val pptSectionData = npp.getPptSections()
          List.fill(pptSectionData.size)(pdfFileName)
        } else {
          List.fill(1)(ppFile + ".pdf")
        }
      }
      
      println("nestedChapterList.size is " + nestedChapterList.size)
      println("nestedChapterList is " + nestedChapterList)
      chList = nestedChapterList.flatten
      println("chList.size is " + chList.size)

      //      println("CONFDIR" + globalConf.getBaseDir(selectedN) + "/" + pdfList(0) + ".p.pdf")      
      for (pdfFile <- pdfList) {
        // This *.pdf.pdf is deleted in class "Main"
        val bStamper = new BuildStamper(pdfFile._1, pdfFile._1 + ".pdf")
        bStamper.stampList(List(pdfFile._2), List(List(1, 70, 709, 35)),
          List(new BaseColor(41,157,55)))
      }

      val nestedCounts = theBook.countPagesNested()

      rangeSeq = theBook.getFromTo(nestedCounts)

      val bToc = new BuildToc(globalConf.getBaseDir(selectedN) + "/toc_tmp.pdf")
      chapterstartPage = theBook.getSectionstart()

      // TODO: dirty workaround
      val tocTitles = chTitles.flatten filter { _._2 == false } map { _._1 }
//      val tocTitles = List(
//          "Overview and Getting Started",
//          "Asset and Endpoint Management", 
//          "Managing Audit & Protect Policies",
//          "Managing Compliance and Governance Policies",
//          "Forensic Analysis in Forcepoint CASB",
//          "Configuring Notifications and Generating Reports"
//          )
      val counts = (nestedCounts map { _._2 }).flatten
      println("theCounts are " + counts.toList)
      bToc.processStatic(tocTitles, counts.toList, chapterstartPage, globalConf.getTocTitle(selectedN))
    }

    println("chList.toArray (len=" + chList.toArray.size + ") is " + chList)
    
    val bigList = if (theBook.getItems().size > 1) { listPrefix ++ chList } else { listPrefix }
    val pageRanges = List.fill(listPrefix.length)((0, 0)) ++ rangeSeq

    val expandedResultFile = theBook.getExpandedResult(dateString)
    println("bigList.toArray (len=" + bigList.toArray.size + ") is " + bigList)
    println("pageRanges      (len=" + pageRanges.size + ") is " + pageRanges)
    if (theBook.getItems().size > 1) {
      manipulatePdf(bigList.toArray,
        pageRanges,
        theBook,
        globalConf.getBaseDir(selectedN) + "/" + expandedResultFile,
        chTitles,
        globalConf.getRunningFooter(selectedN),
        listPrefixSeq.size);
    }
  }
}

