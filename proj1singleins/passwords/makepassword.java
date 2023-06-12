
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.StandardOpenOption;


public class makepassword {
    public static void main(String[] args) {
        String plainPassword = "jasminle";
        String plainEmail = "myfavgirl";
        String hpassword = "";
        String hemail = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedpassword = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            byte[] hashedemail = digest.digest(plainEmail.getBytes(StandardCharsets.UTF_8));

            for (byte b : hashedpassword) {
                //hpassword.append(String.format("%02x", b));
                hpassword += String.format("%02x", b);
            }
            for (byte b : hashedemail) {
                hemail += String.format("%02x", b);
            }
            


            System.out.println("Hashed password: " + hpassword.toString());
            System.out.println("Hashed email: " + hemail.toString());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
        }
        Path filePath = Paths.get("users.txt");

        try {
            Files.write(filePath, plainPassword.getBytes(StandardCharsets.UTF_8),StandardOpenOption.APPEND);
            Files.write(filePath, " ".getBytes(StandardCharsets.UTF_8),StandardOpenOption.APPEND);
            Files.write(filePath, plainEmail.getBytes(StandardCharsets.UTF_8),StandardOpenOption.APPEND);
            Files.write(filePath, " ".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            Files.write(filePath, hpassword.getBytes(StandardCharsets.UTF_8),StandardOpenOption.APPEND);
            Files.write(filePath, " ".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            Files.write(filePath, hemail.getBytes(StandardCharsets.UTF_8),StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
