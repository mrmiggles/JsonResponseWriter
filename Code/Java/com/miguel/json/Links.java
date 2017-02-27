package com.miguel.json;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Links {

	
	public static void getLinks() throws IOException{
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext exCon = ctx.getExternalContext();
		ResponseWriter writer = ctx.getResponseWriter();
		HttpServletResponse response = (HttpServletResponse) exCon.getResponse();	
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("utf-8");
		
		//initialize the main JsonJavaObject for the response
		JsonJavaObject myData = new JsonJavaObject();
		try {
			
			/*Set up some starting points for our Datatable*/
			Map<String,Object> exConP = exCon.getRequestParameterMap();
			String search = (exConP.containsKey("search[value]")) ? exConP.get("search[value]").toString() : null;
			String sIndex = (exConP.containsKey("start")) ? exConP.get("start").toString() : "1";
			String scount = (exConP.containsKey("length")) ? exConP.get("length").toString() : "30";
			String orderColumn = (exConP.containsKey("order[0][column]")) ? exConP.get("order[0][column]").toString() : null;
			String sortOrder = (exConP.containsKey("order[0][dir]")) ? exConP.get("order[0][dir]").toString() : null;
			boolean order = true;
			
			if(sortOrder.equals("desc")){
				order = false;
			}
		
			int index = (Integer.parseInt(sIndex) == 0) ? 1 : Integer.parseInt(sIndex);
			int count = Integer.parseInt(scount);
			JsonJavaArray dataAr = new JsonJavaArray();
			

			Database db = ExtLibUtil.getCurrentDatabase();
			View vw = db.getView("Names");
			
			if(orderColumn != null){
				String n = (String) vw.getColumnNames().get(Integer.parseInt(orderColumn));
				vw.resortView(n, order);
			}
			
			
			int records = 0;
			if(search == null || search.isEmpty()){
				records = DT(vw, dataAr, count, index);
			} else {
				JsonJavaObject curOb = new JsonJavaObject();
				curOb.putJsonProperty("search", "true");
				dataAr.add(curOb);				
				
			}
			
			myData.putJsonProperty("recordsTotal", vw.getEntryCount());
			myData.putJsonProperty("recordsFiltered", vw.getEntryCount());
			vw.recycle();

			//wrap it up and add the JsonArray of JsonJavaObjects to the main object
			myData.putArray("data", dataAr);
			myData.putJsonProperty("error", false);
			
		}catch(Exception e){
			myData.putJsonProperty("error", true);
			myData.putJsonProperty("errorMessage", e.toString());
			System.out.println("Error with data provision method:");
			System.out.println(e.toString());
		}

		writer.write(myData.toString());		
	}
	
	private static int DT(View vw, JsonJavaArray dataAr, int count, int index) throws NotesException{
		ViewNavigator nav = vw.createViewNav();
		ViewEntry entry = nav.getNth(index);
		int counter = 0;
		
		while(entry != null && counter != count){
			JsonJavaObject curOb = new JsonJavaObject();
			String name = entry.getColumnValues().get(0).toString();
			String title = entry.getColumnValues().elementAt(1).toString();
			curOb.putJsonProperty("First", name);
			curOb.putJsonProperty("Last", title);
			dataAr.add(curOb);
			
			
			ViewEntry tmp = nav.getNext(entry);
			entry.recycle();
			entry = tmp;
			counter++;
		}
		
		nav.recycle();
		/*
		if(index != 0){
			JsonJavaObject curOb = new JsonJavaObject();
			curOb.putJsonProperty("index", Integer.toString(index));
			curOb.putJsonProperty("start", Integer.toString(count));
			curOb.putJsonProperty("totalCount", nav.getCount());
			dataAr.add(curOb);
		}
		 
		if(counter > 0){
			JsonJavaObject curOb = new JsonJavaObject();
			curOb.putJsonProperty("counter", Integer.toString(counter));
			dataAr.add(curOb);				
		}	
		*/
		return counter;
	}
}
