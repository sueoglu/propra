package utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FASTAReader {

    public static Map<String, String> readFASTA(String filePath){
        Map<String, String> proteinData = new HashMap<>();
        StringBuilder sequence = new StringBuilder();
        String proteinId = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // Skip empty lines

                if (line.startsWith(">")) {  // Header line
                    if (!proteinId.isEmpty()) {
                        proteinData.put(proteinId, sequence.toString());
                    }
                    proteinId = line.substring(1).trim();
                    sequence.setLength(0);  // Reset sequence buffer
                } else {
                    sequence.append(line);
                }
            }
            // Add the last protein sequence after reading the file
            if (!proteinId.isEmpty()) {
                proteinData.put(proteinId, sequence.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return proteinData;
    }
}
