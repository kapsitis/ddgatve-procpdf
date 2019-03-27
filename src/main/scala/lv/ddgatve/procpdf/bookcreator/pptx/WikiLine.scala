package lv.ddgatve.procpdf.bookcreator.pptx

import scala.collection.mutable.ListBuffer
import org.apache.poi.xslf.usermodel.XSLFTextParagraph
import scala.collection.JavaConverters.asScalaBufferConverter

class WikiLine(var para: XSLFTextParagraph, var enumType:Int) {   
   val runs: ListBuffer[WikiRun] = ListBuffer.empty[WikiRun]
   for (run <- para.getTextRuns.asScala.toList) {
     runs += new WikiRun(run.getRawText, run.isBold(), run.isItalic())
   }
   
   override def toString():String = {
     val result = runs.toList.foldLeft("")((a,b) => a + b.toString())
     if (enumType == 0) {
       return result
     } else if (enumType == 1) {
       if (result.matches("""^(\* )?\s*$""")) {
         return ""
       } else if (result.matches("""^\* .*""")) {
         return result
       } else {
         return "* " + result
       }
     } else if (enumType == 2) {
       if (result.matches("""^([1-9]?[0-9]\. )?\s*$""")) {
         return ""
       } else if (result.matches("""^[1-9]?[0-9]\. .*""")) {
         return result
       } else {
         return "1. " + result 
       }
     } else if (enumType == 3) {
       if (result.matches("""^([a-z]\. )?\s*$""")) {
         return ""
       } else if (result.matches("""^[a-z]\. .*""")) {
         return result
       } else {
         return "a. " + result
       }
     } else {
       return result
     }
   }  
}