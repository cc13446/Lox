package com.cc.lox.token;

import com.cc.lox.Lox;
import com.cc.lox.token.type.TokenMatchFunction;
import com.cc.lox.token.type.TokenMetaType;
import com.cc.lox.token.type.TokenType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cc.lox.token.type.TokenType.*;

/**
 * 分词扫描器
 *
 * @author cc
 */
public class Scanner {

    private static final Function<Character, Boolean> IS_DIGIT = (c) -> c >= '0' && c <= '9';

    private static final Function<Character, Boolean> IS_ALPHA = c -> (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';

    private static final Function<Character, Boolean> IS_ALPHA_NUMERIC = c -> IS_ALPHA.apply(c) || IS_DIGIT.apply(c);

    /**
     * 不做匹配
     */
    public static final TokenMatchFunction<Scanner, String, Boolean> FALSE_MATCH = (f, c) -> false;

    /**
     * 默认匹配全部
     */
    public static final TokenMatchFunction<Scanner, String, Boolean> DEFAULT_MATCH = (scanner, code) -> {
        for (char c : code.toCharArray()) {
            if (!scanner.matchCurrentCharAndNext(c)) {
                return false;
            }
        }
        return true;
    };

    /**
     * 下一个不可以是 noMore 的匹配
     */
    public static TokenMatchFunction<Scanner, String, Boolean> getNoMoreMatch(char noMore) {
        return (scanner, code) -> {
            for (char c : code.toCharArray()) {
                if (!scanner.matchCurrentCharAndNext(c)) {
                    return false;
                }
            }
            boolean more = scanner.matchCurrentCharAndNext(noMore);
            return !more;
        };
    }

    /**
     * 注释的匹配
     */
    public static final TokenMatchFunction<Scanner, String, Boolean> COMMIT_MATCH = (scanner, code) -> {
        for (char c : code.toCharArray()) {
            if (!scanner.matchCurrentCharAndNext(c)) {
                return false;
            }
        }
        boolean inCommit = true;
        while (inCommit) {
            inCommit = !scanner.matchCurrentCharAndNext('\n') && !scanner.isAtEnd();
        }
        return true;
    };

    /**
     * 字符串的匹配
     */
    public static final TokenMatchFunction<Scanner, String, Boolean> STRING_MATCH = (scanner, code) -> {
        char sign = '"';
        if (!scanner.matchCurrentCharAndNext(sign)) {
            return false;
        }
        char cur;
        while ((cur = scanner.peekCharAndNext()) != sign && !scanner.isAtEnd()) {
            if (cur == '\n') {
                scanner.line++;
            }
        }

        if (scanner.isAtEnd()) {
            Lox.error(scanner.line, "Unterminated string.");
            return true;
        }
        return true;
    };


    /**
     * 数字的匹配
     */
    public static final TokenMatchFunction<Scanner, String, Boolean> NUMBER_MATCH = (scanner, code) -> {

        if (!IS_DIGIT.apply(scanner.peekCharAndNext())) {
            return false;
        }

        while (IS_DIGIT.apply(scanner.peekChar())) {
            scanner.peekCharAndNext();
        }

        // Look for a fractional part.
        if (scanner.peekChar() == '.' && IS_DIGIT.apply(scanner.peekNextChar())) {
            // Consume the "."
            scanner.peekCharAndNext();
            while (IS_DIGIT.apply(scanner.peekChar())) {
                scanner.peekCharAndNext();
            }
        }
        return true;
    };


    /**
     * 变量的匹配
     */
    public static final TokenMatchFunction<Scanner, String, Boolean> IDENTIFIER_MATCH = (scanner, code) -> {
        if (!IS_ALPHA.apply(scanner.peekChar())) {
            return false;
        }

        if (IS_DIGIT.apply(scanner.peekChar())) {
            return false;
        }
        // consume the first
        scanner.peekCharAndNext();
        while (IS_ALPHA_NUMERIC.apply(scanner.peekChar())) {
            scanner.peekCharAndNext();
        }
        return true;
    };

    private static final Map<String, TokenType> KEY_WORD_MAP = Arrays.stream(values())
            .filter(t -> t.getType() == TokenMetaType.KEYWORD)
            .collect(Collectors.toMap(TokenType::getCode, t -> t));

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    /**
     * 扫描开始的地方
     */
    private int start = 0;

    /**
     * 当前扫描到的地方
     */
    private int current = 0;

    /**
     * 行号
     */
    private int line = 1;

    /**
     * @param source 源文本
     */
    public Scanner(String source) {
        this.source = source;
    }

    /**
     * @return 扫描结果
     */
    public List<Token> getResult() {
        return List.copyOf(tokens);
    }

    /**
     * 扫描分词
     */
    public void scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanNextToken();
        }

        tokens.add(new Token(EOF, "", null, line));
    }

    /**
     * @return 是否扫描结束
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * 扫描下一个token
     */
    private void scanNextToken() {
        TokenType result = null;
        for (TokenType type : TokenType.values()) {
            final int current = this.current;
            if (type.match(this)) {
                result = type;
                break;
            }
            this.current = current;
        }
        if (Objects.isNull(result)) {
            Lox.error(line, "Unexpected character.");
            // The error char must be consumed;
            peekCharAndNext();
        } else if (result.getType() == TokenMetaType.SPLIT) {
            if (result == SPLASH_N) {
                line++;
            }
        } else if (result == STRING) {
            String value = source.substring(start + 1, current - 1);
            addToken(result, value);
        } else if (result == NUMBER) {
            addToken(result, Double.parseDouble(source.substring(start, current)));
        } else if (result == IDENTIFIER) {
            String text = source.substring(start, current);
            TokenType type = KEY_WORD_MAP.get(text);
            if (Objects.nonNull(type)) {
                result = type;
            }
            addToken(result);
        } else {
            addToken(result);
        }

    }

    /**
     * @param type token 类型
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * @param type    token 类型
     * @param literal 值
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    /**
     * 如果字符匹配则前进一步
     *
     * @param expected 期望的字符
     * @return 是否匹配
     */
    private boolean matchCurrentCharAndNext(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (this.source.charAt(this.current) != expected) {
            return false;
        }

        this.current++;
        return true;
    }

    /**
     * 前进一步
     *
     * @return 当前 char
     */
    private char peekCharAndNext() {
        char res = peekChar();
        current++;
        return res;
    }

    /**
     * @return 当前的 char, 结尾返回 \0
     */
    private char peekChar() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }


    /**
     * @return 下一个的 char, 结尾返回 \0
     */
    private char peekNextChar() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }
}
