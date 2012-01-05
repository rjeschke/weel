/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    ADD, SUB, MUL, DIV, MODULO, POW,

    BINARY_OR, BINARY_AND, BINARY_XOR, BINARY_NOT, SHL, SHR, USHR,

    ASSIGN,

    ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MODULO,

    ASSIGN_AND, ASSIGN_OR, ASSIGN_XOR, ASSIGN_STRCAT, ASSIGN_MAPCAT,

    ASSIGN_SHL, ASSIGN_SHR, ASSIGN_USHR,

    EQUAL, NOT_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    LOGICAL_OR, LOGICAL_AND, LOGICAL_NOT,

    STRING_CONCAT, MAP_CONCAT,

    DOT, SEMICOLON, COLON, DOUBLE_COLON, TERNARY, COMMA, ARROW,

    AT, ANON_OPEN, 

    BRACE_OPEN, BRACE_CLOSE, CURLY_BRACE_OPEN, CURLY_BRACE_CLOSE, BRACKET_OPEN, BRACKET_CLOSE,
}
