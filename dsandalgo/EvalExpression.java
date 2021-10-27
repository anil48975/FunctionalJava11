package dsandalgo;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

//_________________________________________________________________________________________________________________________
//Public Fields declarations


//_________________________________________________________________________________________________________________________
//The class is configurable, one can provide its own expression parser and validator
//The class is thread safe as there is no shared instance variable
public class EvalExpression {
    public static void main(String[] args) {
        EvalExpression evalExpression = new EvalExpression();
        try {
            //Exception handling for Future
            Callable<Integer> task = () -> {
                try {
                    return evalExpression.evaluateExpression("7+8/2-1+3*4");
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                return -1;
            };
            System.out.println("Expected Result: 22 :: Actual result: " + executorService.submit(task).get());
            System.out.println("Expected Result: 26 :: Actual result: " + executorService.submit(() -> evalExpression.evaluateExpression("7+5*2/2+14")).get());
            System.out.println("Expected Result: 17 :: Actual result: " + executorService.submit(() -> evalExpression.evaluateExpression("7+5*2/2+(14-4)/2")).get());
            System.out.println("Expected Result: 17 :: Actual result: " + executorService.submit(() -> evalExpression.evaluateExpression("(7+5*2/2+(14-4)/2)")).get());
            System.out.println("Expected Result: 32 :: Actual result: " + executorService.submit(() -> evalExpression.evaluateExpression("(7+5*(2/2+(14-5))/2)")).get());

            try {
                System.out.println("Expected Result: 32 :: Actual result: " + executorService.submit(() -> evalExpression.evaluateExpression("(7+5*(2 /2+(14-5))/2)")).get());
            } catch(Exception e) {
                System.out.println("Expected exception message: " + e.getMessage());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        while (true) {
            try {
                if (!!executorService.awaitTermination(10, TimeUnit.SECONDS)) break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    public int evaluateExpression(String input) {
        return evaluateExpression(input, parseExpression, validator);
    }

    //The class is configurable, one can provide its own expression parser and validator
    public int evaluateExpression(String input, Function <String, Stack<String>> parseExpression, Consumer<String> validator) {
        validator.accept(input);
        Stack<String> expressionValues = parseExpression.apply(input);
        expressionValues = solveBrackets(expressionValues);
        int finalResult;
        //This condition confirms if there are any more expressions to be calculated,
        //otherwise this is the result.
        if (expressionValues.size() > 1) {
            finalResult = evaluateExpression(expressionValues);
        } else {
            finalResult = Integer.parseInt(expressionValues.pop());
        }
        System.out.println("Result for input : " + input + " is: " + finalResult + " Executed by thread: " + Thread.currentThread().getName());
        return finalResult;
    }

// private methods ______________________________________________________________________________________________________________

    private Stack<String> solveBrackets(Stack<String> expressionValues) {
        Stack<String> newExpressionValues = new Stack<>();
        while (!expressionValues.isEmpty()) {
            String expressionValue = expressionValues.pop();
            if (Brackets.CLOSING.toString().equals(expressionValue)) {
                Stack<String> expreesioValuesInsideBracket = new Stack<>();
                String newExpressionValue = newExpressionValues.pop();
                while (!Brackets.OPENING.toString().equals(newExpressionValue)) {
                    expreesioValuesInsideBracket.push(newExpressionValue);
                    newExpressionValue = newExpressionValues.pop();
                }
                newExpressionValues.push(BLANK_SPACE + evaluateExpression(expreesioValuesInsideBracket));
            } else {
                newExpressionValues.push(expressionValue);
            }
        }
        return reverseStack(newExpressionValues);
    }

    private int evaluateExpression(Stack<String> expressionValues) {
        Map<Integer, Queue<EvalExpression.Operand>> priorityOperandQueueMap = Map.of(1, new PriorityQueue<>((e1, e2) -> e1.seq - e2.seq),
                2, new PriorityQueue<>((e1, e2) -> e1.seq - e2.seq));

        Map<Integer, Queue<EvalExpression.Operator>> priorityOperatorQueueMap = Map.of(1, new PriorityQueue<>((e1, e2) -> e1.seq - e2.seq),
                2, new PriorityQueue<>((e1, e2) -> e1.seq - e2.seq));

        int count = 0;
        String previousOperator = null;
        while (!expressionValues.isEmpty()) {
            count++;
            String expressionValue = expressionValues.pop();
            if (isOperatorChar.test(expressionValue)) {
                Operator operator = new Operator(count, OPERATORS_MAP.get(expressionValue).priority, OPERATORS_MAP.get(expressionValue));
                priorityOperatorQueueMap.get(operator.priority).offer(operator);
                previousOperator = expressionValue;
            }
            if (isNumber(expressionValue)) {
                fillOperand(expressionValues, expressionValue, previousOperator, count, priorityOperandQueueMap);
            }
        }
        return processOperations(priorityOperatorQueueMap, priorityOperandQueueMap);
    }

    private int processOperations(Map<Integer, Queue<EvalExpression.Operator>> priorityOperatorQueueMap,
                                  Map<Integer, Queue<EvalExpression.Operand>> priorityOperandQueueMap ) {
        List<Map.Entry<Integer, Queue<EvalExpression.Operator>>> operatorsEntry = priorityOperatorQueueMap.entrySet().stream()
                .sorted((e1, e2) -> e1.getKey() - e2.getKey()).collect(toList());

        int finalResult = -1;
        for (Map.Entry<Integer, Queue<EvalExpression.Operator>> operatorEntry : operatorsEntry) {
            Queue<EvalExpression.Operator> operatorsQueue = operatorEntry.getValue();

            while (!operatorsQueue.isEmpty()) {
                Operator operator = operatorsQueue.poll();
                Operand operand1 = priorityOperandQueueMap.get(operator.priority).poll();
                Operand operand2 = priorityOperandQueueMap.get(operator.priority).poll();
                finalResult = OPERATIONS_MAP.get(operator.operatorSign).apply(operand1.value, operand2.value);

                if (operand1.firstOperator != null || operand2.secondOperator != null) {
                    OperatorConstantInstances resultFirstOperator = null;
                    OperatorConstantInstances resultSeconsOperator = null;
                    int resultPriority = -1;
                    if (operand1.firstOperator == null) {
                        resultPriority = operand2.secondOperator.priority;
                        resultSeconsOperator = operand2.secondOperator;
                    }
                    if (operand2.secondOperator == null) {
                        resultPriority = operand1.firstOperator.priority;
                        resultFirstOperator = operand1.firstOperator;
                    }
                    if (operand1.firstOperator != null && operand2.secondOperator != null) {
                        resultPriority = Math.min(operand1.firstOperator.priority, operand2.secondOperator.priority);
                        resultFirstOperator = operand1.firstOperator;
                        resultSeconsOperator = operand2.secondOperator;
                    }

                    Operand resultOperand = new Operand(operand2.seq, finalResult, resultPriority);
                    resultOperand.firstOperator = resultFirstOperator;
                    resultOperand.secondOperator = resultSeconsOperator;
                    Queue<Operand> operandQueue = priorityOperandQueueMap.get(resultPriority);
                    operandQueue.offer(resultOperand);
                }
            }
        }

        return finalResult;
    }

    private Map<Integer, Queue<Operand>> fillOperand(Stack<String> expressionValues, String expressionValue, String previousOperator, int count,
                                                     Map<Integer, Queue<EvalExpression.Operand>> priorityOperandQueueMap) {
        OperatorConstantInstances firstOperator = null;
        OperatorConstantInstances secondOperator = null;
        if(previousOperator != null) firstOperator = OPERATORS_MAP.get(previousOperator);
        if(!expressionValues.isEmpty() && expressionValues.peek() != null) secondOperator = OPERATORS_MAP.get(expressionValues.peek());

        int operandPriority = Math.min(firstOperator != null ? firstOperator.priority : secondOperator.priority,
                secondOperator != null ? secondOperator.priority : OPERATORS_MAP.get(previousOperator).priority);
        Operand operand = new Operand(count, Integer.parseInt(expressionValue),
                operandPriority);
        operand.firstOperator = firstOperator;
        operand.secondOperator = secondOperator;
        priorityOperandQueueMap.get(operand.priority).offer(operand);
        return priorityOperandQueueMap;
    }

    private Stack<String> parseExpression (String input) {
        char[] expChars = input.toCharArray();
        Stack<String> expressionValues = new Stack<>();
        StringBuilder number = new StringBuilder();

        for (int count = 0; count < expChars.length; count++) {
            char c = expChars[count];
            boolean isNumber = isNumber(BLANK_SPACE + c);
            boolean isLastDigit = count == expChars.length - 1;
            if (isNumber) {
                number.append(c);
                if (isLastDigit) expressionValues.push(number.toString());
            }
            else {
                expressionValues.push(number.toString());
                number = new StringBuilder();
                expressionValues.push(BLANK_SPACE + c);
            }
        }
        return reverseStack(expressionValues);
    }

    private boolean isNumber(String c) {
        try {
            Integer.parseInt(c);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Stack<String> reverseStack(Stack<String> input) {
        Stack<String> reversedStack = new Stack<>();
        while (!input.isEmpty()) {
            String value = input.pop();
            if (!BLANK_SPACE.equals(value))
                reversedStack.push(value);
        }
        return reversedStack;
    }

// Inner Classes_____________________________________________________________________________________________________________________

    private static  class Operator {
        private int seq;
        private int priority;
        private OperatorConstantInstances operatorSign = null;

        Operator(int seq, int priority, OperatorConstantInstances operatorSign) {
            this.seq = seq;
            this.priority = priority;
            this.operatorSign = operatorSign;
        }

        @Override
        public String toString() {
            return "Sign: " + this.operatorSign + " Priority: " + this.priority + " seq: " + seq;
        }
    }

    private static class Operand {
        private int seq;
        private int value;
        private int priority;
        private OperatorConstantInstances firstOperator;
        private OperatorConstantInstances secondOperator;
        Operand(int seq, int value, int priority) {
            this.seq = seq;
            this.value = value;
            this.priority = priority;
        }
        @Override
        public String toString() {
            return "Seq: " + this.seq + " Value: " + this.value;
        }
    }

    private enum OperatorConstantInstances {
        PLUS("+", 2), MINUS("-", 2),
        MULTIPLICTION("*", 1), DIVISION("/", 1);

        OperatorConstantInstances(String value, int priority) {
            this.value = value;
            this.priority = priority;
        }

        private String value;
        private int priority;

        @Override
        public String toString() {
            return value;
        }
    }

    private enum Brackets {
        OPENING("("), CLOSING(")");

        Brackets(String value) {
            this.value = value;
        }
        private String value;
        @Override
        public String toString() {
            return value;
        }
    }

// Private Field declarations_____________________________________________________________________________________________________

    private static final Map<OperatorConstantInstances, BiFunction<Integer, Integer, Integer>> OPERATIONS_MAP = Map.of(OperatorConstantInstances.MINUS, (op1, op2) ->  op1.intValue() - op2.intValue(),
            OperatorConstantInstances.PLUS, (op1, op2) ->  op1 + op2,
            OperatorConstantInstances.MULTIPLICTION, (op1, op2) ->  op1 * op2,
            OperatorConstantInstances.DIVISION, (op1, op2) ->  op1 / op2);

    private static final Predicate<String> isOperatorChar = input -> OperatorConstantInstances.PLUS.value.equals(input) || OperatorConstantInstances.MINUS.value.equals(input)
            || OperatorConstantInstances.MULTIPLICTION.value.equals(input) || OperatorConstantInstances.DIVISION.value.equals(input);

    private static final Map<String, OperatorConstantInstances> OPERATORS_MAP = Map.of("+", OperatorConstantInstances.PLUS,
            "-", OperatorConstantInstances.MINUS,
            "*", OperatorConstantInstances.MULTIPLICTION,
            "/", OperatorConstantInstances.DIVISION);
    private static final String BLANK_SPACE = "";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

// Private Functions:_____________________________________________________________________________________________________
    private static final Function<Stack<String>, Stack<String>> reverseStack = input -> {
        Stack<String> reversedStack = new Stack<>();
        while (!input.isEmpty()) {
            String value = input.pop();
            if (!BLANK_SPACE.equals(value))
                reversedStack.push(value);
        }
        return reversedStack;
    };

    private static final Function<String, Boolean> isNumber = input -> {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    private static final Function<String, Stack<String>> parseExpression = input -> {
        char[] expChars = input.toCharArray();
        Stack<String> expressionValues = new Stack<>();
        StringBuilder number = new StringBuilder();

        for (int count = 0; count < expChars.length; count++) {
            char c = expChars[count];
            boolean isNumberResult = isNumber.apply(BLANK_SPACE + c);
            boolean isLastDigit = count == expChars.length - 1;
            if (isNumberResult) {
                number.append(c);
                if (isLastDigit) expressionValues.push(number.toString());
            }
            else {
                expressionValues.push(number.toString());
                number = new StringBuilder();
                expressionValues.push(BLANK_SPACE + c);
            }
        }
        return reverseStack.apply(expressionValues);
    };

    private static final Consumer<String> validator = input -> {
        char[] inputChars = input.toCharArray();
        for (int i = 0; i < inputChars.length; i++) {
            String singleInput = inputChars[i]+BLANK_SPACE;
            if (!(isNumber.apply(singleInput) || Brackets.OPENING.toString().equals(singleInput) || Brackets.CLOSING.toString().equals(singleInput)
                    || OPERATORS_MAP.containsKey(singleInput))) {
                throw new RuntimeException("\""+singleInput+"\"" + " is not a valid input.");
            }
        }
    };
}
