package test;

import java.util.Scanner;

public class Multithread extends Thread {

	public static void main(String[] args) {
		Multithread m = new Multithread();
		m.start();
	}

	public void run() {
		System.out.println("How Many Credit Card Numbers do you want to Check? Please Enter number:");
		int num;

		Scanner scanner = new Scanner(System.in);
		num=scanner.nextInt();

		String[] string = new String[num];

		String s = new String();



		for(int i=0; i<num; i++) {

			System.out.println("Enter Credit Card Number: "+(i+1));
			string[i] = scanner.next();
			s=string[i];
			CreditCardLength cdl = new CreditCardLength(s);
			cdl.start();
			
			CreditCardNumberStartsWith ccnsw = new CreditCardNumberStartsWith(s);
			ccnsw.start();

			CreditCardValidation ccv = new CreditCardValidation(s);
			ccv.start();

			CreditValidation cv = new CreditValidation(s);
			cv.start();
			
			try {
				cdl.join();
				ccnsw.join();
				ccv.join();
				cv.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	public class CreditCardLength extends Thread {
		String string;
		CreditCardLength(String creditCardNumber){
			string=creditCardNumber;
		}

		public void run() {
			creditCardNumberLength(string);

		}

		public boolean creditCardNumberLength(String creditCardNumber) {
			if(creditCardNumber.length()>=13 && creditCardNumber.length()<=19) {
				return true;
			}
			else {
				return false;
			}
		}
	}


	public class CreditValidation extends Thread {
		String string;
		CreditValidation(String creditCardNumber){
			string=creditCardNumber;
		}

		public void run() {
			creditValidation(string);
		}

		public boolean creditValidation( String creditCardNumber) {
			CreditCardValidation ccv = new CreditCardValidation(creditCardNumber);
			CreditCardNumberStartsWith sw = new CreditCardNumberStartsWith(creditCardNumber);
			//If total Credit Card Number is divisible by 10, it is a Valid CreditCard Number
			if (ccv.creditCardValidation(creditCardNumber) % 10 == 0) {
				return true;
			}
			//If total Credit Card Number is not divisible by 10, or starts with any other number its Not Valid.
			else {
				return false;
			}
		}

	}

	public class CreditCardValidation extends Thread {
		String string;
		CreditCardValidation(String creditCardNumber){
			string=creditCardNumber;
		}
		public void run() {
			creditCardValidation(string);
		}


		public int creditCardValidation(String creditCardNumber) {
			//Array to store the credit card numbers in user input
			int[] numberArray = new int[creditCardNumber.length()];
			int size = creditCardNumber.length();
			int doubleEverySecondDigit=0;
			int total=0;
			for(int i=0; i<size;i++) {
				char n = creditCardNumber.charAt(i);
				//stores all numbers from a String
				numberArray[i] = Integer.parseInt(n+"");
			}
			for(int j=numberArray.length-2;j>=0;j-=2){
				//from right to left gets doubles every second digit
				doubleEverySecondDigit= numberArray[j];
				doubleEverySecondDigit=doubleEverySecondDigit*2;
				//when digit is greater than 10, add up two digits to get single one
				if(doubleEverySecondDigit>9) {
					doubleEverySecondDigit=(doubleEverySecondDigit/10)+(doubleEverySecondDigit%10);
				}

				numberArray[j]=doubleEverySecondDigit;


			}
			for(int k=0; k<numberArray.length;k++) {
				total+=numberArray[k];
			}
			return total;
		}
	}

	public class CreditCardNumberStartsWith extends Thread {
		String string;
		CreditCardNumberStartsWith(String creditCardNumber){
			string=creditCardNumber;
		}
		public void run() {
			creditCardNumberStartsWith(string);
		}


		public void creditCardNumberStartsWith(String creditCardNumber) {
			CreditCardLength cdl = new CreditCardLength(creditCardNumber);
			CreditValidation cv = new CreditValidation(creditCardNumber);
			
			if(cv.creditValidation(creditCardNumber)&&cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("34")||creditCardNumber.startsWith("37")) {
				System.out.println("American Express "+creditCardNumber+" is a valid Credit Card\n");


			}
			else if(cv.creditValidation(creditCardNumber)&&cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("6")) {
				System.out.println("Discover Card "+creditCardNumber+" is a valid Credit Card\n");

			}
			else if(cv.creditValidation(creditCardNumber)&&cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("51")||cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("52")||
					cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("53")||cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("54")||
					cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("55")) {
				System.out.println("MasterCard "+creditCardNumber+" is a valid Credit Card\n");

			}
			else if(cv.creditValidation(creditCardNumber)&&cdl.creditCardNumberLength(creditCardNumber)&&creditCardNumber.startsWith("4")) {
				System.out.println("Visa "+creditCardNumber+" is a valid Credit Card\n");

			}
			else {
				System.out.println(creditCardNumber+" is Invalid Credit Card \n");
			}
		}
	}

}

