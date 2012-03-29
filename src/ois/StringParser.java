package ois;

import java.util.LinkedList;

/******************************************
 * 
 * 字符串解析器，包含句子分割、全角转半角等功能
 * @author zhzhl
 *
 ******************************************/
public class StringParser {
	private StringBuffer content=null;
	
	public StringParser(String str){
		this.content=new StringBuffer(str);
	}
	
	public StringParser(){
		content=new StringBuffer();
	}
	
	public boolean isEmpty(){
		return content==null||content.length()==0;
	}
	
	public void setContent(String string){
		content.delete(0, content.length());
		content.append(string);
	}
	
	/****************************************
	 * 
	 * Read a sentence from the content
	 * @return a substring of the content
	 * 
	 ****************************************/
	public String readSentence(){
		int tmpindex=Integer.MAX_VALUE,index=Integer.MAX_VALUE;
		String sentence;
		if((tmpindex=content.indexOf("\r"))>0)
			index=tmpindex;
		if((tmpindex=content.indexOf("\n"))>0)
			index=(tmpindex<index)?tmpindex:index;
		if((tmpindex=content.indexOf("。"))>0)
			index=(tmpindex<index)?tmpindex:index;
		if((tmpindex=content.indexOf("!"))>0)
			index=(tmpindex<index)?tmpindex:index;
		if((tmpindex=content.indexOf("?"))>0)
			index=(tmpindex<index)?tmpindex:index;
		if(index==Integer.MAX_VALUE){
			sentence=content.toString();
			content.delete(0, content.length());
		}
		else{
			sentence=content.substring(0,index+1);
			content.delete(0, index+1);
		}
		return sentence.trim();
	}
	
	/**********************************************
	 * 
	 * 对字符串按空白字符进行分割，将所有子串存储在一个链表中
	 * @param str 任意字符串
	 * @return 包含结果的链表
	 * 
	 **********************************************/
	public LinkedList<String> splitToArray(String str){
		LinkedList<String> sentence=new LinkedList<String>();
		str=str.replaceAll("[\\s]+"," ");
		StringBuffer sBuffer=new StringBuffer(str.trim());
		String tmpString;
		int index=0;
		while(sBuffer.length()>0){
			index=sBuffer.indexOf(" ");
			if(index>0){
				tmpString=sBuffer.substring(0, index);
				sentence.add(tmpString);
				sBuffer.delete(0, index+1);
			}
			else if(index==0){
				sBuffer.delete(0, 1);
			}
			else {
				sentence.add(sBuffer.toString());
				sBuffer.delete(0, sBuffer.length());
			}
		}
		return sentence;
	}
	
	/***************************************
	 * 
	 * 全角转换成半角
	 * @param input 原始字符串
	 * @return 转换后的字符串
	 *
	 ***************************************/
	public static String QtoB(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} 
			else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}
	
	public static String BtoQ(String input){
		char  c[]  =  input.toCharArray();
		for  ( int i=0; i<c.length;i++ ) {
			if (c[i] ==' ') {
				c[i] = '\u3000';
			}  
			else if (c[i]<'\177') {
				c[i]= (char) (c[i]+65248);
			 }

		}
		return new String(c);
	}
}
