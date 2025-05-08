import prediction.*;
import model.GORModel;
import utils.*;
import org.apache.commons.cli.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static validation.CVMain.getCommandLine;


//muss noch für gor5_3, 5_4 die text file writer

public class GORPredictMain {
    private static final DecimalFormat df = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));
    public static void main(String[] args) throws IOException {

        CommandLine cmd = parseArguments(args);
        String modelFilePath = cmd.getOptionValue("model");
        String format = cmd.getOptionValue("format");
        String sequenceFilePath = cmd.getOptionValue("seq");
        String alignmentFilePath = cmd.getOptionValue("maf");


        if (cmd.hasOption("window")) {
            int windowSize = Integer.parseInt(cmd.getOptionValue("window"));
            Config.setWINDOW_SIZE(windowSize);
        }

        // for if file

        if (cmd.hasOption("postprc")) {
            Config.setPostProcessing(true);
        }

        HashMap<String, String> mapOfSequences = new HashMap<>();
        if(sequenceFilePath != null) {
            mapOfSequences = seqFileToSeqMap(sequenceFilePath);
        }

        Map<String, HashMap<String, List<String>>> mapOfAlignments = new HashMap<>();

        if(alignmentFilePath != null) {
            AlignmentReader alignmentReader = new AlignmentReader();
            File alignmentDirectory = new File(alignmentFilePath);

            if (alignmentDirectory.isDirectory()) {
                for (File file : alignmentDirectory.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".aln")) {
                        alignmentReader.readAlignmentFile(file.getAbsolutePath());
                    }
                }
            } else{
                alignmentReader.readAlignmentFile(alignmentFilePath);
            }
            mapOfAlignments = alignmentReader.getAlignments();
        }

// if (modelFilePath.startsWith("/Users/oykusuoglu/Desktop/GOR/gor_examples/gor1")) for local test

        if (modelFilePath.contains("gor1")) {
            GORModel model = loadModel(modelFilePath, "gor1");
            if(alignmentFilePath != null) {
                GOR5Predict predictor = new GOR5Predict(model);
                for (String seqName : mapOfAlignments.keySet()) {
                    HashMap<String, List<String>> queryProtein = mapOfAlignments.get(seqName);
                    String seqAA = queryProtein.keySet().iterator().next();
                    String predictedSeq = queryProtein.get(seqAA).getFirst();

                    List<String> sequences = new ArrayList<>();
                    sequences.add(predictedSeq);
                    List<String> alignments = queryProtein.get("Alignments");

                    sequences.addAll(alignments);

                    PredSecondaryStructure prediction = predictor.predict(model, "gor1" ,sequences); //1111
                    if (format.equals("html")) {
                        writeHTMLOutput(seqName, predictedSeq, prediction);
                    }
                    if (format.equals("txt")) {
                        writeTxtOutput(seqName, predictedSeq, prediction);
                    }
                }
            } else {
                GORPredict predictor = new GORPredict(model);
                for (String seqName : mapOfSequences.keySet()) {
                    String predictedSeq = mapOfSequences.get(seqName);

                    PredSecondaryStructure prediction = predictor.predict(model, predictedSeq);

                    if (format.equals("html")) {
                        writeHTMLOutput(seqName, predictedSeq, prediction);
                    }
                    if (format.equals("txt")) {
                        writeTxtOutput(seqName, predictedSeq, prediction);
                    }
                }
            }
        }
