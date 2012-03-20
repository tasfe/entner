package ois.verify;

import java.util.zip.DataFormatException;

import ois.ner.constant.StringConst;

public class Token {
	private String content;
	public Token(String str) throws DataFormatException{
		int i;
		for(i=0;i<StringConst.tokens.length;i++)
			if(StringConst.tokens[i].equals(str)){
				this.content=str;
				break;
			}
		if(i==StringConst.tokens.length)
			throw new DataFormatException("Wrong Format of Token");
	}
	
	public int getTokenID(){
		for(int i=0;i<StringConst.tokens.length;i++)
			if(StringConst.tokens[i].equals(content))
				return i;
		return -1;
	}
	
}
