/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
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
    final static int UOPR_PRIORITY = 14;
    /** The input stream: */
    private Reader reader;
    /** Builder for names/strings and numbers. */
    private StringBuilder builder = new StringBuilder();
    /** Current input char. */
    private int current = ' ';
    /** Current line number. */
    private int lineNumber;
    /** Current filename. */
    private String filename;
    /** The current token. */
    Token token;

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
     */
    public Tokenizer(Reader reader)
    {
        this.reader = reader;
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
        final String m = (args.length > 0) ? String.format(message, args)
                : message;

        return m
                + (this.filename != null ? " at line " + this.lineNumber
                        + " in " + this.filename : " at line "
                        + this.lineNumber);
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
                        this.number = Integer.parseInt(this.builder.toString(),
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
                        this.number = Integer.parseInt(this.builder.toString(),
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
                        this.number = Integer.parseInt(this.builder.toString(),
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
     * Reads the next token.
     * 
     * @return The next token.
     */
    public Token next()
    {
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
                case '+':
                    this.read();
                    return this.token = Token.ADD;
                case '-':
                    this.read();
                    return this.token = Token.SUB;
                case '*':
                    this.read();
                    return this.token = Token.MUL;
                case '%':
                    this.read();
                    return this.token = Token.MODULO;
                    // case '?':
                    // this.read();
                    // return this.token = Token.TERNARY;
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
                case '.':
                    this.read();
                    if (this.current == '.')
                    {
                        return this.token = Token.STRING_CONCAT;
                    }
                    else if (Character.isDigit(this.current))
                    {
                        this.readNumber(true);
                        return this.token = Token.NUMBER;
                    }
                    return Token.DOT;
                case ';':
                    this.read();
                    return this.token = Token.SEMICOLON;
                case '~':
                    this.read();
                    return this.token = Token.BINARY_NOT;
                case '^':
                    this.read();
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
                    return this.token = Token.GREATER;
                case '<':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return this.token = Token.LESS_EQUAL;
                    }
                    return this.token = Token.LESS;
                case '&':
                    this.read();
                    if (this.current == '&')
                    {
                        this.read();
                        return this.token = Token.LOGICAL_AND;
                    }
                    return this.token = Token.BINARY_AND;
                case '|':
                    this.read();
                    if (this.current == '|')
                    {
                        this.read();
                        return this.token = Token.LOGICAL_OR;
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
                case ':':
                    this.read();
                    if (this.current == ':')
                    {
                        this.read();
                        if (this.current == ':')
                        {
                            this.read();
                            return this.token = Token.TRIPPLE_COLON;
                        }
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
        case STRING_CONCAT:
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
        case STRING_CONCAT:
            return 13;
        case MUL:
        case DIV:
        case MODULO:
            return 12;
        case ADD:
        case SUB:
            return 11;
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
