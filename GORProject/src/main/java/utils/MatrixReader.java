package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixReader {
        private int[][] matrixH;
        private int[][] matrixE;
        private int[][] matrixC;

        private Map<Character, int[][]> matrixHMap; //0 [0]
        private Map<Character, int[][]> matrixEMap;
        private Map<Character, int[][]> matrixCMap;



    public MatrixReader() {
        matrixHMap = new HashMap<>();
        matrixEMap = new HashMap<>();
        matrixCMap = new HashMap<>();
    }

        public void readFile(String filePath) {
            List<int[]> matrixHList = new ArrayList<>();
            List<int[]> matrixEList = new ArrayList<>();
            List<int[]> matrixCList = new ArrayList<>();

            boolean isH = false, isE = false, isC = false;
            boolean isGor3 = false;
            boolean isGor4 = false;

            Map<Character, int[][]> currentMatrix = null;
            Character currentCentralAA = null;
            int rowIndex = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean headerSkipped = false;

                while ((line = br.readLine()) != null) {
                    line = line.trim();

                    if(line.startsWith("// Matrix6D")) {
                        isGor4 = true;
                        continue;
                    }

                    if(line.startsWith("// Matrix4D")) {
                        isGor3 = true;
                        continue;
                    }

                    if (!headerSkipped && line.trim().startsWith("// Matrix3D")) {
                        headerSkipped = true;
                        continue;
                    }

                    /* if(isGor4){

                    }*/

                    if (isGor3) {
                        if (line.startsWith("=")) {
                            String[] parts = line.replace("=", "").split(",");
                            if (parts.length == 2) {
                                currentCentralAA = parts[0].charAt(0);
                                char structureType = parts[1].charAt(0);

                                switch (structureType) {
                                    case 'H':
                                        currentMatrix = matrixHMap;//wenn h matrix is in the h matrix map speichern
                                        break;
                                    case 'E':
                                        currentMatrix = matrixEMap;
                                        break;
                                    case 'C':
                                        currentMatrix = matrixCMap;
                                        break;
                                    default:
                                        currentMatrix = null;
                                        break;
                                }

                                // Initialize matrix if new
                                if (currentMatrix != null && !currentMatrix.containsKey(currentCentralAA)) {
                                    currentMatrix.put(currentCentralAA, new int[20][17]);
                                }
                                rowIndex = 0;
                            }
                            continue;
                        }
                        if (line.isEmpty() || currentMatrix == null) continue;

                        String[] parts = line.split("\\s+");
                        if (parts.length > 1) {
                            int[] row = new int[parts.length - 1];
                            for (int i = 1; i < parts.length; i++) {
                                row[i - 1] = Integer.parseInt(parts[i]);
                            }

                            if (currentCentralAA != null) {
                                currentMatrix.get(currentCentralAA)[rowIndex] = row;
                                rowIndex++;
                            }
                        }
                    }
                    else { //gor1
                        if (line.equals("=H=")) {
                            isH = true;
                            isE = false;
                            isC = false;
                            continue;
                        } else if (line.equals("=E=")) {
                            isH = false;
                            isE = true;
                            isC = false;
                            continue;
                        } else if (line.equals("=C=")){
                            isH = false;
                            isE = false;
                            isC = true;
                            continue;
                        }
                        if (line.isEmpty()) continue;

                        String[] parts = line.split("\\s+");
                        if (parts.length > 1) {
                            int[] row = new int[parts.length - 1];
                            for (int i = 1; i < parts.length; i++) {
                                row[i - 1] = Integer.parseInt(parts[i]);
                            }

                            if (isH) {
                                matrixHList.add(row);
                            } else if (isE) {
                                matrixEList.add(row);
                            } else if (isC){
                                matrixCList.add(row);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!isGor3) {
                matrixH = matrixHList.toArray(new int[0][]);
                matrixE = matrixEList.toArray(new int[0][]);
                matrixC = matrixCList.toArray(new int[0][]);
            }
        }
        public int[][] getMatrixH() {
            return matrixH;
        }

        public int[][] getMatrixE() {
            return matrixE;
        }

        public int[][] getMatrixC() {
        return matrixC;
        }

        public Map<Character, int[][]> getMatrixHMap() {
            return matrixHMap; }
        public Map<Character, int[][]> getMatrixEMap() {
            return matrixEMap; }
        public Map<Character, int[][]> getMatrixCMap() {
            return matrixCMap; }

        public void printMatrix(int[][] matrix) {
            for (int[] row : matrix) {
                for (int value : row) {
                    System.out.print(value + "\t");
                }
                System.out.println();
            }
        }


}
