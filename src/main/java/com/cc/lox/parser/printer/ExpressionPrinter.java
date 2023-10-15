package com.cc.lox.parser.printer;

import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.ExpressionVisitor;
import com.cc.lox.parser.expression.impl.*;
import com.cc.lox.scanner.type.TokenType;

/**
 * @author cc
 * @date 2023/10/9
 */
public class ExpressionPrinter implements ExpressionVisitor<String> {

    public String print(Expression expr) {
        return expr.accept(this);
    }

    private String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expr : expressions) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }


    @Override
    public String visitAssignExpression(AssignExpression expression) {
        return parenthesize(expression.getName().getLexeme(), expression.getValue());
    }

    @Override
    public String visitBinaryExpression(BinaryExpression expression) {
        return parenthesize(expression.getOperator().getLexeme(), expression.getLeft(), expression.getRight());
    }

    @Override
    public String visitCallExpression(CallExpression expression) {
        Expression[] expressions = new Expression[expression.getArguments().size() + 1];
        expressions[0] = expression.getCallee();
        for (int i = 0; i < expression.getArguments().size(); i++) {
            expressions[i + 1] = expression.getArguments().get(i);
        }

        return parenthesize("call", expressions);
    }

    @Override
    public String visitGetExpression(GetExpression expression) {
        return parenthesize(expression.getName().getLexeme(), expression.getObject());
    }

    @Override
    public String visitGroupingExpression(GroupingExpression expression) {
        return parenthesize("group", expression.getExpression());
    }

    @Override
    public String visitLiteralExpression(LiteralExpression expression) {
        if (expression.getValue() == null) {
            return "nil";
        }
        return expression.getValue().toString();
    }

    @Override
    public String visitLogicalExpression(LogicalExpression expression) {
        return parenthesize(expression.getOperator().getType().name(), expression.getLeft(), expression.getRight());
    }

    @Override
    public String visitSetExpression(SetExpression expression) {
        return parenthesize(expression.getName().getLexeme(), expression.getObject(), expression.getValue());
    }

    @Override
    public String visitSuperExpression(SuperExpression expression) {
        return parenthesize(expression.getKeyword().getLexeme() + TokenType.DOT.getCode() +  expression.getMethod().getLexeme());
    }

    @Override
    public String visitThisExpression(ThisExpression expression) {
        return parenthesize(expression.getKeyword().getLexeme());
    }

    @Override
    public String visitUnaryExpression(UnaryExpression expression) {
        return parenthesize(expression.getOperator().getLexeme(), expression.getRight());
    }

    @Override
    public String visitVariableExpression(VariableExpression expression) {
        return parenthesize(expression.getName().getLexeme());
    }
}
