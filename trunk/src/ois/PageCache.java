
package ois;

/*****************
 *
 *@author zhzhl
 *
 *****************/
public class PageCache {
	private String Owner="";
	private String URL;
	private String Title="";
	
 	public void setURL(String url){
		this.URL=url;
	}
	
	public void setTitle(String titleString){
		this.Title=titleString;
	}
	
	public void setOwner(String owner){
		this.Owner=owner;
	}
	
	public String getTitle(){
		return this.Title;
	}
	
	public String getURL(){
		return this.URL;
	}
	
	public String getOwner(){
		return this.Owner;
	}
	
	public void print(){
		System.out.println("");
		System.out.println("网页标题: "+Title);
		System.out.println("URL: " + URL);
		System.out.println("版权所有者： "+Owner);
	}
}
