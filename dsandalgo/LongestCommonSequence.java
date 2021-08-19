import java.util.Arrays;

public class LongestCommonSequence {

    public static final String FIRST_STRING = "++++a-----b++c+++d ";
    public static final String SECOND_STRING =  "12a56b78c90d";
    //public static final String FIRST_STRING = "++++a-----b++c+++d ef+++++  gh-----ii";
    //public static final String SECOND_STRING =  "12a56b78c90d ef3gh 44444 ii0000";

    public static void main(String[] args) {
        String lcs = getLCS(0, 0);
        System.out.println("LCS is : " + lcs);
    }

    static String getLCS(int firstStringSequence, int secondStringSequence) {
        if (firstStringSequence >= FIRST_STRING.length() || secondStringSequence >= SECOND_STRING.length()) {
            return "";
        }
        String stringSequence = null;
        int counter = 0;
        for(int index=secondStringSequence; index<SECOND_STRING.length(); index++) {
            ++counter;
            if (SECOND_STRING.charAt(index) == FIRST_STRING.charAt(firstStringSequence)) {
                stringSequence = FIRST_STRING.charAt(firstStringSequence) + getLCS(++firstStringSequence, secondStringSequence + counter);
            }
        }

        if (stringSequence ==  null) {
            stringSequence = getLCS(++firstStringSequence, secondStringSequence);
        } else {
            return stringSequence;
        }
        return stringSequence;
    }
}
