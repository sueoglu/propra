package training;

import model.GORModel;
import utils.ProteinSeq;

import java.util.List;

public class GOR1 extends GORTrain {
    public GOR1(List<ProteinSeq> trainSet) {
        super(trainSet);
    }
    @Override
    public GORModel GORTrain() {
        trainedModel = new GORModel();
        int windowSize = config.getWINDOW_SIZE();

        // Step 1: Iterate over the training set
        for (ProteinSeq seq : trainSet) {
            String aaSeq = seq.getSequence();
            String ss = seq.getSecondaryStructure();
            int windowSizeHalf = windowSize/2;

            // Step 2: Iterate over pos i+8 to i-8 in seq mit seq length = i
            for (int posInSeq = windowSizeHalf; posInSeq < aaSeq.length() - windowSizeHalf; posInSeq++) {

                // Step 3: check middle pos of window for current secondary structure ( = i + 7)
                char curSS = ss.charAt(posInSeq);

                // Step 4: PROCESS WINDOW
                // update aa counts in SSMatrix of the Model (SS = SS at pos 0) for all aa in window
                int posInWindow = 0;
                for (int windowPos = posInSeq - windowSizeHalf; windowPos <= posInSeq + windowSizeHalf; windowPos ++) {
                    char curAA = aaSeq.charAt(windowPos);

                    trainedModel.updateSSMatrix(posInWindow, curAA, curSS);

                    posInWindow++;
                }
            }
        }
        return trainedModel;
    }

}


