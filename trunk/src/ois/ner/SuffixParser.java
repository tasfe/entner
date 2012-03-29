
package ois.ner;

import java.util.ArrayList;
import java.util.Iterator;

import ois.ner.constant.XMLConfig;
import ois.xmlParser.XMLParser;

import org.dom4j.Element;

/***************************************
 * 
 * 实体名称触发词解析器
 * @author zhzhl
 * 
 ***************************************/
public class SuffixParser extends XMLParser {
	ArrayList<String> KeyWord;
	
	public SuffixParser(){
		super(XMLConfig.CONFIG_KEYWORD);
		KeyWord = new ArrayList<String>();
	}
	public void initKeyWord(){
		Element orgElement=root.element(XMLConfig.KEY_ORG_LIST);
		Iterator<Element> i = orgElement.elementIterator(XMLConfig.KEY_ITEM);
		String keyword;
	    while(i.hasNext()){
	    	keyword=i.next().getTextTrim();
	    	KeyWord.add(keyword);
	    }
	}
	
	/***********************************************************************
	 * 
	 * check the keyword.xml to determine if the word is in the type list
	 * @param word String of word
	 * @param type type of named entity,it can be:
	 * 				XMLConfig.KEY_ORG_LIST,
	 * 				XMLConfig.KEY_DATE_LIST, 
	 *				XMLConfig.KEY_LOC_LIST, 
	 * 				XMLConfig.KEY_CUR_LIST.
	 * @return  true if there is such key word or false 
	 *
	 ************************************************************************/
	public boolean Exists(String word, String type){		
		Element orgElement=root.element(type);
		Iterator<Element> i = orgElement.elementIterator(XMLConfig.KEY_ITEM);
		boolean suffix=type.equals(XMLConfig.KEY_DATE_LIST)||type.equals(XMLConfig.KEY_LOC_LIST)||type.equals(XMLConfig.KEY_ORG_LIST);
		String keyword;
	    while(i.hasNext()){
	    	keyword=i.next().getTextTrim();
	    	if(suffix && word.endsWith(keyword))
	    		return true;
	    	else if(word.equals(keyword))
	    		return true;
		}
		return false;
	}
	
	/********************************************
	 * 
	 * 返回一个关键词的类型
	 * @param word String of word
	 * @param type type of named entity,it can be:
	 * 				XMLConfig.KEY_ORG_LIST,
	 * 				XMLConfig.KEY_DATE_LIST, 
	 *				XMLConfig.KEY_LOC_LIST, 
	 * 				XMLConfig.KEY_CUR_LIST.
	 * @return 关键词的type属性，若其为空则返回null
	 ********************************************/
	public String getType(String word, String type){
		Element orgElement=root.element(type);
		Iterator<Element> i = orgElement.elementIterator(XMLConfig.KEY_ITEM);
		while(i.hasNext()){
			Element tmp =i.next();
		    if(tmp.getTextTrim().equals(word))
		    	return tmp.attributeValue(XMLConfig.KEY_TYPE);
		}
		return null;
	}
 
}
