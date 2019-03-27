package lv.ddgatve.procpdf.bookcreator.html

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerHelper
 
// Variant of CssCreator
object ParseWithCss {
 
//    val HTML = "resources1/brasil.html"
//    val DEST = "resources1/brasil.pdf"
    val HTML = "lab01.html"
    val DEST = "lab01.pdf" 
    
    def main(args:Array[String]): Unit = {
        val file = new File(DEST)
        //file.getParentFile().mkdirs()
        createPdf(DEST)
    }
 

    def createPdf(file:String) {
        val document = new Document()
        val writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open()
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new FileInputStream(HTML), Charset.forName("UTF-8"))
        document.close()
    }
}