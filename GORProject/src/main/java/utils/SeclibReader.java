package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SeclibReader {
    public static List<ProteinSeq> readSeclibFile(String filePath) throws IOException {
        List<ProteinSeq> sequences = new ArrayList<>();
        StringBuilder sequence = new StringBuilder();
        StringBuilder secondaryStructure = new StringBuilder();
        String id = "";
        boolean inSequence = false;
        boolean inSecondaryStructure = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;  // Skip empty lines

                // Start of a new protein entry
                if (line.startsWith(">")) {

                    // Add previous protein if exists
                    if (!sequence.isEmpty() && !secondaryStructure.isEmpty()) {
                        sequences.add(new ProteinSeq(id, sequence.toString(), secondaryStructure.toString()));
                    }

                    // Reset for new protein
                    id = line.substring(1).trim();
                    sequence.setLength(0);
                    secondaryStructure.setLength(0);
                    inSequence = false;
                    inSecondaryStructure = false;
                }

                // Protein sequence line
                else if (line.startsWith("AS ")) {
                    sequence.append(line.substring(3).trim());  // Start of a new sequence
                    inSequence = true;
                    inSecondaryStructure = false;
                }

                // Secondary structure line
                else if (line.startsWith("PS ") || line.startsWith("SS ")) {
                    secondaryStructure.append(line.substring(3).trim());  // Start of a new secondary structure
                    inSecondaryStructure = true;
                    inSequence = false;
                }

                else {
                    // Continuation of sequence or secondary structure
                    if (inSequence) {
                        sequence.append(line.trim());
                    } else if (inSecondaryStructure) {
                        secondaryStructure.append(line.trim());
                    }
                }
            }

            // Add the last protein sequence after reading the file
            if (!sequence.isEmpty() && !secondaryStructure.isEmpty()) {
                sequences.add(new ProteinSeq(id, sequence.toString(), secondaryStructure.toString()));

            }
        }
        return sequences;
    }
}

