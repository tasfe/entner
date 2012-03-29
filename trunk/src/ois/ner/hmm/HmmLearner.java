package ois.ner.hmm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ois.ner.ICTCLASFactory;


/**********************************
 * 
 * HMM模型学习函数
 * @author zhzhl,wxt
 *
 **********************************/
public class HmmLearner implements Runnable {
	HMM hmm=new HMM(HmmConfig.States.length, HmmConfig.Observes.length);
	ArrayList<Observation>oseq;
	String filename;
	public HmmLearner(String file){
		filename=file;
	}
	 
	private void initDataFile(){
		ICTCLASFactory iFactory =new ICTCLASFactory();
		iFactory.fileProcess(filename, HmmConfig.TrainingFile);
		iFactory.exit();
	}
	
	public ArrayList<Observation> StrToObse(String sentence){
		ArrayList<Observation> oseq=new ArrayList<Observation>();
		String[] posString=sentence.split(" ");
		int index;
		for(int i=0;i<posString.length;i++)
			if((index=posString[i].indexOf("/"))>0)
				oseq.add(new Observation(posString[i].substring(index+1)));
		return oseq;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		this.initDataFile();
		String sentence;
		ArrayList<Observation> oseq;
		try {
			BufferedReader bReader=new BufferedReader(new FileReader(HmmConfig.TrainingFile));
			while((sentence=bReader.readLine())!=null){	
				if((oseq=this.StrToObse(sentence)).size()<2)
					continue;
				if(hmm.Baum_Welch(oseq))
//					hmm.SaveModel();
					;
				else  
					break;
			}
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();return;
		} catch (IOException e) {
			e.printStackTrace();return;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new HmmLearner("HmmTrain.txt")).start();
	}

}
