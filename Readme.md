# TP Cryptage asymetrique et Hashage

## RSA

### 1.

```java
public static String encrypt(String message,BigInteger e,BigInteger n){
        BigInteger m=new BigInteger(message.getBytes());
        BigInteger x=m.modPow(e,n);
        return x.toString();
        }
```

la fonction prends en entrée une string a encrypter, e l'exposant de chiffrement et n le module de chiffrement

apres avoir transformé le message en un nombre, il est encrypté en le multipliant a la puissance du modulo entre e et n
dans une variable c qui est ensuite retounée

### 2.

```java
public static String decrypt(String message,BigInteger d,BigInteger n){
        BigInteger c=new BigInteger(message);
        BigInteger m=c.modPow(d,n);
        return new String(m.toByteArray());
        }
``` 

la fonction prends en entrée une string a decrypter, d l'exposant de dechiffrement et n le module de chiffrement

de la meme maniere que la fonction precedente,
apres avoir transformé le message en un nombre, il est decrypté en le multipliant a la puissance du modulo entre d et n
dans une variable m qui est ensuite retounée en string

### Code Complet `Main.java`

```java
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class Main {

    // encrypts the message using the public key
    public static String encrypt(String message, BigInteger e, BigInteger n) {
        BigInteger m = new BigInteger(message.getBytes());
        BigInteger c = m.modPow(e, n);
        return c.toString();
    }

    // decrypts the message using the private key
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
            byte[] hash = HashGenerator.generateHash("src/file_examples/test.txt");
            System.out.println(Arrays.toString(hash));
        } catch (IOException ea) {
            ea.printStackTrace();
        }
    }
}
```

### Terminal output

```bash
Message: hey coucou
Encrypted message: 840199221548842291457641361693223098303971044359661793626928280403514264203813606278370942044386812221568962787864036082894507593277964299453514638576832846721713173821602599907126438248480405085209212288711193904395840770367129382664148283271332811569293410317806691185026516165687351303556298079047068948352143317594449032833518845640286054082325588890556614596330295917593698742261468546104975232730948533332730818584560872309036011372228048061172444446969988978653638564790894844755739444073675721648320492028598378108632093849895757106943333485938467532505068708775189525679884395128230181404586177235236546629
Decrypted message: hey coucou
```

---

## Hashage

### Code complet `HashGenerator.java`

```java
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class HashGenerator {

    private static final int MATRIX_ROWS = 16;
    private static final int MATRIX_COLS = 100;
    private static final int BLOCK_SIZE = MATRIX_ROWS * MATRIX_COLS;

    public static byte[] generateHash(String filename) throws IOException {
        byte[] hash = new byte[MATRIX_ROWS];
        byte[] block = new byte[BLOCK_SIZE];
        int bytesRead;

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename))) {
            while ((bytesRead = inputStream.read(block)) != -1) {
                if (bytesRead < BLOCK_SIZE) {
                    // Padding with zeros if the last block is not complete
                    for (int i = bytesRead; i < BLOCK_SIZE; i++) {
                        block[i] = 0;
                    }
                }

                // Generate matrix and calculate XOR of each column
                byte[][] matrix = generateMatrix(block);
                for (int col = 0; col < MATRIX_COLS; col++) {
                    byte xor = 0;
                    for (int row = 0; row < MATRIX_ROWS; row++) {
                        xor ^= matrix[row][col];
                    }
                    hash[col % MATRIX_ROWS] ^= xor;
                }
            }
        }

        return hash;
    }

    private static byte[][] generateMatrix(byte[] block) {
        byte[][] matrix = new byte[MATRIX_ROWS][MATRIX_COLS];
        int index = 0;
        for (int col = 0; col < MATRIX_COLS; col++) {
            for (int row = 0; row < MATRIX_ROWS; row++) {
                matrix[row][col] = block[index];
                index++;
            }
        }
        return matrix;
    }
}
```

la classe commence par calculer la taille d'un block, ici 1600 bits

ensuite elle lit le fichier block par block et le stocke dans un tableau de byte
si le block n'est pas complet, il est rempli de 0 avec la boucle for

ensuite on genere une matrice a partir du tableau de block grace a la fonction `generateMatrix`
qui fonctionne en parcourant la matrice par colonne et en remplissant chaque case avec un byte du tableau de block

ensuite on calcule le hash en parcourant la matrice par colonne et en calculant le xor de chaque colonne

pour finir on retourne le hash generé

### Terminal output

avec le fichier `test.txt` contenant `lorem ipsum dolor sit amet`

```
[3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
```

le hash est donc `03010000000000000000000000000000`

avec le fichier `bee_movie_script.txt` contenant le script du film `bee movie`

```
[105, 115, 47, 54, 84, 68, 38, 105, 47, 12, 6, 79, 55, 28, 73, 1]
```

le hash est donc `69732f36544426692f0c064f371c4901`

_Note : Ces hash ont ete generes sur un systeme linux et sont donc different sur windows et mac os a cause de leur
differente gestion des sauts de ligne_

---
[Github Repo](https://github.com/TxMat/CybersecRSA)