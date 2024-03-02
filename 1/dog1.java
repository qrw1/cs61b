public class dog1 {
    public void bark(){
        System.out.println("Moo");
    }
    public static void runFast(){
        System.out.println("Ruff Run");
    }
    public static void main(String[] args) {
        dog1 poppa = new dog1();
        poppa.bark();
        poppa.runFast();
        dog1.runFast();
    }

}
