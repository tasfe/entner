package ois.ner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ois.StringParser;
import ois.ner.constant.Tagging;
import ois.ner.hmm.HMM;
import ois.ner.hmm.HmmConfig;
import ois.ner.hmm.Observation;

/******************************************
 *
 * 商品识别和分类
 * @author zhzhl
 *
 ******************************************/
public class CommodityAnalyzer {
	HMM hmm;
	LinkedList<String> proList;
	public CommodityAnalyzer(){
		hmm=new HMM(HmmConfig.States.length, HmmConfig.Observes.length);
		proList=getProductList(11);
	}
	public boolean isProduct(String word){
		
		System.err.println(proList);
		return proList.contains(word);
	}
	
	/*******************************************
	 *
	 * 采用HMM模型进行商品实体的识别和标注
	 * @param sentence 句子，词列表
	 *
	 *******************************************/
	public void Recognize(List<String> sentence){
		ArrayList<Observation> oseq = new ArrayList<Observation>();
		String word;
		int start=0;
		for(int i=0;i<sentence.size();i++){
			word=sentence.get(i);
			oseq.add(new Observation(word.substring(word.lastIndexOf("/")+1)));
		}
		int[] states=hmm.Viterbi(oseq);
		int endIndex ;
		int i=0;
		while(i<states.length){
			System.out.print(states[i]+" ");
			if(states[i]==HmmConfig.BrandIndex){
				if((endIndex=StateCheck(states,i))>i){
					for(int j=i;j<=endIndex;j++)
						preEntityTag(sentence, j, j, states[j]);
				};
				i=endIndex+1;
			}
			else {
				i++;
			}
		}
		this.EntityTag(sentence);
	}
	
	/***************************************
	 *
	 * 商品实体预标注，将属于商品实体的词特殊标注，但保留在原位置，
	 * @param sentence 句子
	 * @param start 第一个词的下标
	 * @param end 最后一个词的下标
	 * @param state 状态，0为Brand，1为Model, 2为Description, 3为Product
	 * 
	 ***************************************/
	public void preEntityTag(List<String> sentence,int start, int end, int state){
		String word;
		int length=end-start;
		int k=0;
		String tagString=(state==HmmConfig.BrandIndex)?Tagging.TAG_BRAND:Tagging.TAG_PRO;
		while(k<=length){
			word=sentence.get(start+k);
			word=word.substring(0, word.lastIndexOf("/"))+tagString;
			sentence.set(start+k, word);
			k++;
		}
	}
	
	/*****************************************************
	 * 
	 * 检查状态序列是否符合商品名称格式
	 * @param states 状态序列
	 * @param start 起始下标
	 * @return 结束下标，若等于起始下标，则该状态序列不属于商品
	 * 
	 *****************************************************/
	public int StateCheck(int[]states, int start){
		int end=start;
		String stateString="";
		for(int i=start;i<states.length;i++){
			if(states[i]!=HmmConfig.OthersIndex)
				stateString+=states[i];
			else {
				break;
			}
		}
		if(stateString.matches("[0]+[1]*[2]*[3]+") || 
				stateString.matches("[0]+[2]+[3]*[1]+"))
			end+=stateString.length()-1;
		return end;
	}
	
	/***************************************
	 *
	 * 商品实体标注
	 * @param sentence 句子
	 * 
	 ***************************************/
	public void EntityTag(List<String> sentence){
		String word,entity="";
		boolean inbrand=false;
		int i=0;
		while(i<sentence.size()){
			word=sentence.get(i);
			if(word.endsWith(Tagging.TAG_BRAND)){
				if(!inbrand)
					entity+="<Brand>"+word.substring(0,word.lastIndexOf("/"));
				else {
					entity+=word.substring(0,word.lastIndexOf("/"));
				}
				inbrand=true;
				sentence.remove(i);
			}
			else if(word.endsWith(Tagging.TAG_PRO)){
				if(inbrand)	{
					entity+="</Brand>"+word.substring(0,word.lastIndexOf("/"));
					inbrand=false;
				}
				else {
					entity+=word.substring(0,word.lastIndexOf("/"));
				}
				sentence.remove(i);
			}
			else{
				if(entity.length()>0)
				{
					entity+=Tagging.TAG_PRO;
					sentence.add(i, entity);
					entity="";
				}
				i++;
			}
		}
	}
	
	public LinkedList<String> getProductList(int category){
		try {
			BufferedReader bReader=new BufferedReader(
					new InputStreamReader(new FileInputStream("conf/pname.txt"), "gbk"));
			String lineString;
			while((lineString=bReader.readLine())!=null){
				if(lineString.matches("<[0-9]+>")){
					if(category!=Integer.valueOf(lineString.substring(1,lineString.indexOf(">"))))
						continue;
					else {
						lineString=bReader.readLine();
						break;
					}
				}
			}
			return new StringParser().splitToArray(lineString);
		} catch (FileNotFoundException e) {
			e.printStackTrace();return null;
		} catch (IOException e) {
			e.printStackTrace();return null;
		}
	}
}
