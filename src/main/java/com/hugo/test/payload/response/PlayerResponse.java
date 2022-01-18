package com.hugo.test.payload.response;

import java.util.ArrayList;
import java.util.List;

public class PlayerResponse {
	private List<String> result;


	public PlayerResponse(){
		result=new ArrayList<String>();
	}
	public void addResponse(String response){
		this.result.add(response);
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}


}
