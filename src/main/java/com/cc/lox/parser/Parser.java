package com.cc.lox.parser;

import com.cc.lox.Lox;
import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.impl.BinaryExpression;
import com.cc.lox.parser.expression.impl.GroupingExpression;
import com.cc.lox.parser.expression.impl.LiteralExpression;
import com.cc.lox.parser.expression.impl.UnaryExpression;
import com.cc.lox.scanner.Token;
import com.cc.lox.scanner.type.TokenType;

import java.util.List;

import static com.cc.lox.scanner.type.TokenType.*;

/**
 * 递归向下的语法解析器
 *
 * @author cc
 * @date 2023/10/10
 */
public class Parser {

    private final List<Token> tokens;

    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * @return 解析好的语法树，如果解析失败则返回null
     */
    public Expression parse() {
        try {
            return expression();
        } catch (ParserException error) {
            return null;
        }
    }

    private ParserException error(Token token, String message) {
        Lox.error(token, message);
        return new ParserException(message);
    }

    /**
     * @param type    token type
     * @param message 信息
     * @return 当前 token
     */
    private Token consumeToken(TokenType type, String message) {
        if (checkCurrent(type)) {
            return peekTokenAndNext();
        }
        throw error(peekToken(), message);
    }

    /**
     * 匹配一个token，如果匹配到了，前进一步
     *
     * @param types token types
     * @return 是否匹配
     */
    private boolean matchCurrentTokenAndNext(TokenType... types) {
        for (TokenType type : types) {
            if (checkCurrent(type)) {
                peekTokenAndNext();
                return true;
            }
        }
        return false;
    }

    /**
     * 消费当前的token 并返回
     *
     * @return 当前的 token
     */
    private Token peekTokenAndNext() {
        if (!isAtEnd()) current++;
        return previousToken();
    }

    /**
     * @return 是否结束
     */
    private boolean isAtEnd() {
        return peekToken().getType() == TokenType.EOF;
    }

    /**
     * @return 当前的token
     */
    private Token peekToken() {
        return tokens.get(current);
    }

    /**
     * @return 前一个 token
     */
    private Token previousToken() {
        return tokens.get(current - 1);
    }

    /**
     * @param type token type
     * @return 当前 token 是否匹配
     */
    private boolean checkCurrent(TokenType type) {
        if (isAtEnd()) return false;
        return peekToken().getType() == type;
    }


    /**
     * 表达式
     * expression -> equality;
     *
     * @return Expression
     */
    private Expression expression() {
        return equality();
    }

    /**
     * 等于
     * equality -> comparison ( ( "!=" | "==" ) comparison )*
     *
     * @return expression
     */
    private Expression equality() {
        Expression expr = comparison();

        while (matchCurrentTokenAndNext(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previousToken();
            Expression right = comparison();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    /**
     * 比较
     * comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
     *
     * @return expression
     */
    private Expression comparison() {
        Expression expr = term();

        while (matchCurrentTokenAndNext(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previousToken();
            Expression right = term();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }


    /**
     * 加减
     * term -> factor ( ( "-" | "+" ) factor )*
     *
     * @return expression
     */
    private Expression term() {
        Expression expr = factor();

        while (matchCurrentTokenAndNext(MINUS, PLUS)) {
            Token operator = previousToken();
            Expression right = factor();
            expr = new BinaryExpression(expr, operator, right);
        }
        return expr;
    }


    /**
     * 乘除
     * factor -> unary ( ( "/" | "*" ) unary )*
     *
     * @return expression
     */
    private Expression factor() {
        Expression expr = unary();

        while (matchCurrentTokenAndNext(SLASH, STAR)) {
            Token operator = previousToken();
            Expression right = unary();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }


    /**
     * 一元运算
     * unary -> ( "!" | "-" ) unary | primary
     *
     * @return expression
     */
    private Expression unary() {
        if (matchCurrentTokenAndNext(BANG, MINUS)) {
            Token operator = previousToken();
            Expression right = unary();
            return new UnaryExpression(operator, right);
        }

        return primary();
    }

    /**
     * 终止符
     * primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
     *
     * @return expression
     */
    private Expression primary() {
        if (matchCurrentTokenAndNext(FALSE)) {
            return new LiteralExpression(false);
        }
        if (matchCurrentTokenAndNext(TRUE)) {
            return new LiteralExpression(true);
        }
        if (matchCurrentTokenAndNext(NIL)) {
            return new LiteralExpression(null);
        }

        if (matchCurrentTokenAndNext(NUMBER, STRING)) {
            return new LiteralExpression(previousToken().getLiteral());
        }

        if (matchCurrentTokenAndNext(LEFT_PAREN)) {
            Expression expr = expression();
            consumeToken(RIGHT_PAREN, "Expect ')' after expression.");
            return new GroupingExpression(expr);
        }
        throw error(peekToken(), "Expect expression.");
    }


    /**
     * 发生错误的时候，需要忽略当前语句，调到下一个语句
     */
    private void synchronize() {
        peekTokenAndNext();

        while (!isAtEnd()) {
            if (previousToken().getType() == SEMICOLON) return;

            switch (peekToken().getType()) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            peekTokenAndNext();
        }
    }
}
