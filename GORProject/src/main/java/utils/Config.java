package utils;

import java.util.HashMap;

public class Config {

    public static final char[] AMINO_ACIDS = {
            'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L',
            'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y'
    };

    public static final String AMINOACIDS = "ACDEFGHIKLMNPQRSTVWY";
    public static final char[] SECONDARY_STRUCTURES = {'C', 'H', 'E'};
    public static int WINDOW_SIZE = 17;
    public static int ROW_SIZE = 20;
    public static double pseudoCount = 1.0;
    public static boolean postProcessing = false;

    private static final HashMap<Character, Integer> aminoAcidIndexMap = new HashMap<>();

    public static int getWindowSize() {
        return WINDOW_SIZE;
    }

    public static void setWindowSize(int windowSize) {
        WINDOW_SIZE = windowSize;
    }

    public static int getRowSize() {
        return ROW_SIZE;
    }

    public static void setRowSize(int rowSize) {
        ROW_SIZE = rowSize;
    }

    public static boolean isPostProcessing() {
        return postProcessing;
    }
    public static int countSS(char ss, String seq) {
        int count = 0;
        for (char c : seq.toCharArray()) {
            if (c == ss) {
                count++;
            }
        }
        return count;

    }

    public static double getSSContent(char ss, String seq) {
        int count = countSS(ss, seq);
        int length = seq.length();
        if (length == 0) return 0.0;
        return (double) count / length * 100;
    }

    public static void setPostProcessing(boolean postProcessing) {
        Config.postProcessing = postProcessing;
    }

    static {
        for (int i = 0; i < AMINO_ACIDS.length; i++) {
            aminoAcidIndexMap.put(AMINO_ACIDS[i], i);
        }
    }


    public static int mapAminoAcids(char aa) {
        Integer index = aminoAcidIndexMap.get(aa);
        if (index == null) {
            throw new IllegalArgumentException("Amino acid " + aa + " is not valid.");
        }
        return index;
    }

    public static char mapAminoAcids(int index) {
        return AMINOACIDS.charAt(index);
    }


    public static char[] getAMINO_ACIDS() {
        return AMINO_ACIDS;
    }

    public static String getAMINOACIDS() {
        return AMINOACIDS;
    }

    public static void setPseudoCount(double newPseudoCount) {
        pseudoCount = newPseudoCount;
    }

    public static double getPseudoCount() {
        return pseudoCount;
    }

    public static char[] getSECONDARY_STRUCTURES() {
        return SECONDARY_STRUCTURES;
    }

    public static int getWINDOW_SIZE() {
        return WINDOW_SIZE;
    }

    public static int getROW_SIZE() {
        return ROW_SIZE;
    }

    public static void setWINDOW_SIZE(int windowSize) {
        WINDOW_SIZE = windowSize;
    }
}
