
package ois.infoExtract;

import java.util.ArrayList;
import java.util.List;

import ois.EnterpriseCache;
import ois.TrademarkCache;
import ois.ner.ICTCLASFactory;
import ois.ner.constant.RunParam;
import ois.ner.constant.StringConst;
import ois.ner.constant.Tagging;

/*******************************************************
* 
* 企业注册信息抽取模块
* @author wxt
* 
********************************************************/
public class Extractor {
	private TemplateParser tParser;
	ICTCLASFactory iFactory;
	public Extractor(ICTCLASFactory ifactory){
		tParser=new TemplateParser();
		this.iFactory=ifactory;
	}
	
	private boolean triggerScope(String word){
		word=word.substring(0,word.lastIndexOf("/"));
		if(tParser.checkType(word,StringConst.KEYWORD_Ve))
			return true;
		if(word.startsWith("业务") || word.startsWith("经营"))
			return true;
		if(word.startsWith("一体"))
			return true;
		return false;
	}
	
	private boolean triggerBrand(String word){
		if(word.startsWith("品牌") || word.startsWith("厂家"))
			return true;
		return false;
	}
	
	private boolean triggerProduct(String word){
		if(word.startsWith("商品") || word.startsWith("产品"))
			return true;
		return false;
	}
	
	private boolean isRepresentative(String word){
		word=word.substring(0,word.lastIndexOf("/"));
		if(tParser.checkType(word,StringConst.KEYWORD_Np))
			return true; 
		return false;
	}
	
	/**********************************************************
	 *
	 * get all the potential short name of a given full name
	 * @param fullname String of enterprise name
	 * @return  a list contains the short name with POS tag
	 *           
	 **********************************************************/
	private ArrayList<String> candidateShortName(String fullname){
		String addr, name ,trade, type;
		String tmp;
		addr=name=trade=type=null;
		int index;
		boolean share=false;
		ArrayList<String> shortName=new ArrayList<String>();
		String nativeString=iFactory.split(fullname);
			nativeString=nativeString.trim();
			if(nativeString.contains("/wkz"))
			{
				tmp=nativeString.substring(0,nativeString.indexOf("/wkz")-1);
				nativeString=tmp+nativeString.substring(nativeString.indexOf("/wky")+4);
			}
//			第一个词是地名，则属于行政区划
			if (nativeString.startsWith(Tagging.POS_ADDR,nativeString.indexOf("/"))) {
				addr=nativeString.substring(0,nativeString.indexOf(" "));
				addr=addr.substring(0, addr.indexOf("/"));
				nativeString=nativeString.substring(nativeString.indexOf(" ")+1);
			}
			
//			最后一个词是组织形式
			if((index=nativeString.lastIndexOf(" "))>0)
			{
				type=nativeString.substring(index+1,nativeString.lastIndexOf("/"));
				nativeString=nativeString.substring(0,index).trim();
			}
			
//			倒数第二个词若是名词则认为是行业名称
			if((index=nativeString.lastIndexOf(" "))>0)
			{
				trade=nativeString.substring(index+1);
				if(trade.equals("股份"));
				{
					share=true;
					if((index=nativeString.lastIndexOf(" "))>0)
						trade=nativeString.substring(index+1);
				}	
				if(Tagging.isNouns(trade)){
					trade=trade.substring(0,trade.indexOf("/"));
					nativeString=nativeString.substring(0,index);
				}
				else {
					trade=null;
				}
			}
			name=nativeString.replaceAll("/[a-z0-9]+[ ]*", "");
			shortName.add(name);
			if(addr!=null)
			{
				shortName.add(addr+name);
			}
			if(trade!=null)
			{
				shortName.add(name+trade);
			}
			if(type!=null){
				if(type.startsWith("股份")) {
					shortName.add(name+type.substring(type.indexOf("股份")+2));
					shortName.add(name+"股份");
				}
				else 
					shortName.add(name+type);
			}
			name=null;
//			剩下的文本若为一个词则是企业字号，若是多个词，则有可能最后一个也属于行业名称
			if((index=nativeString.lastIndexOf(" "))>0 && trade!=null){
				name=nativeString.substring(0, index);
				name=name.replaceAll("/[a-z0-9]+[ ]*", "");
			}
			if(name!=null)
			{
				shortName.add(name);
				if(addr!=null)
					shortName.add(addr+name);
				if(type.startsWith("股份"))
				{
					shortName.add(name+type.substring(type.indexOf("股份")+2));
					shortName.add(name+"股份");
				}
				else 
					shortName.add(name+type);
			}
			for(int i=0;i<shortName.size();i++)
				if(shortName.get(i).equals(fullname) || shortName.get(i).length()<2)
					shortName.remove(i);
		return shortName;
	}
	
