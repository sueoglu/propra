package model;

import utils.SSMatrix;
import utils.Config;
import java.lang.Math;

public class GORModel {
    Config config;

    // GOR1
    SSMatrix helix;
    SSMatrix sheet;
    SSMatrix coil;

    String type;


    SSMatrix[] helix3D;
    SSMatrix[] sheet3D;
    SSMatrix[] coil3D;

    // GOR 4
    SSMatrix[][][] helix5D;
    SSMatrix[][][] sheet5D;
    SSMatrix[][][] coil5D;

    public SSMatrix[] getHelix3D() {
        return helix3D;
    }

    public SSMatrix[] getSheet3D() {
        return sheet3D;
    }

    public SSMatrix[] getCoil3D() {
        return coil3D;
    }

    public SSMatrix[][][] getHelix5D() {
        return helix5D;
    }

    public GORModel() {
        this.config = new Config();
        this.helix = new SSMatrix();
        this.sheet = new SSMatrix();
        this.coil = new SSMatrix();
        this.type = "gor1";
    }

    public GORModel(String type) {
        this.config = new Config();

        if (type.equals("gor1")) {
            this.helix = new SSMatrix();
            this.sheet = new SSMatrix();
            this.coil = new SSMatrix();
            this.type = type;

        } else if (type.equals("gor3")) {
            this.helix3D = new SSMatrix[config.getROW_SIZE()];
            this.sheet3D = new SSMatrix[config.getROW_SIZE()];
            this.coil3D = new SSMatrix[config.getROW_SIZE()];
            this.type = type;

            for (int i = 0; i < config.getROW_SIZE(); i++) {
                helix3D[i] = new SSMatrix();
                sheet3D[i] = new SSMatrix();
                coil3D[i] = new SSMatrix();
            }

        } else if (type.equals("gor4")) {
            this.type = type;

            this.helix3D = new SSMatrix[config.getROW_SIZE()];
            this.sheet3D = new SSMatrix[config.getROW_SIZE()];
            this.coil3D = new SSMatrix[config.getROW_SIZE()];

            this.helix5D = new SSMatrix[config.getROW_SIZE()][config.getROW_SIZE()][config.getWINDOW_SIZE()];
            this.sheet5D = new SSMatrix[config.getROW_SIZE()][config.getROW_SIZE()][config.getWINDOW_SIZE()];
            this.coil5D =  new SSMatrix[config.getROW_SIZE()][config.getROW_SIZE()][config.getWINDOW_SIZE()];

            for (int i = 0; i < config.getROW_SIZE(); i++) {
                helix3D[i] = new SSMatrix();
                sheet3D[i] = new SSMatrix();
                coil3D[i] = new SSMatrix();
            }


            for (int i = 0; i < config.getROW_SIZE(); i++) {
                for (int j = 0; j < config.getROW_SIZE(); j++) {
                    for (int k = 0; k < config.getWINDOW_SIZE(); k++) {
                        helix5D[i][j][k] = new SSMatrix();
                        sheet5D[i][j][k] = new SSMatrix();
                        coil5D[i][j][k] = new SSMatrix();
                    }
                }
            }

        }
    }

