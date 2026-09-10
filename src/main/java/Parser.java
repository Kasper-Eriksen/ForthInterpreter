import java.util.ArrayList;
import java.util.List;

public class Parser {

    static int subRoutineOffset = 0;
    static boolean containsElse = false;
    static List<String> initializedFunctionNames = new ArrayList<>();
    static List<String> initializedConstantNames = new ArrayList<>();
    static List<String> initializedVariableNames = new ArrayList<>();
    enum CONTEXT{
        NORMAL,
        IN_WHILE,
        IN_DO,
        IN_IF,
        IN_FUNCTION
    }
    static CONTEXT context = CONTEXT.NORMAL;

    public static class SyntaxErrorException extends Exception{
        public SyntaxErrorException(){
            super("Syntax error");
        }
    }
    public static class UnkownWordException extends Exception{
        public UnkownWordException(String word){
            super(word + "?");
        }
    }
    public static class NotInFunctionException extends Exception{
        public NotInFunctionException(){
            super("action is not a function");
        }
    }

    public static List<Word> parse(List<String> tokens) throws UnkownWordException, SyntaxErrorException, NotInFunctionException {
        List<Word> words = new ArrayList<>();


        for (int i = 0; i < tokens.size(); i++){
            switch (tokens.get(i)){
                case "+" -> words.add(new Add());
                case "-" -> words.add(new Subtract());
                case "*" -> words.add(new Multiply());
                case "/" -> words.add(new Divide());
                case "mod" -> words.add(new Modulus());

                case "dup" -> words.add(new Duplication());
                case "drop" -> words.add(new Drop());
                case "swap" -> words.add(new Swap());
                case "over" -> words.add(new Over());
                case "rot" -> words.add(new Rotate());

                case "<" -> words.add(new LessThan());
                case ">" -> words.add(new GreaterThan());
                case "=" -> words.add(new Equal());
                case "and" -> words.add(new And());
                case "or" -> words.add(new Or());
                case "invert" -> words.add(new Not());

                case "." -> words.add(new Dot());
                case "emit" -> words.add(new Emit());
                case "cr" -> words.add(new CarriageReturn());
                case ".\"" -> {
                    i++;
                    words.add(new forthString(tokens.get(i).substring(0, tokens.get(i).length() - 1)));
                }

                case "if" -> {
                    if (context == CONTEXT.NORMAL){
                        throw new NotInFunctionException();
                    }
                    CONTEXT previousContext = context;
                    context = CONTEXT.IN_IF;
                    List<Word> ifBody = parse(tokens.subList(i + 1, tokens.size()));
                    i += subRoutineOffset;
                    List<Word> elseBody = new ArrayList<>();
                    context = previousContext;
                    if (containsElse){
                        elseBody = parse(tokens.subList(i + 1, tokens.size()));
                        i += subRoutineOffset;
                    }
                    words.add(new IfElseThen(ifBody, elseBody));
                }
                case "else" -> {
                    subRoutineOffset = i + 1;
                    containsElse = true;
                    if (context != CONTEXT.IN_IF){
                        throw new SyntaxErrorException();
                    }
                    return words;
                }
                case "then" -> {
                    subRoutineOffset = i + 1;
                    containsElse = false;
                    if (context != CONTEXT.IN_IF){
                        throw new SyntaxErrorException();
                    }
                    return words;
                }

                case ":" -> {
                    i++;
                    String name = tokens.get(i);
                    initializedFunctionNames.add(name);

                    CONTEXT previousContext = context;
                    context = CONTEXT.IN_FUNCTION;
                    List<Word> body = parse(tokens.subList(i + 1, tokens.size()));
                    context = previousContext;

                    i += subRoutineOffset;
                    words.add(new Function(name, body));
                }
                case ";" -> {
                    subRoutineOffset = i + 1;
                    if (context != CONTEXT.IN_FUNCTION){
                        throw new SyntaxErrorException();
                    }
                    return words;
                }
                case "loop" -> {
                    subRoutineOffset = i + 1;
                    if (context != CONTEXT.IN_DO){
                        throw new SyntaxErrorException();
                    }
                    return words;
                }
                case "repeat" -> {
                    subRoutineOffset = i + 1;
                    if (context != CONTEXT.IN_WHILE){
                        throw new SyntaxErrorException();
                    }
                    return words;
                }

                case "do" -> {
                    if (context == CONTEXT.NORMAL){
                        throw new NotInFunctionException();
                    }

                    CONTEXT previousContext = context;
                    context = CONTEXT.IN_DO;
                    List<Word> body = parse(tokens.subList(i + 1, tokens.size()));
                    context = previousContext;

                    i += subRoutineOffset;
                    words.add(new DoLoop(body));
                }
                case "?do" -> {
                    if (context == CONTEXT.NORMAL){
                        throw new NotInFunctionException();
                    }

                    CONTEXT previousContext = context;
                    context = CONTEXT.IN_DO;
                    List<Word> body = parse(tokens.subList(i + 1, tokens.size()));
                    context = previousContext;

                    i += subRoutineOffset;
                    words.add(new RunOnceDoLoop(body));
                }
                case "i" -> words.add(new Index());
                case "while" -> {
                    if (context == CONTEXT.NORMAL){
                        throw new NotInFunctionException();
                    }
                    CONTEXT previousContext = context;

                    context = CONTEXT.IN_WHILE;
                    List<Word> body = parse(tokens.subList(i + 1, tokens.size()));
                    context = previousContext;

                    i += subRoutineOffset;
                    words.add(new WhileRepeat(body));
                }

                case "constant" -> {
                    i++;
                    String name = tokens.get(i);
                    initializedConstantNames.add(name);
                    words.add(new InitializeConstant(name));
                }
                case "variable" ->{
                    i++;
                    String name = tokens.get(i);
                    initializedVariableNames.add(name);
                    words.add(new InitializeVariable(name));
                }
                case "!" -> words.add(new StoreAtAddress());
                case "@" -> words.add(new GetFromAddress());
                case "?" -> {
                    words.add(new GetFromAddress());
                    words.add(new Dot());
                }
                case "allot" -> words.add(new Allot());

                case String s -> {
                    try {
                        words.add(new Literal(Integer.parseInt(s)));
                    } catch (NumberFormatException e){
                        if (initializedFunctionNames.contains(s)){
                            words.add(new FunctionCall(s));
                        } else if (initializedConstantNames.contains(s)){
                            words.add(new ConstantReference(s));
                        } else if (initializedVariableNames.contains(s)){
                            words.add(new VariableReference(s));
                        } else{
                            throw new UnkownWordException(s);
                        }

                    }
                }
            }
        }
        return words;
    }


}

