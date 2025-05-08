package validation;

import org.apache.commons.cli.*;
import utils.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CVMain {
    public static void main(String[] args) throws IOException {
        // Parser ------------------------------------------------------------------------------------------------------
        CommandLine cmd = parseArguments(args);

        String seclibPath = cmd.getOptionValue("seclib");
        String gorVersion = cmd.getOptionValue("gor");
        String output = cmd.getOptionValue("output");
        String mafPath = cmd.getOptionValue("maf");

        boolean postpr = cmd.hasOption("postpr");

        if (cmd.hasOption("window")) {
            Config.setWINDOW_SIZE(Integer.parseInt(cmd.getOptionValue("window")));
        }

        if (cmd.hasOption("pseudoc")) {
            Config.setPseudoCount(Double.parseDouble(cmd.getOptionValue("pseudoc")));
        }

        boolean isGOR5 = cmd.hasOption("maf");
        int folds = cmd.hasOption("fold") ? Integer.parseInt(cmd.getOptionValue("fold")) : 5;

        // Cross Validation --------------------------------------------------------------------------------------------
        List<List<ProteinSeq>> resultPredictions;
        CrossValidation cv = new CrossValidation(folds, true, gorVersion);
        List<ProteinSeq> dataset = SeclibReader.readSeclibFile(seclibPath);

        cv.setPostpr(postpr);

        if (isGOR5) {
            Map<String, HashMap<String, List<String>>> mapOfAlignments = getMapOfAlignment(mafPath);
            resultPredictions = cv.runCV(dataset);

        } else {
            resultPredictions = cv.runCV(dataset);
        }


        // Output ------------------------------------------------------------------------------------------------------
        int foldIndex = 1;

        if (!output.equals("html")) {
            for (List<ProteinSeq> fold : resultPredictions) {
                String filePath = output + "/fold_" + foldIndex + ".prd";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    for (ProteinSeq seq : fold) {
                        String seqName = seq.getId();
                        String predictedSeq = seq.getSequence();
                        PredSecondaryStructure prediction = seq.getPredictedSS();
                        String finalPrediction = prediction.getPredictedSS();

                        if (postpr) {
                            finalPrediction = prediction.getPostprocessedPrediction();
                        }

                        writer.write("> " + seqName + "\n");
                        writer.write("AS " + predictedSeq + "\n");
                        writer.write("PS " + finalPrediction+ "\n");
                    }
                } catch (IOException e) {
                    System.err.println("Fehler beim Schreiben der Datei: " + filePath);
                    e.printStackTrace();
                }

                foldIndex++;
            }
        }
    }

    // Functions -------------------------------------------------------------------------------------------------------
    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();

        Option gorOpt = Option.builder("g")
                .longOpt("gor")
                .hasArg(true)
                .desc("GOR version: gor1, gor3, gor4")
                .required()
                .build();
        options.addOption(gorOpt);

        Option seclibOpt = Option.builder("s")
                .longOpt("seclib")
                .hasArg(true)
                .desc("seclib file for test and train")
                .required()
                .build();
        options.addOption(seclibOpt);

        Option outputOpt = Option.builder("o")
                .longOpt("output")
                .hasArg(true)
                .desc("output folder path or html")
                .required()
                .build();
        options.addOption(outputOpt);

        Option aliOpt = Option.builder("m")
                .longOpt("maf")
                .hasArg(true)
                .desc("multiple alignment folder path")
                .build();
        options.addOption(aliOpt);

        Option foldOpt = Option.builder("f")
                .longOpt("fold")
                .hasArg(true)
                .desc("number of folds")
                .build();
        options.addOption(foldOpt);

        Option postprOpt = Option.builder("p")
                .longOpt("postpr")
                .hasArg(false)
                .desc("postprocessing?")
                .build();
        options.addOption(postprOpt);

        Option windowOpt = Option.builder("w")
                .longOpt("window")
                .hasArg(true)
                .desc("window size")
                .build();
        options.addOption(windowOpt);

        Option countOpt = Option.builder("c")
                .longOpt("pseudoc")
                .hasArg(true)
                .desc("pseudo count")
                .build();
        options.addOption(countOpt);

        return getCommandLine(args, options);
    }

    public static CommandLine getCommandLine(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

        } catch (ParseException e) {
            System.err.println("Error parsing command-line arguments: " + e.getMessage());
            formatter.printHelp("GORPredictMain", options);
            System.exit(1);
        }
        return cmd;
    }

    public static Map<String, HashMap<String, List<String>>> getMapOfAlignment(String alignmentFilePath) {
        Map<String, HashMap<String, List<String>>> mapOfAlignments = new HashMap<>();
        AlignmentReader alignmentReader = new AlignmentReader();

        if (alignmentFilePath != null) {
            File alignmentDirectory = new File(alignmentFilePath);

            if (alignmentDirectory.isDirectory()) {
                for (File file : alignmentDirectory.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".aln")) {
                        alignmentReader.readAlignmentFile(file.getAbsolutePath());
                    }
                }
            } else {
                alignmentReader.readAlignmentFile(alignmentFilePath);
            }
        }

        return alignmentReader.getAlignments();
    }
}
