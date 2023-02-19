import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class Main {

    // create method encrypt message that encrypts the message using the public key
    public static String encrypt(String message, BigInteger e, BigInteger n) {
        BigInteger m = new BigInteger(message.getBytes());
        BigInteger c = m.modPow(e, n);
        return c.toString();
    }

    // create method decrypt message that decrypts the message using the private key
    public static String decrypt(String message, BigInteger d, BigInteger n) {
        BigInteger c = new BigInteger(message);
        BigInteger m = c.modPow(d, n);
        return new String(m.toByteArray());
    }


    public static void main(String[] args) {

        // variables for RSA key generation
        int bitLength = 1024;
        SecureRandom rnd = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength, rnd);
        BigInteger q = BigInteger.probablePrime(bitLength, rnd);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.probablePrime(bitLength / 2, rnd);
        BigInteger d = e.modInverse(phi);


        String message = "hey coucou";
        System.out.println("Message: " + message);

        String encryptMessage = encrypt(message, e, n);
        System.out.println("Encrypted message: " + encryptMessage);

        String decryptMessage = decrypt(encryptMessage, d, n);
        System.out.println("Decrypted message: " + decryptMessage);

        try {
            byte[] hash = HashGenerator.generateHash("src/test.txt");
            System.out.println(Arrays.toString(hash));
        } catch (IOException ea) {
            ea.printStackTrace();
        }

    }
}