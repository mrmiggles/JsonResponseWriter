package com.miguel.test;
import java.io.IOException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

import com.ibm.commons.util.io.json.JsonJavaArray;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class Data {
	/**
	 * This method performs some sample actions against
	 * a Domino View's Documents, reads them into a
	 * JsonArray, attaches it to the JsonJavaObject response
	 * and returns it as a data response via FacesContext.
	 * This should be invoked as part of an XAgent.
	 * 
	 * @return JsonJavaObject sample response
	 * @throws IOException 
	 */
	public static void GetMyDataAsJson() throws IOException{
		//initialize the main JsonJavaObject for the response
		JsonJavaObject myData = new JsonJavaObject();
		/*
		 * Here we're 
		 */
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext exCon = ctx.getExternalContext();
		/*
		 * Using a response writer is one way of directly dumping into the response.
		 * Instead, I'm returning the JsonJavaObject.
		 */
		ResponseWriter writer = ctx.getResponseWriter();
		HttpServletResponse response = (HttpServletResponse) exCon.getResponse();
		
		//set my content type, use a robust character encoding, and don't cache my response
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("utf-8");
		try {
			
			/*
			 * This is how we can get a handle on and use any URL parameters
			 * instead of the Domino SSJS param handle. Note that I check
			 * for the existence of the the parameter of myKey before assigning
			 * it, via ternary operator.
			 */
			Map<String,Object> exConP = exCon.getRequestParameterMap();
			String myParam = (exConP.containsKey("myKey")) ? exConP.get("myKey").toString() : null;
			
			/*
			 * Using the Domino Session class, we can get a handle on our current
			 * session and interact with anything via the Java NotesDomino API.
			 */
			Database db = ExtLibUtil.getCurrentDatabase();
			View vw = db.getView("Random");
			
			JsonJavaArray dataAr = new JsonJavaArray();
			if(myParam != null){
				JsonJavaObject curOb = new JsonJavaObject();
				curOb.putJsonProperty("name", myParam);
				curOb.putJsonProperty("title", "you");
				dataAr.add(curOb);
			}
			
			/*
			 * perform any necessary business logic with the data
			 */
			
			/*
			 * This is an example only as there are easier ways to 
			 * get a JSON response of a View; e.g.- Domino Data/Access Services.
			 */
			Document first = vw.getFirstDocument();
			//simple View iteration of documents and adding of a given value
			while(first!=null){
				//creates current object
				JsonJavaObject curOb = new JsonJavaObject();
				String name = first.getItemValueString("Name");
				String title = first.getItemValueString("Title");
				curOb.putJsonProperty("name", name);
				curOb.putJsonProperty("title", title);
				//adds current object into JsonArray
				dataAr.add(curOb);
				
				//no OpenNTF Domino API implemented, ham fist away!
				Document tmpDoc = vw.getNextDocument(first);
				first.recycle();
				first = tmpDoc;
			}
			//wrap it up and add the JsonArray of JsonJavaObjects to the main object
			myData.putArray("data", dataAr);
			
			/*
			 * Business logic done, setting error to false last, so
			 * if anything errors out, we'll catch it.
			 */
			myData.putJsonProperty("error", false);
			
		}catch(Exception e){
			/*
			 * On error, sets a boolean error value of true
			 * and adds the message into the errorMessage
			 * property.
			 */
			myData.putJsonProperty("error", true);
			myData.putJsonProperty("errorMessage", e.toString());
			System.out.println("Error with data provision method:");
			System.out.println(e.toString());
		}
		/*
		 * This will always return a fully formed JsonJavaObject response.
		 * Meaning that if there's an error, we hear about it and can
		 * handle that on the client side for display while developing,
		 * or logging when in production.
		 * 
		 * Note: since we're hijacking the FacesContext response, we're
		 * returning a string (not data object) into the ResponseWriter.
		 * This is why the method is void. Don't worry, it's application/json.
		 */
		writer.write(myData.toString());
	}
}
