package com.cc.lox.resolve;

import com.cc.lox.Lox;
import com.cc.lox.function.FunctionType;
import com.cc.lox.interpreter.LoxInterpreter;
import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.ExpressionVisitor;
import com.cc.lox.parser.expression.impl.*;
import com.cc.lox.parser.statement.Statement;
import com.cc.lox.parser.statement.StatementVisitor;
import com.cc.lox.parser.statement.impl.*;
import com.cc.lox.scanner.Token;

import java.util.*;

/**
 * 记录每个变量在环境中的位置
 *
 * @author cc
 * @date 2023/10/13
 */
public class Resolver implements ExpressionVisitor<Void>, StatementVisitor<Void> {

    private final LoxInterpreter interpreter;

    /**
     * 标识当前是否在函数里
     */
    private FunctionType currentFunction = FunctionType.NONE;

    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    public Resolver(LoxInterpreter interpreter) {
        this.interpreter = interpreter;
        beginScope();
    }

    /**
     * 开始一个作用域
     */
    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * 离开一个作用域
     */
    private void endScope() {
        scopes.pop();
    }

    /**
     * @param statements statement
     */
    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    /**
     * @param statement statement
     */
    private void resolve(Statement statement) {
        statement.accept(this);
    }

    /**
     * @param expression expression
     */
    private void resolve(Expression expression) {
        expression.accept(this);
    }

    /**
     * 记录一个表达式的位置
     *
     * @param expression expression
     * @param name       token
     */
    private void resolveLocal(Expression expression, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.setLocal(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }

    /**
     * 声明一个token
     *
     * @param name token
     */
    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        if (scopes.peek().containsKey(name.getLexeme())) {
            Lox.error(name, "Already variable with this name in this scope.");
        }
        scopes.peek().put(name.getLexeme(), false);
    }

    /**
     * 定义一个token
     *
     * @param name token
     */
    private void define(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        scopes.peek().put(name.getLexeme(), true);
    }

    /**
     * 解析一个函数
     * @param function function
     * @param type type
     */
    private void resolveFunction(FunctionStatement function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.getParams()) {
            declare(param);
            define(param);
        }
        resolve(function.getBody());
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitAssignExpression(AssignExpression expression) {
        resolve(expression.getValue());
        resolveLocal(expression, expression.getName());
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression expression) {
        resolve(expression.getLeft());
        resolve(expression.getRight());
        return null;
    }

    @Override
    public Void visitCallExpression(CallExpression expression) {
        resolve(expression.getCallee());
        for (Expression expr : expression.getArguments()) {
            resolve(expr);
        }
        return null;
    }

    @Override
    public Void visitGroupingExpression(GroupingExpression expression) {
        resolve(expression.getExpression());
        return null;
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression expression) {
        return null;
    }

    @Override
    public Void visitLogicalExpression(LogicalExpression expression) {
        resolve(expression.getLeft());
        resolve(expression.getRight());
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression expression) {
        resolve(expression.getRight());
        return null;
    }

    @Override
    public Void visitVariableExpression(VariableExpression expression) {
        if (!scopes.isEmpty() && Boolean.FALSE.equals(scopes.peek().get(expression.getName().getLexeme()))) {
            Lox.error(expression.getName(), "Can't read local variable in its own initializer.");
        }

        resolveLocal(expression, expression.getName());
        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {
        beginScope();
        resolve(statement.getStatements());
        endScope();
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {
        declare(statement.getName());
        define(statement.getName());

        resolveFunction(statement, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(statement.getKeyword(), "Can't return from top-level code.");
        }
        if (Objects.nonNull(statement.getValue())) {
            resolve(statement.getValue());
        }
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement statement) {
        resolve(statement.getCondition());
        resolve(statement.getThenBranch());
        if (Objects.nonNull(statement.getElseBranch())) {
            resolve(statement.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement statement) {
        resolve(statement.getCondition());
        resolve(statement.getBody());
        return null;
    }

    @Override
    public Void visitVarStatement(VarStatement statement) {
        if (Objects.nonNull(statement.getInitializer())) {
            resolve(statement.getInitializer());
        }
        declare(statement.getName());
        define(statement.getName());
        return null;
    }
}
