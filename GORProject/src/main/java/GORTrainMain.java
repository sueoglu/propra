import model.GORModel;
import training.GOR1;
import java.io.*;
import training.GOR3;
import training.GOR4;
import utils.*;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.*;

public class GORTrainMain {
    public static void main(String[] args) throws IOException {
        CommandLine cmd = parseArguments(args);
        String trainingFile = cmd.getOptionValue("db");
        String method = cmd.getOptionValue("method");
        String modelFile = cmd.getOptionValue("model");

        // 2. read seclib File
        List<ProteinSeq> trainingSequences = SeclibReader.readSeclibFile(trainingFile);


        // 3. call gor train (1-3)
        GORModel model = new GORModel();

        if (method.equals("gor1")) {
            GOR1 gor1 = new GOR1(trainingSequences);
            model = gor1.GORTrain();

        } else if (method.equals("gor3")) {
            GOR3 gor3 = new GOR3(trainingSequences);
            model = gor3.GORTrain();
        } else if (method.equals("gor4")) {
            GOR4 gor4 = new GOR4(trainingSequences);
            model = gor4.GORTrain();
        }


        // 4. Write model to the file specified by --model
        if (modelFile != null) {
            try {
                PrintStream fileOut = new PrintStream(new File(modelFile));
                System.setOut(fileOut);

                model.printModel();

                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

            } catch (FileNotFoundException e) {
                System.err.println("Fehler beim Schreiben in die Datei: " + e.getMessage());
            }
        } else {
            model.printModel();
        }
    }

    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();

        Option dbOpt = Option.builder("d")
                .longOpt("db")
                .hasArg(true)
                .desc("Training file")
                .required()
                .build();

        Option methodOpt = Option.builder("mtd")
                .longOpt("method")
                .hasArg(true)
                .desc("Method")
                .required()
                .build();

        Option modelOpt = Option.builder("m")
                .longOpt("model")
                .hasArg(true)
                .desc("Path to the model file")
                .build();

        options.addOption(dbOpt);
        options.addOption(methodOpt);
        options.addOption(modelOpt);

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

}
