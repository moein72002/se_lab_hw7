package MiniJava.codeGenerator;

// used to make simple functions only
public class SimpleCodeGeneratorFacade {
    private CodeGenerator codeGenerator;

    public SimpleCodeGeneratorFacade() {
        this.codeGenerator = new CodeGenerator();
    }

    // addition
    public void add(int operand1, int operand2) {
        Address op1 = new Address(operand1, varType.Int, TypeAddress.Imidiate);
        Address op2 = new Address(operand2, varType.Int, TypeAddress.Imidiate);
        Address result = new Address(codeGenerator.getMemory().getTemp(), varType.Int);

        codeGenerator.getStack().push(op1);
        codeGenerator.getStack().push(op2);
        codeGenerator.getMemory().add3AddressCode(Operation.ADD, op1, op2, result);

        System.out.println("Generated addition: " + operand1 + " + " + operand2 + " -> Temp");
    }

    // subtraction
    public void sub(int operand1, int operand2) {
        Address op1 = new Address(operand1, varType.Int, TypeAddress.Imidiate);
        Address op2 = new Address(operand2, varType.Int, TypeAddress.Imidiate);
        Address result = new Address(codeGenerator.getMemory().getTemp(), varType.Int);

        codeGenerator.getStack().push(op1);
        codeGenerator.getStack().push(op2);
        codeGenerator.getMemory().add3AddressCode(Operation.SUB, op1, op2, result);

        System.out.println("Generated subtraction: " + operand1 + " - " + operand2 + " -> Temp");
    }

    // jump
    public void jmp(int targetAddress) {
        Address jumpTarget = new Address(targetAddress, varType.Address, TypeAddress.Imidiate);

        codeGenerator.getMemory().add3AddressCode(Operation.JP, jumpTarget, null, null);

        System.out.println("Generated jump to address: " + targetAddress);
    }

    // used for debug or getting results
    public void printGeneratedCode() {
        codeGenerator.printMemory();
    }
}
