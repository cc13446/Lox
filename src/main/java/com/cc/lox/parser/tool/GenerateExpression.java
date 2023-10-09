package com.cc.lox.parser.tool;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 生成Expression
 *
 * @author cc
 * @date 2023/10/9
 */
@Slf4j
public class GenerateExpression {

    private final static String COLON = ":";

    public static void main(String[] args) throws IOException {
        String outputDirPath = "src/main/java/com/cc/lox/parser/expression/impl";

        // 清空文件夹
        clearOutputDirector(outputDirPath);

        // 清空visitor
        String expressionPath = "src/main/java/com/cc/lox/parser/expression/Expression.java";
        if (!new File(expressionPath).delete()) {
            log.warn("Delete expression fail");
        }

        // 清空visitor
        String visitorPath = "src/main/java/com/cc/lox/parser/expression/ExpressionVisitor.java";
        if (!new File(visitorPath).delete()) {
            log.warn("Delete visitor fail");
        }

        String baseName = "Expression";
        List<String> types = Arrays.asList(
                "Binary   : Expression left, Token operator, Expression right",
                "Grouping : Expression expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expression right"
        );

        defineBaseExpression(expressionPath);
        defineVisitor(visitorPath, baseName, types);
        defineExpression(outputDirPath, baseName, types);
    }

    private static void defineBaseExpression(String expressionPath) throws IOException {
        PrintWriter writer = new PrintWriter(expressionPath, StandardCharsets.UTF_8);
        writer.println("package com.cc.lox.parser.expression;");
        writer.println();
        writer.println("public abstract class Expression {");
        writer.println("    public abstract <R> R accept(ExpressionVisitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(String visitorPath, String baseName, List<String> types) throws IOException {
        PrintWriter writer = new PrintWriter(visitorPath, StandardCharsets.UTF_8);
        writer.println("package com.cc.lox.parser.expression;");
        writer.println();
        for (String type : types) {
            String typeName = type.split(COLON)[0].trim();
            writer.println("import com.cc.lox.parser.expression.impl." + typeName + baseName + ";");
        }

        writer.println();
        writer.println("public interface ExpressionVisitor<R> {");
        for (String type : types) {
            String typeName = type.split(COLON)[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + baseName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("}");
        writer.close();
    }

    private static void clearOutputDirector(String outputDirPath) {
        File dir = new File(outputDirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.warn("create dir fail {}", dir.getAbsolutePath());
                System.exit(-1);
            }
        }
        File[] clearFiles = dir.listFiles();
        if (Objects.isNull(clearFiles)) {
            log.error("{} is not a director", dir.getAbsolutePath());
            System.exit(-2);
        }
        for (File f : clearFiles) {
            if (!f.delete()) {
                log.error("{} can not be delete", f.getAbsolutePath());
                System.exit(-2);
            }
        }
    }

    private static void defineExpression(String outputDir, String baseName, List<String> types) throws IOException {
        for (String type : types) {
            String className = type.split(COLON)[0].trim();
            String fields = type.split(COLON)[1].trim();
            String path = outputDir + "/" + className + baseName + ".java";
            PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);
            writer.println("package com.cc.lox.parser.expression.impl;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println("import lombok.AllArgsConstructor;");
            writer.println("import lombok.Getter;");
            writer.println();
            writer.println("import com.cc.lox.parser.expression.Expression;");
            writer.println("import com.cc.lox.parser.expression.ExpressionVisitor;");
            writer.println("import com.cc.lox.scanner.Token;");
            writer.println();
            writer.println("@AllArgsConstructor");
            writer.println("@Getter");
            writer.println("public class " + className + baseName + " extends Expression {");
            defineType(writer, fields);
            writer.println("    @Override");
            writer.println("    public <R> R accept(ExpressionVisitor<R> visitor) {");
            writer.println("        return visitor.visit" + className + baseName + "(this);");
            writer.println("    }");
            writer.println("}");
            writer.close();
        }
    }

    private static void defineType(PrintWriter writer, String fields) {
        String[] fieldList = fields.split(", ");

        // Fields.
        writer.println();
        for (String field : fieldList) {
            writer.println("    private final " + field + ";");
        }
        writer.println();
    }
}
