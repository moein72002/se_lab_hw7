package MiniJava.parser;

import MiniJava.errorHandler.ErrorHandler;
import MiniJava.scanner.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParseTable {
    private final ArrayList<Map<Token, Action>> actionTable;
    private final ArrayList<Map<NonTerminal, Integer>> gotoTable;

    public ParseTable(String jsonTable) throws Exception {
        actionTable = new ArrayList<>();
        gotoTable = new ArrayList<>();

        String[] rows = parseJsonToRows(jsonTable);
        Map<Integer, Token> terminals = extractTerminals(rows[0]);
        Map<Integer, NonTerminal> nonTerminals = extractNonTerminals(rows[0]);

        populateTables(rows, terminals, nonTerminals);
    }

    private String[] parseJsonToRows(String jsonTable) {
        jsonTable = jsonTable.substring(2, jsonTable.length() - 2);
        return jsonTable.split("\\],\\[");
    }

    private Map<Integer, Token> extractTerminals(String headerRow) {
        Map<Integer, Token> terminals = new HashMap<>();
        String[] columns = headerRow.substring(1, headerRow.length() - 1).split("\",\"");

        for (int i = 1; i < columns.length; i++) {
            if (!columns[i].startsWith("Goto")) {
                terminals.put(i, new Token(Token.getTyepFormString(columns[i]), columns[i]));
            }
        }
        return terminals;
    }

    private Map<Integer, NonTerminal> extractNonTerminals(String headerRow) {
        Map<Integer, NonTerminal> nonTerminals = new HashMap<>();
        String[] columns = headerRow.substring(1, headerRow.length() - 1).split("\",\"");

        for (int i = 1; i < columns.length; i++) {
            if (columns[i].startsWith("Goto")) {
                String nonTerminalName = columns[i].substring(5);
                try {
                    nonTerminals.put(i, NonTerminal.valueOf(nonTerminalName));
                } catch (IllegalArgumentException e) {
                    ErrorHandler.printError("Invalid NonTerminal: " + nonTerminalName);
                }
            }
        }
        return nonTerminals;
    }

    private void populateTables(String[] rows, Map<Integer, Token> terminals, Map<Integer, NonTerminal> nonTerminals)
            throws Exception {
        for (int i = 1; i < rows.length; i++) {
            String[] columns = rows[i].substring(1, rows[i].length() - 1).split("\",\"");
            actionTable.add(new HashMap<>());
            gotoTable.add(new HashMap<>());

            for (int j = 1; j < columns.length; j++) {
                if (!columns[j].isEmpty()) {
                    parseCell(columns[j], i - 1, j, terminals, nonTerminals);
                }
            }
        }
    }

    private void parseCell(String cellValue, int rowIndex, int columnIndex, Map<Integer, Token> terminals,
            Map<Integer, NonTerminal> nonTerminals) throws Exception {
        if ("acc".equals(cellValue)) {
            actionTable.get(rowIndex).put(terminals.get(columnIndex), new Action(act.accept, 0));
        } else if (terminals.containsKey(columnIndex)) {
            char actionType = cellValue.charAt(0);
            int value = Integer.parseInt(cellValue.substring(1));
            act action = (actionType == 'r') ? act.reduce : act.shift;
            actionTable.get(rowIndex).put(terminals.get(columnIndex), new Action(action, value));
        } else if (nonTerminals.containsKey(columnIndex)) {
            int gotoValue = Integer.parseInt(cellValue);
            gotoTable.get(rowIndex).put(nonTerminals.get(columnIndex), gotoValue);
        } else {
            throw new Exception("Unexpected cell value: " + cellValue);
        }
    }

    public int getGotoTable(int currentState, NonTerminal variable) {
        Map<NonTerminal, Integer> stateMap = gotoTable.get(currentState);
        if (stateMap == null || !stateMap.containsKey(variable)) {
            ErrorHandler
                    .printError("Goto table entry not found for state " + currentState + " and variable " + variable);
            return -1; // Return an invalid state to indicate an error
        }
        return stateMap.get(variable);
    }

    public Action getActionTable(int currentState, Token terminal) {
        Map<Token, Action> stateMap = actionTable.get(currentState);
        if (stateMap == null || !stateMap.containsKey(terminal)) {
            ErrorHandler
                    .printError("Action table entry not found for state " + currentState + " and terminal " + terminal);
            return null; // Return null to indicate an error
        }
        return stateMap.get(terminal);
    }
}
