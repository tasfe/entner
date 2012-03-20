
package ois.infoExtract;

import java.util.ArrayList;

/*******************************************************
 * 
 * 企业注册信息抽取模板
 * @author wxt
 * 
 *******************************************************/
public class Template {
	private ArrayList<String> template;
//	private int TemplateType=0;
	public  Template(String temp) {
		template = new ArrayList<String>();
//		this.TemplateType=type;
		this.init(temp);
	}
	
	/****************************************************
	 * 
	 * 根据输入字符串初始化模板内容列表
	 * @param str template string read from a XML file
	 *
	 ****************************************************/
	private void init(String str){
		str=str.trim();
		while(str.length()>0)
		{
			if(str.contains("+"))
			{
				template.add(str.substring(0,str.indexOf("+")));
				str=str.substring(str.indexOf("+")+1);
			}
			else
			{
				template.add(str);
				break;
			}
		}
	}
	
	public String get(int index){
		return template.get(index);
	}
	
	public int size(){
		return template.size();
	}
	
	public ArrayList<String> getTemplate(){
		return this.template;
	}
	
//	public int getType() {
//		return this.TemplateType;
//	}
	
	public String toString(){
		return template.toString();
	}
}
