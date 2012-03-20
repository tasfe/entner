
package ois.infoExtract;

import java.util.ArrayList;

import ois.ner.constant.StringConst;

/***************************************
 * @author wxt
 ***************************************/
public class EnterpriseInfo implements Cloneable{
	//全称
	private String fullName;
	//简称
	private String shortName=null;
	//地址
	private String location=null;
	//注册日期
	private String regDate=null;
	//产品、经营范围
	private ArrayList<String> product;
	//注册资金,单位万元
	private long regAssets=0;
	//注册号
	private String regNumber=null;
//	法人代表
	private String representative=null;
	
	public EnterpriseInfo(String name){
		this.fullName=name;
		product= new ArrayList<String>();
	}
	
	public void setLocation(String loc){
		this.location=loc;
	}
	public void setShortName(String sname){
		this.shortName=sname;
	}
	public void setRegDate(String regdateString){
		this.regDate=regdateString;
	}
	public void setProduct(ArrayList<String> products){
		this.product.clear();
		this.product.addAll(products);
	}
	public void addProduct(ArrayList<String>  pro){
		for(int i=0;i<pro.size();i++)
		 	if(!product.contains(pro.get(i)))
		 		product.add(pro.get(i));
	}
	public void setRegAssets(String regassetString){
		this.regAssets=StringConst.StrToLong(regassetString)/10000;
	}
	public void setRegNumber(String regnum){
		this.regNumber=regnum;
	}
	public void setRepresentative(String per){
		this.representative=per;
	}
	
	
	public String getFullName(){
		return this.fullName;
	}
	public String getShortName(){
		return this.shortName;
	}
	public String getLocation(){
		return this.location;
	}
	public ArrayList<String> getProduct(){
		return this.product;
	}
	public String getRegDate(){
		return this.regDate;
	}
	public long getRegAssets(){
		return this.regAssets;
	}
	public String getRegNumber(){
		return this.regNumber;
	}
	public String getRepresentative() {
		return representative;
	}
	
	public boolean isEmpty(){
		if(shortName!=null || regDate!=null || regAssets!=0 || location!=null||
				product.size()>0 ||regNumber!=null)
			return false;
		return true;
	}
	
	public void print(boolean maincompany){
		String str=(maincompany)?"*":"";
		System.out.println("*******************************");
		System.out.println("企业名称： "+this.fullName+str);
		System.out.println("简称： "+this.shortName);
//		System.out.println("法定代表人： "+this.representative);
		System.out.println("经营范围： "+this.product);
		System.out.println("成立日期: "+this.regDate);
		System.out.println("注册资金： "+((this.regAssets==0)?null:regAssets));
		System.out.println("联系地址： "+this.location);
		System.out.println("*******************************");
	}
}
