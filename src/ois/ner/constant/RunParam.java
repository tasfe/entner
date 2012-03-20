
package ois.ner.constant;

/***************************************
 * Some parameters we used in the project
 * @author wxt
 ***************************************/
public class RunParam {
	public static final int MaxLengthOfSentence=200;
	public static final int MinLengthOfSentence=3;
	
	public static final int MaxLengthOfEnterprise=7;
	
	//企业字号最大长度，单位：词
	public static final int LengthOfEnterpriseName = 3;
	//前缀
	public static final int Prefix = 1;
	//后缀
	public static final int Suffix = 2;
	//单独成实体的词
	public static final int NeWord= 0;
	//The max length of a single address word ends with one of the suffix word
	public static final int LengthOfAddress = 5;
	
	public static final int Template_RegDate = 1;
	public static final int Template_RegAssets = 2;
	public static final int Template_Location = 3;
	public static final int Template_Scope = 4;
	public static final int Template_Brand = 5;
	public static final int Template_Product = 6;
	
	public static final int NumberOfProduct = 2;
	//从经营范围字符串中抽取的关键词个数
	
}
