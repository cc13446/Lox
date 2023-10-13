package com.cc.lox.interpreter;

import com.cc.lox.Lox;
import com.cc.lox.environment.Environment;
import com.cc.lox.function.LoxCallable;
import com.cc.lox.function.impl.LoxFunction;
import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.ExpressionVisitor;
import com.cc.lox.parser.expression.impl.*;
import com.cc.lox.parser.statement.Statement;
import com.cc.lox.parser.statement.StatementVisitor;
import com.cc.lox.parser.statement.impl.*;
import com.cc.lox.scanner.Token;
import com.cc.lox.scanner.type.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 计算表达式
 *
 * @author cc
 * @date 2023/10/10
 */
public class LoxInterpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    private final Environment globals = new Environment();
    private Environment environment = new Environment(globals);

    private final StringBuilder print = new StringBuilder();

    public LoxInterpreter() {
        globals.define(new Token(TokenType.FUN, "clock", "clock", -1), new LoxCallable() {
            @Override
            public int getArity() {
                return 0;
            }

            @Override
            public Object call(LoxInterpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "global fun clock";
            }
        });
    }

    public String getPrint() {
        return print.toString();
    }

    /**
     * 执行语句
     *
     * @param statements 语句
     */
    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    /**
     * 执行一个语法快
     *
     * @param statements  语句
     * @param environment 当前环境
     */
    public void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStatement(BlockStatement statement) {
        Environment newEnv = new Environment(this.environment);
        executeBlock(statement.getStatements(), newEnv);
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        evaluate(statement.getExpression());
        return null;
    }

    @Override
    public Void visitFunctionStatement(FunctionStatement statement) {
        LoxFunction function = new LoxFunction(statement, environment.toClosure());
        environment.define(statement.getName(), function);
        return null;
    }

    @Override
    public Void visitReturnStatement(ReturnStatement statement) {
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement statement) {
        if (isTruthy(evaluate(statement.getCondition()))) {
            execute(statement.getThenBranch());
        } else if (Objects.nonNull(statement.getElseBranch())) {
            execute(statement.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement statement) {
        Object value = evaluate(statement.getExpression());
        String out = stringify(value);
        System.out.println(out);
        this.print.append(out);
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement statement) {
        while (isTruthy(evaluate(statement.getCondition()))) {
            execute(statement.getBody());
        }
        return null;
    }

    @Override
    public Void visitVarStatement(VarStatement statement) {

        if (Objects.isNull(statement.getInitializer())) {
            this.environment.define(statement.getName(), null);
            return null;
        }
        this.environment.define(statement.getName(), evaluate(statement.getInitializer()));
        return null;
    }

    @Override
    public Object visitAssignExpression(AssignExpression expression) {
        Object value = evaluate(expression.getValue());
        environment.assign(expression.getName(), value);
        return value;
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression expression) {
        Object left = evaluate(expression.getLeft());
        Object right = evaluate(expression.getRight());

        switch (expression.getOperator().getType()) {
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expression.getOperator(), left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expression.getOperator(), left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expression.getOperator(), left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expression.getOperator(), left, right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperands(expression.getOperator(), left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(expression.getOperator(), left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expression.getOperator(), left, right);
                return (double) left * (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return left + (String) right;
                }
                throw new RuntimeError(expression.getOperator(), "Operands must be two numbers or two strings.");
            default:
                throw new RuntimeError(expression.getOperator(), "Unknown binaryExpression token");
        }
    }

    @Override
    public Object visitCallExpression(CallExpression expression) {
        Object callee = evaluate(expression.getCallee());

        List<Object> arguments = new ArrayList<>();
        for (Expression argument : expression.getArguments()) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expression.getParen(), "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable) callee;
        if (arguments.size() != function.getArity()) {
            throw new RuntimeError(expression.getParen(), "Expected " + function.getArity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGroupingExpression(GroupingExpression expression) {
        return evaluate(expression.getExpression());
    }

    @Override
    public Object visitLiteralExpression(LiteralExpression expression) {
        return expression.getValue();
    }

    @Override
    public Object visitLogicalExpression(LogicalExpression expression) {
        Object left = evaluate(expression.getLeft());

        if (expression.getOperator().getType() == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }

        return evaluate(expression.getRight());
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression expression) {
        Object right = evaluate(expression.getRight());

        switch (expression.getOperator().getType()) {
            case MINUS:
                checkNumberOperand(expression.getOperator(), right);
                return -(double) right;
            case BANG:
                return !isTruthy(right);
            default:
                throw new RuntimeError(expression.getOperator(), "Unknown unaryExpression token");
        }

    }

    @Override
    public Object visitVariableExpression(VariableExpression expression) {
        return this.environment.get(expression.getName());
    }

    /**
     * @param object value
     * @return 字符串
     */
    private String stringify(Object object) {
        if (Objects.isNull(object)) {
            return "nil";
        }

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    /**
     * @param operator token
     * @param operand  value
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    /**
     * @param operator token
     * @param left     left
     * @param right    right
     */
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }


    /**
     * @param a a
     * @param b b
     * @return 两个对象是否相等
     */
    private boolean isEqual(Object a, Object b) {
        if (Objects.isNull(a) && Objects.isNull(b)) {
            return true;
        }
        if (Objects.isNull(a)) {
            return false;
        }

        return a.equals(b);
    }

    /**
     * @param object 值
     * @return 是否为真
     */
    private boolean isTruthy(Object object) {
        if (Objects.isNull(object)) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        return true;
    }

    /**
     * 对表达式求值
     *
     * @param expression expression
     * @return value
     */
    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }


    /**
     * 执行语句
     *
     * @param statement 语句
     */
    private void execute(Statement statement) {
        statement.accept(this);
    }

}
