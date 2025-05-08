package utils;
import java.util.Arrays;
import java.util.Map;

public class SSMatrix {
    private int[][] matrix;
    private Map<Character, Integer> aaIndexMap;
    private Config config = new Config();



    public SSMatrix() {
        this.matrix = new int[config.getROW_SIZE()][config.getWINDOW_SIZE()];
        for (int row = 0; row < config.getROW_SIZE(); row++) {
            for (int col = 0; col < config.getWINDOW_SIZE(); col++) {
                matrix[row][col] = 0;
            }
        }
    }

    public void setValue(int row, int col, int value) {
        if (isValidIndex(row, col)) {
            matrix[row][col] = value;
        } else {
            throw new IndexOutOfBoundsException("Out of Bounds: (" + row + ", " + col + ")");
        }
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getValue(int row, int col) {
        if (isValidIndex(row, col)) {
            return matrix[row][col];
        } else {
            throw new IndexOutOfBoundsException("Out of Bounds: (" + row + ", " + col + ")");
        }
    }

    public boolean isValidIndex(int row, int col) {
        if (row >= 0 && row < matrix.length && col >= 0){//(row <= 20 && col <= 17) {
            return true;
        } else {
            return false;
        }
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < matrix.length; row++) {
            sb.append(config.getAMINO_ACIDS()[row]).append("\t");
            for (int col = 0; col < matrix[row].length; col++) {
                sb.append(matrix[row][col]).append("\t");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}
