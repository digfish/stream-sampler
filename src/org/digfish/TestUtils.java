package org.digfish;

import java.io.IOException;
import java.io.InputStream;

public class TestUtils {
	
    static public long maxMemorySize() {
        return Runtime.getRuntime().maxMemory();
    }

    static public long totalAllocatedMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    static public long totalFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
    
    /**
     * returns a quantity of bytes in numerical form,
     * if more than 1 MB, it 
     * @param val
     * @return
     */
    static public String toMB(double val) {
    	if (val > 1024*1024) {
    		long mb = Math.round(val / Math.pow(1024, 2));
    		return mb + " MB";
    	} else {
    		return (int) val + " bytes";
    	}
    }
    
    /**
     * returns the inputstream corresponding
     * to the outputstream of the executed process
     * @param command the string of a shell program
     * @return
     */
    static public InputStream exec(String command) {
    	try {
			return Runtime.getRuntime().exec(command).getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
}
