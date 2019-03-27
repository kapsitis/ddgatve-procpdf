package lv.ddgatve.procpdf.bookcreator.pptx

import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

import lv.ddgatve.procpdf.bookcreator.BuildDateUtil

import scala.collection.JavaConverters.asScalaBufferConverter

import org.apache.poi.xslf.usermodel.XMLSlideShow

import org.apache.poi.hslf.usermodel.HSLFSlideShow 


/*
 http://stackoverflow.com/questions/24873725/how-to-get-pptx-slide-notes-text-using-apache-poi
 http://stackoverflow.com/questions/21523267/how-to-convert-pptx-files-to-jpg-or-png-for-each-slide-on-linux
 http://stackoverflow.com/questions/35054419/does-apache-poi-api-support-pptx2png-conversion-in-java-programs-for-pptx-gene
 */
object PowerPointFilter {   
	  
  val destDir = "/home/kalvis/Documents/DLP-course-v85/" 
  val srcDir = "/home/kalvis/Documents/DLP-course-v85/"
//  val destDir = "/Users/kalvis.apsitis/workspace/ddgatve-problems/imo-numbertheory/books/"
//  val srcDir = "/Users/kalvis.apsitis/workspace/ddgatve-problems/imo-numbertheory/slides/"
  val fileNames = List(
      "DataAdmin1-v85"
      //"nt-jun01-divisibility"
      )

	val includeBibliography = false
		
	def main(args: Array[String]): Unit = {
	  
	  val labSb = new StringBuffer()
    for (fileName <- fileNames) {
      
      
					val ppt = new XMLSlideShow(new FileInputStream(srcDir + fileName + ".pptx"))
							val sb = new StringBuffer()

							val pgsize = ppt.getPageSize()

							val slides = ppt.getSlides().asScala
							for (i <- 0 until slides.length) {
								//      println("Slide %d".format(i + 1))
								if (i > 0) {
									sb.append("\r\n\r\n\\newpage\r\n\r\n")
								}
								//sb.append("\\setcounter{subsection}{0}\r\n\r\n")
								
								val currentSlide = slides(i)
								
								
								if (currentSlide.getComments() != null && currentSlide.getComments().getNumberOfComments > 0) {
								  val comments = currentSlide.getComments()
								  for (j <- 0 until comments.getNumberOfComments) {
								    val com = comments.getCommentAt(j).getText
								    println("'" + fileName + "'#" + (i+1) + " ***COM*** " + com)
								  }
								}
								
								val tempNotes = currentSlide.getNotes
														val pars = if (tempNotes != null) {
								  tempNotes.getTextParagraphs.asScala.toList.map(_.asScala.toList).flatten
								} else {
								  List()
								}
								
								// ignore hidden slides
								if (pars.length > 0 && 
								    pars(0).getTextRuns.asScala.toList.size > 0 &&  
								    pars(0).getTextRuns.asScala.toList(0).getRawText.indexOf("<!--HIDDEN-->") > -1) {
								  println("'" + fileName + "'#" + (i+1) + " ***HIDE*** ")
								} else {
								  sb.append("![](%s/Slide%02d.png".format(fileName, i + 1) + 
								      "){ width=100% height=40.5% }\r\n\r\n\\vspace{5mm}\r\n")
								  							var prevIsBullet = false 
								  if (pars.length < 1) {
								  } else {
									  val expr = new WikiExpr(pars)
										sb.append(expr.toTexEscaped())
									}
								}
								  
							}
					if (includeBibliography) {
						val theBibliography = scala.io.Source.fromFile(destDir + fileName + ".bib.txt").mkString
								sb.append(theBibliography)
					}
					Files.write(Paths.get(destDir + fileName + ".txt"),
							sb.toString().getBytes(StandardCharsets.UTF_8))
    }
	}
}
