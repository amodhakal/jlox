package amodhakal.lox;

public record Token(TokenType type, String lexeme, Object literal, Integer line) {
    @Override
    public String toString() {
        return String.format("%s %s %s", type(), lexeme(), line());
    }
}
