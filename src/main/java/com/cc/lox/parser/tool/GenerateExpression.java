package com.cc.lox.parser.tool;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        Map<String, List<String>> all = new HashMap<>();

        all.put("Expression", Arrays.asList(
                "Assign   : Token name, Expression value",
                "Binary   : Expression left, Token operator, Expression right",
                "Grouping : Expression expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expression right",
                "Variable : Token name"
        ));

        all.put("Statement", Arrays.asList(
                "Expression : Expression expression",
                "Print      : Expression expression",
                "Var        : Token name, Expression initializer"
        ));

        for (Map.Entry<String, List<String>> entry : all.entrySet()) {
            String baseName = entry.getKey();

            String implDirPath = "src/main/java/com/cc/lox/parser/" + baseName.toLowerCase() + "/impl";
            String basePath = "src/main/java/com/cc/lox/parser/" + baseName.toLowerCase() + "/" + baseName + ".java";
            String visitorPath = "src/main/java/com/cc/lox/parser/" + baseName.toLowerCase() + "/" + baseName + "Visitor.java";

            clearOutputDirector(implDirPath);
            clearFile(basePath);
            clearFile(visitorPath);

            defineBase(basePath, baseName);
            defineVisitor(visitorPath, baseName, entry.getValue());
            defineImpl(implDirPath, baseName, entry.getValue());
        }

    }

    private static void defineBase(String expressionPath, String baseName) throws IOException {
        PrintWriter writer = new PrintWriter(expressionPath, StandardCharsets.UTF_8);
        writer.println("package com.cc.lox.parser." + baseName.toLowerCase() + ";");
        writer.println();
        writer.println("public abstract class " + baseName + " {");
        writer.println("    public abstract <R> R accept(" + baseName + "Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(String visitorPath, String baseName, List<String> types) throws IOException {
        PrintWriter writer = new PrintWriter(visitorPath, StandardCharsets.UTF_8);
        writer.println("package com.cc.lox.parser." + baseName.toLowerCase() + ";");
        writer.println();
        for (String type : types) {
            String typeName = type.split(COLON)[0].trim();
            writer.println("import com.cc.lox.parser." + baseName.toLowerCase() + ".impl." + typeName + baseName + ";");
        }

        writer.println();
        writer.println("public interface " + baseName + "Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(COLON)[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + baseName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("}");
        writer.close();
    }

    private static void defineImpl(String outputDir, String baseName, List<String> types) throws IOException {
        for (String type : types) {
            String className = type.split(COLON)[0].trim();
            String fields = type.split(COLON)[1].trim();
            String path = outputDir + "/" + className + baseName + ".java";
            PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);
            writer.println("package com.cc.lox.parser." + baseName.toLowerCase() + ".impl;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println("import lombok.AllArgsConstructor;");
            writer.println("import lombok.Getter;");
            writer.println();
            writer.println("import com.cc.lox.parser." + baseName.toLowerCase() + "." + baseName + ";");
            writer.println("import com.cc.lox.parser." + baseName.toLowerCase() + "." + baseName + "Visitor;");
            writer.println("import com.cc.lox.scanner.Token;");
            writer.println("import com.cc.lox.parser.expression.Expression;");
            writer.println();
            writer.println("@AllArgsConstructor");
            writer.println("@Getter");
            writer.println("public class " + className + baseName + " extends " + baseName + " {");
            defineType(writer, fields);
            writer.println("    @Override");
            writer.println("    public <R> R accept(" + baseName + "Visitor<R> visitor) {");
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

    private static void clearFile(String filePath) {
        File file = new File(filePath);
        if (!file.delete()) {
            log.warn("Delete {} fail", file.getAbsolutePath());
        }
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
}
