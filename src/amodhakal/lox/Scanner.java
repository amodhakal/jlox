package amodhakal.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
   private final String source;
   private Integer start = 0;
   private Integer current = 0;
   private Integer line = 1;

   private final List<Token> tokens;
   private static final Map<String, TokenType> keywords;

   static {
      keywords = new HashMap<>();
      keywords.put("and", TokenType.AND);
      keywords.put("class", TokenType.CLASS);
      keywords.put("else", TokenType.ELSE);
      keywords.put("false", TokenType.FALSE);
      keywords.put("for", TokenType.FOR);
      keywords.put("fun", TokenType.FUN);
      keywords.put("if", TokenType.IF);
      keywords.put("nil", TokenType.NIL);
      keywords.put("or", TokenType.OR);
      keywords.put("print", TokenType.PRINT);
      keywords.put("return", TokenType.RETURN);
      keywords.put("super", TokenType.SUPER);
      keywords.put("this", TokenType.THIS);
      keywords.put("true", TokenType.TRUE);
      keywords.put("var", TokenType.VAR);
      keywords.put("while", TokenType.WHILE);
   }

   public Scanner(String source) {
      this.source = source;
      tokens = new ArrayList<>();
   }

   public List<Token> scanTokens() {
      while (!isAtEnd()) {
         start = current;
         scanToken();
      }

      tokens.add(new Token(TokenType.EOF, "", null, line));
      return tokens;
   }

   private Boolean isAtEnd() {
      return current >= source.length();
   }

   private void scanToken() {
      Character ch = advance();
      switch (ch) {
         case '(':
            addToken(TokenType.LEFT_PAREN);
            break;
         case ')':
            addToken(TokenType.RIGHT_PAREN);
            break;
         case '{':
            addToken(TokenType.LEFT_BRACE);
            break;
         case '}':
            addToken(TokenType.RIGHT_BRACE);
            break;
         case ',':
            addToken(TokenType.COMMA);
            break;
         case '.':
            addToken(TokenType.DOT);
            break;
         case '-':
            addToken(TokenType.MINUS);
            break;
         case '+':
            addToken(TokenType.PLUS);
            break;
         case ';':
            addToken(TokenType.SEMICOLON);
            break;
         case '*':
            addToken(TokenType.STAR);
            break;
         case '!':
            addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            break;
         case '=':
            addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            break;
         case '<':
            addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            break;
         case '>':
            addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            break;
         case '/':
            if (match('/')) {
               while (peek() != '\n' && !isAtEnd()) advance();
            } else {
               addToken(TokenType.SLASH);
            }
            break;
         case ' ':
         case '\r':
         case '\t':
            break;
         case '\n':
            line++;
            break;
         case '"':
            string();
            break;
         case 'o':
            if (match('r')) {
               addToken(TokenType.OR);
            }
            break;
         default:
            if (isDigit(ch)) {
               number();
            } else if (isAlpha(ch)) {
               identifier();
            } else {
               Lox.error(line, "Unexpected character.");
            }
            break;
      }
   }

   private void identifier() {
      while (isAlphaNumeric(peek())) advance();
      String text = source.substring(start, current);
      TokenType type = keywords.get(text);
      if (type == null) type = TokenType.IDENTIFIER;
      addToken(type);
   }

   private void number() {
      while (isDigit(peek())) advance();

      if (peek() == '.' && isDigit(peekNext())) {
         do advance();
         while (isDigit(peek()));
      }

      addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
   }

   private void string() {
      while (peek() != '"' && !isAtEnd()) {
         if (peek() == '\n') line++;
         advance();
      }

      if (isAtEnd()) {
         Lox.error(line, "Unterminated string");
         return;
      }

      advance();
      String value = source.substring(start + 1, current - 1);
      addToken(TokenType.STRING, value);
   }

   private Character advance() {
      return source.charAt(current++);
   }

   private void addToken(TokenType type) {
      addToken(type, null);
   }

   private void addToken(TokenType type, Object literal) {
      String text = source.substring(start, current);
      tokens.add(new Token(type, text, literal, line));
   }

   private Boolean match(Character expected) {
      if (isAtEnd()) return false;
      if (source.charAt(current) != expected) return false;

      current++;
      return true;
   }

   private Character peek() {
      if (isAtEnd()) return '\0';
      return source.charAt(current);
   }

   private Character peekNext() {
      if (current + 1 >= source.length()) return '\0';
      return source.charAt(current + 1);
   }

   private Boolean isAlphaNumeric(Character ch) {
      return isAlpha(ch) || isDigit(ch);
   }

   private Boolean isAlpha(Character ch) {
      return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
   }

   private Boolean isDigit(Character ch) {
      return ch >= '0' && ch <= '9';
   }
}
