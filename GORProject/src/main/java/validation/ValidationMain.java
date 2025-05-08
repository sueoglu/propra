package validation;

import org.apache.commons.cli.*;
import utils.ProteinSeq;
import utils.SeclibReader;
import utils.Config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class ValidationMain {
    private static final DecimalFormat df = new DecimalFormat("#0.0", DecimalFormatSymbols.getInstance(Locale.US));
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        int halfWindowSize = config.getWINDOW_SIZE()/2;

        CommandLine cmd = parseArguments(args);
        String predFilePath = cmd.getOptionValue("p");
        String ssFile = cmd.getOptionValue("r");
        String outputFormat = cmd.getOptionValue("f");
        String summaryFilePath = cmd.getOptionValue("s");
        String detailedFilePath = cmd.getOptionValue("d");


        List<Double> q3List = new ArrayList<>();
        List<Double> q_hList = new ArrayList<>();
        List<Double> q_eList = new ArrayList<>();
        List<Double> q_cList = new ArrayList<>();

        List<Double> sov_List = new ArrayList<>();
        List<Double> sovH_List = new ArrayList<>();
        List<Double> sovE_List = new ArrayList<>();
        List<Double> sovC_List = new ArrayList<>();

        int countProtein = 0;
        int sumProteinLength = 0;
        int sumPredPos = 0;

        List<ProteinSeq> proteinSeqList = SeclibReader.readSeclibFile(ssFile);

        List<ProteinSeq> predictedProteinSeqList = SeclibReader.readSeclibFile(predFilePath);
        SOVCalculater sovCalc = new SOVCalculater();

        try(BufferedWriter detailedWriter = new BufferedWriter(new FileWriter(detailedFilePath))) {
            for (ProteinSeq proteinSeq : proteinSeqList) {
                String originalSSeq = proteinSeq.getSecondaryStructure();
                for (ProteinSeq predictedProteinSeq : predictedProteinSeqList) {
                    String predictedSSeq = predictedProteinSeq.getSecondaryStructure();
                    if (predictedProteinSeq.getSequence().equals(proteinSeq.getSequence())) {
                        String trimmedOriginalSSeq;
                        String trimmedPredictedSSeq;

                        if (originalSSeq.length() > 2 * halfWindowSize && predictedSSeq.length() > 2 * halfWindowSize) {
                            trimmedOriginalSSeq = originalSSeq.substring(halfWindowSize, originalSSeq.length() - halfWindowSize);
                            trimmedPredictedSSeq = predictedSSeq.substring(halfWindowSize, predictedSSeq.length() - halfWindowSize);
                        } else {
                            trimmedOriginalSSeq = originalSSeq;
                            trimmedPredictedSSeq = predictedSSeq;
                        }

                        double q3 = EvaluationScores.computeQ3(originalSSeq, predictedSSeq, "all");
                        double q_h = EvaluationScores.computeQ3(originalSSeq, predictedSSeq, "h");
                        double q_e = EvaluationScores.computeQ3(originalSSeq, predictedSSeq, "e");
                        double q_c = EvaluationScores.computeQ3(originalSSeq, predictedSSeq, "c");
                        sumPredPos += EvaluationScores.sumPredictedPos(predictedSSeq);

                        double sov = sovCalc.calculateSOVTotal(trimmedOriginalSSeq, trimmedPredictedSSeq);
                        double sovH = sovCalc.calculateSOVSingleSS(trimmedOriginalSSeq, trimmedPredictedSSeq, 'H');
                        double sovE = sovCalc.calculateSOVSingleSS(trimmedOriginalSSeq, trimmedPredictedSSeq, 'E');
                        double sovC = sovCalc.calculateSOVSingleSS(trimmedOriginalSSeq, trimmedPredictedSSeq, 'C');


                        q3List.add(q3);
                        q_hList.add(q_h);
                        q_eList.add(q_e);
                        q_cList.add(q_c);

                        sov_List.add(sov);
                        sovH_List.add(sovH);
                        sovE_List.add(sovE);
                        sovC_List.add(sovC);

                        if (outputFormat.equals("txt") || outputFormat.equals("html")) {
                            StringBuilder output = new StringBuilder();
                            output.append(String.format("\n> %s %s %s %s %s %s %s %s %s\n",
                                    proteinSeq.getId(),
                                    formatDouble(q3), formatDouble(sov), formatDouble(q_h),
                                    formatDouble(q_e), formatDouble(q_c), formatDouble(sovH),
                                    formatDouble(sovE), formatDouble(sovC)));

                            output.append("AS ").append(proteinSeq.getSequence()).append("\n");
                            output.append("PS ").append(predictedSSeq).append("\n");
                            output.append("SS ").append(proteinSeq.getSecondaryStructure()).append("\n");

                            detailedWriter.write(output.toString());
                            detailedWriter.newLine();
                        }

                        if(outputFormat.equals("html")) {
                            /*System.out.println("\n" + ">" + proteinSeq.getId() + "\t" +
                                    (Double.isNaN(q3) ? "-" : q3) + "\t" +
                                    "0" + "\t" +
                                    (Double.isNaN(q_h) ? "-" : q_h) + "\t" +
                                    (Double.isNaN(q_e) ? "-" : q_e) + "\t" +
                                    (Double.isNaN(q_c) ? "-" : q_c) + "\t" +
                                    "0" + "\t" +
                                    "0" + "\t" +
                                    "0" +"\n" +
                                    "AS" + "\t" + proteinSeq.getSequence() + "\n" +
                                    "PS" + "\t" + predictedSSeq +"\n" +
                                    "SS" + "\t" + proteinSeq.getSecondaryStructure() + "\n");*/
                        }
                    }
                }
                sumProteinLength += originalSSeq.length();
                countProtein++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter sumWriter = new BufferedWriter(new FileWriter(summaryFilePath))){

// Q3
            double mean_q3 = EvaluationScores.mean(q3List);
            double mean_q_h = EvaluationScores.mean(q_hList);
            double mean_q_e = EvaluationScores.mean(q_eList);
            double mean_q_c = EvaluationScores.mean(q_cList);
            double meanProtLength = Math.round((double) sumProteinLength / countProtein);
            double std_q3 = EvaluationScores.sd(q3List, mean_q3);
            double std_q_h = EvaluationScores.sd(q_hList, mean_q_h);
            double std_q_e = EvaluationScores.sd(q_eList, mean_q_e);
            double std_q_c = EvaluationScores.sd(q_cList, mean_q_c);

// SOV
            double mean_sov = EvaluationScores.mean(sov_List);
            double meanH_sov = EvaluationScores.mean(sovH_List);
            double meanE_sov = EvaluationScores.mean(sovE_List);
            double meanC_sov = EvaluationScores.mean(sovC_List);
            double std_sov = EvaluationScores.sd(sov_List, mean_sov);
            double std_sovH = EvaluationScores.sd(sovH_List, meanH_sov);
            double std_sovE = EvaluationScores.sd(sovE_List, meanE_sov);
            double std_sovC = EvaluationScores.sd(sovC_List, meanC_sov);


            String header = "Statistic for protein validation\n";
            String numOfProtein = "Number of Proteins:\t" + countProtein + "";
            String meanProtein = "Mean Protein Length:\t" + meanProtLength + "";
            String sumLength = "Sum of Protein Length:\t" + sumProteinLength + "";
            String sumPrPos = "Sum of Predicted Positions:\t" + sumPredPos + "\n";

            String q3Line = "q3 :\t Mean:\t" + df.format(mean_q3) + "\tDev:\t"+ df.format(std_q3) +"\n";
            String q_hLine = "q0bs_H:\t Mean:\t" + df.format(mean_q_h) + "\tDev:\t"+ df.format(std_q_h) + "\n";
            String q_eLine = "q0bs_E:\t Mean:\t" + df.format(mean_q_e) + "\tDev:\t"+ df.format(std_q_e) + "\n";
            String q_cLine = "q0bs_C:\t Mean:\t" + df.format(mean_q_c) + "\tDev:\t"+ df.format(std_q_c) + "\n";

            String sovLine = "SOV :\t Mean:\t" + df.format(mean_sov) + "\tDev:\t"+ df.format(std_sov) +"\n";
            String sovHLine = "SOV_H :\t Mean:\t" + df.format(meanH_sov) + "\tDev:\t"+ df.format(std_sovH) +"\n";
            String sovELine = "SOV_E :\t Mean:\t" + df.format(meanE_sov) + "\tDev:\t"+ df.format(std_sovE) +"\n";
            String sovCLine = "SOV_C :\t Mean:\t" + df.format(meanC_sov) + "\tDev:\t"+ df.format(std_sovC) +"\n";

            StringBuilder sb = new StringBuilder();
            sb.append(header).append("\n")
                    .append(numOfProtein).append("\n")
                    .append(meanProtein).append("\n")
                    .append(sumLength).append("\n")
                    .append(sumPrPos).append("\n")
                    .append(q3Line)
                    .append(q_hLine)
                    .append(q_eLine)
                    .append(q_cLine).append("\n")
                    .append(sovLine)
                    .append(sovHLine)
                    .append(sovELine)
                    .append(sovCLine);

            String result = sb.toString();

            sumWriter.write(result);

            if (outputFormat.equals("html")) {
                StringBuilder sb2 = new StringBuilder();

                sb2.append("<title>Protein Validation Summary</title>")
                        .append("<style>")
                        .append("body { font-family: Arial, sans-serif; margin: 20px; padding: 20px; }")
                        .append("h2 { color: #333; }")
                        .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
                        .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                        .append("th { background-color: #f2f2f2; }")
                        .append("</style>")
                        .append("</head>")
                        .append("<body>")
                        .append("<h2>Statistic for Protein Validation</h2>")
                        .append("<p><strong>Number of Proteins:</strong> ").append(countProtein).append("</p>")
                        .append("<p><strong>Mean Protein Length:</strong> ").append(meanProtLength).append("</p>")
                        .append("<p><strong>Sum of Protein Length:</strong> ").append(sumProteinLength).append("</p>")
                        .append("<p><strong>Sum of Predicted Positions:</strong> ").append(sumPredPos).append("</p>")
                        .append("<br>")
                        .append("<table border='1'>")
                        .append("<tr><th>Score</th><th>Mean</th><th>Standard Deviation</th></tr>")
                        .append("<tr><td>Q3</td><td>").append(df.format(mean_q3)).append("</td><td>").append(df.format(std_q3)).append("</td></tr>")
                        .append("<tr><td>q0bs_H</td><td>").append(df.format(mean_q_h)).append("</td><td>").append(df.format(std_q_h)).append("</td></tr>")
                        .append("<tr><td>q0bs_E</td><td>").append(df.format(mean_q_e)).append("</td><td>").append(df.format(std_q_e)).append("</td></tr>")
                        .append("<tr><td>q0bs_C</td><td>").append(df.format(mean_q_c)).append("</td><td>").append(df.format(std_q_c)).append("</td></tr>")
                        .append("<tr><td>SOV</td><td>").append(df.format(mean_sov)).append("</td><td>").append(df.format(std_sov)).append("</td></tr>")
                        .append("<tr><td>SOV_H</td><td>").append(df.format(meanH_sov)).append("</td><td>").append(df.format(std_sovH)).append("</td></tr>")
                        .append("<tr><td>SOV_E</td><td>").append(df.format(meanE_sov)).append("</td><td>").append(df.format(std_sovE)).append("</td></tr>")
                        .append("<tr><td>SOV_C</td><td>").append(df.format(meanC_sov)).append("</td><td>").append(df.format(std_sovC)).append("</td></tr>")
                        .append("</table>");


                String html_output = sb2.toString();
                System.out.println(html_output);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();

        Option pOpt = Option.builder("p")
                .hasArg(true)
                .desc("prediction file")
                .required()
                .build();

        Option rOpt = Option.builder("r")
                .hasArg(true)
                .desc("secondary structure file")
                .required()
                .build();

        Option fOpt = Option.builder("f")
                .hasArg(true)
                .desc("output format")
                .required()
                .build();

        Option sOpt = Option.builder("s")
                .hasArg(true)
                .desc("file to write summary outputs to")
                .required()
                .build();

        Option dOpt = Option.builder("d")
                .hasArg(true)
                .desc("file to write detailed information to")
                .required()
                .build();

        options.addOption(pOpt);
        options.addOption(rOpt);
        options.addOption(fOpt);
        options.addOption(sOpt);
        options.addOption(dOpt);

        CommandLineParser parser = new DefaultParser();

        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            new HelpFormatter().printHelp("program", options);
            System.exit(1);
        }
        return null;
    }
    private static String formatDouble(Double value) {
        return Double.isNaN(value) ? "-" : df.format(value);
    }

}