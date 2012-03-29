
package ois.ner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ois.ner.constant.RunParam;
import ois.ner.constant.StringConst;
import ois.ner.constant.Tagging;
import ois.ner.constant.XMLConfig;


/*************************************************************************************
 *
 * Named Entity Analyzer
 * This class reads words from a PipeReader connected to PipWriter from WordSegment.
 * NEAnalyzer can recognize entities such as time, location, currency, enterprise. 
 * @author zhzhl
 * 
 *************************************************************************************/

public class NEAnalyzer {
	private SuffixParser sParser; //recognize suffix word of organization
	public NEAnalyzer()	{
		 sParser=new SuffixParser();
	}

	/***************************************************
	 *
	 * 获取词性规则长度
	 * @param posString 词性规则字符串，如"/ns/n/n"
	 * @return 该规则长度，等于'/'个数
	 * 
	 ***************************************************/
	private int getLengthOfRule(String posString) {
		int length=0;
		String tmp=new String(posString);
		while(tmp.contains("/"))
		{
			length++;
			tmp=tmp.substring(tmp.indexOf("/")+1);
		}
		return length;
	}
	
	/***************************************************
	 *
	 * 日期识别
	 * @param sentence 带词性标注的词语列表
	 * @param index 触发词下标
	 * @param flag 标志当前词属性：前缀、后缀 
	 * @return 除触发词外的实体长度
	 * 
	 ***************************************************/
	public int dateRecognize(final List<String> sentence,int index, int flag){
		String word;
		int length=0,i;
		if(flag==RunParam.Prefix){
			for(i=index+1,length=0;i<sentence.size();i++){
				word=sentence.get(i);
				if(Tagging.isDateWord(word)|| Tagging.isNumber(word)||
						sParser.Exists(word.substring(0,word.lastIndexOf("/")), XMLConfig.KEY_DATE_LIST)){
					length++;
					continue;
				}
				break;
			}
			return length;
		}
		else {
			for(i=index-1,length=0;i>=0;i--){
				word=sentence.get(i);
				if(Tagging.isDateWord(word) || Tagging.isDateEntity(word)|| Tagging.isNumber(word)){
					length++;
					continue;
				}
				break;
			}
			return length;
		}
	}
	
	/***************************************************
	 *
	 * 货币识别
	 * @param sentence 带词性标注的词语列表
	 * @param index 触发词下标
	 * @return  除触发词外的实体长度
	 * 
	 ***************************************************/
	public int moneyRecognize(final List<String> sentence, int index){
		String word;
		int length=0;
		for(int i=index-1;i>=0;i--){
			word=sentence.get(i);
			if(Tagging.isNumber(word) || Tagging.isCurrencyEntity(word) || Tagging.isDouhao(word)) 
				length++;
			else 
				break;
		}
		return length;
	}
	
	/***************************************************
	 *
	 * 地点识别
	 * @param sentence 带词性标注的词语列表
	 * @param index 触发词下标
	 * @return  除触发词外的实体长度
	 * 
	 ***************************************************/
	public int locRecognize(final List<String> sentence, int index){
		String word,suffixword;
		int length=0;
		boolean isAddr=false;
		suffixword=sentence.get(index);
		suffixword=suffixword.substring(0,suffixword.lastIndexOf("/"));
		int i=0;
		for(i=index-1;i>=0 && length<RunParam.LengthOfAddress;i--){
			word=sentence.get(i);
			length++;
			if(Tagging.isStopWords(word)|| Tagging.isEntity(word) || 
					StringConst.isSpecialChars(word)){
				if(word.endsWith(Tagging.POS_MAOHAO)){
					length--;
					isAddr=true;
					break;
				}
				else if(word.endsWith(Tagging.TAG_LOC))
					isAddr=true;
				else
					break;
			}
			else if(Tagging.isAddresWord(word)){
				isAddr=true;
			}
			else {
				if(isAddr)
					break;
			}
		}
		if(i==-1)
			isAddr=true;
		if(isAddr)
			return length;
		else  
			return 0;
	}
	
