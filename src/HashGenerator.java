import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

    private static final int MATRIX_ROWS = 16;
    private static final int MATRIX_COLS = 100;
    private static final int BLOCK_SIZE = MATRIX_ROWS * MATRIX_COLS;

    public static byte[] generateHash(String filename) throws IOException, NoSuchAlgorithmException {
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
                    byte xor = matrix[0][col];
                    for (int row = 1; row < MATRIX_ROWS; row++) {
                        xor ^= matrix[row][col];
                    }
                    hash[col] ^= xor;
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
