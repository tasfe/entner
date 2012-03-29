package ois.ner.hmm;

/**********************************
 * 
 * HMM参数设置，以及本地数据文件路径
 * @author zhzhl,wxt
 *
 **********************************/
public class HmmConfig {
	public static final String DataFile="conf/hmm.dat";
	public static final String[] Observes={"a","ad","ag","al","an","b","bl",
		"c","cc","d","dg","dl","e","f","h","k","m","Mg","mq","n","nc","ng","nl",
		"nr","nr1","nr2","nrf","nrj","ns","nsf","nt","nz","o","p","pba",
		"pbei","q","qt","qv","r","Rg","rr","ry","rys","ryt","ryv","rz",
		"rzs","rzt","rzv","s","t","tg","u","ude1","ude2","ude3","udeng",
		"udh","uguo","ule","ulian","uls","usuo","uyy","uzhe","uzhi","v",
		"vd","vf","vg","vi","vl","vn","vshi","vx","vyou","w","wb","wd",
		"wf","wj","wky","wkz","wm","wn","wp","ws","wt","ww","wyy","wyz","x","y","z"};
	
	public static final String[] States = {"Brand","Model","Description","Product","Others"};
//	public static final String[] States ={"Rainy","Sunny"};
	
//	public static final String[] Observes={"Walk","Shop","Clean"};
	
	public static int BrandIndex=0;
	public static int ModelIndex=1;
	public static int DescriptionIndex=2;
	public static int ProductIndex=3;
	public static int OthersIndex=4;
	
	public static final double epsilon=0.0000001; 
	
	public static final String TrainingFile="Hmm_Train_Data.txt";
	
	public static boolean isZero(double d){
		return Math.abs(d)<Double.MIN_VALUE;
	}

	 
}