	/**********************************************************************
	 *
	 * Recognition enterprise name in the sentence
	 * @param sentence 词语列表
	 * @param suffixindex the index of suffix word
	 * @param param  1: longest-fit; 0: shortest-fit; 0.5, purely depends on statistic
	 * @param  MaxLength  the max length of a named entity
	 * @return  length of enterprise name exclude the suffix word
	 *           
	 ***********************************************************************/
	public int orgReconize(final LinkedList<String> sentence,int suffixindex,float param,int MaxLength) {
		RuleParser rParser = new RuleParser();    
		//parser for POS rules of organization
		double pri;
		//P(ri):事件ri发生的概率，先验概率
		double pori; 
		//P(O|ri)，即当事件ri发生时，组织机构名称被正确识别的概率
		double pro_max=0;
		//the max P(r|O), the corresponding r is selected as result
		int total=rParser.getTotal();
		//total occurs of organization
		int ni=0;
		//sumOfnj is the summary of occurs of all the candidate rules
		int L=0,maxL=0,minL=Integer.MAX_VALUE;
		//L is length of entity name;
		String posString="";
		ArrayList<String> candidateRule;
		candidateRule=rParser.Matched(sentence, suffixindex);
		if(candidateRule.size()>0)
		{
			int i;
			for(i=0;i<candidateRule.size();i++)
			{
				int local=this.getLengthOfRule(candidateRule.get(i));
				maxL=(local>maxL)?local:maxL;
				minL=(local<minL)?local:minL;
			}
			for(i=0;i<candidateRule.size();i++)	
			{	
				if((1-param)<0.00000001)
				{
					L=maxL-1;		
					break;
				}
				if(param<0.00000001) {
					L=minL-1;
					break;
				}
				posString=candidateRule.get(i);
				ni=rParser.getCount(posString);
				pri=(double)ni/total;
				pori=(double)ni/rParser.sumOfcount(posString);
				double tmp = pri*pori*(StrictMath.pow(param*2,getLengthOfRule(posString)*2));			
				if(tmp>pro_max)
				{
					pro_max=tmp;
					L=getLengthOfRule(posString)-1;
				}
			}
		}
		/*
		 * If enterprise name doesn't contain an address name
		 * Trace back to seek an address name, 
		 * if found, update the left border of entity name
		 */
		
		if(!sentence.get(suffixindex-L).contains(Tagging.POS_ADDR))
		{	
			
			int index,wkzIndex=-1;
			int leftborder=suffixindex-MaxLength;
			int startIndex=suffixindex;
			String word;
			boolean kuohao=false,inKuohao=false;	
			boolean subaltern=false;
			//关键词为分公司或子公司或其他可以在ne_org后出现的词
			if(XMLConfig.KEY_TYPE_SUBALTERN.equals(sParser.getType(sentence.get(suffixindex).replaceAll(Tagging.regex, ""), XMLConfig.KEY_ORG_LIST)) && L==0)
				subaltern=true;
			
			for(index=suffixindex-L-1;index>= leftborder && index>=0;index--)
			{
				word=sentence.get(index);
				if(Tagging.isStopWords(word)){
					if(kuohao){
						if(wkzIndex>index+RunParam.LengthOfEnterpriseName)
							L=suffixindex-wkzIndex+1;
						else 
							L=suffixindex-index-1;
					}
					break;
				}
				else if((word.contains(Tagging.TAG_ORG)|| word.contains(Tagging.TAG_ORG_SHORT) )&& subaltern){
					L=suffixindex-index;
					break;
				}
				else if(word.contains("/wky"))
					inKuohao=true;
				else if(word.contains("/wkz")){
					if(!inKuohao)
						break;
					inKuohao=false;
					kuohao=true;
					wkzIndex=index;
				}
				else if(!inKuohao && (word.contains(Tagging.POS_ADDR) ||
						word.contains(Tagging.POS_ORG) ||word.contains(Tagging.POS_ZHUANYOU))){
					L=suffixindex-index;
				}
				if(startIndex>index+1)
					subaltern=false;
			}
			if(index<0 && kuohao)
				L=suffixindex-wkzIndex+1;
		}	
		return L;
	}
	
	/**********************************************************************
	 *
	 * Tag the enterprise entity in the iterator 
	 * @param iterator iterator of a List, the collection of a sentence
	 * @param Length  the length of enterprise name
	 * @param fullname whether it's a full name or a short name
	 *           
	 ***********************************************************************/
	public void orgTag( ListIterator<String> iterator, int Length, boolean fullname){
		String orgword=new String();
		String posString=new String();
		while(Length-->=0 && iterator.hasPrevious())
		{
			String tmpString=iterator.previous();
			posString=tmpString.substring(tmpString.lastIndexOf("/"))+posString;
		    orgword =tmpString.substring(0,tmpString.lastIndexOf("/"))+orgword;
		    iterator.remove();
		}
		new RuleParser().addCount(posString);
		orgword+=(fullname)?Tagging.TAG_ORG:Tagging.TAG_ORG_SHORT;
		iterator.add(orgword);
	}
	
