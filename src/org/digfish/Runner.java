package org.digfish;

public class Runner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int megs = -1;
		boolean usingInternalRandomSource = false;
		int sampleSize = -1;
		
		StreamSampler sampler = null;
		
        if (args.length < 1) {
            System.err.println("Representative set size not provided!");
            System.exit(-1);
        } else {
            sampleSize = Integer.parseInt(args[0]);
            sampler = new StreamSampler(sampleSize);
        }

        if (args.length > 1 && args[1].equals("-i")) {
    		System.out.println("Using Java internal random source.");
    		megs = Integer.parseInt(args[2]);
    		usingInternalRandomSource = true;
            sampler = new StreamSampler(sampleSize,usingInternalRandomSource, megs);
    	}
        

        
        String repSet = sampler.sampleIt();
        
        System.out.println("Random sample: " + repSet);


	}

}
