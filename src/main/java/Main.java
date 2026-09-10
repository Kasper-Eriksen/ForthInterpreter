import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws Exception {
        boolean terminalMode = true;
        boolean verbose = true;

        if (terminalMode){
            Scanner scanner = new Scanner(System.in);
            while(true){
                String line = scanner.nextLine();
                try {
                    List<String> tokens = Lexer.lexLine(line);
                    List<Word> words = Parser.parse(tokens);
                    Interpreter.interpret(words);
                    System.out.println("\nok");
                } catch (Exception e){
                    System.out.println("\n" + e.getMessage());
                }
                Interpreter.stack.print();

            }

        }else{
            Path filePath = Paths.get("src\\main\\java\\Program.txt");
            String program = Files.readString(filePath);
            if (verbose){
                System.out.println("Program:");
                System.out.println(program);
                System.out.println("\n---\n");
            }

            try {
                List<String> tokens = Lexer.lex(program);

                if (verbose){
                    System.out.println("Tokens:");
                    System.out.println(tokens);
                    System.out.println("\n---\n");
                }

                List<Word> words = Parser.parse(tokens);
                if (verbose){
                    System.out.println("Words:");
                    for (Word word : words){
                        System.out.println(word);
                    }
                    System.out.println("\n---\n");
                }
                Interpreter.interpret(words);
                System.out.println("\nok");
            } catch (Exception e) {
                System.out.println("\n" + e.getMessage());
            }
            Interpreter.stack.print();
        }







    }
}