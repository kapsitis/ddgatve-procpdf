package lv.ddgatve.procpdf.bookcreator

import scala.xml.XML
import java.util.Scanner
import java.io.File
import lv.ddgatve.procpdf.bookcreator.util.PathTemplate
import lv.ddgatve.procpdf.bookcreator.conf.GlobalConf
import lv.ddgatve.procpdf.bookcreator.util.FileUtils

object Main {

  def main(args: Array[String]): Unit = {

    val gConf = new GlobalConf("gilmore-global.xml")
    println(gConf.comment)
    gConf.printBatches()
    val globalEntries = gConf.entries

    val labelsDirectories = gConf.getLabDir()
    val in = new Scanner(System.in)
    val n = in.nextInt();
    println("You entered '" + n + "'")

    val theGilmoreDir = gConf.getBaseDir(n)
    val dateString = gConf.getDateStr(n)

    for (k <- 1 to gConf.getCurrentBatch(n).getBooks().size) {
      val fileToProcess = gConf.getCurrentBatch(n).getBooks()(k - 1).getResult()
      val newName = PathTemplate.replaceAll(fileToProcess,
        List(("revision", BuildDateUtil.getDateString(dateString))))
      BuildBookPdf.procPdf(dateString, gConf, n, k)

      // if it is only cover
      if (fileToProcess.indexOf("solo") >= 0) {
        val cover = new File(theGilmoreDir, "my-cover.pdf")
        cover.renameTo(new File(theGilmoreDir, newName))
      }
      for {
        files <- Option(new File(theGilmoreDir).listFiles)
        file <- files if (file.getName.endsWith(".pdf.pdf")
          || file.getName.equals("toc_tmp.pdf")
          || file.getName.equals("copyright.pdf")
          || file.getName.equals("my-cover.pdf"))
      } file.delete()           
      FileUtils.deleteFile("temp.bin")
      
      val theBook = gConf.getCurrentBatch(n).getBooks()(k - 1)
      val pdfList = theBook.getFNamesOfType("multipage-pdf")
      for (pdfFile <- pdfList) {
        (new File(pdfFile + ".pdf")).delete()
      }
    }
  }
}

