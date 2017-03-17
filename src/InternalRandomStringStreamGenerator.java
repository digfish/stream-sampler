import java.util.Random;

public class InternalRandomStringStreamGenerator {

	public static final int BUFFER_SIZE = 8192;
	
    static String generateRandomChars (int length) {
    	Random random = new Random();
    	int[] rand_ints = random.ints(length,0, Byte.MAX_VALUE).toArray();
    	byte[] rand_chars = new byte[rand_ints.length];
    	for (int i=0; i < rand_ints.length; i++) {
    		rand_chars[i] = (byte) rand_ints[i];
    	}
    	return new String(rand_chars);
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String gen = generateRandomChars( BUFFER_SIZE );
		System.out.println(gen);
	}

}
