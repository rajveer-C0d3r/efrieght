package com.grtship.core.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class StringResponse implements Serializable{

	private static final long serialVersionUID = 4920383619288623067L;
	private String response;

    public StringResponse(String s) { 
       this.response = s;
    }

}
