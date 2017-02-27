package com.miguel.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class GenerateData {
	Database db = ExtLibUtil.getCurrentDatabase();
	
	public boolean generate(){
	
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
	
	public void generateNames(){
		Random random = new Random();
		File file = new File("C:\\IBM\\Notes\\Data\\names\\male-names.txt");
		ArrayList<String> names = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    for(String line; (line = br.readLine()) != null; ) {
		    	names.add(line);
		    }
		    br.close();
		    
		    /* Generate 1000 names*/
		    for(int i=0; i<1000; i++){
		    	int first = random.nextInt(names.size()-1) + 1;
		    	int last = random.nextInt(names.size()-1) + 1;
				Document doc = db.createDocument();
				doc.appendItemValue("Form", "Name");
				doc.appendItemValue("FirstName", names.get(first));
				doc.appendItemValue("LastName", names.get(last));
				doc.save();
				doc.recycle();		    	
		    }
		    
		    
		}catch(IOException e){
			e.printStackTrace();
		} catch (NotesException e) {
			e.printStackTrace();
		}		
	}
	

}
