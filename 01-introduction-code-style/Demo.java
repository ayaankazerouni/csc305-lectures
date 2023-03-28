public class Demo {
    public static String delimit(String delimiter, String[] str) {
        String result = "";
        int i;
        for (i = 0; i < str.length; i++) {
            if (i != 0) {
                result += delimiter;
            }

            result += str[i];
        }

        return result;
    }
}
