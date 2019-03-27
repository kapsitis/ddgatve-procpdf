package lv.ddgatve.procpdf.bookcreator.pptx

import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.poi.xslf.util.PPTX2PNG

import org.apache.poi.xslf.usermodel.XMLSlideShow

object PptxToPng {

  /*
	def main(args:Array[String]):Unit =  {
			val is = new FileInputStream("sample.pptx");      
			val ppt = new XMLSlideShow(is);
			is.close();
			val zoom = 2.0;
			val at = new AffineTransform();
			at.setToScale(zoom, zoom);
			val pgsize = ppt.getPageSize();
			val slide = ppt.getSlides();

			val img = new BufferedImage(Math.ceil(pgsize.width*zoom).toInt,
					Math.ceil(pgsize.height*zoom).toInt, BufferedImage.TYPE_INT_RGB);
			val graphics = img.createGraphics();

			graphics.setTransform(at);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

			// Draw first page in the PPTX. First page starts at 0 position
			slide.get(5).draw(graphics);

			val out = new FileOutputStream("sample.png");  
			javax.imageio.ImageIO.write(img, "png", out);
			out.close();
			ppt.close();
			System.out.println("DONE");
	}
	*/
  
  def main(args:Array[String]):Unit = {
    PPTX2PNG.main(Array("-outdir","sample", "sample.pptx"))
  }

}