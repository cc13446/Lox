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
public class VariableExpression extends Expression {

    private final Token name;

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitVariableExpression(this);
    }
}
