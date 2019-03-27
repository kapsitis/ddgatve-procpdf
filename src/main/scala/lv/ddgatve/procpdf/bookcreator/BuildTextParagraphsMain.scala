package lv.ddgatve.procpdf.bookcreator

import lv.ddgatve.procpdf.bookcreator.pdf.BuildTextParagraphs

// TODO: This class should be deleted
object BuildTextParagraphsMain {

  def main(args: Array[String]): Unit = {
    val btp = new BuildTextParagraphs("resources/empty-page.pdf",
      "resources/cop_tmp.pdf")
    val staticData = List(
        """CC-BY Creative Commons.""",
        """This is some licence  text""".replaceAll("""(?m)\s+""", " "))

    btp.processStatic(staticData)
  }
}