	/*************************************************************
	 * 
	 * find the right short name of a given full name
	 * @param fullname String of enterprise name
	 * @param sentence 词语列表
	 * @return  the right short name, or null if not found
	 *  
	 *************************************************************/
	public String shortNameRecognize(String fullname,List<String> sentence ){
		String tmp;
		ArrayList<String> candidateList=this.candidateShortName(fullname);
		int size=candidateList.size();
		String word;
		int length=0;
		int index=-1;
		for(int i=0;i<sentence.size();i++) {
			for(int j=0;j<size;j++) {
				word=sentence.get(i);
				word=word.substring(0,word.lastIndexOf("/"));
				tmp=candidateList.get(j);
				length=0;
				while(tmp.length()>0 && tmp.startsWith(word))
				{
					tmp=tmp.substring(word.length());
					length++;
					if(i+length>=sentence.size())
						break;
					word=sentence.get(i+length);
					word=word.substring(0,word.lastIndexOf("/"));
				}
				if(tmp.length()==0){
					if(index<0)
						index=j;
					else {
						index=(candidateList.get(index).length()>length)?index:j;
					}
				}
			}
		}
		if(index>=0)
			return candidateList.get(index);
		else 
			return null;
	}
	
	/********************************************************
	 * 
	 * 在网页标题中找出企业简称
	 * @param fullname String of enterprise name
	 * @param title 网页标题
	 * @return  the right short name, or null if not found
	 *  
	 ********************************************************/
	public String shortNameInTitle(String fullname, String title ){
		ArrayList<String> candidateList=this.candidateShortName(fullname);
		String shortname;
		if(title.contains(fullname))
			return null;
		for(int i=0;i<candidateList.size();i++)
		{
			shortname=candidateList.get(i);
			shortname=shortname.replaceAll("/[a-z]+[ ]*", "");
			if(title.contains(shortname))
				return shortname;
		}
		return null;
	}
	
	/*************************************************************
	 * 
	 * 在句子中找出简称并标注
	 * @param shortname String of short name
	 * @param sentence 词语列表
	 * @return true if there is any short name in the sentence, 
	 * 		   or false if not found
	 *  
	 *************************************************************/
	public boolean shortNameTag(String shortname,List<String> sentence){
		String word;
		int index=0;
		boolean changed=false;
		String constNameString=new String(shortname);
		for(int i=0;i<sentence.size();i++){
			word=sentence.get(i);
			word=word.substring(0,word.lastIndexOf("/"));
			if(shortname.startsWith(word)){
				index=i;
				int j=i+1;
				shortname=shortname.substring(shortname.indexOf(word)+word.length());
				for(;j<sentence.size() && shortname.length()>0;j++){
					word=sentence.get(j);
					word=word.substring(0,word.lastIndexOf("/"));
					if(!shortname.startsWith(word))
						break;
					shortname=shortname.substring(shortname.indexOf(word)+word.length());
				}
				if(shortname.length()==0)
				{
					while(j-->i)
						sentence.remove(index);
					constNameString+=Tagging.TAG_ORG_SHORT;
					sentence.add(index,constNameString);
					changed=true;
				}
			}
		}
		return changed;
	}

	/******************************************
	 * 
	 * 判断一个企业是否是该页面的主要介绍企业
	 * @param fullname
	 * @return true or false
	 * 
	 ******************************************/
	public boolean isMainEnterprise(String fullname, EnterpriseCache eCache){
		String owner=eCache.getOwner();
		String title=eCache.getTitle();
		if(title.contains(fullname)||
				owner.contains(fullname))
			return true;
		if(shortNameInTitle(fullname, title)!=null || 
				(owner.length()>0 && fullname.contains(owner)))
			return true;
		return false;
	}
	
