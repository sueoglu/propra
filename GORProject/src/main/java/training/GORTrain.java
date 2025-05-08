package training;

import model.GORModel;
import utils.Config;
import utils.ProteinSeq;

import java.util.HashMap;
import java.util.List;

public abstract class GORTrain {
    protected List<ProteinSeq> trainSet;
    protected GORModel trainedModel;
    protected Config config;


    public GORTrain(List<ProteinSeq> trainSet) {
        this.config = new Config();
        this.trainSet = trainSet;

    }

    public abstract GORModel GORTrain();

    public void setWindowSize(int size) {
        config.setWINDOW_SIZE(size);
    }


}
