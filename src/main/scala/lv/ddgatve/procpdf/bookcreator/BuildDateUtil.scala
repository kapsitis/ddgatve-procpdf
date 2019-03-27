package lv.ddgatve.procpdf.bookcreator

import lv.ddgatve.procpdf.bookcreator.util.PathTemplate

object BuildDateUtil {
  
  def getDateString(dd: String): String = {
    val result = new StringBuffer("")
    val yearStr = dd.substring(0, 4)
    val yearCode = yearStr match {
      case "2013" => "BD"
      case "2014" => "BE"
      case "2015" => "BF"
      case "2016" => "BG"
      case "2017" => "BH"
      case "2018" => "BJ"
      case "2019" => "BK"
      case "2020" => "CA"
      case _ => "XX"
    }
    result.append(yearCode)

    val monthList = List(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    val endResult = try {
      val monthNum = dd.substring(5, 7).toInt
      val dateNum = dd.substring(8, 10).toInt
      val suffix = "%05d".format(
        (monthList.slice(0, monthNum - 1).foldLeft(0)(_ + _) + dateNum))
      result.append(suffix)
      result.toString
    } catch {
      case e: Exception => {
        Console.err.println("Wrong number format '" + dd + "' (YYYY-MM-DD expected)")
        dd
      }
    }
    endResult
  }
  

  def getCode(revisionStr: String, dd: String): String = {
    val endResult = getDateString(dd)
//    val templ = new PathTemplate(revisionStr)
    PathTemplate.replaceAll(revisionStr, List(("revision",endResult)))    
  }

  def main(args: Array[String]): Unit = {
    println(getCode("REV:${ref}","2014-01-14"))
  }
}