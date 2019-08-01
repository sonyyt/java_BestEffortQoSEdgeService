package com;


public class test {
	public static void main(String...s){
		
		Service service = processYAML.toService("getTemp");
		ExecPlan p = service.generatePlanFromStr("0*1*2");
		
		int n = 100;
		long startTime = System.nanoTime();
		for(int i = 0; i<n; i++) {
			try {
				String result = p.execute(null, service.es).get();
				if(!result.contains("fail")) {
					p.succeedCounter++;
				}
				System.out.println("final results:"+result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long endTime = System.nanoTime();
		
		long timeElapsed = endTime - startTime;

		System.out.println("Execution time: milliseconds: " + timeElapsed/(1000000*n));
		System.out.println("Execution cost: " + p.executionCost/n);
		System.out.println("Execution reliabiilty: " + (p.succeedCounter+0.0)/n);
		
		
//		for(int j=0; j <5; j++) {
//			service.updateBranchQoS();
//			p = service.ExhausiveSearch();
//			
//			PlanQoS qos = service.CalculateQoS(p);
//			System.out.println("plan:" + qos.executionPlan);
//			System.out.println("cost:" + qos.cost);
//			System.out.println("latency:" + qos.latency);
//			System.out.println("reliability:" + qos.reliability);
//			System.out.println("plan:" + qos.executionPlan);
//			
//			p.initStatusCounter();
//			startTime = System.nanoTime();
//			for(int i = 0; i<n; i++) {		
//				try {
//					String result = p.execute(null, service.es).get();
//					if(!result.contains("fail")) {
//						p.succeedCounter++;
//					}
//					//System.out.println("final results:"+result);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			endTime = System.nanoTime();
//			
//			timeElapsed = endTime - startTime;
//
//			System.out.println("Execution time: milliseconds: " + timeElapsed/(1000000*n));
//			System.out.println("Execution cost: " + p.executionCost/n);
//			System.out.println("Execution relaibility: " + (p.succeedCounter+0.0)/n);
//		}
//		
		
//		ExecPlan p = service.generatePlanFromStr(service.greedySearch());
//		ExecPlan p = service.generatePlanFromStr("0*1*2");
//		ExecPlan p = service.ExhausiveSearch();
		
//		PlanQoS qos = service.CalculateQoS(p);
//		System.out.println("plan:" + qos.executionPlan);
//		System.out.println("cost:" + qos.cost);
//		System.out.println("latency:" + qos.latency);
//		System.out.println("reliability:" + qos.reliability);
//		
//		System.out.println("QoS SI:"+service.QoSSatisfactionIndex(qos.cost,qos.latency,qos.reliability));
		
//		long startTime = System.nanoTime();
//		try {
//			System.out.println("final results:"+p.execute(null, service.es).get());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		long endTime = System.nanoTime();
//		
//		long timeElapsed = endTime - startTime;
//
//		System.out.println("Execution time: milliseconds: " + timeElapsed / 1000000);
//		System.out.println("Execution cost: " + p.executionCost);
//		
	}
}