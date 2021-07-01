public class Utils {
    static String code_cleaner(String s) {
        String newString = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '/')
                break;
            else
                newString += s.charAt(i);
        }

        return newString.strip();
    }
}
