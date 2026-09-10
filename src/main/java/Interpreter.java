import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {
    static Stack stack = new Stack(1024);

    static Map<String, List<Word>> initializedFunctions = new HashMap<>();
    static Map<String, Integer> initializedConstants = new HashMap<>();
    static Map<String, Integer> initializedVariables = new HashMap<>();
    static int[] variableAddresses = new int[1000];
    static int nextFreeAddress = 1000;
    static Integer index = null;

    public static void interpret(List<Word> words) throws Stack.StackUnderflowException, Stack.StackOverflowException {
        for (Word word : words){
            switch (word){
                case Add() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left + right);
                }
                case Subtract() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left - right);
                }
                case Multiply() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left * right);
                }
                case Divide() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left / right);
                }
                case Modulus() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left % right);
                }

                case Duplication() -> stack.duplicate();
                case Drop() -> stack.drop();
                case Swap() -> stack.swap();
                case Over() -> stack.over();
                case Rotate() -> stack.rotate();

                case LessThan() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left < right ? -1 : 0);
                }
                case GreaterThan() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left > right ? -1 : 0);
                }
                case Equal() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    stack.push(left == right ? -1 : 0);
                }
                case And() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    if (left != 0 && right != 0){
                        stack.push(-1);
                    }else{
                        stack.push(0);
                    }
                }
                case Or() -> {
                    int right = stack.pop();
                    int left = stack.pop();
                    if (left != 0 || right != 0){
                        stack.push(-1);
                    }else{
                        stack.push(0);
                    }
                }
                case Not() -> {
                    int top = stack.pop();
                    if (top == 0){
                        stack.push(-1);
                    }else{
                        stack.push(0);
                    }
                }

                case Dot() -> {
                    int top = stack.pop();
                    System.out.print(top + " ");
                }
                case Emit() -> {
                    int top = stack.pop();
                    System.out.print((char) top);
                }
                case CarriageReturn() -> System.out.print("\n");
                case forthString(String body) -> System.out.print(body);

                case IfElseThen(List<Word> ifBody, List<Word> elseBody) -> {
                    int top = stack.pop();
                    if (top != 0){
                        interpret(ifBody);
                    }else{
                        interpret(elseBody);
                    }
                }

                case DoLoop(List<Word> body) -> {
                    int start = stack.pop();
                    int end = stack.pop();
                    for (int i = start; i < end; i++) {
                        index = i;
                        interpret(body);
                    }
                    index = null;
                }
                case RunOnceDoLoop(List<Word> body) -> {
                    int start = stack.pop();
                    int end = stack.pop();
                    index = start;
                    interpret(body);
                    for (int i = start + 1; i < end; i++) {
                        index = i;
                        interpret(body);
                    }
                    index = null;
                }
                case Index() -> {
                    if (index != null){
                        stack.push(index);
                    }
                }
                case WhileRepeat(List<Word> body) -> {
                    while(stack.pop() != 0){
                        interpret(body);
                    }
                }

                case Function(String name, List<Word> body) -> initializedFunctions.put(name, body);
                case FunctionCall(String name) -> interpret(initializedFunctions.get(name));

                case InitializeConstant(String name) -> initializedConstants.put(name, stack.pop());
                case ConstantReference(String name) -> stack.push(initializedConstants.get(name));
                case InitializeVariable(String name) -> {
                    initializedVariables.put(name, nextFreeAddress);
                    nextFreeAddress++;
                }
                case VariableReference(String name) -> stack.push(initializedVariables.get(name));
                case StoreAtAddress() -> variableAddresses[stack.pop() - 1000] = stack.pop();
                case GetFromAddress() -> {
                    int top = stack.pop();
                    stack.push(variableAddresses[top - 1000]);
                }
                case Allot() -> nextFreeAddress += stack.pop();
                case Literal(int value) -> stack.push(value);
                default -> throw new IllegalStateException("Unimplemented Word: " + word);
            }
        }
    }
}
class Stack {
    public static class StackUnderflowException extends Exception{
        public StackUnderflowException(){
            super("Stack underflow");
        }
    }
    public static class StackOverflowException extends Exception{
        public StackOverflowException(){
            super("Stack overflow");
        }
    }

    int[] data;
    int pointer = 0;

    public Stack(int capacity){
        data = new int[capacity];
    }

    public void print(){
        for (int i = 0; i < pointer; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.print("<- Top\n");
    }

    public void push(int value) throws StackOverflowException {
        if (pointer == data.length){
            throw new StackOverflowException();
        }
        data[pointer] = value;
        pointer++;
    }

    public int pop() throws StackUnderflowException {
        if (pointer == 0){
            throw new StackUnderflowException();
        }
        pointer--;
        return data[pointer];
    }

    public void duplicate() throws StackUnderflowException, StackOverflowException {
        if (pointer == 0){
            throw new StackUnderflowException();
        }
        if (pointer == data.length){
            throw new StackOverflowException();
        }

        data[pointer] = data[pointer - 1];
        pointer++;
    }

    public void drop() throws StackUnderflowException {
        if (pointer == 0){
            throw new StackUnderflowException();
        }
        pointer--;
    }

    public void swap() throws StackUnderflowException {
        if (pointer < 2){
            drop();
            drop();
        }
        int temp = data[pointer - 1];
        data[pointer - 1] = data[pointer - 2];
        data[pointer - 2] = temp;
    }

    public void over() throws StackUnderflowException, StackOverflowException {
        if (pointer == data.length){
            throw new StackOverflowException();
        }
        if (pointer < 2){
            drop();
            drop();
        }
        data[pointer] = data[pointer - 2];
        pointer++;
    }

    public void rotate() throws StackUnderflowException {
        if (pointer < 3){
            drop();
            drop();
            drop();
        }
        int temp = data[pointer - 3];
        data[pointer - 3] = data[pointer - 2];
        data[pointer - 2] = data[pointer - 1];
        data[pointer - 1] = temp;
    }
}