package lv.ddgatve.procpdf.books

import _root_.java.io.FileOutputStream

import _root_.com.itextpdf.text.{ Document, PageSize }

import _root_.com.itextpdf.text.pdf.{
  PdfCopy,
  PdfReader,
  PdfWriter
}

object PdfMerge {

  /**
   * @param args
   * @throws IOException
   * @throws DocumentException
   */
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: PdfMerge result.pdf f1.pdf(1-5) f2.pdf...")
      System.err.println("  At least 2 command-line arguments required")
      System.exit(0)
    }

    val ddd = new Document();
    ddd.setPageSize(PageSize.LETTER);
    val copy = new PdfCopy(ddd, new FileOutputStream(args(0)));
    ddd.open();

//    System.out.println("A4.getHeight() = "
//      + PageSize.A4.getHeight());

    for (arg <- args.tail) {
      val argArr = arg.split("""(\(|\))""")
      val reader = new PdfReader(argArr(0))
      val interval = {
        if (argArr.length > 1) {
          Console.err.println("Splitting")
          val nums = argArr(1).split("-") map { x => x.toInt }
          (nums(0), nums(1))
        } else {
          Console.err.println("Non-Splitting")
          (1, reader.getNumberOfPages)
        }
      }
      Console.err.println("interval is " + interval)

      for (j <- interval._1 to interval._2) {
        val imported = copy.getImportedPage(reader, j)
        copy.addPage(imported)
      }
    }
    ddd.close()
  }
}
