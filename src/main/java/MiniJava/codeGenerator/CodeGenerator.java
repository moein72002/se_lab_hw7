 package MiniJava.codeGenerator;

 import MiniJava.Log.Log;
 import MiniJava.errorHandler.ErrorHandler;
 import MiniJava.scanner.token.Token;
 import MiniJava.semantic.symbol.Symbol;
 import MiniJava.semantic.symbol.SymbolTable;
 import MiniJava.semantic.symbol.SymbolType;
 import lombok.Getter;

 import java.util.HashMap;
 import java.util.Map;
 import java.util.Stack;

 interface SemanticAction {
     void execute(CodeGenerator context, Token next);
 }

 class DefMain implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getMemory().add3AddressCode(context.getStack().pop().num, Operation.JP, new Address(context.getMemory().getCurrentCodeBlockAddress(), varType.Address), null, null);
         String methodName = "main";
         String className = context.getSymbolStack().pop();
         context.getSymbolTable().addMethod(className, methodName, context.getMemory().getCurrentCodeBlockAddress());
         context.getSymbolStack().push(className);
         context.getSymbolStack().push(methodName);
     }
 }

 class CheckID implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getSymbolStack().pop();
         if (context.getStack().peek().varType == varType.Non) {
             ErrorHandler.printError("Invalid ID");
         }
     }
 }

 class PID implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         if (context.getSymbolStack().size() > 1) {
             String methodName = context.getSymbolStack().pop();
             String className = context.getSymbolStack().pop();
             try {
                 Symbol s = context.getSymbolTable().get(className, methodName, next.value);
                 varType t = varType.Int;
                 switch (s.type) {
                     case Bool:
                         t = varType.Bool;
                         break;
                     case Int:
                         t = varType.Int;
                         break;
                 }
                 context.getStack().push(new Address(s.address, t));


             } catch (Exception e) {
                 context.getStack().push(new Address(0, varType.Non));
             }
             context.getSymbolStack().push(className);
             context.getSymbolStack().push(methodName);
         } else {
             context.getStack().push(new Address(0, varType.Non));
         }
         context.getSymbolStack().push(next.value);
     }
 }

 class FPID implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().pop();
         context.getStack().pop();

         Symbol s = context.getSymbolTable().get(context.getSymbolStack().pop(), context.getSymbolStack().pop());
         varType t = varType.Int;
         switch (s.type) {
             case Bool:
                 t = varType.Bool;
                 break;
             case Int:
                 t = varType.Int;
                 break;
         }
         context.getStack().push(new Address(s.address, t));
     }
 }

 class KPID implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().push(context.getSymbolTable().get(next.value));
     }
 }

 class IntPID implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().push(new Address(Integer.parseInt(next.value), varType.Int, TypeAddress.Imidiate));
     }
 }

 class StartCall implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         //TODO: method ok
         context.getStack().pop();
         context.getStack().pop();
         String methodName = context.getSymbolStack().pop();
         String className = context.getSymbolStack().pop();
         context.getSymbolTable().startCall(className, methodName);
         context.getCallStack().push(className);
         context.getCallStack().push(methodName);

         //symbolStack.push(methodName);
     }
 }

 class Call implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         //TODO: method ok
         String methodName = context.getCallStack().pop();
         String className = context.getCallStack().pop();
         try {
             context.getSymbolTable().getNextParam(className, methodName);
             ErrorHandler.printError("The few argument pass for method");
         } catch (IndexOutOfBoundsException e) {
         }
         varType t = varType.Int;
         switch (context.getSymbolTable().getMethodReturnType(className, methodName)) {
             case Int:
                 t = varType.Int;
                 break;
             case Bool:
                 t = varType.Bool;
                 break;
         }
         Address temp = new Address(context.getMemory().getTemp(), t);
         context.getStack().push(temp);
         context.getMemory().add3AddressCode(Operation.ASSIGN, new Address(temp.num, varType.Address, TypeAddress.Imidiate), new Address(context.getSymbolTable().getMethodReturnAddress(className, methodName), varType.Address), null);
         context.getMemory().add3AddressCode(Operation.ASSIGN, new Address(context.getMemory().getCurrentCodeBlockAddress() + 2, varType.Address, TypeAddress.Imidiate), new Address(context.getSymbolTable().getMethodCallerAddress(className, methodName), varType.Address), null);
         context.getMemory().add3AddressCode(Operation.JP, new Address(context.getSymbolTable().getMethodAddress(className, methodName), varType.Address), null, null);

         //symbolStack.pop();
     }
 }

 class Arg implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         String methodName = context.getCallStack().pop();
 //        String className = symbolStack.pop();
         try {
             Symbol s = context.getSymbolTable().getNextParam(context.getCallStack().peek(), methodName);
             varType t = varType.Int;
             switch (s.type) {
                 case Bool:
                     t = varType.Bool;
                     break;
                 case Int:
                     t = varType.Int;
                     break;
             }
             Address param = context.getStack().pop();
             if (param.varType != t) {
                 ErrorHandler.printError("The argument type isn't match");
             }
             context.getMemory().add3AddressCode(Operation.ASSIGN, param, new Address(s.address, t), null);

 //        symbolStack.push(className);

         } catch (IndexOutOfBoundsException e) {
             ErrorHandler.printError("Too many arguments pass for method");
         }
         context.getCallStack().push(methodName);
     }
 }

 class Assign implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address s1 = context.getStack().pop();
         Address s2 = context.getStack().pop();
 //        try {
         if (s1.varType != s2.varType) {
             ErrorHandler.printError("The type of operands in assign is different ");
         }
 //        }catch (NullPointerException d)
 //        {
 //            d.printStackTrace();
 //        }
         context.getMemory().add3AddressCode(Operation.ASSIGN, s1, s2, null);
     }
 }

 class Add implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address temp = new Address(context.getMemory().getTemp(), varType.Int);
         Address s2 = context.getStack().pop();
         Address s1 = context.getStack().pop();

         if (s1.varType != varType.Int || s2.varType != varType.Int) {
             ErrorHandler.printError("In add two operands must be integer");
         }
         context.getMemory().add3AddressCode(Operation.ADD, s1, s2, temp);
         context.getStack().push(temp);
     }
 }

 class Sub implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address temp = new Address(context.getMemory().getTemp(), varType.Int);
         Address s2 = context.getStack().pop();
         Address s1 = context.getStack().pop();
         if (s1.varType != varType.Int || s2.varType != varType.Int) {
             ErrorHandler.printError("In sub two operands must be integer");
         }
         context.getMemory().add3AddressCode(Operation.SUB, s1, s2, temp);
         context.getStack().push(temp);
     }
 }

 class Mult implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address temp = new Address(context.getMemory().getTemp(), varType.Int);
         Address s2 = context.getStack().pop();
         Address s1 = context.getStack().pop();
         if (s1.varType != varType.Int || s2.varType != varType.Int) {
             ErrorHandler.printError("In mult two operands must be integer");
         }
         context.getMemory().add3AddressCode(Operation.MULT, s1, s2, temp);
 //        memory.saveMemory();
         context.getStack().push(temp);
     }
 }

 class Label implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().push(new Address(context.getMemory().getCurrentCodeBlockAddress(), varType.Address));
     }
 }

 class Save implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().push(new Address(context.getMemory().saveMemory(), varType.Address));
     }
 }

 class While implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getMemory().add3AddressCode(context.getStack().pop().num, Operation.JPF, context.getStack().pop(), new Address(context.getMemory().getCurrentCodeBlockAddress() + 1, varType.Address), null);
         context.getMemory().add3AddressCode(Operation.JP, context.getStack().pop(), null, null);
     }
 }

 class JPFSave implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address save = new Address(context.getMemory().saveMemory(), varType.Address);
         context.getMemory().add3AddressCode(context.getStack().pop().num, Operation.JPF, context.getStack().pop(), new Address(context.getMemory().getCurrentCodeBlockAddress(), varType.Address), null);
         context.getStack().push(save);
     }
 }

 class JPHere implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getMemory().add3AddressCode(context.getStack().pop().num, Operation.JP, new Address(context.getMemory().getCurrentCodeBlockAddress(), varType.Address), null, null);
     }
 }

 class Print implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getMemory().add3AddressCode(Operation.PRINT, context.getStack().pop(), null, null);
     }
 }

 class Equal implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address temp = new Address(context.getMemory().getTemp(), varType.Bool);
         Address s2 = context.getStack().pop();
         Address s1 = context.getStack().pop();
         if (s1.varType != s2.varType) {
             ErrorHandler.printError("The type of operands in equal operator is different");
         }
         context.getMemory().add3AddressCode(Operation.EQ, s1, s2, temp);
         context.getStack().push(temp);
     }
 }

 class LessThan implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address temp = new Address(context.getMemory().getTemp(), varType.Bool);
         Address s2 = context.getStack().pop();
         Address s1 = context.getStack().pop();
         if (s1.varType != varType.Int || s2.varType != varType.Int) {
             ErrorHandler.printError("The type of operands in less than operator is different");
         }
         context.getMemory().add3AddressCode(Operation.LT, s1, s2, temp);
         context.getStack().push(temp);
     }
 }

 class And implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address temp = new Address(context.getMemory().getTemp(), varType.Bool);
         Address s2 = context.getStack().pop();
         Address s1 = context.getStack().pop();
         if (s1.varType != varType.Bool || s2.varType != varType.Bool) {
             ErrorHandler.printError("In and operator the operands must be boolean");
         }
         context.getMemory().add3AddressCode(Operation.AND, s1, s2, temp);
         context.getStack().push(temp);
     }
 }

 class Not implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         Address temp = new Address(context.getMemory().getTemp(), varType.Bool);
         Address s2 = context.getStack().pop();
         Address s1 = context.getStack().pop();
         if (s1.varType != varType.Bool) {
             ErrorHandler.printError("In not operator the operand must be boolean");
         }
         context.getMemory().add3AddressCode(Operation.NOT, s1, s2, temp);
         context.getStack().push(temp);
     }
 }

 class DefClass implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().pop();
         context.getSymbolTable().addClass(context.getSymbolStack().peek());
     }
 }

 class DefMethod implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().pop();
         String methodName = context.getSymbolStack().pop();
         String className = context.getSymbolStack().pop();

         context.getSymbolTable().addMethod(className, methodName, context.getMemory().getCurrentCodeBlockAddress());

         context.getSymbolStack().push(className);
         context.getSymbolStack().push(methodName);
     }
 }

 class PopClass implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getSymbolStack().pop();
     }
 }

 class Extend implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().pop();
         context.getSymbolTable().setSuperClass(context.getSymbolStack().pop(), context.getSymbolStack().peek());
     }
 }

 class DefField implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().pop();
         context.getSymbolTable().addField(context.getSymbolStack().pop(), context.getSymbolStack().peek());
     }
 }

 class DefVar implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getStack().pop();

         String var = context.getSymbolStack().pop();
         String methodName = context.getSymbolStack().pop();
         String className = context.getSymbolStack().pop();

         context.getSymbolTable().addMethodLocalVariable(className, methodName, var);

         context.getSymbolStack().push(className);
         context.getSymbolStack().push(methodName);
     }
 }

 class MethodReturn implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         //TODO : call ok

         String methodName = context.getSymbolStack().pop();
         Address s = context.getStack().pop();
         SymbolType t = context.getSymbolTable().getMethodReturnType(context.getSymbolStack().peek(), methodName);
         varType temp = varType.Int;
         switch (t) {
             case Int:
                 break;
             case Bool:
                 temp = varType.Bool;
         }
         if (s.varType != temp) {
             ErrorHandler.printError("The type of method and return address was not match");
         }
         context.getMemory().add3AddressCode(Operation.ASSIGN, s, new Address(context.getSymbolTable().getMethodReturnAddress(context.getSymbolStack().peek(), methodName), varType.Address, TypeAddress.Indirect), null);
         context.getMemory().add3AddressCode(Operation.JP, new Address(context.getSymbolTable().getMethodCallerAddress(context.getSymbolStack().peek(), methodName), varType.Address), null, null);

         //symbolStack.pop();
     }
 }

 class DefParam implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         //TODO : call Ok
         context.getStack().pop();
         String param = context.getSymbolStack().pop();
         String methodName = context.getSymbolStack().pop();
         String className = context.getSymbolStack().pop();

         context.getSymbolTable().addMethodParameter(className, methodName, param);

         context.getSymbolStack().push(className);
         context.getSymbolStack().push(methodName);
     }
 }

 class LastTypeBool implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getSymbolTable().setLastType(SymbolType.Bool);
     }
 }

 class LastTypeInt implements SemanticAction {
     @Override
     public void execute(CodeGenerator context, Token next) {
         context.getSymbolTable().setLastType(SymbolType.Int);
     }
 }

 public class CodeGenerator {
     @Getter
     private Memory memory = new Memory();
     @Getter
     private Stack<Address> stack = new Stack<Address>();
     @Getter
     private Stack<String> symbolStack = new Stack<>();
     @Getter
     private Stack<String> callStack = new Stack<>();
     @Getter
     private SymbolTable symbolTable;

     private Map<Integer, SemanticAction> actionMap = new HashMap<>();

     public CodeGenerator() {
         symbolTable = new SymbolTable(memory);
         fillActionMap();
         //TODO
     }

     private void fillActionMap() {
         this.actionMap.put(1, new CheckID());
         this.actionMap.put(2, new PID());
         this.actionMap.put(3, new FPID());
         this.actionMap.put(4, new KPID());
         this.actionMap.put(5, new IntPID());
         this.actionMap.put(6, new StartCall());
         this.actionMap.put(7, new Call());
         this.actionMap.put(8, new Arg());
         this.actionMap.put(9, new Assign());
         this.actionMap.put(10, new Add());
         this.actionMap.put(11, new Sub());
         this.actionMap.put(12, new Mult());
         this.actionMap.put(13, new Label());
         this.actionMap.put(14, new Save());
         this.actionMap.put(15, new While());
         this.actionMap.put(16, new JPFSave());
         this.actionMap.put(17, new JPHere());
         this.actionMap.put(18, new Print());
         this.actionMap.put(19, new Equal());
         this.actionMap.put(20, new LessThan());
         this.actionMap.put(21, new And());
         this.actionMap.put(22, new Not());
         this.actionMap.put(23, new DefClass());
         this.actionMap.put(24, new DefMethod());
         this.actionMap.put(25, new PopClass());
         this.actionMap.put(26, new Extend());
         this.actionMap.put(27, new DefField());
         this.actionMap.put(28, new DefVar());
         this.actionMap.put(29, new MethodReturn());
         this.actionMap.put(30, new DefParam());
         this.actionMap.put(31, new LastTypeBool());
         this.actionMap.put(32, new LastTypeInt());
         this.actionMap.put(33, new DefMain());
     }

     public void printMemory() {
         memory.pintCodeBlock();
     }

     public void semanticFunction(int func, Token next) {
         Log.print("codegenerator : " + func);
         if (func == 0) {
             return;
         } else {
             actionMap.get(func).execute(this, next);
         }
     }

     public void main() { }
 }