/*

package validation;


import utils.FASTAReader;
import utils.ProteinSeq;
import utils.SeclibReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TestSetGenerateMain {
    public static void main(String[] args) throws IOException {
        Map<String, String> trainingSet = FASTAReader.readFASTA("/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/utils/testSetFiles/CB513.fasta");
        List<ProteinSeq> seqData = SeclibReader.readSeclibFile("/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/utils/testSetFiles/test1000biopraktA.seclib");
        Map<String, String> testSequences = new HashMap<>();
        for (ProteinSeq proteinSeq : seqData) {
            testSequences.put(proteinSeq.getId(), proteinSeq.getSequence());
        }

        TestSet testSet = TestSet.generateTestSet(testSequences, trainingSet, 0.6);
        System.out.println("selected");

        writeToFile("1000test_set", testSet.getTestSeq());
        System.out.println("written to file");

        writeRef("1000test_ref", seqData, testSet.getTestSeq().keySet());
        System.out.println("ref written");



    }
    public static void writeToFile(String output, Map<String, String> sequences) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            for (Map.Entry<String, String> entry : sequences.entrySet()) {
                writer.write(">" + entry.getKey());
                writer.newLine();
                writer.write(entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRef (String output, List<ProteinSeq> proteinSeq, Set<String> testSet) {
        List<ProteinSeq> matchedSequences = new ArrayList<>();
        for (ProteinSeq seq : proteinSeq) {
            if(testSet.contains(seq.getId())) {
                matchedSequences.add(seq);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
            for (ProteinSeq seq : matchedSequences) {
                writer.write(">" + seq.getId());
                writer.newLine();
                writer.write("AS " + seq.getSequence());
                writer.newLine();
                writer.write("SS " + seq.getSecondaryStructure());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

*/