	/**************************************************************
	 * 
	 * Tag the named entity in the iterator 
	 * @param iterator 当前句子的迭代器，当前词语为触发词
	 * @param length 实体长度，不包括当前触发词
	 * @param flag 标志当前词属性：前缀、后缀、单独实体词
	 * @param ne_tag 实体类型，可以是时间、货币、地点 、机构
	 * 
	 **************************************************************/
	public void EntityTag(ListIterator<String> iterator,int length, int flag, String ne_tag){
		String word;
		String neString=new String();
		if(flag==RunParam.NeWord){
			word=iterator.previous();
			word=word.substring(0,word.lastIndexOf("/"));
			word+=ne_tag;
			iterator.remove();
			iterator.add(word);
		}
		else if(flag==RunParam.Prefix){
			iterator.previous();
			while(length-->=0 && iterator.hasNext()){
				word=iterator.next();
				neString+=word.substring(0,word.lastIndexOf("/"));
				iterator.remove();
			}
			neString+=ne_tag;
			iterator.add(neString);
		}
		else if(flag==RunParam.Suffix){
			while(length-->=0 && iterator.hasPrevious()){
				word=iterator.previous();
				neString=word.substring(0,word.lastIndexOf("/"))+neString;
				iterator.remove();
			}
			neString+=ne_tag;
			iterator.add(neString);
		}
	}

	/****************************************************
	 *
	 * merge several continuous entities of same kind to single entity
	 * @param Iterator of a List, the collection of a sentence
	 *           
	 ****************************************************/
	public void entityMerge(List<String> sentence){
		int i;
		String word,lastword,lastType,newType;
		if(sentence.size()<=1)
			return;
		lastType=Tagging.getEntityType(sentence.get(0));
		for(i=1;i<sentence.size();i++)
		{
			word=sentence.get(i);
			newType=Tagging.getEntityType(word);
			if(newType!=null && lastType!=null){
				if(newType.equals(lastType)) {
					lastword=sentence.get(--i);
					word=lastword.substring(0,lastword.lastIndexOf("/"))+word;
					sentence.remove(i);
					sentence.remove(i);
					sentence.add(i, word);
				}
			}		
			lastType=newType;
		}
	}
	
	public void Process(LinkedList<String> sentence){
		ListIterator<String> iterator;
		int length=0,index;
		iterator=sentence.listIterator();
		String word;
		int posindex;
		while(iterator.hasNext())
		{
			word=iterator.next();
			int suffixindex;
			if((posindex=word.lastIndexOf("/"))<0)
				continue;
		/*
	     * recognize the organization first, and merge the words of a organization name into a single word.
		 * For example: 中国/n 联/v 碳/n 化学/n 有限公司/n are tagged as 中国联碳化学有限公司/ne_org
		 * Than store the tagged words in a WordPool for a while and than submitted to other NE analyzers. 
	     */
			if(sParser.Exists(word.substring(0,posindex),XMLConfig.KEY_ORG_LIST))
			{
				suffixindex=iterator.nextIndex()-1;
				if(suffixindex<=0)
					continue;
				length=this.orgReconize(sentence,suffixindex ,1.f,RunParam.MaxLengthOfEnterprise);
				if(length>0)
					this.orgTag(iterator,length,true);
			}
		}

		//		其他实体识别
		iterator=sentence.listIterator();
		while(iterator.hasNext())
		{
			word=iterator.next();		
			if((posindex=word.lastIndexOf("/"))<0)
				continue;
			if(Tagging.isEnterpriseWord(word))
			{
				this.orgTag(iterator,0,false);
				continue;
			}
			else if(sParser.Exists(word.substring(0,posindex), XMLConfig.KEY_DATE_LIST))
			{		
				if(Tagging.isDateWord(word)){
					this.EntityTag(iterator, 0, RunParam.NeWord, Tagging.TAG_DATE);
					continue;
				} 
				index=iterator.nextIndex()-1;
				if(XMLConfig.KEY_TYPE_PRE.equals(sParser.getType(word.substring(0,word.lastIndexOf("/")), XMLConfig.KEY_DATE_LIST))){
					length=this.dateRecognize(sentence,index,RunParam.Prefix);
					if(length>0)
						this.EntityTag(iterator, length, RunParam.Prefix, Tagging.TAG_DATE);
				}
				else{
					length=this.dateRecognize(sentence,index,RunParam.Suffix);
					if(length>0)
						this.EntityTag(iterator, length, RunParam.Suffix, Tagging.TAG_DATE);
				}
			}
			else if(Tagging.isAddresWord(word) || sParser.Exists(word.substring(0,posindex), XMLConfig.KEY_LOC_LIST)){
				index=iterator.nextIndex()-1;
				length=this.locRecognize(sentence,index);
				if(length>0)
					this.EntityTag(iterator, length, RunParam.Suffix, Tagging.TAG_LOC);
			}
			else if(Tagging.isCurrencyWord(word)){
				index=iterator.nextIndex()-1;
				length=this.moneyRecognize(sentence,index);
				if(length>0)
					this.EntityTag(iterator, length, RunParam.Suffix, Tagging.TAG_CUR);
			}
		}
		this.entityMerge(sentence);
	}
}
