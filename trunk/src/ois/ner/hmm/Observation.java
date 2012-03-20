package ois.ner.hmm;

public class Observation {
	private String POS;
	
	/***************************************
	 * 
	 * 观察值构造函数
	 * @param posString 词性标注，不包括'/'
	 * 
	 ***************************************/
	public Observation(String posString){
		this.POS=posString;
	}
	
	/************************************
	 * 
	 * @return 观察值在词性列表中的下标,若不存在则返回-1
	 * 
	 ************************************/
	public int getIndex(){
		for(int i=0;i<HmmConfig.Observes.length;i++)
			if(HmmConfig.Observes[i].equals(POS))
				return i;
		return -1;
	}
	
	public String toString(){
		return this.POS;
	}
}
