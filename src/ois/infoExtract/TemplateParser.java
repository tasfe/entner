package ois.infoExtract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ois.ner.constant.RunParam;
import ois.ner.constant.Tagging;
import ois.ner.constant.XMLConfig;
import ois.xmlParser.XMLParser;
import org.dom4j.Element;

/*******************************************************
 * 
 * 信息抽取模板解析模块，读取关键词和模板并与文本中进行匹配
 * @author wxt
 *
 *******************************************************/
public class TemplateParser extends XMLParser  {
	private HashMap<String, String> keyWords;
	
	public TemplateParser(){
		super(XMLConfig.CONFIG_EXTRACT);
		keyWords=new HashMap<String, String>();
		this.initKeyWord();
	}
	
	/************************************************************
	 * 
	 * initialize this class. Read extract keyword from file
	 * 
	 ************************************************************/
	private void initKeyWord(){
		String type;
		Element element;
		Iterator iterator = root.element(XMLConfig.EXTRACT_KEYWORD).elementIterator(XMLConfig.EXTRACT_KEYWORD_ITEM);
		while(iterator.hasNext())
		{
			element=(Element)iterator.next();
			type=element.attributeValue(XMLConfig.EXTRACT_KEYWORD_TYPE);
			if(type.length()>0){
				keyWords.put( element.getText(),type);
			}
		}
	}
	
	public boolean checkType(String word,String type){
		String value=keyWords.get(word);
		if(value!=null && value.equals(type))
			return true;
		return false;
	}
	
	/**********************************************************
	 * 获取指定类型模板的根节点，可以通过迭代获取所有该类型的模板
	 * @param type 模板类型，参考XMLconfig类
	 * @return Element 模板根节点元素
	 * 
	 **********************************************************/
 	private Element getTemplateNode(String type){
		Element temp = root.element(XMLConfig.EXTRACT_TEMP);
		Iterator iterator=temp.elementIterator();
		Element element=null;
		while(iterator.hasNext())
		{
			element=(Element)iterator.next();
			if(element.getName().equals(type))
				break;
		}
		return element;
	}
	
	private ArrayList<Template> readTemplates(Element element){
		 ArrayList<Template> templates=new ArrayList<Template>();	
		 Iterator iterator=element.elementIterator();
		 String tmpString;
		 while(iterator.hasNext())
		 {
			tmpString=((Element)iterator.next()).getTextTrim();
			if(tmpString.length()>0)
				templates.add(new Template(tmpString));
		}
		return templates;
	}
	
	/*****************************************************
	 *
	 * 判断当前词语与模板中的词是否吻合
	 * @param tempword 来自模板的词
	 * @param word 来自文本的词
	 * @return 0 不匹配； 1 完全匹配；  2 模板词是*； 3 可选词匹配；
	 * 
	 *****************************************************/
 	private int WordMatched(String tempword,String word){
 		String newWord =word.substring(0,word.lastIndexOf("/"));
 		if(tempword.startsWith("[") && tempword.endsWith("]"))
 			tempword=tempword.substring(1, tempword.length()-1);
//		* 若干个词
		if(tempword.equals("*"))
			return 2;
//		? 任意一个词
		else if(tempword.equals("?"))
			return 1;
//		/词性或实体标注
		else if(tempword.startsWith("/"))
		{
			if(word.endsWith(tempword))
				return 1;
		}
//		""必须的词
		else if(tempword.contains("\""))
		{
			tempword=tempword.substring(tempword.indexOf("\"")+1,tempword.lastIndexOf("\""));
			if(newWord.equals(tempword))
				return 1;
		}
//		()可选词
		else if(tempword.contains("("))
		{
			tempword=tempword.substring(tempword.indexOf("(")+1,tempword.lastIndexOf(")"));
			if(tempword.startsWith("/"))
			{
				if(word.endsWith(tempword))
					return 1;
			}
			else if(newWord.equals(tempword))
				return 1;
			return 3;
		}
//		Ve,Vc等关键词
		else if(keyWords.containsKey(newWord) && keyWords.get(newWord).equals(tempword))
			return 1;
		return 0;
	}
 	
