package ois.verify;

import java.util.Iterator;
import java.util.Stack;

import org.dom4j.Element;

import ois.ner.constant.XMLConfig;
import ois.xmlParser.XMLParser;

public class LibParser extends XMLParser{
	private Iterator iterator;
	private Stack<String> tokenStack=new Stack<String>();
	public LibParser() {
		super(XMLConfig.CONFIG_ILLEGAL);
		iterator=root.elementIterator(XMLConfig.ILLEGAL_ITEM);
	}
	 
	/**************************************
	 * 
	 * 根据对象和属性，返回原始字符串形式的违法条件
	 * @param obj 对象
	 * @param attr 属性
	 * @return 违法条件，未找到则返回null
	 **************************************/
	public String getCondition(String obj,String attr){
		Element element;
		while(iterator.hasNext()){
			element=(Element)iterator.next();
			if(element.elementText(XMLConfig.ILLEGAL_OBJECT).equals(obj) &&
					element.elementText(XMLConfig.ILLEGAL_ATTRIBUTE).equals(attr))
				return element.elementTextTrim(XMLConfig.ILLEGAL_CONDITION);
		}
		return null;
	}
 
}