    public void updateSSMatrix(int windowPos, char aa, char ss) {
        if (config.getAMINOACIDS().indexOf(aa) == -1) {

            return;
        }

        int aa_index = config.mapAminoAcids(aa);
        int newCount;

        switch (ss) {
            case 'C':
                newCount = coil.getValue(aa_index, windowPos) + 1;
                coil.setValue(aa_index, windowPos, newCount);
                break;
            case 'H':
                newCount = helix.getValue(aa_index, windowPos) + 1;
                helix.setValue(aa_index, windowPos, newCount);
                break;
            case 'E':
                newCount = sheet.getValue(aa_index, windowPos) + 1;
                sheet.setValue(aa_index, windowPos, newCount);
                break;
        }

    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setHelix5D(SSMatrix[][][] helix5D) {
        this.helix5D = helix5D;
    }

    public SSMatrix[][][] getSheet5D() {
        return sheet5D;
    }

    public void setSheet5D(SSMatrix[][][] sheet5D) {
        this.sheet5D = sheet5D;
    }

    public SSMatrix[][][] getCoil5D() {
        return coil5D;
    }

    public void setCoil5D(SSMatrix[][][] coil5D) {
        this.coil5D = coil5D;
    }

    public void updateSSMatrix(int windowPos, char aa, char ss, char centralAA) {
        if (config.getAMINOACIDS().indexOf(aa) == -1 || config.getAMINOACIDS().indexOf(centralAA) == -1) {
            return;
        }

        int aaIndex = config.mapAminoAcids(aa);
        int centralAAIndex = config.mapAminoAcids(centralAA);
        int newCount;

        switch (ss) {
            case 'C':
                newCount = coil3D[centralAAIndex].getValue(aaIndex, windowPos) + 1;
                coil3D[centralAAIndex].setValue(aaIndex, windowPos,newCount);
                break;
            case 'H':
                newCount = helix3D[centralAAIndex].getValue(aaIndex, windowPos) + 1;
                helix3D[centralAAIndex].setValue(aaIndex, windowPos,newCount);
                break;
            case 'E':
                newCount = sheet3D[centralAAIndex].getValue(aaIndex, windowPos) + 1;
                sheet3D[centralAAIndex].setValue(aaIndex, windowPos,newCount);
                break;
        }

    }

    public void updateSSMatrix(int windowPos, char aa, char ss, char centralAA, char pairAA, int pairPos) {
        if (config.getAMINOACIDS().indexOf(aa) == -1 || config.getAMINOACIDS().indexOf(centralAA) == -1 || config.getAMINOACIDS().indexOf(pairAA) == -1) {
            return;
        }


        try {
            int aaCountingIndex = config.mapAminoAcids(aa);
            int AAPairIndex = config.mapAminoAcids(pairAA);
            int centralAAIndex = config.mapAminoAcids(centralAA);
            int newCount;

            switch (ss) {
                case 'C':
                    newCount = coil5D[centralAAIndex][AAPairIndex][pairPos].getValue(aaCountingIndex, windowPos) + 1;
                    coil5D[centralAAIndex][AAPairIndex][pairPos].setValue(aaCountingIndex, windowPos, newCount);
                    break;
                case 'H':
                    newCount = helix5D[centralAAIndex][AAPairIndex][pairPos].getValue(aaCountingIndex, windowPos) + 1;
                    helix5D[centralAAIndex][AAPairIndex][pairPos].setValue(aaCountingIndex, windowPos, newCount);
                    break;
                case 'E':
                    newCount = sheet5D[centralAAIndex][AAPairIndex][pairPos].getValue(aaCountingIndex, windowPos) + 1;
                    sheet5D[centralAAIndex][AAPairIndex][pairPos].setValue(aaCountingIndex, windowPos, newCount);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error in updateSSMatrix:");
            System.err.println("windowPos: " + windowPos + ", aa: " + aa + ", ss: " + ss +
                    ", centralAA: " + centralAA + ", pairAA: " + pairAA + ", pairPos: " + pairPos);
            e.printStackTrace();
        }
    }


    public void printModel() {
        StringBuilder sb = new StringBuilder();

        switch (type) {
            case "gor1":
                sb.append("// Matrix3D\n\n");

                sb.append("=C=\n\n");
                sb.append(coil.asString()).append("\n\n");

                sb.append("=E=\n\n");
                sb.append(sheet.asString()).append("\n\n");

                sb.append("=H=\n\n");
                sb.append(helix.asString()).append("\n");
                break;
            case "gor3":
                sb.append("// Matrix4D\n\n");

                for (int index = 0; index < config.getROW_SIZE(); index++) {
                    char curAA = config.mapAminoAcids(index);

                    sb.append("="+curAA+",C=\n\n");
                    sb.append(coil3D[index].asString()).append("\n\n");

                    sb.append("="+curAA+",E=\n\n");
                    sb.append(sheet3D[index].asString()).append("\n\n");

                    sb.append("="+curAA+",H=\n\n");
                    sb.append(helix3D[index].asString()).append("\n\n");
                }

                break;

            case "gor4":
                sb.append("// Matrix6D\n\n");

                for (int centralAAIndex = 0; centralAAIndex < config.getROW_SIZE(); centralAAIndex++) {
                    for (int pairAAIndex = 0; pairAAIndex < config.getROW_SIZE(); pairAAIndex++) {
                        for (int windowPos = 0; windowPos < config.getWINDOW_SIZE(); windowPos++) {
                            char curCentralAA = config.mapAminoAcids(centralAAIndex);
                            char pairAA = config.mapAminoAcids(pairAAIndex);
                            int indexMapForHeader = windowPos - 8;

                            sb.append("=C," + curCentralAA + "," + pairAA + "," + indexMapForHeader+ "=\n\n");
                            sb.append(coil5D[centralAAIndex][pairAAIndex][windowPos].asString()).append("\n\n");


                        }
                    }
                }

                for (int centralAAIndex = 0; centralAAIndex < config.getROW_SIZE(); centralAAIndex++) {
                    for (int pairAAIndex = 0; pairAAIndex < config.getROW_SIZE(); pairAAIndex++) {
                        for (int windowPos = 0; windowPos < config.getWINDOW_SIZE(); windowPos++) {
                            char curCentralAA = config.mapAminoAcids(centralAAIndex);
                            char pairAA = config.mapAminoAcids(pairAAIndex);
                            int indexMapForHeader = windowPos - 8;

                            sb.append("=E," + curCentralAA + "," + pairAA + "," + indexMapForHeader + "=\n\n");
                            sb.append(sheet5D[centralAAIndex][pairAAIndex][windowPos].asString()).append("\n\n");
                        }
                    }
                }

                for (int centralAAIndex = 0; centralAAIndex < config.getROW_SIZE(); centralAAIndex++) {
                    for (int pairAAIndex = 0; pairAAIndex < config.getROW_SIZE(); pairAAIndex++) {
                        for (int windowPos = 0; windowPos < config.getWINDOW_SIZE(); windowPos++) {
                            char curCentralAA = config.mapAminoAcids(centralAAIndex);
                            char pairAA = config.mapAminoAcids(pairAAIndex);
                            int indexMapForHeader = windowPos - 8;

                            sb.append("=H," + curCentralAA + "," + pairAA + "," + indexMapForHeader + "=\n\n");
                            sb.append(helix5D[centralAAIndex][pairAAIndex][windowPos].asString()).append("\n\n");
                        }
                    }
                }

                sb.append("// Matrix4D\n\n");

                for (int index = 0; index < config.getROW_SIZE(); index++) {
                    char curAA = config.mapAminoAcids(index);

                    sb.append("="+curAA+",C=\n\n");
                    sb.append(coil3D[index].asString()).append("\n\n");

                    sb.append("="+curAA+",E=\n\n");
                    sb.append(sheet3D[index].asString()).append("\n\n");

                    sb.append("="+curAA+",H=\n\n");
                    sb.append(helix3D[index].asString()).append("\n\n");
                }

                break;


        }

        System.out.println(sb.toString());
    }

    public double getScore1(int windowPos, char aa, char ssType) {
        if (config.getAMINOACIDS().indexOf(aa) == -1) {
            return 0.0;
        }

        int aaIndex = config.mapAminoAcids(aa);
        int AAcount = 0;
        int totalCountOfAA = 0;
        int count2 = 0;
        int countOfallAAinAllMatrices = 0;

        switch (ssType) { // given parameter
            case 'C':
                AAcount = coil.getValue(aaIndex, windowPos);
                totalCountOfAA = AAcount + helix.getValue(aaIndex, windowPos) + sheet.getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    int countOfAA = coil.getValue(i, windowPos);
                    count2 += countOfAA;
                    countOfallAAinAllMatrices += countOfAA + helix.getValue(i, windowPos) + sheet.getValue(i, windowPos);
                }

                break;

            case 'H':
                AAcount = helix.getValue(aaIndex, windowPos);
                totalCountOfAA = AAcount + sheet.getValue(aaIndex, windowPos) + coil.getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    int countOfAA = helix.getValue(i, windowPos);
                    count2 += countOfAA;
                    countOfallAAinAllMatrices += countOfAA + sheet.getValue(i, windowPos) + coil.getValue(i, windowPos);
                }
                break;
            case 'E':
                AAcount = sheet.getValue(aaIndex, windowPos);
                totalCountOfAA = AAcount + helix.getValue(aaIndex, windowPos) + coil.getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    int countOfAA = sheet.getValue(i, windowPos);
                    count2 += countOfAA;
                    countOfallAAinAllMatrices += countOfAA + helix.getValue(i, windowPos) + coil.getValue(i, windowPos);
                }
                break;
        }

