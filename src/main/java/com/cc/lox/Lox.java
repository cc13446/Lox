package com.cc.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.StringTokenizer;

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
            run(line);
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
        StringTokenizer tokenizer = new StringTokenizer(script, "\n");
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();
            System.out.println(line);
            if (hadError) {
                System.exit(65);
            }
        }
    }

    /**
     * @param line    行号
     * @param message 信息
     */
    public static void report(int line, String message) {
        System.err.println("[line " + line + "] Error " + ": " + message);
        hadError = true;
    }


}
