
package ois.ner.constant;

/*********************************************************
 * 
 * 词性标注相关的常量和静态变量
 * @author wxt
 *
 *********************************************************/
public class Tagging {
	public static final String regex="/[a-z0-9]+";
	//以下标点符号用于分割文本
	public static final String POS_JUHAO = "/wj";
	public static final String POS_WENHAO = "/ww";
	public static final String POS_TANHAO = "/wt";
	public static final String POS_FENHAO = "/wf";
	public static final String POS_DOUHAO = "/wd";
	public static final String POS_KUOHAO = "/wk";
	public static final String POS_MAOHAO = "/wp";
	public static final String POS_BIAODIAN = "/w";
	
	public static final String POS_VNOUNS = "/vn";
	public static final String POS_NOUNS = "/n";
	public static final String POS_VERB = "/v";
	public static final String POS_ENGLISH = "/x";
	
	public static final int INT_POS_n = 21;
	public static final int INT_POS_nz = 32;
	public static final int INT_POS_nt = 31;
	public static final int INT_POS_vn= 74;
	
	public static final String POS_NUM = "/m";
	public static final String POS_DATE = "/t";
	public static final String POS_ADDR = "/ns";
	public static final String POS_CUR = "/nc";
	public static final String POS_ORG = "/nt";
	public static final String POS_ZHUANYOU = "/nz";
	public static final String POS_PER = "/nr";
	
	public static final String STOP_VSHI = "/vshi"; //动词“是”
	public static final String STOP_RR = "/rr"; //人称代词
	public static final String STOP_PBA = "/pba"; //介词“把”
	public static final String STOP_PBEI = "/pbei";// 介词“被”
	public static final String STOP_ULE = "/ule"; //了 喽
	public static final String STOP_Y = "/y"; // 语气词
	public static final String STOP_P = "/p";  //介词
	public static final String STOP_UDENG = "/udeng"; //等
	
	public static final String TAG_ORG = "/ne_org";
	public static final String TAG_ORG_SHORT = "/nes_org";
	public static final String TAG_LOC = "/ne_loc";    //地点
	public static final String TAG_DATE = "/ne_date";
	public static final String TAG_CUR = "/ne_cur";   //货币
	public static final String TAG_PRO = "/ne_pro";  //产品实体
	public static final String TAG_BRAND = "/ne_brand"; //品牌
	public static final String TAG_PER = "/ne_per";   //人名
	
	public static boolean isNouns(String word) {
		return word.trim().endsWith(POS_NOUNS)||word.trim().endsWith(POS_VNOUNS);
	}
	
	public static boolean isEnglish(String word) {
		return word.trim().endsWith(POS_ENGLISH);
	}
	
	public static boolean isDouhao(String word){
		return word.trim().endsWith(POS_DOUHAO);
	}
	
	public static boolean isNumber(String word){
		return word.trim().endsWith(POS_NUM);
	}
	
	public static boolean isDateWord(String word){
		return word.startsWith(POS_DATE,word.lastIndexOf("/"));
	}	
	public static boolean isDateEntity(String word){
		return word.trim().endsWith(TAG_DATE);
	}
	
	public static boolean isAddresWord(String word){
		return word.startsWith(POS_ADDR,word.lastIndexOf("/"));
	}
	public static boolean isAddresEntity(String word){
		return word.trim().endsWith(TAG_LOC);
	}
	
	public static boolean isPersonWord(String word){
		return word.startsWith(POS_PER, word.lastIndexOf("/"));
	}
	public static boolean isPersonEntity(String word){
		return	word.trim().endsWith(TAG_PER);
	}
	
	public static boolean isCurrencyWord(String word){
		return word.trim().endsWith(POS_CUR);
	}
	public static boolean isCurrencyEntity(String word){
		return word.trim().endsWith(TAG_CUR);
	}	
	
	public static boolean isEnterpriseWord(String word){
		return word.trim().endsWith(POS_ORG);
	}
	public static boolean isEnterpriseEntity(String word){
		return word.trim().endsWith(TAG_ORG);
	}
	
	public static boolean isEntity(String word){
		return word.startsWith("/ne_",word.lastIndexOf("/"));
	}	
	
	/*public static int getEntityType(String word){
		if(word.trim().endsWith(TAG_DATE))
			return NE_DATE;
		if(word.trim().endsWith(TAG_LOC))
			return NE_LOC;
		if(word.trim().endsWith(TAG_CUR))
			return NE_CUR;
		if(word.trim().endsWith(TAG_ORG))
			return NE_ORG;
		if(word.trim().endsWith(TAG_PER))
			return NE_PER;
		if(word.trim().endsWith(TAG_BRAND))
			return NE_BRAND;
		return NOT_NE;
	}	*/
	public static String getEntityType(String word){
		if(isEntity(word))
			return word.substring(word.lastIndexOf("/"));
		else {
			return null;
		}
	}
	
	public static boolean isStopWords(String word){
		if(word.startsWith(STOP_VSHI,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(STOP_RR,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(STOP_PBA,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(STOP_PBEI,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(STOP_ULE,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(STOP_Y,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(STOP_P,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(STOP_UDENG,word.lastIndexOf("/")))
			return true;
		if(word.startsWith(POS_BIAODIAN,word.lastIndexOf("/")) && !word.contains(POS_KUOHAO))
			return true;
		return false;
	}
	
	/**************************************************
	 * 
	 * check whether the word is end of a sentence
	 * @param word string of word with tag
	 *  
	 **************************************************/
	public static boolean endOfSentence(String word){
		if(word.trim().endsWith(Tagging.POS_JUHAO))
			return true;
		if(word.trim().endsWith(Tagging.POS_TANHAO))
			return true;
		if(word.trim().endsWith(Tagging.POS_WENHAO))
			return true;
		return false;
	}

}
