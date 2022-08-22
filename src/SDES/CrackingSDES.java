package SDES;

import java.util.Arrays;

public class CrackingSDES {
	public static final int S0[][] = { { 1, 0, 3, 2 }, { 3, 2, 1, 0 }, { 0, 2, 1, 3 }, { 3, 1, 3, 2 } };

	public static final int S1[][] = { { 0, 1, 2, 3 }, { 2, 0, 1, 3 }, { 3, 0, 1, 0 }, { 2, 1, 0, 3 } };

	public static void main(String[] args) {
		
		byte[] key = {0, 1, 1, 1, 0, 0, 1, 1, 0, 1};
		String text = "CRYPTOGRAPHY"; 	//text to encrypt
		byte[] ciphertext = new byte[64];
		
		byte[] plaintext = CASCII.Convert(text); 	//convert text to plaintext in CASCII
		
		byte[] temp = new byte[8]; //temp array to hold 8 bits of plaintext to encrypt
		int step = 0;				//used to keep track of each 8 bits 
		System.out.println("\n----------Begin Question 1---------");
		while(step != plaintext.length) {
		
			for(int i = 0; i < 8; i++) {
				temp[i] = plaintext[i + step];
			}
			temp = Encrypt(key, temp);
			int j = 0;
			for(int i = step; i < step + 8; i++) {
				ciphertext[i] = temp[j];
				j++;
			}
			step = step + 8;	
		}

		System.out.println("\n"+ "Encrypted Value Or Ciphertext is: " + Arrays.toString(ciphertext)); 
		String message = CASCII.toString(ciphertext);
		System.out.println("\n"+ "Encrypted message is: " + message);
		
		
		System.out.println("\n----------Decryption---------");
		//Decryption
		byte[] decryption = new byte[64];
		step = 0;
		        
		while(step != ciphertext.length) {
		    for(int i = 0; i < 8; i++) {
		        temp[i] = ciphertext[i + step];
		    }
		    temp = Decrypt(key, temp);
		    int j = 0;
		    for(int i = step; i < step + 8; i++) {
		        decryption[i] = temp[j];
		        j++;
		    }
		    step = step + 8;    
		}

		String decryptedMessage = CASCII.toString(decryption);
		System.out.println("\n"+ "Decrypted Value Or Plaintext is: " + Arrays.toString(decryption)); 
		System.out.println("\n"+ "Decrypted message is: " + decryptedMessage);
		
		
	}
	
	//Encryption of plain text using key
		public static byte[] Encrypt(byte[] rawkey, byte[] plaintext) {
			//System.out.println("Key Generation started..");
			byte[][] subkeys = subkeyGenerator(rawkey);

			//System.out.println("\n"+  "Encryption started..");
			int[] IP = { 2, 6, 3, 1, 4, 8, 5, 7 };
			int[] IP_Inverse = { 4, 1, 3, 5, 7, 2, 8, 6 };

			byte[] text = permutator(plaintext, IP);
			
			//System.out.println("\n"+ "calling FK with K1");
			byte[] FKResult1=functionFK(text,subkeys[0]);
			//System.out.println("Result of FK with K1:	" + Arrays.toString(FKResult1));

			byte[] swapped = swap(FKResult1, 4);
			//System.out.println("\n" + "After SWAP:		" + Arrays.toString(swapped));
			
			//System.out.println("\n"+ "calling FK with K2");
			byte[] FKResult2=functionFK(swapped,subkeys[1]);
			//System.out.println("Result of FK with K2:	" + Arrays.toString(FKResult2));

			byte[] CT = permutator(FKResult2, IP_Inverse);
			
			return CT;
		}
		
