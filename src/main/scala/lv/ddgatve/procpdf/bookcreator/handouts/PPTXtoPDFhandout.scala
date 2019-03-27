package lv.ddgatve.procpdf.bookcreator.handouts

import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import org.apache.poi.xslf.usermodel.XMLSlideShow
import lv.ddgatve.procpdf.bookcreator.pptx.WikiExpr
import scala.collection.JavaConverters._

/*
 http://stackoverflow.com/questions/24873725/how-to-get-pptx-slide-notes-text-using-apache-poi
 http://stackoverflow.com/questions/21523267/how-to-convert-pptx-files-to-jpg-or-png-for-each-slide-on-linux
 http://stackoverflow.com/questions/35054419/does-apache-poi-api-support-pptx2png-conversion-in-java-programs-for-pptx-gene
 */
object PptxToPdfHandout {   
	  
  val rootDir = "c:/Training/Role-Based-Curriculum/4-Administrator/CASB/" 
  val fileNames = List(
      "CasbAdminBeta"
      )

	val includeBibliography = false
		
	def main(args: Array[String]): Unit = {

	  val labSb = new StringBuffer()
    for (fileName <- fileNames) {
      
      
					val ppt = new XMLSlideShow(new FileInputStream(rootDir + fileName + ".pptx"))
							val sb = new StringBuffer()

							val pgsize = ppt.getPageSize()

							val slides = ppt.getSlides().asScala
							for (i <- 0 until slides.length) {
//								if (i > 0 && i % 2 == 0) {
//									sb.append("\r\n\r\n\\clearpage\r\n\r\n")
//								}
								
								val currentSlide = slides(i)
								
								
//								if (currentSlide.getComments() != null && currentSlide.getComments().getNumberOfComments > 0) {
//								  val comments = currentSlide.getComments()
//								  for (j <- 0 until comments.getNumberOfComments) {
//								    val com = comments.getCommentAt(j).getText
//								    println("'" + fileName + "'#" + (i+1) + " ***COM*** " + com)
//								  }
//								}
								
//								val tempNotes = currentSlide.getNotes
//														val pars = if (tempNotes != null) {
//								  tempNotes.getTextParagraphs.asScala.toList.map(_.asScala.toList).flatten
//								} else {
//								  List()
//								}
								
								// ignore hidden slides
								
								  sb.append("![](" + fileName + "/Slide%d.png".format(i + 1, i + 1) + "){ width=100% }\r\n\r\n")								
								  
							}
					if (includeBibliography) {
						val theBibliography = scala.io.Source.fromFile(rootDir + fileName + ".bib.txt").mkString
								sb.append(theBibliography)
					}
					Files.write(Paths.get(rootDir + fileName + ".txt"),
							sb.toString().getBytes(StandardCharsets.UTF_8))
    }
	}
}
