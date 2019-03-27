package lv.ddgatve.procpdf.bookcreator.pdf

import java.io.FileOutputStream
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.BaseFont

class BuildTextParagraphs(var pdfInFile: String, var pdfOutFile: String) {
	val logger = org.apache.log4j.Logger
			.getLogger(classOf[BuildTextParagraphs])

			def processStatic(staticData: List[String]): Unit = {
					val document = new Document();
					document.setPageSize(PageSize.LETTER);
					PdfWriter.getInstance(document, new FileOutputStream(pdfOutFile));
					document.open();

//					for (line <- staticData) {
//						document.add(new Paragraph(line,
//								FontFactory
//								.getFont(FontFactory.TIMES, 12,
//										Font.NORMAL, BaseColor.BLACK)));
//					}

					
					val NEWFONT = "resources/times.ttf"
					val bf = BaseFont.createFont(NEWFONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
					for (line <- staticData) {
						document.add(new Paragraph(line,
										new Font(bf, 12, Font.NORMAL, BaseColor.BLACK)
						));
					}

					document.close();
	}

}