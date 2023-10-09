package com.cc.lox.parser.expression.impl;

import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.ExpressionVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupingExpression extends Expression {

    private final Expression expression;

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitGroupingExpression(this);
    }
}
