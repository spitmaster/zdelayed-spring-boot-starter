package io.github.spitmaster.zdelayed.exceptions;

/**
 * Zdelayed运行时异常
 */
public class ZdelayedException extends RuntimeException {

    public ZdelayedException(String message) {
        super(message);
    }

    public ZdelayedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZdelayedException(Throwable cause) {
        super(cause);
    }
}
