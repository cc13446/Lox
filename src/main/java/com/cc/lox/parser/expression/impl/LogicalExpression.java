package com.cc.lox.parser.expression.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.ExpressionVisitor;
import com.cc.lox.scanner.Token;
import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.impl.*;

@AllArgsConstructor
@Getter
public class LogicalExpression extends Expression {

    private final Expression left;
    private final Token operator;
    private final Expression right;

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitLogicalExpression(this);
    }
}
