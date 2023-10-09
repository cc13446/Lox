package com.cc.lox.parser.expression;

public abstract class Expression {
    public abstract <R> R accept(ExpressionVisitor<R> visitor);
}
