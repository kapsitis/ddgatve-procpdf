package lv.ddgatve.procpdf.bookcreator.html

import com.itextpdf.text.Image
import com.itextpdf.text.pdf.codec.Base64
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider

// helper for CssCreator
class Base64ImageProvider extends AbstractImageProvider {

  override def retrieve(src: String): Image = {
    val pos = src.indexOf("base64,");

    if (src.startsWith("data") && pos > 0) {
      val img = Base64.decode(src.substring(pos + 7));
      return Image.getInstance(img);
    } else {
      return Image.getInstance(src);
    }

  }

  override def getImageRootPath(): String = {
    return null;
  }

}