package ois.xmlParser;

import java.io.FileOutputStream;
import java.io.IOException;

import ois.ner.constant.XMLConfig;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public abstract class XMLParser {
	//document object of the xml file	
	protected Document doc=null;
	protected String filename;	
	//root element of the document 
	protected Element root;

	private void init(String config) {
		try{
			SAXReader reader = new SAXReader();
			doc= reader.read(XMLConfig.CONFIG);
			filename = doc.getRootElement().element(XMLConfig.CONFIG_PATH).getText();
			filename += "/"+doc.getRootElement().element(config).getText();
			doc=reader.read(filename);
			root=doc.getRootElement();
		}catch(DocumentException e){
			e.printStackTrace();return;
		}
	}
	public XMLParser(String config){
		this.init(config);
	}
	protected void update() {
		try {
			XMLWriter dWriter=new XMLWriter(new FileOutputStream(filename));
			dWriter.write(doc);
			dWriter.flush();
			dWriter.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
}
