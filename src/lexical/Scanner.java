package lexical;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import exception.LexicalException;
import utils.TokenType;

public class Scanner {

  char currentChar;
  char[] source_code;
  int state;
  int pos;

  public int current_line = 1; // Variavel responsável por contar as linhas
  // a linha só é incrementada quando um '\n' é lido, por isso começa com 1

  public int current_column = 0; // Variavel responsável por contar as colunas

  // ArrayList responsavel por armazenar as palavras reservadas
  ArrayList<String> reserved_table = new ArrayList<>(Arrays.asList(
    "int", "float", "PRINTF", "SI", "SINON","DEBUT", "FIN", "PENDENT",
    "DECLARATION","ALGORITHME","SCANF", "ALORS"

  ));

  public int getCurrent_line() {
    return current_line;
  }

  public int getCurrent_column() {
    return current_column;
  }

  public Scanner(String filename) {
    try {
      String contentBuffer = Files.readString(Paths.get(filename));
      contentBuffer = contentBuffer + ' '; // solução do problema do EOF
      this.source_code = contentBuffer.toCharArray();
      this.pos = 0;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Token nextToken() throws Exception {
    StringBuilder content = new StringBuilder();
    this.state = 0;

    while (true) {
      currentChar = nextChar();
      switch (state) {
        case 0 -> {
          if (isLetterOrUnderscore(currentChar)) {
            content.append(currentChar);
            this.state = 1;
          } else if (isDigit(currentChar)) {
            content.append(currentChar);
            this.state = 2;
          } else if (isDot(currentChar)) {
            content.append(currentChar);
            this.state = 3;
          } else if (isSpace(currentChar)) {
            this.state = 0;
          } else if (isOperator(currentChar)) {
            content.append(currentChar);
            this.state = 5;
          } else if (isAttribution(currentChar)) {
            content.append(currentChar);
            this.state = 6;
          } else if (isRelational(currentChar) || isNot(currentChar)) {
            content.append(currentChar);
            this.state = 8;
          } else if (isParenthesesLeft(currentChar)) {
            content.append(currentChar);
            this.state = 9;
          } else if (isComment(currentChar)) {
            this.state = 10;
          }  else if (isParenthesesRight(currentChar)) {
            content.append(currentChar);
            this.state = 11;
          } else if (isColon(currentChar)){
            content.append(currentChar);
            this.state = 12;
          }  else {
              LexicalException.unrecognizedSymbol(this.currentChar, this.current_line, this.current_column);
          }
        }
        case 1 -> {
          /*
           * Estado 1 do autômato, responsável por indentificar Identfiers, aceitará
           * qualquer
           * caractere desde que seja um letras ou letras e números juntos, se e somente
           * se
           * os numerais vierem após as letras.
           * Qualquer outra coisa lida no arquivo será intepretada como um erro caso seja
           * um
           * caracter não reconchecido pela gramática do analisador.
           */
          if (isLetterOrUnderscore(currentChar) || isDigit(currentChar)) {
            content.append(currentChar);
          } else if (isSpace(currentChar) || isRelational(currentChar) || isNot(currentChar)|| isColon(currentChar)
            || isParenthesesLeft(currentChar) || isComment(currentChar) || isParenthesesRight(currentChar)) {
            if (research(content, reserved_table)) {
              back();
              return new Token(TokenType.RESERVED, content.toString());
            } else if (content.toString().equals("ET") ||content.toString().equals("OU") ){
              return new Token(TokenType.BOOLEAN, content.toString());
            } else {
              back();
              return new Token(TokenType.IDENTIFIER, content.toString());
            }
          } else {
            LexicalException.badIdentifier(this.currentChar, this.current_line, this.current_column);
          }
        }
        case 2 -> {
          /*
           * Estado 2 irá identificar números inteiros, caso seja identificado um ponto
           * '.' entre os números,
           * currentChar irá para o estado 3 onde receberá uma identificação de número com
           * ponto flutuante.
           * Caso contrário a essas ações, será disparado uma exeção relatando mal
           * formação de números
           */
          if (isDigit(currentChar)) {
            content.append(currentChar);
          } else if (isDot(currentChar)) {
            content.append(currentChar);
            this.state = 3;
          } else if (isSpace(currentChar) || isOperator(currentChar) || isNot(currentChar) || isRelational(currentChar)
              || isParenthesesLeft(currentChar) || isComment(currentChar) || isParenthesesRight(currentChar)) {
            back();
            return new Token(TokenType.INTEGER, content.toString());
          } else {
            LexicalException.badValue(this.currentChar, this.current_line, this.current_column);
          }
        }
        case 3 -> {
          /*
           * Estado 3 é responsável por averiguar se o que foi lido é um ponto '.', caso
           * seja lido o ponto, currentChar
           * entrará no estado 4 onde lá será verificado se o que foi lido no arquivo é um
           * número de ponto flutuante
           */
          if (isDigit(currentChar)) {
            content.append(currentChar);
            this.state = 4;
          } else {
            LexicalException.badValue(this.currentChar, this.current_line, this.current_column);
          }
        }
        case 4 -> {
          /*
           * Estado 4, verifica se o que está sendo lido é um número de ponto flutuante,
           * seja ele iniciando
           * com um ponto, ex: .12 ou entre números, ex: 4.25. Qualquer coisa diferente do
           * que isso é retornado um erro
           * de mal formação de números
           */
          if (isDigit(currentChar)) {
            content.append(currentChar);
          } else if (
            isSpace(currentChar) || isOperator(currentChar) || isNot(currentChar) || isRelational(currentChar) ||
            isAttribution(currentChar) || isParenthesesLeft(currentChar) || isComment(currentChar) ||
            isParenthesesRight(currentChar)) {
            back();
            return new Token(TokenType.FLOAT, content.toString());
          } else {
            LexicalException.badValue(this.currentChar, this.current_line, this.current_column);
          }
        }
        // O estado 5 lida com operadores, retornando-os imediamente ao serem lidos.
        case 5 -> {
          back();
          return new Token(TokenType.MATH_OP, content.toString());
        }
        /*
         * o estado 6 lida com atribuição no caso do nextchar ser diferente de
         * igual, caso contrário é delegado ao estado 7 sua manipulação.
         */
        case 6 -> {
          if (isAttribution(currentChar)) {
            content.append(currentChar);
            this.state = 7;
          } else {
            back();
            return new Token(TokenType.ASSIGN, content.toString());
          }
        }
        case 7 -> {
          /*
           * Estado 7 retorna o operador relacional se foi acrecido do caratere '=', foi necessário
		   * a criação desse estado para a diferenciação do operador de atribuição '=' dos demais
           * operadores como '==' de comparação, diferente '!=' e dos de maior e menor igual 
           */
          back();
          return new Token(TokenType.REL_OP, content.toString());
        }
        case 8 -> {
          /*
           * Estado 8 irá reconhecer um maior que ('>') ou menor que ('<') e o caractere '!', caso
		   * caso haja um caractere de '=' após, poderá ser feito um retorno entre maior igual ('>='),
		   * menor igual ('<=') e diferente (!=) no estado 7
           */
          if (isAttribution(currentChar)) {
            content.append(currentChar);
            this.state = 7;
          } else {
            back();
            return new Token(TokenType.REL_OP, content.toString());
          }
        }
        case 9 -> { 
			/*
			Estado 9 irá reconhecer os parenteses esquerdo '(' ou e retornar seu tipo como delimitador
			*/
          back();
          return new Token(TokenType.PAR_LEFT, content.toString());
        }
        case 10 -> {
			/*
			* No estado 10, é reconhecido o caractere '#' o qual irá representar um comentário no arquivo,
			* o que significa que todos os elementos seguintes a '#' serão desconciderados no processo de 
			* tolkeinização e com isso será totalmente descartados pelo analisador lexíco
			*/
          if (currentChar == '#' || currentChar == '\n' || currentChar == '\r') {
            back();
            this.state = 0;
          }
        }
        case 11 -> {
          back();
          return new Token(TokenType.PAR_RIGHT, content.toString());
        } 

        case 12 -> {
          back();
          return new Token(TokenType.COLON, content.toString());
        }
        default -> {
        }
      }

      if (isEOF()) {
        return null;
      }
    }
  }

  ////////////////////////////// Métodos//////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////
  private boolean isSpace(char currentChar) {
    return currentChar == ' ' || currentChar == '\n' ||
        currentChar == '\t' || currentChar == '\r';
  }

  private void back() {
    this.pos--;
  }

  private boolean isDigit(char currentChar) {
    return currentChar >= '0' && currentChar <= '9';
  }

  private boolean isLetterOrUnderscore(char currentChar) {
    return (currentChar >= 'a' && currentChar <= 'z') ||
        (currentChar >= 'A' && currentChar <= 'Z') ||
        (currentChar == '_');
  }

  private char nextChar() {
    goForward();
    return this.source_code[pos++];
  }

  private boolean isEOF() {
    return this.pos >= this.source_code.length;
  }

  private boolean isDot(char currentChar) {
    return currentChar == '.';
  }

  // Método responsavel por verificar se o caractere digitado é um operador
  private boolean isOperator(char currentChar) {
    return (currentChar == '+') || (currentChar == '-') ||
        (currentChar == '/') || (currentChar == '*');
  }

  // Método responsavel por verificar se o caractere digitado é um operador
  // relacional
  private boolean isRelational(char currentChar) {
    return (currentChar == '>') || (currentChar == '<');
  }

  // Método que irá ler um caractere '!' e após isso irá para o estado 7 afim de
  // verificar que
  // o conteudo no arquivo é um operador lógico de diferente ou apenas um símbolo
  // de exclamação
  private boolean isNot(char currentChar) {
    return (currentChar == '!');
  }

  // Método responsavel por verificar se o caractere digitado é uma atribuição
  private boolean isAttribution(char currentChar) {
    return (currentChar == '=');
  }

  private boolean isColon(char currentChar) {
    return (currentChar == ':');
  }
  // Método responsavel por verificar se o caractere digitado é um parêntese
  private boolean isParenthesesLeft(char currentChar) {
    return (currentChar == '(');
  }

   private boolean isParenthesesRight(char currentChar) {
    return (currentChar == ')');
  }

  // Método responsavel por verificar se o caractere digitado é um '#' para
  // determinar que o conteúdo
  // no arquivo é um comentario
  private boolean isComment(char currentChar) {
    return (currentChar == '#');
  }

  // faz pesquisa de palavras reservadas no array de palavras reservadas
  public boolean research(StringBuilder content, ArrayList<String> array) {
    for (String word : array) {
      if (word.contentEquals(content)) {
        return true;
      }
    }
    return false;
  }

  // responsável por incrementar os contadores de linha e coluna
  public void goForward() {
    if (currentChar == '\n') {
      this.current_line++;
      this.current_column = 1;
    } else {
      this.current_column++;
    }
  }
}