//if (modelFilePath.startsWith("/Users/oykusuoglu/Desktop/GOR/gor_examples/gor3"))
        if (modelFilePath.contains("gor3")) {
            GORModel model = loadModel(modelFilePath, "gor3");
            if (alignmentFilePath != null) {
                GOR5Predict predictor = new GOR5Predict(model);
                for (String seqName : mapOfAlignments.keySet()) {
                    HashMap<String, List<String>> queryProtein = mapOfAlignments.get(seqName);
                    String seqAA = queryProtein.keySet().iterator().next();
                    String predictedSeq = queryProtein.get(seqAA).getFirst();

                    List<String> sequences = new ArrayList<>();
                    sequences.add(predictedSeq);
                    List<String> alignments = queryProtein.get("Alignments");

                    sequences.addAll(alignments);

                    PredSecondaryStructure prediction = predictor.predict(model,"gor3" ,sequences); //1111
                    if (format.equals("html")) {
                        writeHTMLOutput(seqName, predictedSeq, prediction);
                    }
                    if (format.equals("txt")) {
                        writeTxtOutput(seqName, predictedSeq, prediction);
                    }
                }
            } else {
                GOR3Predict predictor = new GOR3Predict(model);
                for (String seqName : mapOfSequences.keySet()) {
                    String predictedSeq = mapOfSequences.get(seqName);

                    PredSecondaryStructure prediction = predictor.predict(model, predictedSeq);

                    if (format.equals("html")) {
                        writeHTMLOutput(seqName, predictedSeq, prediction);
                    }
                    if (format.equals("txt")) {
                        writeTxtOutput(seqName, predictedSeq, prediction);
                    }
                }
            }
        }

        if (modelFilePath.contains("gor4")) {
            GORModel model = new GORModel("gor4");
            MatrixReaderGOR4 reader = new MatrixReaderGOR4();
            reader.readFile(modelFilePath);

            model.setCoil3D(reader.getCoil3D());
            model.setHelix3D(reader.getHelix3D());
            model.setSheet3D(reader.getSheet3D());
            model.setCoil5D(reader.getCoil5D());
            model.setHelix5D(reader.getHelix5D());
            model.setSheet5D(reader.getSheet5D());


            if (alignmentFilePath != null) {
                GOR5Predict predictor = new GOR5Predict(model);
                for (String seqName : mapOfAlignments.keySet()) {
                    HashMap<String, List<String>> queryProtein = mapOfAlignments.get(seqName);
                    String seqAA = queryProtein.keySet().iterator().next();
                    String predictedSeq = queryProtein.get(seqAA).getFirst();

                    List<String> sequences = new ArrayList<>();
                    sequences.add(predictedSeq);
                    List<String> alignments = queryProtein.get("Alignments");

                    sequences.addAll(alignments);

                    PredSecondaryStructure prediction = predictor.predict(model, "gor4", sequences); //1111

                    if (format.equals("html")) {
                        writeHTMLOutput(seqName, predictedSeq, prediction);
                    }
                    if (format.equals("txt")) {
                        writeTxtOutput(seqName, predictedSeq, prediction);
                    }
                }
            } else {
                GOR4Predict predictor = new GOR4Predict(model);
                for (String seqName : mapOfSequences.keySet()) {
                    String predictedSeq = mapOfSequences.get(seqName);

                    PredSecondaryStructure prediction = predictor.predict(model, predictedSeq);
                    if (format.equals("html")) {
                        writeHTMLOutput(seqName, predictedSeq, prediction);
                    }
                    if (format.equals("txt")) {
                        writeTxtOutput(seqName, predictedSeq, prediction);
                    }
                }
            }
        }
    }

    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();

        Option modelOpt = Option.builder("m")
                .longOpt("model")
                .hasArg(true)
                .desc("Path to the model file")
                .required()
                .build();
        options.addOption(modelOpt);

        Option formatOpt = Option.builder("f")
                .longOpt("format")
                .hasArg(true)
                .desc("Format of the input")
                .required()
                .build();
        options.addOption(formatOpt);

        Option seqOpt = Option.builder("s")
                .longOpt("seq")
                .hasArg(true)
                .desc("Path to the sequence file")
                .build();
        options.addOption(seqOpt);

        Option alnOpt = Option.builder("a")
                .longOpt("maf")
                .hasArg(true)
                .desc("Path to directory with aln. files")
                .build();
        options.addOption(alnOpt);

        Option fileOpt = Option.builder("h")
                .longOpt("file")
                .hasArg(true)
                .desc("generate a file")
                .build();
        options.addOption(fileOpt);

        Option postprcOpt = Option.builder("p")
                .longOpt("postprc")
                .hasArg(false)
                .desc("postprocess")
                .build();
        options.addOption(postprcOpt);

        Option windowOpt = Option.builder("w")
                .longOpt("window")
                .hasArg(true)
                .desc("window size")
                .build();
        options.addOption(windowOpt);

