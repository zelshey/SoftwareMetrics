public class Test
{
    public static void main(String[] args) {
        printOne();
        printOne();
        printTwo(1, 2);
    }
    
    public static void printOne() {
        System.out.println("Hello World");
    }
    
    public static void printTwo(int t, int t2) {
        printOne();
        printOne();
    }
}