public class Test
{
    public static void main(String[] args) throws Exception{
        printOne();
        printOne();
        printTwo(3, 2);
    }
    
    public static void printOne() {
        System.out.println("Hello World");
    }
    
    private static void printTwo(int t, int t2) {
        //a comment
		printOne();
        printOne();
    }
	
	public int test(){
		int a = 1;
		int b = 4;
		return a + b;
		//do a thing
	}
	
	public void testCasts(){
		int a = (int) 0.5;
	}
}