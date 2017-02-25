package com.miguel.test;

import java.util.HashSet;
import java.util.Set;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class GenerateData {
	
	public boolean generate(){
		Database db = ExtLibUtil.getCurrentDatabase();
		
		for(int i=0; i<1000; i++){
			try {
				Document doc = db.createDocument();
				doc.appendItemValue("Form", "Random");
				doc.appendItemValue("Name", randomIdentifier());
				doc.appendItemValue("Title", randomIdentifier());
				
				doc.save();
				doc.recycle();
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return true;
	}
	// class variable
	final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

	final java.util.Random rand = new java.util.Random();

	// consider using a Map<String,Boolean> to say whether the identifier is being used or not 
	final Set<String> identifiers = new HashSet<String>();

	public String randomIdentifier() {
	    StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(5)+5;
	        for(int i = 0; i < length; i++) {
	            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	        }
	        if(identifiers.contains(builder.toString())) {
	            builder = new StringBuilder();
	        }
	    }
	    return builder.toString();
	}
	

}
