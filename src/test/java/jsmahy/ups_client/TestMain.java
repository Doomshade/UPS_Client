package jsmahy.ups_client;

public class TestMain {
    public static void main(String[] args) {
        byte fromX = 7 & 0b111;
        byte fromY = 5 & 0b111;
        byte toX = 6 & 0b111;
        byte toY = 1 & 0b111;

        short pos = (short) ((fromX << 9) | (fromY << 6) | (toX << 3) | toY);
        System.out.println(Integer.toBinaryString(pos));
        System.out.println(pos);
    }

}
