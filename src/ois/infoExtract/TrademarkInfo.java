package ois.infoExtract;

import org.hamcrest.core.Is;

import ois.ner.constant.StringConst;


public class TrademarkInfo implements Cloneable {
	//商标， 产品名称,生产者
	private String trademark=null,product=null,producer=null;
	//国际分类,商标类型
	private int category=0,type=0;
	public TrademarkInfo(String name){
		this.trademark=name;
	}
	public TrademarkInfo(){
	}
	
	public void setName(String name){
		this.trademark=name;
	}
	public void setProduct(String pro){
		this.product=pro;
	}
	public void setProducer(String str){
		this.producer=str;
	}
	public void setCategory(int cate){
		this.category=cate;
	}
	public void setType(int t ){
		this.type=t;
	}
	public String getName(){
		return trademark;
	}
	public String getProduct( ){
		return this.product ;
	}
	public String getProducer(){
		return this.producer;
	}
	public int getCategory(){
		return this.category;
	}
	public int getType(){
		return this.type;
	}
	
	public boolean equals(TrademarkInfo tInfo){
		return (trademark.equals(tInfo.getName()) && category==tInfo.getCategory());
	}
	
	public void print(){
		if(trademark==null)
			return;
		System.out.println("*******************************");
		System.out.println("商标名称： "+this.trademark);
		System.out.println("商品名称： "+this.product);
		System.out.println("生产厂家： "+this.producer);
		System.out.println("国际分类号： "+(this.category==0?null:category));
		System.out.println("商标类型: "+StringConst.TRADEMARK_TYPE[this.type]);
		System.out.println("*******************************");
	}
}
