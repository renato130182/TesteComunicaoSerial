/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.struts;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * REST Web Service
 *
 * @author Ususario
 */
@Path("event")
public class Event {

    @Context
    private UriInfo context;
    private final Gson gson = new Gson();
    /**
     * Creates a new instance of Event
     */
    public Event() {
    }

    /**
     * Retrieves representation of an instance of com.myapp.struts.Event
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public String getJSON() {
        return "Meu WS";
    }
    
    
    /**
     * PUT method for updating or creating an instance of Event
     * @param content representation for the resource 
     */
    @POST
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public void postJSON(String content) {
        Inssue evt = new Inssue();
        evt=gson.fromJson(content, Inssue.class);
        System.out.println(evt.getNum());
        try {
            JSONObject json = new JSONObject(content);
            System.out.println("converteu");
            
            //return "Recebido";
        } catch (JSONException ex) {
            Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
