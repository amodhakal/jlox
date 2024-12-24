package amodhakal.lox

class Scanner internal constructor(private val source: String) {
    private val tokens: MutableList<Token> = ArrayList()
    private var start = 0
    private val current = 0
    private val line = 1

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
}