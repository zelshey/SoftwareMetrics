public class TestOne {

	public int test(int a, int b){
		while(a > b){
			b = a + b;
		}
		b = a / b;
		return b;
	}
}
