package ois;

import java.util.ArrayList;

import ois.infoExtract.EnterpriseInfo;


public class EnterpriseCache extends PageCache{
	private String mainEnterprise=null;//当前页面主要描述的企业名称，根据页面标题获取
	private ArrayList<EnterpriseInfo> enterpriseList;
	
	public EnterpriseCache(){
		super();
		enterpriseList=new ArrayList<EnterpriseInfo>();
	}
	
	public void setMainEnterprise(String name){
		mainEnterprise=name;
	}
	public String getMainEnterprise(){
		return	mainEnterprise;
	}
	
	public boolean isMainEnterprise(String name){
		return name.equals(mainEnterprise);
	}
	public boolean isMainEnterprise(int index){
		if(index<enterpriseList.size())
			return enterpriseList.get(index).getFullName().equals(mainEnterprise);
		return false;
	}
	public boolean isMainShortName(String shortname){
		if(mainEnterprise==null)
			return false;
		EnterpriseInfo eInfo=this.getEnterpriseInfo(mainEnterprise);
		return shortname.equals(eInfo.getShortName());
	}
	
	public boolean EnterpriseExist(String name){
		for(int i=0;i<enterpriseList.size();i++)
			if(enterpriseList.get(i).getFullName().equals(name))
				return true;
		return false;
	}
	
	public boolean EnterpriseExist(EnterpriseInfo eInfo){
		for(int i=0;i<enterpriseList.size();i++)
			if(enterpriseList.get(i).getFullName().equals(eInfo.getFullName()))
				return true;
		return false;
	}
	
	public EnterpriseInfo getEnterpriseInfo(String name){
		for(int i=0;i<enterpriseList.size();i++)
			if(enterpriseList.get(i).getFullName().equals(name))
				return enterpriseList.get(i);
		return null;
	}
	
	public EnterpriseInfo getEnterpriseInfo(int index){
		if(index<enterpriseList.size())
			return enterpriseList.get(index);
		else {
			return null;
		}
	}
	
	public EnterpriseInfo getMainEnterpriseInfo(){
		for(int i=0;i<enterpriseList.size();i++)
			if(enterpriseList.get(i).getFullName().equals(mainEnterprise))
				return enterpriseList.get(i);
		if(enterpriseList.size()>0)
			return enterpriseList.get(enterpriseList.size()-1);
		return null;
	}
	
	public int numberOfEnterprise(){
		return enterpriseList.size();
	}
	
	public void update(EnterpriseInfo eInfo){
		for(int i=0;i<enterpriseList.size();i++)
			if(enterpriseList.get(i).getFullName().equals(eInfo.getFullName()))
			{
				enterpriseList.remove(i);
				break;
			}
		enterpriseList.add(eInfo);
	}
	
	@Override
	public void print() {
		super.print();
		EnterpriseInfo eInfo;
		for(int i=0;i<enterpriseList.size();i++){
			eInfo=enterpriseList.get(i);
			if(!eInfo.isEmpty()) 
				eInfo.print(eInfo.getFullName().equals(mainEnterprise));
		}
	}
}
