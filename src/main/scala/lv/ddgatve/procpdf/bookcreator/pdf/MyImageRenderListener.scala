package lv.ddgatve.procpdf.bookcreator.pdf

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

class MyImageRenderListener extends RenderListener {

  var path = ""

  def setPath(myPath: String): Unit = {
    path = myPath
  }

  def beginTextBlock(): Unit = {
  }

  def endTextBlock(): Unit = {
  }

  def renderImage(renderInfo: ImageRenderInfo): Unit = {
    try {
      var filename: String = "temp.bin"
      var os: FileOutputStream = null
      val image: PdfImageObject = renderInfo.getImage()
      if (image == null) return
      ExtractImages.slideStarts(ExtractImages.currentPage - 1) = 1
      os = new FileOutputStream(filename)
      os.write(image.getImageAsBytes())
      os.flush()
      os.close()
    } catch {
      case ioe: IOException => {
//        println("Check in MyImageRenderListener (page " +
//          ExtractImages.currentPage + "): " + ioe.getMessage())
         // ioe.printStackTrace(System.out)
      }

      case e: Exception => { 
//        println("Check in MyImageRenderListener (page " +      
//        ExtractImages.currentPage + "): " + e.getMessage())
        //e.printStackTrace(System.out)
      }
    }
  }

  def renderText(renderInfo: TextRenderInfo): Unit = {
  }
}

