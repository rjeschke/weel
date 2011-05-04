### Types, names and reserved words

#### Types

*   Null:
        null

*   Strings:
        'Hello world'
        "Hello world"
        'With " inside'
        "With ' inside"
        "With
        line break"
        
    Allowed escape characters:
        \n  \r  \t  \'  \"  \\

*   Numbers:
    *   Decimal:
            -10.3
            42
            1.23e-6
    *   Octal:
            0o777
            0O1723
    *   Binary:
            0b11001010
            0B111001010101
    *   Hexadecimal:
            0xdeadbeaf
            0XCafeBabe
            
*   Maps:
        {1, 2, 3, 4}
        {"Hello", null, 1, true, println}
        {
            [0] = "Test",
            ["key"] = 42,
            name = "Hund",
            arr = {1, 2, 3}
        }
        
#### Names

*   Names start with a letter and may contain letters, digits and `_`
 
#### Reserved words

    break,
    case, continue,
    default, do,
    else, elseif, end, exit,
    false, for, foreach, func,
    global,
    if, in,
    local, lock,
    null,
    outer,
    return,
    sub, switch,
    then, this, true,
    until,
    while    

[$PROFILE$]: extended