package com.cc.lox.parser.printer;

import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.ExpressionVisitor;
import com.cc.lox.parser.expression.impl.*;

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
    public String visitBinaryExpression(BinaryExpression expression) {
        return parenthesize(expression.getOperator().getLexeme(), expression.getLeft(), expression.getRight());
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
    public String visitUnaryExpression(UnaryExpression expression) {
        return parenthesize(expression.getOperator().getLexeme(), expression.getRight());
    }

    @Override
    public String visitVariableExpression(VariableExpression expression) {
        return parenthesize(expression.getName().getLexeme());
    }
}
