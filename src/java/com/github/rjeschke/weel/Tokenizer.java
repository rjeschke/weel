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

    /** Tokenized reserved word. */
    private ReservedWord reserved = null;
    /** Tokenized string or name. */
    private String string;
    /** Tokenized number. */
    private double number;

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
     * Gets the last tokenized reserved word.
     * 
     * @return The reserved word.
     */
    public ReservedWord getReservedWord()
    {
        return this.reserved;
    }

    /**
     * Gets the last tokenized string or name.
     * 
     * @return The string or name.
     */
    public String getString()
    {
        return this.string;
    }

    /**
     * Gets the last tokenized number.
     * 
     * @return The number.
     */
    public double getNumber()
    {
        return this.number;
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
                    return Token.EOF;
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
                    return Token.STRING;
                case '+':
                    this.read();
                    return Token.ADD;
                case '-':
                    this.read();
                    return Token.SUB;
                case '*':
                    this.read();
                    return Token.MUL;
                case '?':
                    this.read();
                    return Token.TERNARY;
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
                    return Token.DIV;
                case '(':
                    this.read();
                    return Token.BRACE_OPEN;
                case ')':
                    this.read();
                    return Token.BRACE_CLOSE;
                case '{':
                    this.read();
                    return Token.CURLY_BRACE_OPEN;
                case '}':
                    this.read();
                    return Token.CURLY_BRACE_CLOSE;
                case '[':
                    this.read();
                    return Token.BRACKET_OPEN;
                case ']':
                    this.read();
                    return Token.BRACKET_CLOSE;
                case '.':
                    this.read();
                    if (this.current == '.')
                    {
                        return Token.STRING_CONCAT;
                    }
                    else if (Character.isDigit(this.current))
                    {
                        this.readNumber(true);
                        return Token.NUMBER;
                    }
                    return Token.DOT;
                case ';':
                    this.read();
                    return Token.SEMICOLON;
                case '~':
                    this.read();
                    return Token.BINARY_NOT;
                case '^':
                    this.read();
                    return Token.BINARY_XOR;
                case '=':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return Token.EQUAL;
                    }
                    return Token.ASSIGN;
                case '>':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return Token.GREATER_EQUAL;
                    }
                    return Token.GREATER;
                case '<':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return Token.LESS_EQUAL;
                    }
                    return Token.LESS;
                case '&':
                    this.read();
                    if (this.current == '&')
                    {
                        this.read();
                        return Token.LOGICAL_AND;
                    }
                    return Token.BINARY_AND;
                case '|':
                    this.read();
                    if (this.current == '|')
                    {
                        this.read();
                        return Token.LOGICAL_OR;
                    }
                    return Token.BINARY_OR;
                case '!':
                    this.read();
                    if (this.current == '=')
                    {
                        this.read();
                        return Token.NOT_EQUAL;
                    }
                    return Token.LOGICAL_NOT;
                case ':':
                    this.read();
                    if (this.current == ':')
                    {
                        this.read();
                        if(this.current == ':')
                        {
                            this.read();
                            return Token.TRIPPLE_COLON;
                        }
                        return Token.DOUBLE_COLON;
                    }
                    return Token.COLON;
                default:
                    if (Character.isLetter(this.current) || this.current == '_')
                    {
                        this.readName();
                        if ((this.reserved = ReservedWord
                                .fromString(this.string)) != null)
                            return Token.RESERVED;
                        return Token.NAME;
                    }
                    else if (Character.isDigit(this.current))
                    {
                        this.readNumber(false);
                        return Token.NUMBER;
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
}
