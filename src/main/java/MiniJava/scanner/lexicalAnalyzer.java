package MiniJava.scanner;

import MiniJava.errorHandler.ErrorHandler;
import MiniJava.scanner.token.Token;
import MiniJava.scanner.type.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lexicalAnalyzer {
    private final Matcher matcher;

    public lexicalAnalyzer(java.util.Scanner sc) {
        String input = readInput(sc);
        Pattern tokenPattern = compileTokenPattern();
        matcher = tokenPattern.matcher(input);
    }

    public Token getNextToken() {
        while (matcher.find()) {
            Token token = matchToken();
            if (token != null)
                return token;
        }
        return new Token(Type.EOF, "$");
    }

    private String readInput(java.util.Scanner sc) {
        StringBuilder input = new StringBuilder();
        while (sc.hasNext()) {
            input.append(sc.nextLine());
        }
        return input.toString();
    }

    private Pattern compileTokenPattern() {
        StringBuilder tokenPattern = new StringBuilder();
        for (Type tokenType : Type.values()) {
            tokenPattern.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
        }
        return Pattern.compile(tokenPattern.substring(1)); // remove the leading '|'
    }

    private Token matchToken() {
        for (Type t : Type.values()) {
            if (matcher.group(t.name()) != null) {
                if (isComment(t))
                    return null;
                if (isInvalidId(t))
                    return null;
                return new Token(t, matcher.group(t.name()));
            }
        }
        return null;
    }

    private boolean isComment(Type tokenType) {
        if (tokenType == Type.COMMENT && matcher.group(Type.COMMENT.name()) != null) {
            return true;
        }
        return false;
    }

    private boolean isInvalidId(Type tokenType) {
        if (tokenType == Type.ErrorID && matcher.group(Type.ErrorID.name()) != null) {
            ErrorHandler.printError("The id must start with a character");
            return true;
        }
        return false;
    }
}