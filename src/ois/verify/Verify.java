package ois.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import ois.EnterpriseCache;
import ois.infoExtract.EnterpriseInfo;
import ois.infoExtract.TrademarkInfo;
import ois.ner.constant.StringConst;

public class Verify {
	private LibParser lParser;

	public Verify(){
		lParser=new LibParser();
	}
 
	public void PrintLaw() {

	}
	
	/**********************************************
	 * 返回公司类型，有限责任公司或者股份有限公司
	 * @param fullname 企业名称
	 * @return 有限责任公司 或者 股份有限公司
	 **********************************************/
	public String getCompanyType(String fullname){
		String type;
		if(fullname.endsWith("股份有限公司")||fullname.endsWith("股份公司"))
			type="股份有限公司";
		else if(fullname.endsWith("有限公司")||fullname.endsWith("有限责任公司"))
			type="有限责任公司";
		else {
			type="企业";
		}
		return type;
	}
	
	public boolean isEnterpriseExist(String fullname){
		return fullname!=null;
	}	
	
	/*****************************************
	 * 
	 * 企业注册资本最少要求判断
	 * @param eInfo 企业信息
	 * @return true or false
	 * 
	 *****************************************/
	public boolean checkMiniRegAssets(EnterpriseInfo eInfo){
		String fullname=eInfo.getFullName();
		String [] strings;
		long currency=eInfo.getRegAssets();
		if(currency==0)
			return true;
		String obj=getCompanyType(fullname);
		String condition=lParser.getCondition(obj, StringConst.ENTERPRISE_REG_ASSEST);
		if(condition==null)
			return true;
		strings=condition.split(" ");
		if(ConditionParser.isLessThan(strings[0]))
			if(currency<StringConst.StrToLong(strings[1]))
				return false;
		return true;
	}

	/*********************************
	 * 检查注册资金
	 * @param eInfo 抽取到的企业信息
	 * @param currency 查询到的注册资金数目
	 * @return true or false
	 *********************************/
	public boolean checkRegAssets(EnterpriseInfo eInfo, long currency){
		if(eInfo.getRegAssets()>currency)
			return false;
		return true;
	}
	
	/*********************************
	 * 检查成立日期
	 * @param eInfo 抽取到的企业信息
	 * @param date 查询到的成立日期
	 * @return true or false
	 *********************************/
	public boolean checkRegDate(EnterpriseInfo eInfo, Date date){
		int year=0, month=0, day=0;
		int index;
		String tmpString;
		String dateString=eInfo.getRegDate();
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		if(dateString==null)
			return true;
		if((index=dateString.indexOf("年"))>0){
			tmpString=dateString.substring(0,index);
			dateString=dateString.substring(index+1);
			if(tmpString.matches("[^0-9]*[0-9]+"))
				year=Integer.valueOf(tmpString.replaceAll("[^0-9]+",""));
		}
		if((index=dateString.indexOf("月"))>0){
			tmpString=dateString.substring(0,index);
			dateString=dateString.substring(index+1);
			if(tmpString.matches("[^0-9]*[0-9]+"))
				year=Integer.valueOf(tmpString.replaceAll("[^0-9]+",""));
		}
		if((index=dateString.indexOf("日"))>0){
			tmpString=dateString.substring(0,index);
			dateString=dateString.substring(index+1);
			if(tmpString.matches("[^0-9]*[0-9]+"))
				year=Integer.valueOf(tmpString.replaceAll("[^0-9]+",""));
		}
		if(year!=0 && calendar.get(Calendar.YEAR)!=year)
			return false;
		else if(month!=0 && calendar.get(Calendar.MONTH)!=month)
			return false;
		else if(day!=0 && calendar.get(Calendar.DATE)!=day)
			return false;
		return true;
	}
	
	/*********************************
	 * 检查经营范围
	 * @param eInfo 抽取到的企业信息
	 * @param scope 查询到的经营范围
	 * @return true or false
	 *********************************/
	public boolean checkScope(EnterpriseInfo eInfo,String scope){
		ArrayList<String> products=eInfo.getProduct();
		if(products.size()>0){
			for(int i=0;i<products.size();i++)
				if(!scope.contains(products.get(i)))
					return false;
		}
		return true;
	}

	public boolean checkLocation (EnterpriseInfo eInfo,String loc){
		String location=eInfo.getLocation();
		if(location==null)
			return true;
		if(location.contains(loc)||loc.contains(location))
			return true;
		return false;
	}
	
	public boolean checkType(EnterpriseInfo eInfo, String type){
		String localType=getCompanyType(eInfo.getFullName());
		if(type.contains(localType))
			return true;
		return false;
	}
	
	
	
	public boolean checkBrandName(String brandName){
		return brandName!=null;
	}
	public boolean checkBrandRegDate(Date regdate){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(regdate);
		int year=calendar.get(Calendar.YEAR);
		if(Calendar.getInstance().get(Calendar.YEAR)>year+10)
			return false;
		return true;
	}
	public boolean checkBrandOwner(TrademarkInfo tInfo, String owner){
		return owner.contains(tInfo.getProducer());
	}
	
	public boolean checkBrandType(TrademarkInfo tInfo, String type){
		return type.equals(StringConst.TRADEMARK_TYPE[0]);
	}
	
	public void process(EnterpriseCache eCache,	 TrademarkInfo tInfo){
		EnterpriseInfo eInfo;
		if(!checkBrandRegDate(Calendar.getInstance().getTime())){
			System.err.println("商标已过期");
			System.err.println("商标名称："+tInfo.getName());
		}	
		if(checkBrandOwner(tInfo,new String("海尔集团"))){
			System.out.println("OK");
			System.out.println("商标持有者为海尔集团");
			System.err.println("商标名称："+tInfo.getName());
		}
		for(int i=0;i<eCache.numberOfEnterprise();i++)
		{
			eInfo=eCache.getEnterpriseInfo(i);
//			if(!checkRegDate(eInfo,Calendar.getInstance().getTime())){
//				System.err.println("成立日期不正确");
//				System.err.println("企业名称： "+eInfo.getFullName());
//			}
			if(!checkLocation(eInfo,"浙江省")){
				System.err.println("企业住所不正确");
				System.err.println("企业名称： "+eInfo.getFullName());
			}
		}
	}
}