        if (totalCountOfAA == 0 || countOfallAAinAllMatrices == 0) {
            return 0.0;
        }

        double prob1 = (double) AAcount / (double) totalCountOfAA;
        double prob2 = (double) count2 / (double) countOfallAAinAllMatrices;

        double prob = Math.log(prob1 / (1-prob1) )  + Math.log((1-prob2) / prob2 );

        return prob;
    }


    public double getScore3(int windowPos, char aa, char ssType, char centralAA) {
        if (config.getAMINOACIDS().indexOf(aa) == -1 || config.getAMINOACIDS().indexOf(centralAA) == -1) {
            return 0.0;
        }

        int aaIndex = config.mapAminoAcids(aa);
        int centralAAIndex = config.mapAminoAcids(centralAA);
        int count = 0;
        int totalCount = 0;
        int count2 = 0;
        int countOfallAAinAllMatrices = 0;

        switch (ssType) { // given parameter
            case 'C':
                count = coil3D[centralAAIndex].getValue(aaIndex, windowPos); //haufigkeit der window aa an wi pos wenn centrale aminoacid AAIndex
                totalCount = count + helix3D[centralAAIndex].getValue(aaIndex, windowPos) + sheet3D[centralAAIndex].getValue(aaIndex, windowPos); //summe aller werte in spalte wi pos

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    int countOfAA = coil3D[centralAAIndex].getValue(i, windowPos);
                    count2 += countOfAA;
                    countOfallAAinAllMatrices += countOfAA + helix3D[centralAAIndex].getValue(i, windowPos) + sheet3D[centralAAIndex].getValue(i, windowPos);
                }

                break;
            case 'H':
                count = helix3D[centralAAIndex].getValue(aaIndex, windowPos);
                totalCount = count + coil3D[centralAAIndex].getValue(aaIndex, windowPos) + sheet3D[centralAAIndex].getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    int countOfAA = helix3D[centralAAIndex].getValue(i, windowPos);
                    count2 += countOfAA;
                    countOfallAAinAllMatrices += countOfAA + coil3D[centralAAIndex].getValue(i, windowPos) + sheet3D[centralAAIndex].getValue(i, windowPos);
                }

                break;
            case 'E':
                count = sheet3D[centralAAIndex].getValue(aaIndex, windowPos);
                totalCount = count + helix3D[centralAAIndex].getValue(aaIndex, windowPos) + coil3D[centralAAIndex].getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    int countOfAA = sheet3D[centralAAIndex].getValue(i, windowPos);
                    count2 += countOfAA;
                    countOfallAAinAllMatrices += countOfAA + coil3D[centralAAIndex].getValue(i, windowPos) + helix3D[centralAAIndex].getValue(i, windowPos);
                }
                break;
        }

        if (totalCount == 0) {
            return 0.0;
        }

        double prob1 = (double) count / (double) totalCount;
        double prob2 = (double) count2 / (double) countOfallAAinAllMatrices;

        double prob = Math.log(prob1 / (1-prob1) )  + Math.log((1-prob2) / prob2 );

        return prob; // final score, amino acid being in the given position in der ss configuration wenn centralAA
    }

    public double getScore4(int windowPos, char aa, char ssType, char centralAA, char pairAA, int pairIndex) {
        if (config.getAMINOACIDS().indexOf(aa) == -1 || config.getAMINOACIDS().indexOf(centralAA) == -1) {
            return 0.0;
        }

        int aaIndex = config.mapAminoAcids(aa);
        int pairAAIndex = config.mapAminoAcids(pairAA);
        int centralAAIndex = config.mapAminoAcids(centralAA);
        double count = 0;
        double totalCount = 0;
        double pseudoCount = Config.getPseudoCount();

        switch (ssType) { // given parameter
            case 'C':
                count = coil5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos) + pseudoCount;
                totalCount = helix5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos)
                            + sheet5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos)
                            + count
                            + pseudoCount;
                break;
            case 'H':
                count = helix5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos) + pseudoCount;
                totalCount = coil5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos)
                        + sheet5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos)
                        + count
                        + pseudoCount;

                break;
            case 'E':
                count = sheet5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos) + pseudoCount;
                totalCount = helix5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos)
                        + coil5D[centralAAIndex][pairAAIndex][pairIndex].getValue(aaIndex, windowPos)
                        + count
                        + pseudoCount;
                break;
        }


        double prob1 = count / totalCount;


        return Math.log(prob1 / (1-prob1) );


    }

    public double getScore5(int windowPos, char countingAA, char ssType, char centralAA) {
        if (config.getAMINOACIDS().indexOf(countingAA) == -1 || config.getAMINOACIDS().indexOf(centralAA) == -1) {
            return 0.0;
        }

        int aaIndex = config.mapAminoAcids(countingAA);
        int centralAAIndex = config.mapAminoAcids(centralAA);
        double count = 0;
        double totalCount = 0;
        int pseudoCount = 1;

        switch (ssType) { // given parameter
            case 'C':
                count = coil3D[centralAAIndex].getValue(aaIndex, windowPos) + pseudoCount;
                totalCount = helix3D[centralAAIndex].getValue(aaIndex, windowPos)
                        + sheet3D[centralAAIndex].getValue(aaIndex, windowPos)
                        + count
                        + pseudoCount;
                break;
            case 'H':
                count = helix3D[centralAAIndex].getValue(aaIndex, windowPos) + pseudoCount;
                totalCount = coil3D[centralAAIndex].getValue(aaIndex, windowPos)
                        + sheet3D[centralAAIndex].getValue(aaIndex, windowPos)
                        + count
                        + pseudoCount;

                break;
            case 'E':
                count = sheet3D[centralAAIndex].getValue(aaIndex, windowPos) + pseudoCount;
                totalCount = helix3D[centralAAIndex].getValue(aaIndex, windowPos)
                        + coil3D[centralAAIndex].getValue(aaIndex, windowPos)
                        + count
                        + pseudoCount;
                break;
        }


        double prob1 = count / totalCount;


        return Math.log(prob1 / (1-prob1) );

    }

    /*
    public double getScore5(int windowPos, char aa, char ssType, char centralAA) {
        if (config.getAMINOACIDS().indexOf(aa) == -1 || config.getAMINOACIDS().indexOf(centralAA) == -1) {
            return 0.0;
        }

        int aaIndex = config.mapAminoAcids(aa);
        int AAcount = 0;
        int totalCountOfAA = 0;
        int count2 = 0;
        int countOfallAAinAllMatrices = 0;

        switch (ssType) { // given parameter
            case 'C':
                AAcount = coil.getValue(aaIndex, windowPos);
                totalCountOfAA = AAcount + helix.getValue(aaIndex, windowPos) + sheet.getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    count2 += coil.getValue(i, windowPos);
                    countOfallAAinAllMatrices = count2  + helix.getValue(i, windowPos) + sheet.getValue(i, windowPos);
                }

                break;

            case 'H':
                AAcount = helix.getValue(aaIndex, windowPos);
                totalCountOfAA = AAcount + sheet.getValue(aaIndex, windowPos) + coil.getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    count2 += helix.getValue(i, windowPos);
                    countOfallAAinAllMatrices =  count2  + sheet.getValue(i, windowPos) + coil.getValue(i, windowPos);
                }
                break;
            case 'E':
                AAcount = sheet.getValue(aaIndex, windowPos);
                totalCountOfAA = AAcount + helix.getValue(aaIndex, windowPos) + coil.getValue(aaIndex, windowPos);

                for(int i = 0; i < config.getROW_SIZE(); i++) {
                    count2 += sheet.getValue(i, windowPos);
                    countOfallAAinAllMatrices =  count2 + helix.getValue(i, windowPos) + coil.getValue(i, windowPos);
                }
                break;
        }

        if (totalCountOfAA == 0 || countOfallAAinAllMatrices == 0) {
            return 0.0;
        }

        double prob1 = (double) AAcount / (double) totalCountOfAA;
        double prob2 = (double) count2 / (double) countOfallAAinAllMatrices;

        double prob = Math.log(prob1 / (1-prob1) )  + Math.log((1-prob2) / prob2 );

        return prob;
    }

     */

    public SSMatrix getSheet() {
        return sheet;
    }

    public void setSheet(SSMatrix sheet) {
        this.sheet = sheet;
    }

    public SSMatrix getHelix() {
        return helix;
    }

    public void setHelix(SSMatrix helix) {
        this.helix = helix;
    }

    public SSMatrix getCoil() {
        return coil;
    }

    public void setCoil(SSMatrix coil) {
        this.coil = coil;
    }

    public void setHelix3D(SSMatrix[] helix3D) {
        this.helix3D = helix3D;
    }

    public void setSheet3D(SSMatrix[] sheet3D) {
        this.sheet3D = sheet3D;
    }

    public void setCoil3D(SSMatrix[] coil3D) {
        this.coil3D = coil3D;
    }
}