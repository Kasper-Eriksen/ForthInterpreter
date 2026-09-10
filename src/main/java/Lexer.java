import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public static List<String> lexLine(String programLine){
        List<String> out = new ArrayList<>();
        StringBuilder token = new StringBuilder();

        boolean inString = false;
        for (int i = 0; i < programLine.length(); i++) {
            char charInt = programLine.charAt(i);
            switch (charInt){
                case '"' -> {
                    inString = !inString;
                    token.append(charInt);
                    out.add(token.toString());
                    token = new StringBuilder();
                    i+=1;
                }
                case ' ' -> {
                    if (inString){
                        token.append(charInt);
                    }else{
                        if (!token.isEmpty()){
                            out.add(token.toString());
                            token = new StringBuilder();
                        }
                    }
                }
                case '\\' -> {
                    if (inString){
                        token.append(charInt);
                    }else{
                        i = programLine.length();
                    }

                }
                default -> token.append(charInt);
            }
        }
        if (!token.isEmpty()){
            out.add(token.toString());
        }

        return out;
    }
    public static List<String> lex(String program){
        List<String> out = new ArrayList<>();
        String[] programLines = program.split("\\R");
        for (String programLine : programLines){
            out.addAll(lexLine(programLine));
        }

        return out;
    }
}

