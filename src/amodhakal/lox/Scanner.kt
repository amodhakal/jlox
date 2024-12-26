package amodhakal.lox

import com.sun.org.apache.bcel.internal.generic.RETURN
import com.sun.tools.example.debug.expr.ExpressionParserConstants.ELSE
import com.sun.tools.example.debug.expr.ExpressionParserConstants.FOR
import com.sun.tools.example.debug.expr.ExpressionParserConstants.IF
import com.sun.tools.example.debug.expr.ExpressionParserConstants.WHILE
import com.sun.tools.javac.code.Kinds.KindSelector.NIL
import java.util.HashMap


class Scanner internal constructor(private val source: String) {
    private val tokens: MutableList<Token> = ArrayList()
    private var start = 0
    private var current = 0
    private var line = 1

    private var keywords: MutableMap<String, TokenType> = initKeywords()

    private fun initKeywords(): MutableMap<String, TokenType> {
        val keywords = HashMap<String, TokenType>()
        keywords["and"] = TokenType.AND;
        keywords["class"] = TokenType.CLASS;
        keywords["else"] = TokenType.ELSE;
        keywords["false"] = TokenType.FALSE;
        keywords["for"] = TokenType.FOR;
        keywords["fun"] = TokenType.FUN;
        keywords["if"] = TokenType.IF;
        keywords["nil"] = TokenType.NIL;
        keywords["or"] = TokenType.OR;
        keywords["print"] = TokenType.PRINT;
        keywords["return"] = TokenType.RETURN;
        keywords["super"] = TokenType.SUPER;
        keywords["this"] = TokenType.THIS;
        keywords["true"] = TokenType.TRUE;
        keywords["var"] = TokenType.VAR;
        keywords["while"] = TokenType.WHILE;
        return keywords
    }


    fun scanTokens(): List<Token> {
        while (!isAtEnd) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private val isAtEnd: Boolean
        get() = current >= source.length

    private fun scanToken() {
        when (val ch = advance()) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)

            ' ', '\r', '\t' -> return
            '\n' -> line++

            '"' -> string()

            '/' -> if (match('/')) {
                while (peek() != '\n' && !isAtEnd) advance()
            } else {
                addToken(TokenType.SLASH)
            }

            else -> if (isDigit(ch)) {
                number()
            } else if (isAlpha(ch)) {
                identifier()
            } else {
                Lox.error(line, "Unexpected character.")
            }
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()
        val text = source.substring(start, current)
        var type = keywords[text]
        if (type == null) type = TokenType.IDENTIFIER
        addToken(type)
    }

    private fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()
            while (isDigit(peek())) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun string() {
        while (peek() != '"' && isAtEnd) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd) {
            Lox.error(line, "Unterminated string")
            return
        }

        advance()
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd) return '\u0000'
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun isAlpha(ch: Char): Boolean {
        return (ch in 'a'..'z') || (ch in 'A'..'Z') || ch == '_'
    }

    private fun isAlphaNumeric(ch: Char): Boolean {
        return isAlpha(ch) || isDigit(ch)
    }

    private fun isDigit(ch: Char): Boolean {
        return ch in '0'..'9'
    }
}
