
package ois;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import ois.infoExtract.Extractor;
import ois.infoExtract.TrademarkInfo;
import ois.ner.CommodityAnalyzer;
import ois.ner.ICTCLASFactory;
import ois.ner.NEAnalyzer;
import ois.ner.constant.RunParam;
import ois.verify.Verify;
import ois.xmlParser.HTMLParser;

/*********************************************************
 * 
 * 网页处理，线程类
 * 调用分词模块和信息抽取模块完成对一个网页的处理
 * @author wxt
 * 
 *********************************************************/
public class Analyzer extends HTMLParser implements Runnable {
	private EnterpriseCache eCache;
	private TrademarkInfo tInfo;
	private StringParser strParser;
	private ICTCLASFactory iFactory;
	private NEAnalyzer nAnalyzer;
	private Extractor extractor;
	private CommodityAnalyzer cAnalyzer;
	private Verify eVerify;
	
	/************************************
	 * 
	 * 构造函数
	 * @param url URL or file name
	 * 
	 ************************************/
	public Analyzer(String url){
		super(url);
		eCache=new EnterpriseCache();
		tInfo=new TrademarkInfo();
		strParser=new StringParser(super.content);
		iFactory=new ICTCLASFactory();
		nAnalyzer=new NEAnalyzer();
		extractor=new Extractor(iFactory);
		//cAnalyzer=new CommodityAnalyzer();
	}

	public static void main(String[] args) {
		Analyzer analyzer=new Analyzer("http://www.hengshengtang.com/qiyejianjie/qiyejianjie.htm");
		new Thread(analyzer).start();
//		analyzer.test("Test.txt");
	}
	
	public void run(){
		this.eCache.setTitle(this.getTitle());
		this.eCache.setURL(this.getURL());
		this.eCache.setOwner(this.getOwner());
		System.out.println(content);
		String sString;
		LinkedList<String> sentence=new LinkedList<String>();
		while(!strParser.isEmpty()){
				sString=strParser.readSentence();
				sentence=strParser.splitToArray(iFactory.split(sString));
				if(sentence.size()<RunParam.MinLengthOfSentence)
					continue;
//				cAnalyzer.Recognize(sentence);
//				System.out.println(sentence);
				nAnalyzer.Process(sentence);
				extractor.findShortName(sentence, eCache);
//				System.out.println(sentence);
				extractor.process(sentence, eCache,tInfo);
				
		}
		eCache.print();
//		tInfo.print();
//		eVerify=new Verify();
//		eVerify.process(eCache,tInfo);
		iFactory.exit();
		
	}

	public void test(String filename){
		try {
			BufferedReader bReader=new BufferedReader(new FileReader(filename));
//			BufferedWriter bWriter=new BufferedWriter(new FileWriter("NER_TEST.txt"));
			StringParser sParser=new StringParser();
			String content;
			String sentencestr;
			LinkedList<String> sentence=new LinkedList<String>();
			while((content=bReader.readLine())!=null){
				if(content.length()==0)
					continue;
				sParser.setContent(content);
				while(!sParser.isEmpty()){
					sentencestr=sParser.readSentence();
					sentence=sParser.splitToArray(iFactory.split(sentencestr));
					nAnalyzer.Process(sentence);
//					bWriter.write(sentence.toString()+"\n");
					System.out.println(sentence);
				}
			}
			bReader.close();
//			bWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**************************
	 * 
	 * 获取网页版权所有者
	 * @return 版权所有者或者空字符串
	 * 
	 **************************/
	public String getOwner(){
		String owner,tmpString;
		int index1 = content.lastIndexOf("版权所有");
		int index2 = content.lastIndexOf("Copyright"); 
		int index3 = content.lastIndexOf("©");
		int index4 = content.lastIndexOf("(C)");
		int index;
		index=(index1>index2)?index1:index2;
		index=(index>index3)?index:index3;
		index=(index>index4)?index:index4;
		if(index<0)
			return "";
		owner=content.substring(index).trim();
		if((index=owner.indexOf(" "))<0)
			return "";
		owner=owner.substring(index).trim();
		if((index1=owner.indexOf(" "))<0)
			return owner;
		tmpString=owner.substring(0,index1).trim();
		if(tmpString.matches("[0-9]+(-[0-9]+)?"))
			owner=owner.replace(tmpString, "").trim();
		owner=owner.replaceAll("[\t\r\n]+", " ");
		if((index=owner.indexOf(" "))>0)
			return owner.substring(0,index);
		else  
			return owner;
	}
}
