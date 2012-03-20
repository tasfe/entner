package ois.verify;

public class ConditionParser {
	
	public static boolean isLessThan(String str){
		return "LessThan".equals(str);
	}
	public static boolean isMoreThan(String str){
		return "MoreThan".equals(str);
	}
	public static boolean isContain(String str){
		return "Contain".equals(str);
	}
	public static boolean isDifferent(String str){
		return "Different".equals(str);
	}
	public static boolean isOR(String str){
		return "OR".equals(str);
	} 
	public static boolean isAND(String str){
		return "AND".equals(str);
	} 
}
