package validation;

import java.util.List;

public class EvaluationScores {
    public static double computeQ3(String originalSS, String predictedSS, String type){
        char[] originalSSList = originalSS.toCharArray();
        char[] predictedSSList = predictedSS.toCharArray();
        int A_hh = 0;
        int A_ee = 0;
        int A_cc = 0;

        int b_h = 0;
        int b_e = 0;
        int b_c = 0;

        for (int i = 0; i < originalSSList.length; i++) {

            if (originalSSList[i] == '-' || predictedSSList[i] == '-') continue;

            if(originalSSList[i] == 'H'){
                b_h += 1;
            }
            if(originalSSList[i] == 'E'){
                b_e += 1;
            }
            if(originalSSList[i] == 'C'){
                b_c += 1;
            }

            if (originalSSList[i] == predictedSSList[i]) {
                if (originalSSList[i] == 'H') {
                    A_hh += 1;
                }
                if (originalSSList[i] == 'E') {
                    A_ee += 1;
                }
                if (originalSSList[i] == 'C') {
                    A_cc += 1;
                }
            }
        }

        int b = b_h + b_e + b_c;

        double Q_h, Q_e, Q_c, Q3;

        if (b_h > 0) {
            Q_h = ((double) A_hh / b_h) * 100;
        } else {
            Q_h = 0;
        }

        if (b_e > 0) {
            Q_e = ((double) A_ee / b_e) * 100;
        } else {
            Q_e = 0;
        }

        if (b_c > 0) {
            Q_c = ((double) A_cc / b_c) * 100;
        } else {
            Q_c = 0;
        }

        if (b > 0) {
            Q3 = ((double) (A_hh + A_ee + A_cc) / b) * 100;
        } else {
            Q3 = 0;
        }

        if (type.equals("h")) {
            if (Double.isNaN(Q_h)) {
                return 0;
            } else {
                return Q_h ;
            }
        }

        if (type.equals("e")) {
            if (Double.isNaN(Q_e)) {
                return 0;
            } else {
                return Q_e;
            }
        }

        if (type.equals("c")) {
            if (Double.isNaN(Q_c)) {
                return 0;
            } else {
                return Q_c;
            }
        }

        if (Double.isNaN(Q3)) {
            return 0;
        } else {
            return Q3;
        }
    }

    public static double mean(List<Double> scores) {
        double sum = 0;
        for (int i = 0; i < scores.size(); i++) {
            Double score = scores.get(i);

            if (score != null && !score.isNaN()) {
                sum += score;
            }
        }
        return sum / scores.size();
    }

    public static int countLength (String originalSS){
        char[] originalSSList = originalSS.toCharArray();
        int count = 0;
        for (int i = 0; i < originalSSList.length; i++) {
            count += 1;
        }
        return count;
    }

    public static double sd (List<Double> scores, double mean){
        if(scores.isEmpty()){
            return 0.0;
        }
        double sum = 0;
        for (Double score : scores) {
            if (score != null && !score.isNaN()) {
                sum += Math.pow(score - mean, 2);
            }
        }
        return Math.sqrt(sum/scores.size());
    }

    public static int sumPredictedPos(String predictedSS){
        char[] predictedSSList = predictedSS.toCharArray();
        int count = 0;
        for (int i = 0; i < predictedSSList.length; i++) {
            if (predictedSSList[i] == '-') {
                continue;
            }
            count += 1;
        }
        return count;
    }

}
