package validation;

public class SOVCalcTestMain {
    public static void main(String[] args) {
        SOVCalculater calculater = new SOVCalculater();
        double SOV = calculater.calculateSOVTotal("CCCCCCCCEEEEEEECCCCEEEEEEECEECCEECCCCCCCCEEECCCCCCCEEEEECCCEEEEECCCCCEEEEEECCCCCCEEEEECCCCCC",
                "CCECCCCCEEEEEHHCEEEHEEHECHEECEEEECHEECHEEEEECECECCEEEEHHCEEECEEECEEEEEEEEEECEHEEEECHCHECHCEC");

        double SOV2 = calculater.calculateSOVTotal("HHHHHHHHHHHCCCCEEEEECCHHHHHHHHHHHHCCCEEEEEEEEECCEEEEEEEECE",
                                                   "HHHHHHHHHHCCCCEEEEECCHHHHHHHHHHHHCCCEEEEEEEEEECEEEEEEEECE");

       System.out.println(SOV);
        System.out.println(SOV2);
    }
}
