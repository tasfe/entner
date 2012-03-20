package ois;

import java.util.ArrayList;

import ois.infoExtract.TrademarkInfo;

public class TrademarkCache extends PageCache {
	private ArrayList<TrademarkInfo> brandList=new ArrayList<TrademarkInfo>();
	
	public boolean add(TrademarkInfo tInfo){
		for(int i=0;i<brandList.size();i++)
			if(brandList.get(i).equals(tInfo))
				return false;
		brandList.add(tInfo);
		return true;
	}
	public void print() {
		TrademarkInfo tInfo;
		for(int i=0;i<brandList.size();i++){
			tInfo=brandList.get(i);
				tInfo.print();
		}
	}
}
