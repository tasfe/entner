package ois.ner.hmm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**********************************************
 * 
 * 隐马尔可夫模型，用于商品实体识别
 * @author zhzhl,wxt
 *
 **********************************************/
public class HMM {
	//状态数目:品牌、修饰词、产品名称、非商品实体词
	private int N;  
	//每个状态可能的观察值数目：词性数目
	private int M;
	//与时间无关的状态转移概率矩阵
	private double[][] A=null;
	//给定状态下，观察值概率分布:每个状态的词性分布概率
	private double[][] B=null;
	//初始状态空间的概率分布
	private double [] Pi=null;
	//配置文件解析，用于保存参数
	private FileParser fParser;
	
	public HMM(int numOfState, int numOfObservation){
		this.N=numOfState;
		this.M=numOfObservation;
		A=new double[N][N];
		B=new double[N][M];
		Pi=new double[N];
		this.init(N, M);
	}
	
	/*******************************************
	 * 
	 * 初始化，从文件中读取参数
	 * @param n 状态数目
	 * @param m 观察值数目
	 *******************************************/
	public void init(int n,int m){
		fParser= new FileParser();
		if(fParser.open() && fParser.canRead()){
			try {
				fParser.readA(A, N);
				fParser.readB(B, N, M);
				fParser.readPi(Pi, N);
				fParser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**************************
	 * 将最新的参数保存到文件中
	 **************************/
	public void SaveModel(){
		fParser.write(A, B, Pi, N, M);
	}
	
	public void print(double[][] data,int row,int col){
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++)
				System.out.print(data[i][j]+" ");
			System.out.print("\n");
		}
		System.out.println("");
	}	
	public void print(double[]data,int length){
		for(int i=0;i<length;i++) 
			System.out.print(data[i]+" ");
		System.out.println("\n");
	}
	public void print(int[][] data,int row,int col){
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++)
				System.out.print(data[i][j]+" ");
			System.out.print("\n");
		}
		System.out.println("");
	}
	
	/********************************************
	 * 
	 * 计算Alpha[t][i]
	 * @param oseq 观察值序列
	 * @return Alpha[t][i]
	 * 
	 ********************************************/
	public double[][] computeAlpha(final List<Observation> oseq) {
		double[][] alpha=new double[oseq.size()][N];
		double sum=0.0;
		int i,j,t;
		for(i=0;i<N;i++){
			alpha[0][i]=Pi[i]*B[i][oseq.get(0).getIndex()];
		}
		for(t=1;t<oseq.size();t++){
			for(j=0;j<N;j++){
				for(i=0,sum=0.0;i<N;i++)
					sum+=alpha[t-1][i]*A[i][j];
				alpha[t][j]=sum*B[j][oseq.get(t).getIndex()];
			}
		}
		return alpha;
	}
	
	/********************************************
	 * 
	 * 计算Beta[t][i]
	 * @param oseq 观察值序列
	 * @return Beta[t][i]
	 * 
	 ********************************************/
	public double[][] computeBeta(final List<Observation> oseq){
		double[][] beta=new double[oseq.size()][N];
		double sum;
		int i;
		for(i=0;i<N;i++){
			beta[oseq.size()-1][i]=1;
		}
		for(int t=oseq.size()-2;t>=0;t--){
			for(i=0;i<N;i++){
				sum=0.0;
				for(int j=0;j<N;j++)
					sum+=A[i][j]*B[j][oseq.get(t+1).getIndex()]*beta[t+1][j];
				beta[t][i]=sum;
			}
		}
		return beta;
	}
		
	/********************************************************
	 * 
	 * 给定观察序列O=O1,O2,…OT,以及模型λ, 使用向前法计算P(O|λ)
	 * @param oseq 观察值序列
	 * @return P(O|λ)
	 * 
	 ********************************************************/
	public double Foward(final List<Observation> oseq ){
		double[][] alpha=computeAlpha(oseq);
		double result=0.0;
		for(int i=0;i<N;i++)
			result+=alpha[oseq.size()-1][i];
		return result;
	}
	
	/********************************************************
	 * 
	 * 给定观察序列O=O1,O2,…OT,以及模型λ, 使用向后法计算P(O|λ)
	 * @param oseq 观察值序列
	 * @return P(O|λ)
	 * 
	 ********************************************************/
	public double Backward(final List<Observation> oseq ){
		double[][]beta=computeBeta(oseq);
		double result;
		int i;
		for(i=0,result=0.0;i<N;i++)
			result+=beta[0][i];
		return result;
	}

	/*******************************************************
	 * 
	 * 给定观察序列O以及模型λ,采用Viterbi算法选择一个对应的状态序列S，
	 * 使得S能够最为合理的解释观察序列O？
	 * @param oseq 观察值序列
	 * @return 对应的状态序列
	 * 
	 *******************************************************/
	public int[] Viterbi(final List<Observation> oseq){
		int T=oseq.size();
		double[][] delta=new double[T][this.N]; 
		int[][] psy=new int[T][this.N];
		int[] q=new int[T];
		
		int i,t,j;
		double maxdelta=0.0,tmp=0.0;
//		初始化delta和psy
		for(i=0;i<N;i++){
			delta[0][i]=Pi[i]*B[i][oseq.get(0).getIndex()];
			psy[0][i]=0;
		}
//		递归，计算delta和psy
		for(t=1;t<T;t++){	
			for(j=0;j<N;j++){
				for(i=0,maxdelta=0.;i<N;i++){
					tmp=delta[t-1][i]*A[i][j];
					if(tmp>maxdelta){
						maxdelta=tmp;
						psy[t][j]=i;
					}
				}
				delta[t][j]=maxdelta*B[j][oseq.get(t).getIndex()];
			}
		}
//		终结，求qT
		for(i=0,maxdelta=0.0;i<N;i++){
			tmp=delta[T-1][i];
			if(tmp>maxdelta){
				maxdelta=tmp;
				q[T-1]=i;
			}
		}
//		求状态序列
		for(i=T-2;i>=0;i--)
			q[i]=psy[i+1][q[i+1]];
		return q;
	}
	
	/*************************************************
	 * 
	 * 给定观察值序列O，采用Baum_Welch算法通过计算确定一个模型λ，使得P(O|λ)最大,
	 * @param oseq 观察值序列
	 * @return true 如果需要继续学习， false 如果参数已达到稳定值
	 *************************************************/
	public boolean Baum_Welch(final List<Observation> oseq){
		int T=oseq.size();
		double[][][] Xi=new double[T-1][N][N];   // ξt(i,j)
		double[][] gamma=new double[T][N];
		double[][] alpha=this.computeAlpha(oseq);
		double[][] beta=this.computeBeta(oseq);
		double[][] B1=new double[N][M];
		double tmp;
		double oldProb,newProb;
		oldProb=this.Foward(oseq);
		int t,i,j;
//		this.print(alpha, T,N);
//		this.print(beta, T,N);
		
//		ξt(i,j)表示 t 时状态为 i 以及 t+1 时状态为 j的概率.  
		boolean zero=false;
		for(t=0;t<T-1;t++){
			tmp=0;
			for(i=0;i<N;i++)
				for(j=0;j<N;j++)
					tmp+=alpha[t][i]*A[i][j]*B[j][oseq.get(t+1).getIndex()]*beta[t+1][j];
			if(HmmConfig.isZero(tmp))
				zero=true;
			for(i=0;i<N;i++)
				for(j=0;j<N;j++){
					if(zero)
						Xi[t][i][j]=0.;
					else
						Xi[t][i][j]=alpha[t][i]*A[i][j]*B[j][oseq.get(t+1).getIndex()]*beta[t+1][j]/tmp;
				}
		}
//		System.out.println("ξ");
//		for(t=0;t<T-1;t++)
//			print(Xi[t], N, N);
		
//		γ t(i)=Σξt(i,j) 表示t时刻处于处于状态Si的概率 
		for(t=0;t<T;t++)
			Arrays.fill(gamma[t], 0.);
		for(t=0;t<T-1;t++)
			for(i=0;i<N;i++){
				for(j=0;j<N;j++)
					gamma[t][i]+=Xi[t][i][j];
			}
		for (j = 0; j <N; j++)
			for (i = 0; i <N; i++)
				gamma[T-1][j] += Xi[T-2][i][j];
//		System.out.println("Gamma");
//		this.print(gamma, T, N);
		
//		计算Pi[]
		for(i=0;i<N;i++)
			Pi[i]=gamma[0][i];
		System.out.println("Pi");
		this.print(Pi, N);
		
//		计算A[i][j]
		double sumoft,sumoftj;
		for( i=0;i<N;i++){
			for(t=0,sumoftj=0.0;t<T-1;t++){
				sumoftj+=gamma[t][i];
			}
			for(j=0;j<N;j++){
				for(t=0,sumoft=0.;t<T-1;t++)
					sumoft+=Xi[t][i][j];
				if(!HmmConfig.isZero(sumoftj))
					A[i][j]=sumoft/sumoftj;
				else {
					A[i][j]=A[i][j];
				}
			}
		}
		System.out.println("A");
		this.print(A, N, N);
		
		//计算机B[i][j]		
		double gammat1,gammat2;
		for(j=0;j<N;j++)
			for(int k=0;k<M;k++){
				gammat1=gammat2=0;
				for(t=0;t<T;t++){
					if(oseq.get(t).getIndex()==k)
						gammat1+=gamma[t][j];
					gammat2+=gamma[t][j];
				}
				if(!HmmConfig.isZero(gammat2))
					B1[j][k]=gammat1/gammat2;
				else {
					B1[j][k]=B[j][k];
				}
			}
		
		for(i=0;i<N;i++)
			for(j=0;j<M;j++)
				B[i][j]=(B1[i][j]+B[i][j])/2;
//				B[i][j]=B1[i][j];
//		System.out.println("B1");
//		this.print(B1, N,M);
		System.out.println("B");
		this.print(B, N, M);
		
		newProb=this.Foward(oseq);
//		
		if(HmmConfig.isZero(oldProb)|| HmmConfig.isZero(newProb) || Math.abs(Math.log(newProb)-Math.log(oldProb))<HmmConfig.epsilon)
		{System.err.println("old: "+oldProb+"   new:"+newProb);
			return false;
		}
		return true;
	}


}
