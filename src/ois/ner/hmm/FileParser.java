package ois.ner.hmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/************************************
 * 
 * 参数保存文件的操作类，用于保存和读取参数
 * @author zhzhl,wxt
 *
 ************************************/
public class FileParser {
	File datafile;
	BufferedReader bReader=null;
	public FileParser(){
		datafile=new File("conf/hmm.dat");
	}
	
	public boolean open(){
		try {
			bReader=new BufferedReader(new FileReader(datafile));
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
			return false;
		}
		return true;
	}
	
	public boolean canRead(){
		try {
			if(bReader==null || !bReader.ready())
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void close(){
		try {
			bReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*************************************************
	 * 
	 * 将模型参数保存到文件中
	 * @param A 与时间无关的状态转移概率矩阵
	 * @param B 给定状态下，观察值概率分布
	 * @param Pi 初始状态空间的概率分布
	 * @param N 状态数目
	 * @param M 每个状态可能的观察值数目
	 * 
	 *************************************************/
	public void write(double[][] A, double[][] B, double[] Pi,int N, int M){
		try {
			int i,j;
			FileWriter fWriter = new FileWriter(datafile);
			fWriter.write("<A>\n");
			for(i=0;i<N;i++){
				for(j=0;j<N;j++) 
					fWriter.write(String.valueOf(A[i][j])+" ");
				fWriter.write("\n");
			}
			fWriter.write("\n<B>\n");
			for(i=0;i<N;i++){
				for(j=0;j<M;j++) 
				fWriter.write(String.valueOf(B[i][j])+" ");
				fWriter.write("\n");
			}
			fWriter.write("\n<Pi>\n");
			for(i=0;i<N;i++)
				fWriter.write(String.valueOf(Pi[i])+" ");
			fWriter.write("\n");
			fWriter.flush();
			fWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	public void readA(double[][] A,int N)throws IOException{
		String lineString;
		int i,j,index;
		while((lineString=bReader.readLine())!=null)
			if(lineString.equals("<A>")){
				for(i=0;i<N;i++){
					lineString=bReader.readLine().trim();
					String[] doubleStrings=lineString.split(" ");
					index=0;
					for(j=0;j<doubleStrings.length;j++){
						if(doubleStrings[j].length()>=0){
							A[i][index]=Double.valueOf(doubleStrings[j]);
							index++;
						}
					}
				}
				break;
			}
	}
	
	public void readB(double[][] B,int N,int M) throws IOException {
		String lineString;
		int i,j,index;
		while((lineString=bReader.readLine())!=null)
			if(lineString.equals("<B>")){
				for(i=0;i<N;i++){
					lineString=bReader.readLine().trim();
					String[] doubleStrings=lineString.split(" ");
					index=0;
					for(j=0;j<doubleStrings.length;j++){
						if(doubleStrings[j].length()>=0){
							B[i][index]=Double.valueOf(doubleStrings[j]);
							index++;
						}
					}
				}
				break;
			}
	}	
	
	public void readPi(double[] Pi, int N) throws IOException{
		String lineString;
		int i,index;
		while((lineString=bReader.readLine())!=null)
			if(lineString.equals("<Pi>")){
				lineString=bReader.readLine().trim();
				String[] doubleStrings=lineString.split(" ");
				index=0;
				for(i=0;i<doubleStrings.length;i++){
					if(doubleStrings[i].length()>=0){
						Pi[index]=Double.valueOf(doubleStrings[i]);
						index++;
					}
				}
				break;
			}
	}

	public static void main(String[] args){
		new FileParser().Reset();
	}
	
	public void Reset(){
		File dataFile=new File("conf/hmm.dat");
		int i ;
		int N=HmmConfig.States.length;
		int M=HmmConfig.Observes.length;
		try {
			FileWriter fWriter = new FileWriter(dataFile);
			String line;
			fWriter.write("<A>\n");
			fWriter.write("0.0 0.3 0.3 0.3 0.1 \n0.0 0.2 0.4 0.3 0.1\n"+
			"0.0 0.1 0.4 0.4 0.1\n0.0 0.5 0.0 0.0 0.5\n0.2 0.0 0.0 0.0 0.8\n");
			
//			for(i=0,line="";i<N;i++)
//				line+= (double)(1.0/N)+" ";
//			for(i=0;i<N;i++)
//				fWriter.write(line+"\n");
				
			fWriter.write("\n<B>\n");
			for(i=0,line="";i<M;i++)
				line+= (double)(1.0/M)+" ";
			for(i=0;i<N;i++)
				fWriter.write(line+"\n");
			
			fWriter.write("\n<Pi>\n");
//			for(i=0,line="";i<N;i++)
//				line+=(double)(1.0/N)+" ";
//			fWriter.write(line+"\n");
			fWriter.write("0.4 0.0 0.0 0.0 0.6\n");
			fWriter.flush();
			fWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();return;
		} catch (IOException e) {
			e.printStackTrace();return;
		}
	}
}