		// Decryption of key and ciphertext
		public static byte[] Decrypt(byte[] rawkey, byte[] ciphertext) {
			// System.out.println("Key Generation started..");
			byte[][] subkeys = subkeyGenerator(rawkey);

			// System.out.println("\n"+ "Decryption started..");
			int[] IP = { 2, 6, 3, 1, 4, 8, 5, 7 };
			int[] IP_Inverse = { 4, 1, 3, 5, 7, 2, 8, 6 };

			byte[] ct = permutator(ciphertext, IP);

			// System.out.println("\n"+ "calling FK with K1");
			byte[] FKResult1 = functionFK(ct, subkeys[1]);
			// System.out.println("Result of FK with K1: " + Arrays.toString(FKResult1));

			byte[] swapped = swap(FKResult1, 4);
			// System.out.println("\n" + "After SWAP: " + Arrays.toString(swapped));

			// System.out.println("\n"+ "calling FK with K2");
			byte[] FKResult2 = functionFK(swapped, subkeys[0]);
			// System.out.println("Result of FK with K2: " + Arrays.toString(FKResult2));

			byte[] text = permutator(FKResult2, IP_Inverse);
			return text;

		}
		
		//Generate K1,K2 from K
		public static byte[][] subkeyGenerator(byte[] rawkey) {

			int[] p10 = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6 };
			int[] p8 = { 6, 3, 7, 4, 8, 5, 10, 9 };

			byte[] key = permutator(rawkey, p10);

			byte[] left_key = getLeftHalf(key);
			byte[] right_key = getRightHalf(key);

			left_key = left_shift(left_key, 1);
			right_key = left_shift(right_key, 1);
			key = concatenateByteArrays(left_key, right_key);

			byte[] subkey_1 = permutator(key, p8);

			left_key = left_shift(left_key, 2);
			right_key = left_shift(right_key, 2);
			key = concatenateByteArrays(left_key, right_key);

			byte[] subkey_2 = permutator(key, p8);

