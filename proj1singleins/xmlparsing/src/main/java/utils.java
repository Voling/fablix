import java.util.Random;

public class utils {
    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        String string = "";

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            string += characters.charAt(randomIndex);
        }

        return string;
    }
}