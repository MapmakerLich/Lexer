import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Lexer {
    public static void main(String[] args) {
        File input = new File("input.txt");
        int lineCount = 1;
        try (FileInputStream inputStream = new FileInputStream(input)) {
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                StringBuilder output = new StringBuilder();
                int str = lineCount;
                int i = 0;
                while (i < line.length()) {
                    char current = line.charAt(i);
                    char next = i < line.length() - 1 ? line.charAt(i + 1) : current;
                    char previous = i > 0 ? line.charAt(i - 1) : current;
                    Tokens currentToken = Tokens.INIT;
                    int position = i + 1;
                    switch (current) {
                        case '!':
                            if (next == '=') {
                                output = new StringBuilder("!=");
                                currentToken = Tokens.NOT_EQUAL;
                                i++;
                            } else {
                                output = new StringBuilder("!");
                                currentToken = Tokens.ERROR;
                            }
                            break;
                        case '=':
                            if (next != '=' && (i != line.length() - 1)) {
                                output = new StringBuilder("=");
                                currentToken = Tokens.ASSIGNMENT;
                            } else {
                                output = new StringBuilder("==");
                                currentToken = Tokens.COMPARISON;
                                i++;
                            }
                            break;
                        case '+':
                            output = new StringBuilder("+");
                            currentToken = Tokens.PLUS;
                            break;
                        case '-':
                            output = new StringBuilder("-");
                            currentToken = Tokens.MINUS;
                            break;
                        case '*':
                            output = new StringBuilder("*");
                            currentToken = Tokens.MULTIPLICATION;
                            break;
                        case '/':
                            if (next == '/' && (i != line.length() - 1)) {
                                output = new StringBuilder("//");
                                currentToken = Tokens.ONE_LINE_COMMENT;
                            } else if (next == '*') {
                                output = new StringBuilder("/*");
                                currentToken = Tokens.MULTILINE_COMMENT;
                            } else if (i != line.length() - 1 && previous != '*') {
                                output = new StringBuilder("/");
                                currentToken = Tokens.DIVISION;
                            }
                            break;
                        case '^':
                            output = new StringBuilder("^");
                            currentToken = Tokens.EXPONENTIATION;
                            break;
                        case '(':
                            output = new StringBuilder("(");
                            currentToken = Tokens.BRACKET_OPEN;
                            break;
                        case ')':
                            output = new StringBuilder(")");
                            currentToken = Tokens.BRACKET_CLOSE;
                            break;
                        case '{':
                            output = new StringBuilder("{");
                            currentToken = Tokens.BRACE_OPEN;
                            break;
                        case '}':
                            output = new StringBuilder("}");
                            currentToken = Tokens.BRACE_CLOSE;
                            break;
                        case '.':
                            output = new StringBuilder(".");
                            currentToken = Tokens.POINT;
                            break;
                        case ',':
                            output = new StringBuilder(",");
                            currentToken = Tokens.COMMA;
                            break;
                        case ':':
                            output = new StringBuilder(":");
                            currentToken = Tokens.COLON;
                            break;
                        case ';':
                            output = new StringBuilder(";");
                            currentToken = Tokens.SEMICOLON;
                            break;
                        case '[':
                            output = new StringBuilder("[");
                            currentToken = Tokens.SQUARE_BRACKETS_OPEN;
                            break;
                        case ']':
                            output = new StringBuilder("]");
                            currentToken = Tokens.SQUARE_BRACKETS_CLOSE;
                            break;
                        case '<':
                            if (next == '=') {
                                output = new StringBuilder("<=");
                                currentToken = Tokens.LESS_EQUAL;
                                i++;
                            } else {
                                output = new StringBuilder("<");
                                currentToken = Tokens.SMALLER;
                            }
                            break;
                        case '>':
                            if (next == '=') {
                                output = new StringBuilder(">=");
                                currentToken = Tokens.MORE_EQUAL;
                                i++;
                            } else {
                                output = new StringBuilder(">");
                                currentToken = Tokens.MORE;
                            }
                            break;
                        case ' ':
                            currentToken = Tokens.SPACE;
                            break;
                        default:
                            if (current == '"') {
                                currentToken = Tokens.STRING_PARAM;
                                StringBuilder string = new StringBuilder(Character.toString(current));
                                while (next != '"') {
                                    i++;
                                    if (i == line.length()) {
                                        try
                                        {
                                            line = scanner.nextLine();
                                        }
                                        catch (NoSuchElementException e)
                                        {
                                            currentToken = Tokens.ERROR;
                                            String result = currentToken.toString() + ' ' + output + " in line " + str + " position " + position;
                                            System.out.println(result);
                                            return;
                                        }
                                        i = 0;
                                        lineCount++;
                                    }
                                    current = line.charAt(i);
                                    next = i < line.length() - 1 ? line.charAt(i + 1) : current;
                                    string.append(current);
                                }
                                output = new StringBuilder(string.toString() + '"');
                                i++;
                            } else if (Character.isDigit(current)) {
                                boolean isInt = true;
                                boolean isDouble = false;
                                boolean isFloat = false;
                                boolean isOctal = true;
                                boolean isHex = false;
                                boolean isBinary = false;
                                output = new StringBuilder(Character.toString(current));
                                if (current == '0' && (Character.isDigit(next) || next == 'b' || next == 'B'
                                        || next == 'x' || next == 'X' || next == 'A' || next == 'C'
                                        || next == 'D' || next == 'E' || next == 'F')) {
                                    while (Character.isDigit(current) || current == 'b' || current == 'B'
                                            || current == 'x' || current == 'X' || current == 'A' || current == 'C'
                                            || current == 'D' || current == 'E' || current == 'F') {
                                        if ((current == 'B' || current == 'b') && !isHex) {
                                            isBinary = true;
                                            isOctal = false;
                                        }
                                        if (current == 'X' || current == 'x') {
                                            isHex = true;
                                            isOctal = false;
                                        }
                                        if (isBinary && current != '0' && current != '1' && current != 'b' && current != 'B') {
                                            currentToken = Tokens.ERROR;
                                            break;
                                        }
                                        if (isOctal && (current == '8' || current == '9')) {
                                            currentToken = Tokens.ERROR;
                                            break;
                                        }
                                        if (isHex && current != 'x' && current != 'X' && current != 'B' && current != 'A' && current != 'C'
                                                && current != 'D' && current != 'E' && current != 'F' && !Character.isDigit(current)) {
                                            currentToken = Tokens.ERROR;
                                            break;
                                        }
                                        i++;
                                        if (i >= line.length()) {
                                            break;
                                        }
                                        current = line.charAt(i);
                                        next = i < line.length() - 1 ? line.charAt(i + 1) : current;
                                        if (Character.isDigit(current) || current == 'b' || current == 'B' || current == 'x' || current == 'X'
                                                || current == 'A' || current == 'C'
                                                || current == 'D' || current == 'E' || current == 'F') {
                                            output.append(current);
                                        }
                                    }
                                    if (!Character.isDigit(current)) {
                                        i--;
                                    }
                                    if (isBinary) {
                                        currentToken = Tokens.BINARY;
                                    } else if (isOctal) {
                                        currentToken = Tokens.OCTAL;
                                    } else if (isHex) {
                                        currentToken = Tokens.HEX;
                                    }
                                } else {
                                    while (Character.isDigit(current) || current == '.' || current == 'E' || current == 'e' || current == '-') {
                                        if (current == '.') {
                                            isInt = false;
                                            isDouble = true;
                                        }
                                        if (current == 'E' || current == 'e') {
                                            isInt = false;
                                            isDouble = false;
                                            isFloat = true;
                                        }
                                        i++;
                                        try
                                        {
                                            current = line.charAt(i);
                                        }
                                        catch (StringIndexOutOfBoundsException e)
                                        {
                                            currentToken = Tokens.ERROR;
                                            line = scanner.next();
                                            i = 0;
                                        }
                                        next = i < line.length() - 1 ? line.charAt(i + 1) : current;
                                        if (Character.isDigit(current) || current == '.' || current == 'E' || current == 'e' || current == '-') {
                                            output.append(current);
                                        }
                                    }
                                    if (!Character.isDigit(current)) {
                                        i--;
                                    }
                                    if (isInt) {
                                        currentToken = Tokens.INT_NUMBER;
                                    } else if (isDouble) {
                                        currentToken = Tokens.DOUBLE_NUMBER;
                                    } else if (isFloat) {
                                        currentToken = Tokens.FLOAT_NUMBER;
                                    }
                                }
                            } else if (Character.isAlphabetic(current) || current == '_') {
                                StringBuilder ident = new StringBuilder();
                                while (Character.isAlphabetic(current) || current == '_') {
                                    if (Character.isAlphabetic(current) || current == '_' || Character.isDigit(current)) {
                                        ident.append(current);
                                        i++;
                                        if (i >= line.length()) {
                                            break;
                                        }
                                        current = line.charAt(i);
                                        next = i < line.length() - 1 ? line.charAt(i + 1) : current;
                                    }
                                }
                                if (!Character.isAlphabetic(current) && current != '_' && !Character.isDigit(current)) {
                                    i--;
                                }
                                output = new StringBuilder(ident.toString());
                                switch (ident.toString()) {
                                    case "private":
                                        currentToken = Tokens.PRIVATE;
                                        break;
                                    case "public":
                                        currentToken = Tokens.PUBLIC;
                                        break;
                                    case "void":
                                        currentToken = Tokens.VOID;
                                        break;
                                    case "var":
                                        currentToken = Tokens.VAR;
                                        break;
                                    case "class":
                                        currentToken = Tokens.CLASS;
                                        break;
                                    case "int":
                                        currentToken = Tokens.INT;
                                        break;
                                    case "double":
                                        currentToken = Tokens.DOUBLE;
                                        break;
                                    case "bool":
                                        currentToken = Tokens.BOOL;
                                        break;
                                    case "char":
                                        currentToken = Tokens.CHAR;
                                        break;
                                    case "String":
                                        currentToken = Tokens.STRING;
                                        break;
                                    case "if":
                                        currentToken = Tokens.IF;
                                        break;
                                    case "else":
                                        currentToken = Tokens.ELSE;
                                        break;
                                    case "while":
                                        currentToken = Tokens.WHILE;
                                        break;
                                    case "for":
                                        currentToken = Tokens.FOR;
                                        break;
                                    case "read":
                                        currentToken = Tokens.READ;
                                        break;
                                    case "write":
                                        currentToken = Tokens.WRITE;
                                        break;
                                    default:
                                        currentToken = Tokens.IDENTIFICATION;
                                        break;
                                }
                            } else {
                                currentToken = Tokens.ERROR;
                            }
                            break;
                    }
                    if (currentToken.equals(Tokens.ONE_LINE_COMMENT)) {
                        i = line.length();
                    }
                    if (currentToken.equals(Tokens.MULTILINE_COMMENT)) {
                        while (current != '*' || next != '/') {
                            i++;
                            if (i == line.length() - 1) {
                                line = scanner.nextLine();
                                i = 0;
                                lineCount++;
                            }
                            try
                            {
                                current = line.charAt(i);
                            }
                            catch (StringIndexOutOfBoundsException e)
                            {
                                if (!scanner.hasNextLine())
                                {
                                    currentToken = Tokens.ERROR;
                                    String result = currentToken.toString() + ' ' + output + " in line " + str + " position " + position;
                                    System.out.println(result);
                                    return;
                                }
                                line = scanner.next();
                            }
                            next = i < line.length() - 1 ? line.charAt(i + 1) : current;
                        }
                    }
                    if (currentToken == Tokens.INT_NUMBER)
                    {
                        try
                        {
                            parseInt(output.toString());
                        }
                        catch (NumberFormatException e)
                        {
                            currentToken = Tokens.ERROR;
                        }
                    }
                    if (currentToken == Tokens.BINARY)
                    {
                        String part = output.substring(2);
                        try
                        {
                            parseInt(part, 2);
                        }
                        catch (NumberFormatException e)
                        {
                            currentToken = Tokens.ERROR;
                        }
                    }
                    if (currentToken == Tokens.OCTAL)
                    {
                        String part = output.substring(2);
                        try
                        {
                            parseInt(part, 8);
                        }
                        catch (NumberFormatException e)
                        {
                            currentToken = Tokens.ERROR;
                        }
                    }
                    if (currentToken == Tokens.HEX)
                    {
                        String part = output.substring(2);
                        try
                        {
                            parseInt(part, 16);
                        }
                        catch (NumberFormatException e)
                        {
                            currentToken = Tokens.ERROR;
                        }
                    }
                    if (!currentToken.equals(Tokens.SPACE)
                            && !currentToken.equals(Tokens.ERROR)
                            && !currentToken.equals(Tokens.INIT)
                            && !currentToken.equals(Tokens.MULTILINE_COMMENT)
                            && !currentToken.equals(Tokens.ONE_LINE_COMMENT)) {
                        String result = currentToken.toString() + ' ' + output + ' ' + str + ' ' + position;
                        System.out.println(result);
                    } else if (currentToken.equals(Tokens.ERROR)) {
                        String result = currentToken.toString() + ' ' + output + " in line " + str + " position " + position;
                        System.out.println(result);
                    }
                    i++;
                }
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}