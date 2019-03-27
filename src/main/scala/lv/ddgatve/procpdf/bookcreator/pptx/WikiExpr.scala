package lv.ddgatve.procpdf.bookcreator.pptx

import scala.collection.mutable.ListBuffer
import org.apache.poi.xslf.usermodel.XSLFTextParagraph
import org.apache.poi.sl.usermodel.AutoNumberingScheme

class WikiExpr(val pars:List[XSLFTextParagraph]) {

	val wikiLines = pars map { 
		par => {
			val isBullet = par.isBullet()
					val isEnum = par.getAutoNumberingScheme
					var enumType = 0
					if (isEnum != null) {
						if (isEnum.toString().equals("arabicPeriod")) { 
							enumType = 2 
						} else if (isEnum.toString().equals("alphaLcPeriod")) { 
							enumType = 3 
						} else { 
							enumType = 3 
						}
					} else if (isBullet) {
						enumType = 1  
					} else if (par.getText.startsWith("* ")) {
						enumType = 1
					} else if (par.getText.matches("""^[1-9]?[0-9]\. .*""")) {
						enumType = 2
					} else if (par.getText.matches("^[a-z]\\. .*")) {
						enumType = 3
					}

//			println("    (%s:%s:%s):'%s'".format(enumType, isBullet, isEnum, par.getText))
			new WikiLine(par, enumType)
		} 
	}

	override def toString():String = {
			val sb = new StringBuffer()
					var prevEnumType = 0 
					for (wikiLine <- wikiLines) {
						if (wikiLine.enumType != prevEnumType) {
							sb.append("\r\n")
						}
						sb.append(wikiLine.toString() + "\r\n")
						prevEnumType = wikiLine.enumType
					}
			return sb.toString()
	}
	
	def toTexEscaped():String = {
	  val result = toString()
	  val newResult = result.replaceAllLiterally("ģ", "\\v{g}")
	  return newResult
	}
	
	
}
