package com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

enum ExecPlanTypes
{ 
	//Microservice, Sequential, Speculative Parallel
    equBranch, SEQ, spePAR; 
}

public class ExecPlan {
	public static double  executionCost=0;
	public static int succeedCounter=0;
    public ExecPlanTypes type;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public void initStatusCounter() {
    	executionCost = 0;
    	succeedCounter = 0;
    }
    public Future<String> execute(String serviceInput, executionStatus es) {
		if(this.type==ExecPlanTypes.equBranch) { 
			return executor.submit(() -> {
				Leaf tmp = (Leaf)this;
				String parameterInput = serviceInput; // parameterInput, the output of last microservice execution;
				String executionUrl = "";
				for(Microservice ms:tmp.branch.microservices) {
					if(ms.msQueryInput.equals("N.A")) {
						executionUrl = ms.url;
					}else {
						executionUrl = ms.url+"?"+ms.msQueryInput+"="+parameterInput;
					}
					long startTime = System.nanoTime();
					try {
						//System.out.println(executionUrl);
						String rt = tools.queryMicroservice(executionUrl);
						ExecPlan.executionCost += ms.cost;
						long endTime = System.nanoTime();
						long timeElapsed = (endTime - startTime)/1000000;
						//System.out.println(rt);
						if(rt.contains("Error")) { // if ms execution fails, the return must contain "Error"!!!
							es.insertMSExecutionStatus(ms.id, timeElapsed, false);
							return "fail";				
						}else {
							es.insertMSExecutionStatus(ms.id, timeElapsed, true);
							parameterInput = rt;
						}
					}catch(Exception e) {
						long endTime = System.nanoTime();
						long timeElapsed = (endTime - startTime)/1000000;
						es.insertMSExecutionStatus(ms.id, timeElapsed, false);
						//System.out.println("timeout");
						return "fail";
					}
				}		
				//System.out.println(parameterInput);
				return parameterInput;
			});
		}else if(this.type == ExecPlanTypes.SEQ) {
			return executor.submit(() -> {
				Seq tmp = (Seq)this;
				String rt = tmp.left.execute(serviceInput, es).get();
				if(rt.equals("fail")) {
					rt = tmp.right.execute(serviceInput, es).get();
				}
				return rt;
			});
		}else {
			return executor.submit(() -> {
				Par tmp = (Par)this;
				List<Future<String>> futures = new ArrayList<Future<String>>();
				for(ExecPlan cp: tmp.children) {
					Future<String> cpF = cp.execute(serviceInput, es);
					futures.add(cpF);
				}

				while(true) {
					int unfinishedThreads = futures.size();
					for(Future<String> f:futures) {
						if(f.isDone()) {
							unfinishedThreads--;
							String rt = f.get();		
							if(!rt.equals("fail")){
								return rt;
							}
						}
					}
					if(unfinishedThreads==0) {
						return "no return fail";
					}
				}
			});
		}
    }
    
    
    
	double[] estCostandReliability() {
		double[] rt = new double[2];
		if(this.type==ExecPlanTypes.equBranch) { 
			Leaf tmp = (Leaf)this;
			rt[0] = tmp.branch.cost;
			rt[1] = tmp.branch.reliability;
			return rt;
		}else if(this.type == ExecPlanTypes.SEQ) {
			Seq tmp = (Seq)this;
			double[] costAndReliabilityL = tmp.left.estCostandReliability();
			double[] costAndReliabilityR = tmp.right.estCostandReliability();
			rt[0] = costAndReliabilityL[0] + costAndReliabilityR[0]*(100-costAndReliabilityL[1])/100;
			rt[1] = 100 - (100-costAndReliabilityL[1])*(100-costAndReliabilityR[1])/100;
			return rt;
		}else {
			Par tmp = (Par)this;
			double tmpR = 100;
			for(ExecPlan cp: tmp.children) {
				double[] costAndReliabilityCP = cp.estCostandReliability();
				rt[0] += costAndReliabilityCP[0];
				tmpR = tmpR * (100 - costAndReliabilityCP[1])/100;
			}
			//System.out.println("reliability:"+tmpR);
			rt[1] = 100 - tmpR;
			return rt;
		}
	}
	
	@SuppressWarnings("rawtypes")
	HashMap<EquivalentMS, Double> estLatency(){
		HashMap<EquivalentMS, Double> rt = new HashMap<EquivalentMS, Double>();
		if(this.type==ExecPlanTypes.equBranch) { 
			Leaf tmp = (Leaf)this;
			rt.put(tmp.branch, tmp.branch.latency);
			return rt;
		}else if(this.type == ExecPlanTypes.SEQ) {
			Seq tmp = (Seq)this;
			rt = tmp.left.estLatency();
			HashMap<EquivalentMS, Double> latR= tmp.right.estLatency();
			double maxLatencyLeft = (Collections.max(rt.values()));
			for (Map.Entry me : latR.entrySet()) {
				rt.put((EquivalentMS) me.getKey(), (Double)me.getValue()+maxLatencyLeft);
			}
			return rt;
		}else {
			Par tmp = (Par)this;
			for(ExecPlan cp: tmp.children) {
				HashMap<EquivalentMS, Double> latC= cp.estLatency();
				rt.putAll(latC);
			}
			return rt;
		}

	}
	
    @Override
	public String toString() {
		String rt = "";
		if(this.type==ExecPlanTypes.equBranch) { 
			Leaf tmp = (Leaf)this;
			for(Microservice ms:tmp.branch.microservices) {
				rt += ms.id+">";
			}
			return rt.substring(0, rt.length() - 1);
		}else if(this.type == ExecPlanTypes.SEQ) {
			Seq tmp = (Seq)this;
			rt = tmp.left.toString()+ "-" + tmp.right.toString();
			return rt;
		}else {
			Par tmp = (Par)this;
			for(ExecPlan p:tmp.children){
				if(p.type==ExecPlanTypes.SEQ) {
					rt += "("+p.toString()+")*";
				}else {
					rt += p.toString()+"*";
				}
			}
			return rt.substring(0, rt.length() - 1);
		}
    }
}

class Leaf extends ExecPlan{
	public EquivalentMS branch;
	public Leaf(EquivalentMS b) {
		super();
		this.type = ExecPlanTypes.equBranch;
		branch = b;
	}
}

class Seq extends ExecPlan{
	public ExecPlan left;
	public ExecPlan right;
	public Seq() {
		super();
		this.type = ExecPlanTypes.SEQ;
	}
	public Seq(ExecPlan l, ExecPlan r) {
		super();
		this.type = ExecPlanTypes.SEQ;
		this.setLeft(l);
		this.setRight(r);
	}
	
	public void setLeft(ExecPlan l) {
		this.left = l;
	}
	public ExecPlan getLeft() {
		return this.left;
	}
	public void setRight(ExecPlan r) {
		this.right = r;
	}
	public ExecPlan getRight() {
		return this.right;
	}
}

class Par extends ExecPlan{
	public List<ExecPlan> children = new ArrayList<ExecPlan>();
	public Par() {
		super();
		this.type = ExecPlanTypes.spePAR;
	}
	
	public Par(List<ExecPlan> c) {
		super();
		this.type = ExecPlanTypes.spePAR;
		children = c;
	}
	
	public void append(ExecPlan c) {
		children.add(c);
	}
}