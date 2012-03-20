
package ois.ner.constant;

/*********************************************************
 * 
 * XML文件标签名称和各项参数
 * @author wxt
 *
 *********************************************************/
public class XMLConfig {
	public static final String CONFIG = "OIS_conf.xml";		
	public static final String CONFIG_PATH = "ConfigPath";
	public static final String CONFIG_KEYWORD = "KeyWord";
	public static final String CONFIG_RULE = "Rule";
	public static final String CONFIG_EXTRACT = "Extract";
	public static final String CONFIG_PRODUCT = "Product";
	public static final String CONFIG_ILLEGAL = "IllegalLib";
	
	public static final String KEY_ROOT = "KeyList";
	public static final String KEY_ITEM = "word";
	public static final String KEY_ORG_LIST= "ORG";
	public static final String KEY_DATE_LIST = "DATE";
	public static final String KEY_LOC_LIST = "LOC";
	public static final String KEY_CUR_LIST = "CURRENCY";
	public static final String KEY_TYPE = "type";
	public static final String KEY_SCOPE = "scope";
	public static final String KEY_TYPE_PRE = "prefix";
	public static final String KEY_TYPE_SUFFIX ="suffix";
	public static final String KEY_TYPE_SUBALTERN = "subaltern";
	
	public static final String RULE_ROOT = "RuleList";
	public static final String RULE_TOTAL = "total";
	public static final String RULE_ITEM = "rule";
	public static final String RULE_POS = "POS";
	public static final String RULE_COUNT = "count";
	

	public static final String EXTRACT_ROOT ="Extract";
	public static final String EXTRACT_KEYWORD = "KeyWord";
	public static final String EXTRACT_KEYWORD_VC = "Vc";
	public static final String EXTRACT_KEYWORD_VE = "Ve";
	public static final String EXTRACT_KEYWORD_VM = "Vm";
	public static final String EXTRACT_KEYWORD_NP = "Np";
	public static final String EXTRACT_KEYWORD_ITEM = "word";
	public static final String EXTRACT_KEYWORD_TYPE = "type";
	
	public static final String EXTRACT_TEMP = "Template";
	public static final String EXTRACT_TEMP_REGDATE = "RegDate";
	public static final String EXTRACT_TEMP_REGASSETS = "RegAssets";
	public static final String EXTRACT_TEMP_SCOPE = "Scope";
	public static final String EXTRACT_TEMP_LOCATION = "Location";
	public static final String EXTRACT_TEMP_REPRESNETATIVE = "Representative";
	public static final String EXTRACT_TEMP_ITEM = "Item";
	public static final String EXTRACT_TEMP_BRAND = "Brand";
	public static final String EXTRACT_TEMP_PRODUCT = "Product";
	
	public static final String ILLEGAL_ROOT = "Illegal";
	public static final String ILLEGAL_ITEM = "Instance";
	public static final String ILLEGAL_OBJECT = "Object";
	public static final String ILLEGAL_ATTRIBUTE = "Attribute";
	public static final String ILLEGAL_CONDITION = "Condition";
	public static final String ILLEGAL_LAW = "Law";
	public static final String ILLEGAL_TERM = "Item"; 
	
}
