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

import java.io.IOException;
import java.io.Reader;

/**
 * Straightforward, non RegExp, hand written tokenizer.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class Tokenizer
{
    /** Unary operator priority. */
    final static int UOPR_PRIORITY = 15;
    /** The input stream: */
    private Reader reader;
    /** Builder for names/strings and numbers. */
    private StringBuilder builder = new StringBuilder();
    /** Current input char. */
    private int current = ' ';
    /** Current line number. */
    private int lineNumber = 1;
    /** Current filename. */
    private String filename;
    /** The current token. */
    Token token;
    /** The ungot token. */
    Token ungot = null;

    /** Tokenized reserved word. */
    ReservedWord reserved = null;
    /** Tokenized string or name. */
    String string;
    /** Tokenized number. */
    double number;

    /**
     * Constructor.
     * 
     * @param reader
     *            Input stream.
     * @param filename
     *            The filename.
     */
    public Tokenizer(final Reader reader, final String filename)
    {
        this.reader = reader;
        this.filename = filename;
    }

    /**
     * Build an error message containing line number and file name.
     * 
     * @param message
     *            The message.
     * @param args
     *            Optional arguments.
     * @return The error message.
     * @see java.lang.String#format(String, Object...)
     */
    public String error(String message, Object... args)
    {
        String m = (args.length > 0) ? String.format(message, args) : message;

        if (this.filename != null)
            m += " in '" + this.filename + "'";
        return m + " around line " + this.lineNumber;
    }

    /**
     * Reads the next character from the input stream.
     * 
     * @return The next character.
     * @throws IOException
     *             If an IO error occurred.
     */
    private int read() throws IOException
    {
        return this.current = this.reader.read();
    }

    /**
     * Reads a number.
     * 
     * @param wasDot
     *            <code>true</code> if this number starts with a dot.
     */
    private void readNumber(final boolean wasDot)
    {
        this.builder.setLength(0);
        this.builder.append(wasDot ? '.' : (char) this.current);
        final boolean wasZero = this.current == '0';
        try
        {
            if (!wasDot)
            {
                this.read();
                if (wasZero)
                {
                    switch (this.current)
                    {
                    case 'X':
                    case 'x':
                        this.builder.setLength(0);
                        this.read();
                        for (;;)
                        {
                            final char c = Character
                                    .toLowerCase((char) this.current);
                            if ((c < '0' || c > '9') && (c < 'a' || c > 'f'))
                                break;
                            this.builder.append(c);
                            this.read();
                        }
                        if (this.builder.length() == 0)
                            throw new WeelException(this.error("Syntax error"));
                        this.number = (int)Long.parseLong(this.builder.toString(),
                                16);
                        return;
                    case 'O':
                    case 'o':
                        this.builder.setLength(0);
                        this.read();
                        for (;;)
                        {
                            if (this.current < '0' || this.current > '7')
                                break;
                            this.builder.append((char) this.current);
                            this.read();
                        }
                        if (this.builder.length() == 0)
                            throw new WeelException(this.error("Syntax error"));
                        this.number = (int)Long.parseLong(this.builder.toString(),
                                8);
                        return;
                    case 'B':
                    case 'b':
                        this.builder.setLength(0);
                        this.read();
                        for (;;)
                        {
                            if (this.current < '0' || this.current > '1')
                                break;
                            this.builder.append((char) this.current);
                            this.read();
                        }
                        if (this.builder.length() == 0)
                            throw new WeelException(this.error("Syntax error"));
                        this.number = (int)Long.parseLong(this.builder.toString(),
                                2);
                        return;
                    }
                }
                while (Character.isDigit(this.current))
                {
                    this.builder.append((char) this.current);
                    this.read();
                }
            }
            if (this.current == '.')
            {
                this.builder.append('.');
                this.read();
            }
            while (Character.isDigit(this.current))
            {
                this.builder.append((char) this.current);
                this.read();
            }
            if (this.current == 'E' || this.current == 'e')
            {
                this.builder.append('e');
                this.read();
                if (this.current == '+' || this.current == '-')
                {
                    this.builder.append((char) this.current);
                    this.read();
                }
                while (Character.isDigit(this.current))
                {
                    this.builder.append((char) this.current);
                    this.read();
                }
            }
            this.number = Double.parseDouble(this.builder.toString());
        }
        catch (final IOException e)
        {
            throw new WeelException(this.error(e.toString()), e);
        }
    }

    /**
     * Reads a name.
     */
    private void readName()
    {
        this.builder.setLength(0);
        try
        {
            while (Character.isLetterOrDigit(this.current)
                    || this.current == '_')
            {
                this.builder
                        .append((char) (Character.isLetter(this.current) ? Character
                                .toLowerCase(this.current)
                                : this.current));
                this.read();
            }
            this.string = this.builder.toString();
        }
        catch (final IOException e)
        {
            throw new WeelException(this.error(e.toString()), e);
        }
    }

    /**
     * Reads a string.
     */
    private void readString()
    {
        final int lim = this.current;
        this.builder.setLength(0);
        try
        {
            this.read();
            while (this.current != lim)
            {
                switch (this.current)
                {
                case -1:
                    throw new WeelException(this
                            .error("Unexpected end of file"));
                case '\\':
                    this.read();
                    switch (this.current)
                    {
                    case -1:
                        throw new WeelException(this
                                .error("Unexpected end of file"));
                    case 'n':
                        this.builder.append('\n');
                        break;
                    case 'r':
                        this.builder.append('\r');
                        break;
                    case 't':
                        this.builder.append('\t');
                        break;
                    case '"':
                        this.builder.append('"');
                        break;
                    case '\'':
                        this.builder.append('\'');
                        break;
                    case '\\':
                        this.builder.append('\\');
                        break;
                    default:
                        throw new WeelException(this.error(
                                "Unsupported or illegal escape character '%c'",
                                (char) this.current));
                    }
                    this.read();
                    break;
                default:
                    this.builder.append((char) this.current);
                    this.read();
                    break;
                }
            }
            this.read();
            this.string = this.builder.toString();
        }
        catch (final IOException e)
        {
            throw new WeelException(this.error(e.toString()), e);
        }
    }

    /**
     * Ungets a token.
     * 
     * @param t
     *            The token.
     */
    public void ungetToken(final Token t)
    {
        this.ungot = this.token;
        this.token = t;
    }

    /**
     * Reads the next token.
     * 
     * @return The next token.
     */
    public Token next()
    {
        if (this.ungot != null)
        {
            this.token = this.ungot;
            this.ungot = null;
            return this.token;
        }
        try
        {
            for (;;)
            {
                switch (this.current)
                {
                case -1:
                    return this.token = Token.EOF;
                case '\n':
                    this.lineNumber++;
                    //$FALL-THROUGH$
                case ' ':
                case '\t':
                case '\r':
                    this.read();
                    continue;
                case '"':
                case '\'':
                    this.readString();
                    return this.token = Token.STRING;
                case '`':
                    this.readString();
                    if(this.string.length() != 1)
                    {
                        throw new WeelException(this.error("Illegal character constant"));
                    }
                    this.number = this.string.charAt(0);
                    return this.token = Token.NUMBER;
                case '+':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_ADD;
                    }
                    else if(this.current == '+')
                    {
                        this.read();
                        if(this.current == '=')
                        {
                            this.read();
                            return this.token = Token.ASSIGN_MAPCAT;
                        }
                        return this.token = Token.MAP_CONCAT;
                    }
                    return this.token = Token.ADD;
                case '-':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_SUB;
                    }
                    else if (this.current == '>')
                    {
                        this.read();
                        return this.token = Token.ARROW;
                    }
                    return this.token = Token.SUB;
                case '*':
                    this.read();
                    if (this.current == '*')
                    {
                        this.read();
                        return this.token = Token.POW;
                    }
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_MUL;
                    }
                    return this.token = Token.MUL;
                case '%':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_MODULO;
                    }
                    return this.token = Token.MODULO;
                case '?':
                    this.read();
                    return this.token = Token.TERNARY;
                case '/':
                    this.read();
                    if (this.current == '/')
                    {
                        while (this.current != '\n' && this.current != -1)
                            this.read();
                        continue;
                    }
                    else if (this.current == '*')
                    {
                        this.read();
                        boolean inComment = true;
                        while (inComment)
                        {
                            switch (this.current)
                            {
                            case -1:
                                throw new WeelException(this
                                        .error("Unexpected end of file"));
                            case '*':
                                this.read();
                                if (this.current == '/')
                                {
                                    this.read();
                                    inComment = false;
                                }
                                break;
                            case '\n':
                                this.lineNumber++;
                                //$FALL-THROUGH$
                            default:
                                this.read();
                                break;
                            }
                        }
                        continue;
                    }
                    else if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_DIV;
                    }
                    return this.token = Token.DIV;
                case '(':
                    this.read();
                    return this.token = Token.BRACE_OPEN;
                case ')':
                    this.read();
                    return this.token = Token.BRACE_CLOSE;
                case '{':
                    this.read();
                    return this.token = Token.CURLY_BRACE_OPEN;
                case '}':
                    this.read();
                    return this.token = Token.CURLY_BRACE_CLOSE;
                case '[':
                    this.read();
                    return this.token = Token.BRACKET_OPEN;
                case ']':
                    this.read();
                    return this.token = Token.BRACKET_CLOSE;
                case ',':
                    this.read();
                    return this.token = Token.COMMA;
                case '.':
                    this.read();
                    if (this.current == '.')
                    {
                        this.read();
                        if (this.current == '=')
                        {
                            this.read();
                            return this.token = Token.ASSIGN_STRCAT;
                        }
                        return this.token = Token.STRING_CONCAT;
                    }
                    else if (Character.isDigit(this.current))
                    {
                        this.readNumber(true);
                        return this.token = Token.NUMBER;
                    }
                    return this.token = Token.DOT;
                case ';':
                    this.read();
                    return this.token = Token.SEMICOLON;
                case '~':
                    this.read();
                    return this.token = Token.BINARY_NOT;
                case '^':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_XOR;
                    }
                    return this.token = Token.BINARY_XOR;
                case '=':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.EQUAL;
                    }
                    return this.token = Token.ASSIGN;
                case '>':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.GREATER_EQUAL;
                    }
                    else if(this.current == '>')
                    {
                        this.read();
                        if(this.current == '=')
                        {
                            this.read();
                            return this.token = Token.ASSIGN_SHR;
                        }
                        else if(this.current == '>')
                        {
                            this.read();
                            if(this.current == '=')
                            {
                                this.read();
                                return this.token = Token.ASSIGN_USHR;
                            }
                            return this.token = Token.USHR;
                        }
                        return this.token = Token.SHR;
                    }
                    return this.token = Token.GREATER;
                case '<':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.LESS_EQUAL;
                    }
                    else if(this.current == '<')
                    {
                        this.read();
                        if(this.current == '=')
                        {
                            this.read();
                            return this.token = Token.ASSIGN_SHL;
                        }
                        return this.token = Token.SHL;
                    }
                    return this.token = Token.LESS;
                case '&':
                    this.read();
                    if (this.current == '&')
                    {
                        this.read();
                        return this.token = Token.LOGICAL_AND;
                    }
                    else if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_AND;
                    }
                    return this.token = Token.BINARY_AND;
                case '|':
                    this.read();
                    if (this.current == '|')
                    {
                        this.read();
                        return this.token = Token.LOGICAL_OR;
                    }
                    else if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.ASSIGN_OR;
                    }
                    return this.token = Token.BINARY_OR;
                case '!':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.NOT_EQUAL;
                    }
                    return this.token = Token.LOGICAL_NOT;
                case '@':
                    this.read();
                    if (this.current == '{')
                    {
                        this.read();
                        return this.token = Token.ANON_OPEN;
                    }
                    return this.token = Token.AT;
                case ':':
                    this.read();
                    if (this.current == ':')
                    {
                        this.read();
                        return this.token = Token.DOUBLE_COLON;
                    }
                    return this.token = Token.COLON;
                default:
                    if (Character.isLetter(this.current) || this.current == '_')
                    {
                        this.readName();
                        if ((this.reserved = ReservedWord
                                .fromString(this.string)) != null)
                            return this.token = Token.RESERVED;
                        return this.token = Token.NAME;
                    }
                    else if (Character.isDigit(this.current))
                    {
                        this.readNumber(false);
                        return this.token = Token.NUMBER;
                    }
                    throw new WeelException(this.error(
                            "Illegal character '%c'", this.current));
                }
            }
        }
        catch (final IOException e)
        {
            throw new WeelException(this.error(e.toString()), e);
        }
    }

    /**
     * Checks if the token is a unary operator.
     * 
     * @param t
     *            The token.
     * @return <code>true</code> if it is a unary operator.
     */
    public boolean isUnary(final Token t)
    {
        switch (t)
        {
        case LOGICAL_NOT:
        case BINARY_NOT:
        case SUB:
        case BRACE_OPEN:
            return true;
        default:
            return false;
        }
    }

    /**
     * Checks if the token is a binary operator.
     * 
     * @param t
     *            The token.
     * @return <code>true</code> if it is a binary operator.
     */
    public boolean isBinary(final Token t)
    {
        switch (t)
        {
        case ADD:
        case SUB:
        case MUL:
        case DIV:
        case MODULO:
        case POW:
        case STRING_CONCAT:
        case MAP_CONCAT:
        case EQUAL:
        case NOT_EQUAL:
        case LESS:
        case LESS_EQUAL:
        case GREATER:
        case GREATER_EQUAL:
        case LOGICAL_AND:
        case LOGICAL_OR:
        case BINARY_AND:
        case BINARY_OR:
        case BINARY_XOR:
        case SHR:
        case USHR:
        case SHL:
            return true;
        default:
            return false;
        }
    }

    /**
     * Check if the current token starts an expression.
     * 
     * @return <code>true</code> if so.
     */
    public boolean isExpression()
    {
        if (this.isUnary(this.token) || this.isBinary(this.token))
            return true;

        switch (this.token)
        {
        case NAME:
        case NUMBER:
        case STRING:
        case BRACE_OPEN:
        case CURLY_BRACE_OPEN:
            return true;
        case RESERVED:
            switch (this.reserved)
            {
            case FUNC:
            case SUB:
            case TRUE:
            case FALSE:
            case NULL:
            case THIS:
                return true;
            default:
                return false;
            }
        default:
            return false;
        }
    }

    /**
     * Gets the priority of a binary operator.
     * 
     * @param t
     *            The token.
     * @return The priority.
     */
    public int getBinaryPriority(final Token t)
    {
        switch (t)
        {
        case MAP_CONCAT:
        case STRING_CONCAT:
            return 14;
        case POW:
            return 13;
        case MUL:
        case DIV:
        case MODULO:
            return 12;
        case ADD:
        case SUB:
            return 11;
        case SHL:
        case SHR:
        case USHR:
            return 10;
        case GREATER:
        case GREATER_EQUAL:
        case LESS:
        case LESS_EQUAL:
            return 9;
        case NOT_EQUAL:
        case EQUAL:
            return 8;
        case BINARY_AND:
            return 7;
        case BINARY_XOR:
            return 6;
        case BINARY_OR:
            return 5;
        case LOGICAL_AND:
            return 4;
        case LOGICAL_OR:
            return 3;
        default:
            return 0;
        }
    }
}
