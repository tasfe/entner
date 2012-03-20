package ois.ner;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ois.StringParser;

import ICTCLAS.kevin.zhang.ICTCLAS2011;


/***************************************************
 * 
 * ICTCLAS2011工厂类，封装了ICTCLAS2011的对象和方法，
 * 提供了分词、关键词提取等相关功能
 * @author andy
 * 
 ****************************************************/
public class ICTCLASFactory {
	
	
	private ICTCLAS2011 ictclas2011;
	
	/************************
	 * 
	 * 默认构造函数,加载用户词典
	 * 
	 ************************/
	public ICTCLASFactory(){
		this.init(0);
	}
	
	public ICTCLASFactory(int flag){
		this.init(flag);
	}
	
	/********************************************
	 * 
	 * @function init
	 * @description 初始化ICTCLAS对象
	 * @param flag 0为加载用户词典，否则不加载
	 *
	 ********************************************/
	private void init(int flag){
		ictclas2011=new ICTCLAS2011();
		int k=0;
		try { //ictclas2011.ICTCLAS_Init(sDataPath, encoding)
			while(!ictclas2011.ICTCLAS_Init(new String(".").getBytes("gbk"),0) && k++<5) {
				System.err.println("Init Fail!");
			}
			if(flag==0)
				ictclas2011.ICTCLAS_ImportUserDict(new String("data/userdict.txt").getBytes("gbk"));
			ictclas2011.ICTCLAS_SetPOSmap(0);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); 
		}
	}
	
	/*********************************
	 * 
	 * 分词，直接调用分词包中功能
	 * @param str 任意字符串
	 * @return 分词后带标注的字符串
	 *
	 *********************************/
	public String split(String str){
		try {	
			str=str.replaceAll("[\\s]+", "");
			byte[] bytes=ictclas2011.ICTCLAS_ParagraphProcess(str.getBytes("gb2312"), 1);
			String nativeString;
			nativeString = new String(bytes,"gb2312");
			return nativeString;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); return "";
		}
	}
	
	public LinkedList<String> splitToList(String str){
		
		String nativeString =this.split(str);
		return new LinkedList<String>(Arrays.asList(nativeString.split(" ")));

	}
	
	public List<String> splitWithoutTag(String str){
		try {	
			ictclas2011.ICTCLAS_SetPOSmap(2);
			str=str.replaceAll("[\\s]+", "");
			byte[] bytes=ictclas2011.ICTCLAS_ParagraphProcess(str.getBytes("gbk"), 0);
//			ArrayList<String> nativeStr = Arrays.asList(bytes);
//			Collections.
			String[] nativeString;
			String temp =new String(bytes,"gb2312");
			nativeString = temp.substring(0, temp.length()-1).split(" ");
			List<String> words = Arrays.asList(nativeString);
			return words;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); return null;
		}
	}
	
	/***********************************************
	 *
	 * 提取指定词性和数量的关键词
	 * @param str 待处理字符串
	 * @param numOfKey 要提取的关键词数量
	 * @param pos 若干指定的词性 
	 * @return 提取出的关键词列表
	 *           
	 ***********************************************/
	public ArrayList<String> KeyExtract(String str,int numOfKey,int... pos){
		ArrayList<String> keyList=new ArrayList<String>();
		if(ictclas2011==null)
			return keyList;
		int nCountKey = 0;
		try {
		    byte[] nativeBytes =ictclas2011.nativeProcAPara(StringParser.BtoQ(str).getBytes("GB2312"));
		    int nativeElementSize = ictclas2011.ICTCLAS_GetElemLength(0);
//		    int nativeElementSize = 4*6+8;
		    //size of result_t in native code
		    int nElement = nativeBytes.length / nativeElementSize;

		    nativeBytes = new byte[nativeBytes.length];
		    nCountKey = ictclas2011.ICTCLAS_KeyWord(nativeBytes, nElement);
	
		    Result[] resultArr = new Result[nCountKey];
		    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(nativeBytes));
			int iSkipNum;
			for (int i = 0; i < nCountKey; i++)
			{
				resultArr[i] = new Result();
				resultArr[i].start = Integer.reverseBytes(dis.readInt());
				iSkipNum = ictclas2011.ICTCLAS_GetElemLength(1) - 4;
				if (iSkipNum > 0)
					dis.skipBytes(iSkipNum);
				resultArr[i].length = Integer.reverseBytes(dis.readInt());
				iSkipNum = ictclas2011.ICTCLAS_GetElemLength(2) - 4;
				if (iSkipNum > 0)
					dis.skipBytes(iSkipNum);
				dis.skip(ictclas2011.ICTCLAS_GetElemLength(3));
				resultArr[i].iPOS = Integer.reverseBytes(dis.readInt());
				iSkipNum = ictclas2011.ICTCLAS_GetElemLength(4) - 4;
				if (iSkipNum > 0)
					dis.skipBytes(iSkipNum);
				resultArr[i].word_ID = Integer.reverseBytes(dis.readInt());
				iSkipNum = ictclas2011.ICTCLAS_GetElemLength(5) - 4;
				if (iSkipNum > 0)
					dis.skipBytes(iSkipNum);
				resultArr[i].word_type = Integer.reverseBytes(dis.readInt());
				iSkipNum = ictclas2011.ICTCLAS_GetElemLength(6) - 4;
				if (iSkipNum > 0)
					dis.skipBytes(iSkipNum);
				resultArr[i].weight = Integer.reverseBytes(dis.readInt());
				iSkipNum = ictclas2011.ICTCLAS_GetElemLength(7) - 4;
				if (iSkipNum > 0)
					dis.skipBytes(iSkipNum);
			}
			dis.close();
			
			int start,length;
			for (int k=0,i=0; i<resultArr.length && k<numOfKey ; i++)
			{
				if(resultArr[i].start%2!=0)
					start=(resultArr[i].start+1)/2;
				else {
					start=(resultArr[i].start)/2;
				}
				length=resultArr[i].length/2;
				for(int j=0;j<pos.length;j++)
					if(resultArr[i].iPOS==pos[j])
					{
						keyList.add(str.substring(start, start+length));
						k++;
					}
				
			}
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return keyList;
	}
	
	/*********************************************
	 * 
	 * 对文件中的文本进行分词，输出结果到另一个文件
	 * @param in 输入文本文件
	 * @param out 输出文件
	 * 
	 *********************************************/
	public void  fileProcess(String infile,String outfile ){
		try {
			ictclas2011.ICTCLAS_FileProcess(infile.getBytes("gb2312"),outfile.getBytes("gb2312"),1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get the prob of a particular word 
	 * 
	 */
	public float getProb(String word){
		try{
			return ictclas2011.ICTCLAS_GetUniProb(word.getBytes("GB2312"));
		}catch(UnsupportedEncodingException e){
			System.err.println("编码不匹配");
		}
		return 0.f;
		
	}
	public void exit(){
		this.ictclas2011.ICTCLAS_Exit();
	}
	
	/**********************************
	 * 
	 * 关键词提取处理结果数据结构
	 * @author wxt
	 *
	 **********************************/
	class Result{
	  int start; //start position,词语在输入句子中的开始位置
	  int length; //length,词语的长度
	  char[] sPOS = new char[8];
	  int	iPOS;//词性
	  int word_ID; //如果是未登录词，设成或者-1
	  int word_type; //区分用户词典;1，是用户词典中的词；，非用户词典中的词
	  int weight;// word weight
	}
	/**
	 * 用户的测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{
			ICTCLASFactory ifa=new ICTCLASFactory();
			ifa.init(0);
			String sInput = "中国石油天然气集团公司（简称中国石油集团）是一家集油气勘探开发、炼油化工、油品销售、油气储运、石油贸易、工程技术服务和石油装备制造于一体的综合性能源公司。在世界50家大石油公司中排名第5位";

			//分词
			ifa.split(sInput);

			//对UTF8进行分词处理
			//SplitUTF8();

			//对BIG5进行分词处理
			//SplitBIG5();
		}
		catch (Exception ex)
		{
		} 


	}
}