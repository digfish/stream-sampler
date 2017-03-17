
import sun.nio.cs.StandardCharsets.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by sam on 07-03-2017.
 *
 * @noinspection ALL
 */
public class StreamSampler {

    static String maxMemorySize() {
        return toMB(Runtime.getRuntime().maxMemory());
    }

    static String totalAllocatedMemory() {
        return toMB(Runtime.getRuntime().totalMemory());
    }

    static String totalFreeMemory() {
        return toMB(Runtime.getRuntime().freeMemory());
    }

    
    static String toMB(double val) {
    	if (val > 1024*1024) {
    		long mb = Math.round(val / Math.pow(1024, 2));
    		return mb + " MB";
    	} else {
    		return (int) val + " by";
    	}
    }


    
    // we assume a BUFFER SIZE of 8192 since is the sector size most frequent in the majority
    // of the filesystems nowadays, eg NTFS
    
    final static int BUFFER_SIZE = 8192;
    
    public static long now() {
    	return System.currentTimeMillis();
    }

    static byte[] generateRandomChars (int length) {
    	Random random = new Random();
    	int[] rand_ints = random.ints(length,32, Byte.MAX_VALUE).toArray();
    	byte[] rand_bytes = new byte[rand_ints.length];
    	for (int i=0; i < rand_ints.length; i++) {
    		rand_bytes[i] = (byte) rand_ints[i];
    	}
    	return rand_bytes;
    }
    
    
    public static void main(String[] args) {


        TreeMap counts = new TreeMap();
        
        boolean usingInternalRandomSource = false;
        
        int megs = -1;

        System.out.println("Maximum available memory at beginning: " + maxMemorySize() + ", allocated: " + totalAllocatedMemory());
        if (args.length < 1) {
            System.err.println("Representative set size not provided!");
            System.exit(-1);
        } 

        if (args.length > 1 && args[1].equals("-i")) {
    		System.out.println("Using Java internal random source.");
    		megs = Integer.parseInt(args[2]);
    		usingInternalRandomSource = true;
    	}
        

        int output_size = Integer.parseInt(args[0]);
        
        long total_chars_read = 0;
        ArrayList<byte[]> holder = new ArrayList();
        
        long before = now();

        try {

        	byte[] bbuf = null;
            int qty_chars_read = -1;
            
            if (usingInternalRandomSource) {
            	int chunks = megs * (1024*1024) / BUFFER_SIZE;
            	for (int i=0; i < chunks; i++) {
            		bbuf = generateRandomChars(BUFFER_SIZE);
            		holder.add(bbuf);
            		qty_chars_read = bbuf.length;
            		total_chars_read += qty_chars_read ;
	                System.out.printf("\rTotal data read: %10s",toMB(total_chars_read));
            	}
            } else {
            	bbuf = new byte[BUFFER_SIZE];
                //reading bytes from the stdin

	            while ((qty_chars_read = System.in.read(bbuf)  ) > 0) {
	                total_chars_read += qty_chars_read;
	                System.out.printf("\rTotal data read: %10s",toMB(total_chars_read));
	                holder.add(bbuf);
	            }
            }
            


        } catch (Exception e) {
            e.printStackTrace();
        }
        
        long after = now();
        
        
        System.out.println("\r\n\n\n");

        System.out.println("++ Total allocated memory at the end of streaming: " + totalAllocatedMemory());
        System.out.println("++ Available is: " + totalFreeMemory() );
        
        
        // now that we have a arraylist containing all the contents of the stream, we can extract random chars
        // at will
        StringBuffer rep_set = new StringBuffer(output_size);
        Random random = new Random();
        for (int i = 0; i < output_size; i++) {
            int random_int = random.nextInt(holder.size());
            String str =  new String((byte[]) holder.get(random_int));
            char c = str.charAt( random.nextInt(str.length()) );
            rep_set.append(c);
        }

        System.out.println(toMB(total_chars_read) + " total where read from stdin!");
        System.out.println("==> Random representative set is '" + rep_set + "'");
//        System.out.println(counts);
        System.out.println("Total time processing: " + (after-before)/1000 + " s");
    }

}