//

        OptionGroup optionGroupMAForFASTA = new OptionGroup();
        optionGroupMAForFASTA.setRequired(true);
        optionGroupMAForFASTA.addOption(seqOpt);
        optionGroupMAForFASTA.addOption(alnOpt);
        options.addOptionGroup(optionGroupMAForFASTA);


        return getCommandLine(args, options);
    }

    private static GORModel loadModel(String filePath, String modelType) {
        MatrixReader reader = new MatrixReader();
        reader.readFile(filePath);

        if(modelType.equals("gor1")) {
            GORModel model = new GORModel();

            int[][] matrixH = reader.getMatrixH();
            int[][] matrixE = reader.getMatrixE();
            int[][] matrixC = reader.getMatrixC();

            Config config = new Config();
            SSMatrix helixMatrix = new SSMatrix();
            SSMatrix sheetMatrix = new SSMatrix();
            SSMatrix coilMatrix = new SSMatrix();

            for (int row = 0; row < config.getROW_SIZE(); row++) {
                for (int col = 0; col < config.getWINDOW_SIZE(); col++) {
                    helixMatrix.setValue(row, col, matrixH[row][col]);
                    sheetMatrix.setValue(row, col, matrixE[row][col]);
                    coilMatrix.setValue(row, col, matrixC[row][col]);
                }
            }
            model.setHelix(helixMatrix);
            model.setSheet(sheetMatrix);
            model.setCoil(coilMatrix);

            return model;
        }
        else {
            GORModel trainedModel = new GORModel("gor3");

            Map<Character, int[][]> matrixHMap = reader.getMatrixHMap();
            Map<Character, int[][]> matrixEMap = reader.getMatrixEMap();
            Map<Character, int[][]> matrixCMap = reader.getMatrixCMap();

            Config config = new Config();

            SSMatrix[] helixMatrices = new SSMatrix[config.getROW_SIZE()];
            SSMatrix[] sheetMatrices = new SSMatrix[config.getROW_SIZE()];
            SSMatrix[] coilMatrices = new SSMatrix[config.getROW_SIZE()];


            for (int i = 0; i < config.getROW_SIZE(); i++) {
                char centralAA = config.mapAminoAcids(i); // Get amino acid from index

                int[][] matrixH = matrixHMap.getOrDefault(centralAA, new int[config.getROW_SIZE()][config.getWINDOW_SIZE()]);
                int[][] matrixE = matrixEMap.getOrDefault(centralAA, new int[config.getROW_SIZE()][config.getWINDOW_SIZE()]);
                int[][] matrixC = matrixCMap.getOrDefault(centralAA, new int[config.getROW_SIZE()][config.getWINDOW_SIZE()]);

                SSMatrix helixMatrix = new SSMatrix();
                SSMatrix sheetMatrix = new SSMatrix();
                SSMatrix coilMatrix = new SSMatrix();

                for (int row = 0; row < config.getROW_SIZE(); row++) {
                    for (int col = 0; col < config.getWINDOW_SIZE(); col++) {
                        helixMatrix.setValue(row, col, matrixH[row][col]);
                        sheetMatrix.setValue(row, col, matrixE[row][col]);
                        coilMatrix.setValue(row, col, matrixC[row][col]);
                    }
                }

                helixMatrices[i] = helixMatrix;
                sheetMatrices[i] = sheetMatrix;
                coilMatrices[i] = coilMatrix;

                trainedModel.setHelix3D(helixMatrices);
                trainedModel.setSheet3D(sheetMatrices);
                trainedModel.setCoil3D(coilMatrices);

            }
            return trainedModel;
        }

    }

    private static HashMap<String, String> seqFileToSeqMap(String filePath) {
        HashMap<String, String> seqFileToSeqList = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String seqName = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.startsWith(">")){
                    if (seqName != null) {
                        seqFileToSeqList.put(seqName, sb.toString());
                    }
                    seqName = line.substring(1);
                    sb = new StringBuilder();
                }
                else {
                    sb.append(line);
                }
            }
            if (seqName != null) { //very last seq
                seqFileToSeqList.put(seqName, sb.toString());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return seqFileToSeqList;
    }

    private static void writeHTMLOutput(String seqName, String predictedSeq, PredSecondaryStructure prediction) {
        System.out.println("<h2>" + seqName + "</h2>");
        System.out.println(formatAlignedOutput(predictedSeq, prediction.getPredictedSS(), 80) + "</p>");
        System.out.println("</body></html>");
    }

    public static String formatAlignedOutput(String as, String ps, int lineLength) {
        StringBuilder result = new StringBuilder();


        for (int i = 0; i < as.length(); i += lineLength) {

            String asChunk = as.substring(i, Math.min(i + lineLength, as.length()));
            String psChunk = ps.substring(i, Math.min(i + lineLength, ps.length()));

            while (psChunk.length() < asChunk.length()) {
                psChunk += " ";
            }


            StringBuilder coloredPS = new StringBuilder();
            for (char c : psChunk.toCharArray()) {
                String color = switch (c) {
                    case 'C' -> "orange";
                    case 'H' -> "blue";
                    case 'E' -> "red";
                    default -> "black";
                };
                coloredPS.append("<span style='color:").append(color).append(";'>").append(c).append("</span>");
            }

            String asLine = "<strong>AS:</strong> <span style='font-family: monospace; white-space: pre;'>" + asChunk + "</span><br>";
            String psLine = "<strong>PS:</strong> <span style='font-family: monospace; white-space: pre;'>" + coloredPS + "</span><br>";

            result.append(asLine).append(psLine).append("<br>");
        }

        result.append("<details>")
                .append("<summary>Details</summary>")
                .append("<p>Sequence Length: ").append(as.length()).append("</p>")
                .append("<p>Helix: ").append(Config.countSS('H', ps)).append(" is ").append(df.format(Config.getSSContent('H',ps))).append("%</p>")
                .append("<p>Sheet: ").append(Config.countSS('E', ps)).append(" is ").append(df.format(Config.getSSContent('E',ps))).append("%</p>")
                .append("<p>Coil: ").append(Config.countSS('C', ps)).append(" is ").append(df.format(Config.getSSContent('C',ps))).append("%</p>")
                .append("</details>");



        return result.toString();
    }




    public static String addLineBreaks(String input, int lineLength) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (i > 0 && i % lineLength == 0) {
                result.append("<br>");
            }
            result.append(input.charAt(i));
        }
        return result.toString();
    }

    private static void writeTxtOutput (String seqName, String predictedSeq, PredSecondaryStructure prediction){
        System.out.println(">" + seqName);
        System.out.println("AS " + predictedSeq);
        System.out.println("PS " + prediction.getPredictedSS());

    }
}
