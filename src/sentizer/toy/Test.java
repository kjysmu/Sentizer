package sentizer.toy;

public class Test {
	
	public static void main(String args[]){
		
		String input = "abbf";
		
		String output = "";
		for(int i = 1; i <= input.length(); i++){
			
			String prefix = input.substring(0, input.length() - i);
			String suffix = input.substring(i, input.length());
			
			if( prefix.equals(suffix)) {
				
				output = prefix;
				break;
			}
			
		}
		
		System.out.println( "result : " + output );
		System.out.println( "length : " + output.length());
		
		
		
		
	}

}
