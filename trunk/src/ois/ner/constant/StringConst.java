package ois.ner.constant;


public class StringConst {
	private static final String[] specialChars ={"|","%","+","*","<",">","┆"};
	
	public static final String KEYWORD_Ve = "Ve";
	public static final String KEYWORD_Vc = "Vc";
	public static final String KEYWORD_Vm = "Vm";
	public static final String KEYWORD_Np = "Np";
	
	public static String[] tokens={"LessThan","MoreThan",
		"Contain","Or","And","Not"};

	public static final String ENTERPRISE_NAME= "NAME";
	public static final String ENTERPRISE_REG_ASSEST= "REG_ASSEST";
	public static final String ENTERPRISE_BUSSINESS_SCOPE = "BUSSINESS_SCOPE";
	public static final String ENTERPRISE_REG_DATE = "REG_DATE";
	public static final String ENTERPRISE_REG_NUMBER = "REG_NUMBER";
	
	public static final String[] TRADEMARK_TYPE={"一般商标","集体商标","证明商标 "};
	public static final String[] ENTERPRISE_TYPE={"企业","公司","股份有限公司",
		"有限责任公司","非公司企业","一人独资企业","合伙企业"};
	
	public static boolean isToken(String str){
		for(int i=0;i<StringConst.tokens.length;i++)
			if(StringConst.tokens[i].equals(str))
				return true;
		return false;
	}
	
	public static boolean isSpecialChars(String word){
		for(int i=0;i<specialChars.length;i++)
			if(word.contains(specialChars[i]))
				return true;
		return false;
	}
	

	/*****************************************
	 * 
	 * 将汉语描述的数字转化为Long
	 * @param str 汉语描述的数字
	 * @return Long型数字
	 *****************************************/
	public static long StrToLong(String str){
		long result=0;
		String numString="";
		try{
			result=Long.valueOf(str);
		}catch(NumberFormatException e){
		for(int i=0;i<str.length();i++){
			if(str.charAt(i)>='0' && str.charAt(i)<='9') 
				numString+=str.charAt(i);
			else if(str.charAt(i)=='一')
				numString+=1;
			else if(str.charAt(i)=='二')
				numString+=2;
			else if(str.charAt(i)=='三')
				numString+=3;
			else if(str.charAt(i)=='四')
				numString+=4;
			else if(str.charAt(i)=='五')
				numString+=5;
			else if(str.charAt(i)=='六')
				numString+=6;
			else if(str.charAt(i)=='七')
				numString+=7;
			else if(str.charAt(i)=='八')
				numString+=8;
			else if(str.charAt(i)=='九')
				numString+=9;
			else if(str.charAt(i)=='十'){
				if(numString.length()==0){
					numString+=1;
				}
			}
			else if(str.charAt(i)=='百'){
				if(numString.length()>0){
					numString+="00";
					result+=Long.valueOf(numString);
					numString="";
				}
			}
			else if(str.charAt(i)=='千'){
				if(numString.length()>0){
					numString+="000";
					result+=Long.valueOf(numString);
					numString="";
				}
			}
			else if(str.charAt(i)=='万'){
				if(numString.length()>0){
					numString+="0000";
					if(result<Long.valueOf(numString))
						result=result*10000+Long.valueOf(numString);
					else {
						result+=Long.valueOf(numString);
					}
					numString="";
				}
				else {
					result*=10000;
				}
			}
			else if(str.charAt(i)=='亿')
				if(numString.length()>0){
					numString+="00000000";
					if(result<Long.valueOf(numString))
						result=result*100000000+Long.valueOf(numString);
					else {
						result+=Long.valueOf(numString);
					}
					numString="";
				}
				else {
					result*=100000000;
				}
			else {
				break;
			}
		} 
		if(numString.length()>0)
			result+=Long.valueOf(numString);
		}
		return result;
	}
}
