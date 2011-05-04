/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.weel;

/**
 * Weel language tokens.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
enum Token
{
    NONE, EOF,

    NAME, STRING, NUMBER, RESERVED,

    ADD, SUB, MUL, DIV, MODULO,

    BINARY_OR, BINARY_AND, BINARY_XOR, BINARY_NOT,

    ASSIGN,

    ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MODULO, ASSIGN_AND, ASSIGN_OR, ASSIGN_XOR, ASSIGN_STRCAT,

    EQUAL, NOT_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    LOGICAL_OR, LOGICAL_AND, LOGICAL_NOT,

    STRING_CONCAT,

    DOT, SEMICOLON, COLON, DOUBLE_COLON,
    TERNARY, COMMA, ARROW,

    AT, ANON_OPEN,
    
    BRACE_OPEN, BRACE_CLOSE, CURLY_BRACE_OPEN, CURLY_BRACE_CLOSE, BRACKET_OPEN, BRACKET_CLOSE,
}