			byte[][] subkeys = { subkey_1, subkey_2 };
			//System.out.println("K1: " + Arrays.toString(subkey_1));
			//System.out.println("K2: " + Arrays.toString(subkey_2));
			return subkeys;
		}

		public static byte[] functionFK(byte[] text, byte[] subKey) {
			byte[] left_half = getLeftHalf(text);
			byte[] right_half = getRightHalf(text);

			int[] EP_Box = { 4, 1, 2, 3, 2, 3, 4, 1 };
			byte[] E_right_text = permutator(right_half, EP_Box);

			//System.out.println("After EP:		" + Arrays.toString(E_right_text));

			byte[] E_right_text_XOR = XORArray(E_right_text, subKey);
			//System.out.println("After XOR:		" + Arrays.toString(E_right_text_XOR));

			byte[] sboxArray = sBoxes(E_right_text_XOR);
			int[] p4 = { 2, 4, 3, 1 };
			sboxArray = permutator(sboxArray, p4);
			byte[] sboxArrayafterXOR = XORArray(sboxArray, left_half);

			byte[] concatSbox = concatenateByteArrays(sboxArrayafterXOR, right_half);
			//System.out.println("After SBOX:		" + Arrays.toString(concatSbox));
			return concatSbox;
		}

		// Substitution box
		public static byte[] sBoxes(byte[] E_right_text_XOR) {
			byte[] substituted = new byte[4];

			byte[] oneFour = new byte[2];
			oneFour[0] = E_right_text_XOR[0];
			oneFour[1] = E_right_text_XOR[3];

			byte[] twoThree = new byte[2];
			twoThree[0] = E_right_text_XOR[1];
			twoThree[1] = E_right_text_XOR[2];

			byte[] fiveEight = new byte[2];
			fiveEight[0] = E_right_text_XOR[4];
			fiveEight[1] = E_right_text_XOR[7];

			byte[] sixSeven = new byte[2];
			sixSeven[0] = E_right_text_XOR[5];
			sixSeven[1] = E_right_text_XOR[6];

			byte[] firstTwo = sBox1(oneFour, twoThree);
			byte[] lastTwo = sBox2(fiveEight, sixSeven);

			substituted = concatenateByteArrays(firstTwo, lastTwo);
			return substituted;
		}

		public static byte[] sBox1(byte[] oneFour, byte[] twoThree) {
			int row = getDecimal(oneFour);
			int col = getDecimal(twoThree);

			return getByte(S0[row][col], 2);
		}

		public static byte[] sBox2(byte[] fiveEight, byte[] sixSeven) {
			int row = getDecimal(fiveEight);
			int col = getDecimal(sixSeven);

			return getByte(S1[row][col], 2);

		}

		public static int getDecimal(byte[] arr) {
			int total = 0;
			int count = arr.length - 1;

			for (byte b : arr) {
				if (b == 1)
					total += Math.pow(2, count);
				count--;
			}
			return total;
		}

		public static byte[] getByte(int decimal, int binary_size) {
			byte[] binary = new byte[binary_size];

			while (binary_size > 0) {
				binary[binary_size - 1] = (byte) (decimal % 2);
				decimal = decimal >> 1;
				binary_size--;
			}
			return binary;
		}

		// Common methods
		//convert to string To ByteArray
		
		public static byte[] stringToByteArray(String s) {
			byte[] array = new byte[s.length()];

			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				array[i] = (byte) Character.getNumericValue(c);
			}
			return array;
		}

		 //This method used to permutation of key and plain text
		public static byte[] permutator(byte[] raw, int[] order) {
			byte[] permutated = new byte[order.length];

			for (int index = 0; index < permutated.length; index++) {
				permutated[index] = raw[order[index] - 1];
			}
			return permutated;
		}

		
		 // Get left part of an array
		 
		public static byte[] getLeftHalf(byte[] bytearray) {
			byte[] left_part = Arrays.copyOfRange(bytearray, 0, bytearray.length / 2);
			return left_part;
		}


		 // Get right part of an array
		public static byte[] getRightHalf(byte[] bytearray) {
			byte[] right_part = Arrays.copyOfRange(bytearray, bytearray.length / 2, bytearray.length);
			return right_part;
		}

		 //Left shift
		public static byte[] left_shift(byte[] raw, int shift_count) {
			byte[] shifted = raw;

			for (int x = 0; x < shift_count; x++) {
				byte first = raw[0];

				for (int i = 0; i < raw.length - 1; i++) {
					shifted[i] = raw[i + 1];
				}
				shifted[shifted.length - 1] = first;
			}
			return shifted;
		}

		
		 //concatenate Byte Arrays
		 
		public static byte[] concatenateByteArrays(byte[] left, byte[] right) {
			byte[] concatedAttay = new byte[left.length + right.length];

			System.arraycopy(left, 0, concatedAttay, 0, left.length);
			System.arraycopy(right, 0, concatedAttay, left.length, right.length);
			return concatedAttay;
		}

		public static byte[] XORArray(byte[] a, byte[] b) {
			byte[] result = new byte[a.length];

			for (int i = 0; i < result.length; i++) {
				result[i] = (byte) (a[i] ^ b[i]);
			}
			return result;
		}

		public static byte[] swap(byte[] array, int n) {
			byte[] l = new byte[n];
			byte[] r = new byte[n];

			for (int i = 0; i < n; i++) {
				l[i] = array[i];
				r[i] = array[i + n];
			}

			byte[] output = new byte[2 * n];
			for (int i = 0; i < n; i++) {
				output[i] = r[i];
				output[i + n] = l[i];
			}

			return output;
		}
	

	public static boolean check_message(String message) {
		
		for(int i = 0; i < message.length() - 1; i++) {
			if(message.charAt(i) == ',' || message.charAt(i) == '?' || message.charAt(i) == ':' || message.charAt(i) == '.') {
				if(message.charAt(i + 1) != ' ') {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static byte[] increment_key(byte[] key) {
		//System.out.println( Arrays.toString(key));
		for(int i = 9; i >= 0; i--) {
			if(key[i] == 0) {
				key[i] = 1;
				break;
			}
			else {
				key[i] = 0;	
			}
		}
		return key;
	}
}

