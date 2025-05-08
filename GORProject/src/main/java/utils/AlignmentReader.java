package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlignmentReader {
    private Map<String, HashMap<String, List<String>>> alignments;

    public AlignmentReader() {
        this.alignments = new HashMap<>();
    }

    public void readAlignmentFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentSequenceName = null;
            HashMap<String, List<String>> sequenceData = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith(">")) {
                    currentSequenceName = line.substring(1).trim();
                    sequenceData = new HashMap<>();
                    sequenceData.put("AS", new ArrayList<>());
                    sequenceData.put("Alignments", new ArrayList<>());
                    alignments.put(currentSequenceName, sequenceData);
                }

                else if (line.startsWith("AS") && currentSequenceName != null) {
                    sequenceData.get("AS").add(line.substring(3).trim());
                }
                // Store alignment sequences (lines starting with a number)
                else if (line.matches("^\\d+\\s.*") && currentSequenceName != null) { //d+ number s space .* any
                    String alignment = line.replaceFirst("^\\d+\\s+", "").trim();
                    sequenceData.get("Alignments").add(alignment);
                }

            }

        } catch (IOException e) {
            e.getLocalizedMessage();
        }
    }

    public Map<String, HashMap<String, List<String>>> getAlignments() {
        return alignments;
    }

    public void printAlignments() {
        for (Map.Entry<String, HashMap<String, List<String>>> entry : alignments.entrySet()) {
            System.out.println("Sequence Name: " + entry.getKey());
            System.out.println("AS: " + String.join("", entry.getValue().get("AS")));
            System.out.println("Alignments:");
            for (String alignment : entry.getValue().get("Alignments")) {
                System.out.println(alignment);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        AlignmentReader reader = new AlignmentReader();
        reader.readAlignmentFile("/Users/oykusuoglu/Desktop/GOR/CB513/CB513MultipleAlignments/1acx.aln"); // probe
    }
}

