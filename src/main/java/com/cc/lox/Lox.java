package com.cc.lox;

import com.cc.lox.parser.Parser;
import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.printer.ExpressionPrinter;
import com.cc.lox.scanner.Scanner;
import com.cc.lox.scanner.Token;
import com.cc.lox.scanner.type.TokenType;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * @author cc
 * @date 2023/10/8
 */
public class Lox {

    private static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        System.out.println("Lox start");
        if (args.length > 1) {
            System.out.println("Usage jLox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runScript(args[0]);
        } else {
            runPrompt();
        }
    }


    /**
     * 交互式运行
     *
     * @throws IOException io 错误
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print("> ");
            String line = reader.readLine();
            if (Objects.isNull(line)) {
                break;
            }
            if (StringUtils.isNoneBlank(line)) {
                run(line);
            }
            hadError = false;
        }
    }

    /**
     * 脚本式运行
     *
     * @param script 脚本
     * @throws IOException io 错误
     */
    private static void runScript(String script) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(script));
        run(new String(bytes, Charset.defaultCharset()));
    }

    /**
     * 正式运行
     *
     * @param script 脚本
     */
    private static void run(String script) {
        // 扫猫
        Scanner scanner = new Scanner(script);
        List<Token> tokens = scanner.scanTokens();

        // 解析
        Parser parser = new Parser(tokens);
        Expression expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) {
            return;
        }
        System.out.println(new ExpressionPrinter().print(expression));
    }

    /**
     * @param line    行号
     * @param message 信息
     */
    public static void error(int line, String message) {
        error(line, " ", message);
    }

    /**
     * @param line    行号
     * @param where   where
     * @param message 信息
     */
    public static void error(int line, String where, String message) {
        System.err.println("[line " + line + "]" + where + "Error " + ": " + message);
    }

    /**
     * @param token   token
     * @param message 信息
     */
    public static void error(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            error(token.getLine(), " at end", message);
        } else {
            error(token.getLine(), " at '" + token.getLexeme() + "'", message);
        }
    }

}
