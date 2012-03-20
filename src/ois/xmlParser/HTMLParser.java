
package ois.xmlParser;


import ois.StringParser;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

/*****************
 * 
 * HTML解析模块
 * @author wxt 
 * 
 *****************/
public abstract class HTMLParser {
	private Parser parser;
	private HtmlPage hPage;
	private StringBean sBean;
	protected String content;
	public HTMLParser(String URL){
		this.init(URL);
	}
	
	public HTMLParser(String URL,String charset){
		this.init(URL,charset);
	}
	
	private void init(String url){
		try {
			parser=new Parser(url);
			hPage = new HtmlPage(parser);
			sBean=new StringBean(); 
			sBean.setReplaceNonBreakingSpaces (true);
			sBean.setCollapse (true);
			sBean.setURL(url);
			content=StringParser.QtoB(sBean.getStrings());
			parser.visitAllNodesWith(hPage);
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	
	private void init(String url,String charset){
		try {
			parser=new Parser(url);
			parser.setEncoding(charset);
			hPage = new HtmlPage(parser);
			sBean=new StringBean();
			sBean.setURL(url);
			content=StringParser.QtoB(sBean.getStrings());
			parser.visitAllNodesWith(hPage);
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	public String getURL(){
		return parser.getURL();
	}
	
	public String getTitle(){
		String titleString=hPage.getTitle();
		if(titleString==null || titleString.trim().length()==0)
			return content.substring(0,content.indexOf("\n")).trim();
		return StringParser.QtoB(titleString);
	}
	
	public String getContent(){
		return content;
	}
}
