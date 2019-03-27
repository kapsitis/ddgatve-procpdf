package lv.ddgatve.procpdf.bookcreator.pdf

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfWriter;

object ImageDirect {

  def main(args: Array[String]): Unit = {    
    val RESULT = "resources/data-cover-solo-down.pdf"
    val RESOURCE = "resources/data-cover-solo-down.png"

    //val aa = PageSize.LETTER
    val aa = new com.itextpdf.text.Rectangle(7f*72, 8.5f*72)
    println("aa.width = " + aa.getWidth)
    println("aa.height = " + aa.getHeight)
    val document = new Document(aa, 0, 0, 0, 0);
    val writer = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
    writer.setCompressionLevel(0);
    document.open();
    val img = Image.getInstance(RESOURCE);
    img.scaleToFit(aa.getWidth, aa.getHeight)
    img.setAbsolutePosition((aa.getWidth() - img.getScaledWidth()) / 2,
      (aa.getHeight() - img.getScaledHeight()) / 2);
    writer.getDirectContent().addImage(img);
       
//    val p = new Paragraph("Foobar Film Festival", new Font(FontFamily.HELVETICA, 22));
//    p.setAlignment(Element.ALIGN_CENTER);
//    document.add(p);    
    document.close();
  }
}
    
    
  
  
