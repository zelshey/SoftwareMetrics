public class TestOne {

	public void test(int a, int b){
		while(a > b){
			b++;
			
			try{
				a++;
				
			}catch(Exception e){
				b++;
			}
			
			while(a == 2){
				a++;
			}
		}
		a++;
	}
}
