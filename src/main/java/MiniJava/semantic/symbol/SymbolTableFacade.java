package MiniJava.semantic.symbol;

import MiniJava.codeGenerator.Address;
import MiniJava.codeGenerator.Memory;

// Facade for Symbol Table
public class SymbolTableFacade {
    private SymbolTable symbolTable;

    public SymbolTableFacade(Memory memory) {
        this.symbolTable = new SymbolTable(memory);
    }

    // Simplified methods to interact with the symbol table
    public void createClass(String className) {
        symbolTable.addClass(className);
    }

    public void setSuperClass(String className, String superClass) {
        symbolTable.setSuperClass(superClass, className);
    }

    public void addFieldToClass(String className, String fieldName, SymbolType type) {
        symbolTable.setLastType(type);
        symbolTable.addField(fieldName, className);
    }

    public void addMethodToClass(String className, String methodName, int address, SymbolType returnType) {
        symbolTable.setLastType(returnType);
        symbolTable.addMethod(className, methodName, address);
    }

    public void addMethodParameter(String className, String methodName, String parameterName, SymbolType type) {
        symbolTable.setLastType(type);
        symbolTable.addMethodParameter(className, methodName, parameterName);
    }

    public void addLocalVariableToMethod(String className, String methodName, String localVariableName, SymbolType type) {
        symbolTable.setLastType(type);
        symbolTable.addMethodLocalVariable(className, methodName, localVariableName);
    }

    public Symbol getField(String className, String fieldName) {
        return symbolTable.get(fieldName, className);
    }

    public Symbol getVariable(String className, String methodName, String variableName) {
        return symbolTable.get(className, methodName, variableName);
    }

    public SymbolType getMethodReturnType(String className, String methodName) {
        return symbolTable.getMethodReturnType(className, methodName);
    }

    public int getMethodAddress(String className, String methodName) {
        return symbolTable.getMethodAddress(className, methodName);
    }

    public void startMethodCall(String className, String methodName) {
        symbolTable.startCall(className, methodName);
    }

    public Symbol getNextMethodParameter(String className, String methodName) {
        return symbolTable.getNextParam(className, methodName);
    }

    public Address getKeywordAddress(String keywordName) {
        return symbolTable.get(keywordName);
    }
}