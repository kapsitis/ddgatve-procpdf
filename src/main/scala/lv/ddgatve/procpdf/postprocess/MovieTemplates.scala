package lv.ddgatve.procpdf.postprocess

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
 
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
 
/**
 * Draws a time table to the direct content using lines and simple shapes,
 * adding blocks representing a movies.
 */
class MovieTemplates {
 
    /** The resulting PDF. */
    val RESULT
        = "results/part1/chapter03/movie_templates.pdf";
 
    /**
     * Creates a PDF document.
     * @param filename the path to the new PDF document
     * @throws    DocumentException 
     * @throws    IOException
     */
    def createPdf(filename:String): Unit = {
    	// step 1
        val document = new Document(PageSize.A4.rotate());
        // step 2
        val writer:PdfWriter
            = PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        // step 4
        val over:PdfContentByte = writer.getDirectContent();
        val under:PdfContentByte = writer.getDirectContentUnder();
        /*
        try {
            DatabaseConnection connection = new HsqldbConnection("filmfestival");
            locations = PojoFactory.getLocations(connection);
            PdfTemplate t_under = under.createTemplate(
                    PageSize.A4.getHeight(), PageSize.A4.getWidth());
            drawTimeTable(t_under);
            PdfTemplate t_over = over.createTemplate(
                    PageSize.A4.getHeight(), PageSize.A4.getWidth());
            drawTimeSlots(t_over);
            drawInfo(t_over);
            List<Date> days = PojoFactory.getDays(connection);
            List<Screening> screenings;
            int d = 1;
            for (Date day : days) {
                over.addTemplate(t_over, 0, 0);
                under.addTemplate(t_under, 0, 0);
                drawDateInfo(day, d++, over);
                screenings = PojoFactory.getScreenings(connection, day);
                for (Screening screening : screenings) {
                    drawBlock(screening, under, over);
                    drawMovieInfo(screening, over);
                }
                document.newPage();
            }
            connection.close();
        }
        catch(SQLException sqle) {
            sqle.printStackTrace();
            document.add(new Paragraph("Database error: " + sqle.getMessage()));
        }
        */
        // step 5
        document.close();
    }
 
    /**
     * Main method creating the PDF.
     * @param    args    no arguments needed
     * @throws DocumentException 
     * @throws IOException 
     */
    def main(args:Array[String]): Unit = {
        new MovieTemplates().createPdf(RESULT);
    }
}
 