import java.nio.charset.Charset;
import java.util.Map;

public class Utils {
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
    
    static byte[] toBytes(char[] chars) {
        return new String(chars).getBytes(Charset.forName("UTF-8"));
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

}
