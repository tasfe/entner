
package ois.ner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ois.ner.constant.StringConst;
import ois.ner.constant.Tagging;
import ois.ner.constant.XMLConfig;
import ois.xmlParser.XMLParser;

import org.dom4j.Element;

/*************************************
 * 
 * 企事业单位名称词性规则分析和匹配
 * @author zhzhl
 * 
 *************************************/
public class RuleParser extends XMLParser {
	private Iterator iterator;
	//root element of the document, it is <List> here.
	
	public RuleParser(){
		super(XMLConfig.CONFIG_RULE);
		iterator=root.elementIterator(XMLConfig.RULE_ITEM);
	}
	
	public static void main (String [] args){
		new RuleParser().Training("RuleTrain.txt","gb2312");
	}
	
	/*
	 * If there is more rule in the document
	 */
 	private boolean hasNext(){
		return iterator.hasNext();
	}
	
	/*
	 * Next POS rule in the current document
	 */
	private Element nextRule()
	{
		return (Element)iterator.next();
	}

	/*
	 * Iterator is reseted to first element
	 */
	private void reset() {
		iterator = root.elementIterator(XMLConfig.RULE_ITEM);
	}
	
	/*************************************************
	 * 
	 * Get POS string from a tagged String of entity
	 * 
	 *************************************************/
	public String getPOSString(String entity) {
		String posString=new String();
		int index,end;
		String str=entity.trim();
		while(str.length()>0 && (index=str.indexOf("/"))>=0)
		{
			if(!Character.isLowerCase(str.charAt(index+1)))
			{
				str=str.substring(index+1);
				continue;
			}
			end=str.indexOf(" ");
			if(end<0)
			{
				posString+=str.substring(index);
				break;
			}
			posString+=str.substring(index, end);
			str=str.substring(end+1).trim();		
		}
		return posString;
	}
	
	/**************************************************
	 * 
	 * New occur of a POS rule and add it to rule set
	 * 
	 **************************************************/
	public synchronized void addCount(String posString) {
		String xpath = "//"+XMLConfig.RULE_ROOT+"/"+XMLConfig.RULE_ITEM+
			"["+XMLConfig.RULE_POS+"='"+posString+"']";
		Element rule=(Element)doc.selectSingleNode(xpath);
		if(rule!=null){
			Integer count=Integer.valueOf(rule.elementText((XMLConfig.RULE_COUNT)))+1;
			rule.element(XMLConfig.RULE_COUNT).setText(count.toString());
			Element total=doc.getRootElement().element(XMLConfig.RULE_TOTAL);
			Integer tInteger=Integer.valueOf(total.getText())+1;
			total.setText(tInteger.toString());
			super.update();
		}
	}
	
	public synchronized void newOccur(String posString){
		String xpath = "//"+XMLConfig.RULE_ROOT+"/"+XMLConfig.RULE_ITEM+
			"["+XMLConfig.RULE_POS+"='"+posString+"']";
		Element rule=(Element)doc.selectSingleNode(xpath);
		if(rule==null)
		{
			rule=(Element) root.element(XMLConfig.RULE_ITEM).clone();
			rule.element(XMLConfig.RULE_POS).setText(posString);
			rule.element(XMLConfig.RULE_COUNT).setText("0");
			root.add(rule);
		}
		Integer count=Integer.valueOf(rule.elementText((XMLConfig.RULE_COUNT)))+1;
		rule.element(XMLConfig.RULE_COUNT).setText(count.toString());
		Element total=doc.getRootElement().element(XMLConfig.RULE_TOTAL);
		Integer tInteger=Integer.valueOf(total.getText())+1;
		total.setText(tInteger.toString());
		this.update();
	}
	
	/*********************************************************************
	 * 
	 * Do training from a file, the content in the file should be one entity each line
	 * @param filename String of file name
	 * @param charset character set of the file
	 * 
	 ********************************************************************/
	public void Training(String filename, String charset) {
		try {
			String file="train_data.txt";
			ICTCLASFactory tmp = new ICTCLASFactory();
			if(tmp==null)
				return;
			tmp.fileProcess(filename, file);
			InputStreamReader iReader = new InputStreamReader(new FileInputStream(file) , charset);
			BufferedReader bReader=new BufferedReader(iReader);
			String str;
			while((str=bReader.readLine())!=null && str.trim().length()>0){
				newOccur(getPOSString(str));
			}
			tmp.exit();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**************************************************
	 * 
	 * get the total number of occurs of organization
	 * 
	 **************************************************/
	public int getTotal(){
		return Integer.valueOf(root.elementTextTrim(XMLConfig.RULE_TOTAL));
	}
	
	/*****************************************
	 * 
	 * return the count of POS rule
	 *
	 ****************************************/
	public int getCount(String posString){
		String xpath = "//"+XMLConfig.RULE_ROOT+"/"+XMLConfig.RULE_ITEM+
		"["+XMLConfig.RULE_POS+"='"+posString+"']";
		Element element=(Element)doc.selectSingleNode(xpath);
		int k=0;
		if(element!=null)
			k=Integer.valueOf(element.elementTextTrim(XMLConfig.RULE_COUNT));
		return k;
	}

	/*****************************************************************************
	 * 
	 * Check whether there is any organization in this sentence
	 * @param sentence List of words, 
	 * @param RightBorder index of suffix word,and it's also right border.
	 * @returns ArrayList of candidate POS rules matched this sentence.
	 *****************************************************************************/
	public ArrayList<String> Matched(final List<String> sentence, int RightBorder) {
		int posindex, wordindex;
		String posString;
		String word,sentencepos2="",sentencepos1="";
		ArrayList<String> CandidateRule=new ArrayList<String>();
		boolean inKuohao=false;
		//has more POS rules
		while(this.hasNext()){ 
			//POS rule
			posString=this.nextRule().elementText(XMLConfig.RULE_POS); 
			if(posString==null || posString.length()==0)
				continue;
			sentencepos1="";
			for(wordindex=RightBorder;wordindex>=0;wordindex--){
				word=sentence.get(wordindex);
				sentencepos1=word.substring(word.lastIndexOf("/")).trim()+sentencepos1;
				if(StringConst.isSpecialChars(word))
					break;
			}
			if((posindex=sentencepos1.indexOf(Tagging.POS_KUOHAO))>=0)
				sentencepos2=sentencepos1.replaceAll("/wkz[a-z0-9_]/wky", "");
			if(sentencepos1.endsWith(posString)||sentencepos2.endsWith(posString))
				CandidateRule.add(posString);
		}
		reset();
		return CandidateRule;
	}
	
	/********************************************************************
	 *
	 * For rule ri, the count of each rule that ends with ri means 
	 *               the occurs of entity  when ri is true.
	 * @param posString POS string like '/ns/nv/n'
	 * @return the count of each rule that ends with ri
	 *           
	 *********************************************************************/
	public int sumOfcount(String posString){
		int k=0;
		while (this.hasNext()) {
			Element rule=this.nextRule();
			if(rule.elementText(XMLConfig.RULE_POS).endsWith(posString))
				k+=Integer.valueOf(rule.elementText(XMLConfig.RULE_COUNT));
		}
		reset();
		return k;
	}
}
