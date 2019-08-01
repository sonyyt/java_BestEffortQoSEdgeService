package com;

class Microservice{
	String id;
	String input;
	String output;
	String MSInput;
	String MSOutput;
	String msQueryInput;
	String url;
	double cost;
	
	public Microservice(String id, String input, String output) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.url = "";
		
		// input: gps as location (location, the original input of the microservice)
		if(this.input!=null && this.input.contains(">")) {
			this.MSInput=this.input.split(">")[0];
			this.msQueryInput = this.input.split(">")[1];
		}else {
			this.MSInput = this.input;
			this.msQueryInput  = this.input;
		}
		
		// output: temp as temperature (temp, the original output of the microservice)
		if(this.output!=null && this.output.contains(">")) {
			this.MSOutput=this.output.split(">")[1];
		}else {
			this.MSOutput = this.output;
		}
	}
	
	
	
}
