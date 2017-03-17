import sun.nio.cs.StandardCharsets.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by sam on 07-03-2017.
 *
 * @noinspection ALL
 */
public class CompressedStreamSampler {

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

    /**
     * @noinspection Since15
     */
    static byte[] toBytes(char[] chars) {
        return new String(chars).getBytes(Charset.forName("UTF-8"));
    }

    static void pprint(String obj) {
        System.out.print("'"+obj+"'");
        System.out.println();
    }

    static void refreshCounts(Map counts, String buffer) {
        for (int i=0; i < buffer.length(); i++) {
            Character key =  new Character( buffer.charAt(i) );
            Integer count = (Integer) counts.get(key);
            if (count != null) {
                counts.replace(key,count,new Integer(count.intValue()+1));
            } else {
                counts.put(key,new Integer(1));
            }
        }

    }
    
    
    static String detectNullChars(byte[] arrayofchar) {
    	String str = new String(arrayofchar);
    	int start = -1;
    	if (str.endsWith("\0")) {
    		for (int i=str.length()-1; i >= 0; i--) {
    			char c = str.charAt(i);
    			if (c != '\0') {
    				start = i+1;
    				break;
    			}
    		}
    	}
    	if (start !=-1) {
    		return str.substring(0,start);
    	} else {
    		return str;
    	}
       	//System.out.println( "\n'" + str + "': first index of \0: " + firstPosNull +  " , NullatEnd? " + charatEnd  );
    }
    
    // we assume a BUFFER SIZE of 8192 since is the sector size most frequent in the majority
    // of the filesystems nowadays, eg NTFS, ext
    
    final static int BUFFER_SIZE = 8192;
    
    public static long now() {
    	return System.currentTimeMillis();
    }

    static byte[] generateRandomChars (int length) {
    	Random random = new Random();
    	int[] rand_ints = random.ints(length,32, 127).toArray();
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

        System.out.println("Maximum available memory at beginning: " + maxMemorySize());
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
        ArrayList<byte[]> holder = new ArrayList<byte[]>();
        
        long before = now();

        try {

        	byte[] bbuf = null;
            int qty_chars_read = 0;
            
            if (usingInternalRandomSource) {
            	int chunks = megs * (1024*1024) / BUFFER_SIZE;
            	for (int i=0; i < chunks; i++) {
            		bbuf = generateRandomChars(BUFFER_SIZE);
            		
            		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            		GZIPOutputStream gzip = new GZIPOutputStream(baos);
            		gzip.write(bbuf);
            		gzip.close();
            		
            		// write to
            		byte [] compressed = baos.toByteArray();
            		holder.add(compressed);
            		qty_chars_read = compressed.length;
            		total_chars_read += qty_chars_read ;
	                System.out.printf("\rTotal data read: %10s",toMB(total_chars_read));
            	}
            	System.out.println();
            } else {
            	bbuf = new byte[BUFFER_SIZE];
                //reading bytes from the stdin

	            while ((qty_chars_read = System.in.read(bbuf)  ) > 0) {
	        		ByteArrayOutputStream baos = new ByteArrayOutputStream();

	            	GZIPOutputStream gzip = new GZIPOutputStream(baos);
	            	
            		gzip.write(bbuf);
            		
            		// write to
            		byte [] compressed = baos.toByteArray();
            		holder.add(compressed);
	            	
            		qty_chars_read = compressed.length;
            		total_chars_read += qty_chars_read ;
	                System.out.printf("\rTotal data read: %10s",toMB(total_chars_read));
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
            byte[] gzippedarraybytes = (byte[]) holder.get(random_int);
    		ByteArrayInputStream bais = new ByteArrayInputStream(gzippedarraybytes);
    		GZIPInputStream gunzip;
    		byte[] decompressed = new byte[BUFFER_SIZE];
			try {
				gunzip = new GZIPInputStream(bais);
	    		gunzip.read(decompressed);
	    		gunzip.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String decStr = new String(decompressed); 
            char c = decStr.charAt( random.nextInt(decStr.length()) );
            rep_set.append(c);
        }

        System.out.println(toMB(total_chars_read) + " total where read from stdin!");
        System.out.println("==> Random representative set is '" + rep_set + "'");
//        System.out.println(counts);
        System.out.println("Total time processing: " + (after-before)/1000 + " s");
    }

}
