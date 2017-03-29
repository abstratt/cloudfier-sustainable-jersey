package alexandria_forms;

public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(String message) {
        super(message);
    }
    public ConstraintViolationException() {
        super("A constraint was violated");
    }
}