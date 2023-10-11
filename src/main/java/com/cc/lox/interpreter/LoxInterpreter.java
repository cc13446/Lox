package com.cc.lox.interpreter;

import com.cc.lox.Lox;
import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.ExpressionVisitor;
import com.cc.lox.parser.expression.impl.BinaryExpression;
import com.cc.lox.parser.expression.impl.GroupingExpression;
import com.cc.lox.parser.expression.impl.LiteralExpression;
import com.cc.lox.parser.expression.impl.UnaryExpression;
import com.cc.lox.parser.statement.Statement;
import com.cc.lox.parser.statement.StatementVisitor;
import com.cc.lox.parser.statement.impl.ExpressionStatement;
import com.cc.lox.parser.statement.impl.PrintStatement;
import com.cc.lox.scanner.Token;

import java.util.List;
import java.util.Objects;

/**
 * 计算表达式
 *
 * @author cc
 * @date 2023/10/10
 */
public class LoxInterpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    private final StringBuilder print = new StringBuilder();

    public String getPrint() {
        return print.toString();
    }

    public void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatement statement) {
        evaluate(statement.getExpression());
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
    public Object visitGroupingExpression(GroupingExpression expression) {
        return evaluate(expression.getExpression());
    }

    @Override
    public Object visitLiteralExpression(LiteralExpression expression) {
        return expression.getValue();
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
     * @param statement 语句
     */
    private void execute(Statement statement) {
        statement.accept(this);
    }

}
