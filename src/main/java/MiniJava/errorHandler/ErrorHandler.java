package MiniJava.errorHandler;

import lombok.Setter;

public class ErrorHandler {
    @Setter
    private static boolean hasError = false;

    public static boolean hasError() {
        return hasError;
    }

    public static void printError(String msg) {
        System.out.println(msg);
    }
}

