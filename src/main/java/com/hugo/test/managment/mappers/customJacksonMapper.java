package com.hugo.test.managment.mappers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;


@Component
public class customJacksonMapper 
	extends JsonSerializer<Date>  {

	    private static final String dateFormat = ("dd/MM/yyyy");


   
   
 
    @Override
	    public void serialize(Date date, JsonGenerator json, SerializerProvider provider)
	            throws IOException, JsonProcessingException {

	    	String s=("/Date("+date.getTime()+")/");
	    //	s=JSON.parse(JSON.stringify("kIurhgFBOzDW5il89\\/lB1ZQnmmY="))
	    	//System.out.println(s);
		    
	    	json.writeString(s);    
	    }

}
