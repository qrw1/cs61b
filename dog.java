public class dog {
    public int weightInPounds;
    public static String binomen = "Canis familiari";

    public dog(int w) {
        weightInPounds = w;
    }

    public void makeNoise() {
        if (weightInPounds < 10) {
            System.out.println("yip!");
        } else if (weightInPounds < 30) {
            System.out.println("bark.");
        } else {
            System.out.println("woooof!");
        }
    }

    public static dog maxdog(dog d1, dog d2) {
        if (d1.weightInPounds > d2.weightInPounds) {
            return d1;
        } else {
            return d2;
        }
    }
}