 	/***********************************************************
	 * 
	 * 判断当前句子与指定的信息抽取模板是否符合
	 * @param temp Extract template 
	 * @param sentence 句子
	 * @param index current index
	 * @return 抽取项在句子中的起始下标，如果不匹配则返回-1
	 * 
	 ***********************************************************/
	public int TemplateMatched(final Template temp,final List<String> sentence,int index){
		String tempword;
		String word;
		int TempIndex,TextIndex;
		int lastMatch=0,currentMatch;
		int FetchIndex=-1;
		
		word=sentence.get(index);
		for(int i=0;i<temp.size();i++)
		{
			tempword=temp.get(i);
			
			if((lastMatch=this.WordMatched(tempword, word))==1)
			{
				if(tempword.startsWith("[") && tempword.endsWith("]"))
					FetchIndex=index;
				TempIndex=i-1;
				TextIndex=index-1;
				while(TempIndex>=0 && TextIndex>=0)
				{
					tempword=temp.get(TempIndex);	
					word=sentence.get(TextIndex);	
					if(tempword.startsWith("[") && tempword.endsWith("]"))
						FetchIndex=TextIndex;
					currentMatch=this.WordMatched(tempword, word);
					if(currentMatch==0)
					{
						if(lastMatch==2){
							if(FetchIndex==TextIndex+1)
								FetchIndex--;
							TextIndex--;
						}
						else
							break;
					}
					else if(currentMatch==1 || currentMatch==2)
					{	
						lastMatch=currentMatch;
						TempIndex--;
						TextIndex--;
					}
					else if(currentMatch==3)
					{
						lastMatch=currentMatch;
						TempIndex--;
					}
					
				}
				if(TempIndex>=0)
					return -1; 
				TempIndex=i+1;
				TextIndex=index+1;
				while(TempIndex<temp.size() && TextIndex<sentence.size())
				{					
					tempword=temp.get(TempIndex);
					word=sentence.get(TextIndex);
					if(tempword.contains("[") && tempword.contains("]"))
						FetchIndex=TextIndex;
					currentMatch=this.WordMatched(tempword, word);
					if(currentMatch==0)
					{
						if(lastMatch==2){
							TextIndex++;
						}
						else
							break;
					}	
					else if(currentMatch==1 || currentMatch==2)
					{	
						lastMatch=currentMatch;
						TempIndex++;
						TextIndex++;
					}
					else if(currentMatch==3)
					{
						lastMatch=currentMatch;
						TempIndex++;
					}
				}
				if(TempIndex==temp.size())
					return FetchIndex; 
				else 
					return -1;
			}
		}
		return -1;
	}

	/*************************************************************
	 * 
	 * 判断当前句子中的日期是否属于注册日期
	 * @param sentence 句子
	 * @param index 句子中的当前下标
	 * @return 字符串形式的日期
	 * 
	 *************************************************************/
	public String RegDate(final List<String> sentence,int index){
		Template temp;
		String RegDate=null;
		int FetchIndex;
		ArrayList<Template> templates=readTemplates(getTemplateNode(XMLConfig.EXTRACT_TEMP_REGDATE));
		for(int i=0;i<templates.size();i++)
		{
			temp=templates.get(i);
			if((FetchIndex=this.TemplateMatched(temp,sentence,index))>=0){
				RegDate=sentence.get(FetchIndex);
				RegDate=RegDate.substring(0,RegDate.lastIndexOf("/"));
			}
		}
		return RegDate;
	}
	
	/*************************************************************
	 * 
	 * 判断当前句子中的货币是否属于注册资本
	 * @param sentence 句子
	 * @param index 句子中的当前下标
	 * @return 字符串形式的货币
	 * 
	 *************************************************************/
	public String RegAssets(final List<String> sentence,int index){
		Template temp;
		String RegAssets=null;
		int FetchIndex;
		ArrayList<Template> templates=readTemplates(getTemplateNode(XMLConfig.EXTRACT_TEMP_REGASSETS));
		for(int i=0;i<templates.size();i++)
		{
			temp=templates.get(i);
			if((FetchIndex=this.TemplateMatched(temp,sentence,index))>=0){
				RegAssets=sentence.get(FetchIndex);
				RegAssets=RegAssets.substring(0,RegAssets.lastIndexOf("/"));
			}
		}
		return RegAssets;
	}
	
	/*************************************************************
	 * 
	 * 判断当前句子中的地址是否属于企业住所
	 * @param sentence 要处理的句子
	 * @param index 句子中的当前下标
	 * @return 字符串形式的地址或null
	 * 
	 *************************************************************/
	public String Location(final List<String> sentence,int index){
		Template temp;
		String location=null;
		int FetchIndex;		
		ArrayList<Template> templates=readTemplates(getTemplateNode(XMLConfig.EXTRACT_TEMP_LOCATION));
		for(int i=0;i<templates.size();i++)
		{
			temp=templates.get(i);
			if((FetchIndex=this.TemplateMatched(temp,sentence,index))>=0){
				location=sentence.get(FetchIndex);
				location=location.substring(0,location.lastIndexOf("/"));
			}
		}
		return location;
	}
	