	/*************************************************************
	 * 
	 * 找出句子中的企业名称，并加入到当前页面缓存中
	 * @param sentence 要处理的句子
	 * @param eCache 存储信息的页面缓存
	 * 
	 *************************************************************/
	private void findEnterprise(final List<String> sentence,EnterpriseCache eCache){
		String word;
		for(int i=0;i<sentence.size();i++){
			word=sentence.get(i);
			if(Tagging.isEnterpriseEntity(word))
			{
				word=word.substring(0,word.lastIndexOf("/"));
				if(isMainEnterprise(word, eCache)) 
					eCache.setMainEnterprise(word);		
							 
				if(eCache.EnterpriseExist(word))
					continue;
				else{
					eCache.update(new EnterpriseInfo(word));
				}
			}
		}
	}
	
	/*************************************************************
	 * 
	 * 找出文本中的企业简称并标注，同时将简称添加到页面缓存中
	 * @param sentence 要处理的句子 
	 * @param eCache 存储信息的页面缓存
	 * 
	 *************************************************************/
	public void findShortName(List<String> sentence,EnterpriseCache eCache){
		String fullname,shortname;
		EnterpriseInfo eInfo;
		this.findEnterprise(sentence, eCache);
		
		for(int i=0;i<eCache.numberOfEnterprise();i++)
		{
			eInfo=eCache.getEnterpriseInfo(i);
			if((shortname=eInfo.getShortName())!=null)
				this.shortNameTag(shortname,sentence);
			else {
				fullname=eInfo.getFullName();
				shortname=this.shortNameInTitle(fullname, eCache.getTitle());
				if(shortname==null)
					shortname=this.shortNameRecognize(fullname, sentence);
				if(shortname!=null)	{
					eInfo.setShortName(shortname);
					eCache.update(eInfo);
					this.shortNameTag(shortname,sentence);
				}
			}
		}
	}
	
	/*************************************************************
	 * 
	 * 信息抽取处理函数
	 * @param sentence 要处理的句子
	 * @param eCache 页面缓存
	 * 
	 *************************************************************/
	public void process(final List<String> sentence,EnterpriseCache eCache ,TrademarkInfo tInfo){
		String word,enterprisename,regDate,regAssets,location;
		ArrayList<String> product;
		EnterpriseInfo eInfo=null;
		boolean changed=false;
		for(int i=0;i<sentence.size();i++){
			word=sentence.get(i);
			if(Tagging.isEnterpriseEntity(word))
			{
				enterprisename=word.substring(0,word.lastIndexOf("/"));
				if(eCache.EnterpriseExist(enterprisename))
					eInfo=eCache.getEnterpriseInfo(enterprisename);
				else {
					changed=true;
					eInfo=new EnterpriseInfo(enterprisename);
				}
			}
		}
		if(eInfo==null && (eInfo=eCache.getMainEnterpriseInfo())==null){
			eInfo=eCache.getEnterpriseInfo(0);
			if(eInfo==null)
				eInfo=new EnterpriseInfo("Default Enterprise Name");
		}
		
		for(int i=0;i<sentence.size();i++)
		{
			word=sentence.get(i);
			if(Tagging.isDateEntity(word))
			{
				regDate=tParser.RegDate(sentence,i);
				if(regDate!=null){
					changed=true;
					eInfo.setRegDate(regDate);
				}
			}
			else if(Tagging.isCurrencyEntity(word))
			{
				regAssets=tParser.RegAssets(sentence,i);
				if(regAssets!=null){
					changed=true;
					eInfo.setRegAssets(regAssets);
				}
			}
			else if (this.triggerScope(word)) {
				String proString=tParser.Scope(sentence,i);
				if(proString==null) 
					continue;
				product=iFactory.KeyExtract(proString, RunParam.NumberOfProduct, Tagging.INT_POS_n,Tagging.INT_POS_vn);
				if(product.size()>0){
					changed=true;
					eInfo.addProduct(product);	
				}
			} 
			else if (Tagging.isAddresEntity(word)){
				location=tParser.Location(sentence,i);
				if(location!=null){
					changed=true;
					eInfo.setLocation(location);
				}
			}
			else if(this.isRepresentative(word)){
				String person=tParser.Representative(sentence, i);
				if(person!=null){
					changed=true;
					eInfo.setRepresentative(person);
				}
			}
			else if(this.triggerBrand(word)){
				String brand=tParser.Brand(sentence, i);
				if(brand!=null){
					tInfo.setName(brand);
					tInfo.setProducer(brand);
				}
			}
			else if(this.triggerProduct(word)){
				String pro=tParser.Product(sentence, i);
				if(pro!=null){
					tInfo.setProduct(pro);
				}
			}
		}
		if(changed)
			eCache.update(eInfo);
	}


}