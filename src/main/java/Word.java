import java.util.List;

public interface Word {}

// Output
record Dot() implements Word {}
record Emit() implements Word {}
record CarriageReturn() implements Word {}
record forthString(String body) implements Word {}

// Stack manipulations
record Duplication() implements Word {}
record Drop() implements Word {}
record Swap() implements Word {}
record Over() implements Word {}
record Rotate() implements Word {}

// Arithmetics
record Add() implements Word {}
record Subtract() implements Word {}
record Divide() implements Word {}
record Multiply() implements Word {}
record Modulus() implements Word {}

// Boolean operators
record LessThan() implements Word {}
record GreaterThan() implements Word {}
record Equal() implements Word {}
record And() implements Word {}
record Or() implements Word {}
record Not() implements Word {}

// Conditional
record IfElseThen(List<Word> ifBody, List<Word> elseBody) implements Word{}

// Repetition
record DoLoop(List<Word> body) implements Word {}
record RunOnceDoLoop(List<Word> body) implements Word {}
record Index() implements Word {}
record WhileRepeat(List<Word> body) implements Word {}

// Variables, Constants, Literals
record InitializeVariable(String name) implements Word {}
record VariableReference(String name) implements Word {}
record StoreAtAddress() implements Word {}
record GetFromAddress() implements Word {}
record Allot() implements Word {}
record InitializeConstant(String name) implements Word {}
record ConstantReference(String name) implements Word {}
record Literal(int value) implements Word {}


// Functions
record Function(String name, List<Word> body) implements Word {}
record FunctionCall(String name) implements Word {}