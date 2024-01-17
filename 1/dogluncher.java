public class dogluncher {
    public static  void main(String[] args) {
        dog d = new dog(51);

        dog d2 = new dog(100);

        dog bigger = dog.maxdog(d,d2);
        bigger.makeNoise();

        System.out.println(d.binomen);
    }
}