	/************************************************************
	 *
	 * 根据匹配的抽取模板在当前句子中抽取经营范围
	 * @param temp 抽取模板
	 * @param sentence 要处理的句子
	 * @param index 模板触发词下标
	 * @param Fetchindex 抽取项起始下标
	 * @return 字符串形式的经营范围
	 * 
	 ************************************************************/
	private String fetchScope(final Template temp,final List<String> sentence,int index, int FetchIndex){
		String result="";
		String tempword;
		String word;
		int TemplateIndex,TextIndex;
		word=sentence.get(index);
		for(int i=0;i<temp.size();i++)
		{
			tempword=temp.get(i); 
			if(this.WordMatched(tempword, word)==1)
			{
				if(FetchIndex<index){
					TemplateIndex=i-1;
					TextIndex=index-1;
					while(TemplateIndex>=0){
						 tempword=temp.get(TemplateIndex);
						 if(tempword.startsWith("[") && tempword.endsWith("]"))
							 break;
						 TemplateIndex--;
						 TextIndex--;
					}
					for(int j=FetchIndex;j<=TextIndex;j++) { 
						 word=sentence.get(j);
						 result+=word.substring(0,word.lastIndexOf("/"));
					 }
					break;
				}
				if(FetchIndex>index){
					TemplateIndex=i+1;
					while(TemplateIndex<temp.size()){
						tempword=temp.get(TemplateIndex);
						TemplateIndex++;
						if(tempword.startsWith("[") && tempword.endsWith("]"))
							break;
					}
					TextIndex=FetchIndex+1;
					while(TextIndex<sentence.size()){
						tempword=temp.get(TemplateIndex);
						word=sentence.get(TextIndex);
						if(WordMatched(tempword,word)%2==1)
							break;
						TextIndex++;
					} 
					for(int j=FetchIndex;j<=TextIndex;j++) { 
						 word=sentence.get(j);
						 result+=word.substring(0,word.lastIndexOf("/"));
					 }
					break;
				}
			} 
		}			 
		return result;
	}
	
	/*************************************************************
	 * 
	 * 抽取当前句子中的经营范围
	 * @param sentence 要处理的句子
	 * @param index 句子中的当前下标
	 * @return  经营范围
	 * 
	 *************************************************************/
	public String Scope(final List<String> sentence,int index){
		Template temp;
		String pro=null;
		int FetchIndex;	
		ArrayList<Template> templates=readTemplates(getTemplateNode(XMLConfig.EXTRACT_TEMP_SCOPE));
		for(int i=0;i<templates.size();i++)
		{
			temp=templates.get(i);
			if((FetchIndex=this.TemplateMatched(temp,sentence,index))>=0){
				pro=this.fetchScope(temp, sentence, index,FetchIndex);
			}
			
		}
		return pro;
	}
	
	/*************************************************************
	 * 
	 * 抽取法定代表人
	 * @param sentence 要处理的句子
	 * @param index 句子中的当前下标
	 * @return 法定代表人
	 * 
	 *************************************************************/
	public String Representative(final List<String> sentence,int index){
		String word;
		int k=0;
		while(k++<4 && index+k<sentence.size()){
			word=sentence.get(index+k);
			if(Tagging.isPersonWord(word)||Tagging.isPersonEntity(word))
				return word.substring(0,word.lastIndexOf("/"));
		}
		return null;
	}

	/********************************************
	 * 
	 * 抽取品牌名称
	 * @param sentence 句子
	 * @param index 匹配触发词下标
	 * @return 品牌名或null
	 * 
	 ********************************************/
	public String Brand(final List<String> sentence,int index){
		Template temp;
		String brand="";
		int FetchIndex;
		String word;	
		ArrayList<Template> templates=readTemplates(getTemplateNode(XMLConfig.EXTRACT_TEMP_BRAND));
		for(int i=0;i<templates.size();i++)
		{
			temp=templates.get(i);
			if((FetchIndex=this.TemplateMatched(temp,sentence,index))>=0)
			{
				for(int j=FetchIndex;j<sentence.size();j++)
				{
					word=sentence.get(j);
					brand+=word.substring(0,word.lastIndexOf("/"));
				}
			}
		}
		return brand.length()>0?brand:null;
	}
	
	/********************************************
	 * 
	 * 抽取商品名称
	 * @param sentence 句子
	 * @param index 匹配触发词下标
	 * @return 商品名或null
	 * 
	 ********************************************/
	public String Product(final List<String> sentence,int index){
		Template temp;
		String product="";
		int FetchIndex;
		String word;	
		ArrayList<Template> templates=readTemplates(getTemplateNode(XMLConfig.EXTRACT_TEMP_PRODUCT));
		for(int i=0;i<templates.size();i++)
		{
			temp=templates.get(i);
			if((FetchIndex=this.TemplateMatched(temp,sentence,index))>=0)
			{
				for(int j=FetchIndex;j<sentence.size();j++)
				{
					word=sentence.get(j);
					product+=word.substring(0,word.lastIndexOf("/"));
				}
			}
		}
		return product.length()>0?product:null;
	}
}
