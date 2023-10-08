package syntax;

import exception.ParserException;
import lexical.Scanner;
import lexical.Token;
import utils.TokenType;
import lexical.*;

import java.util.Objects;

public class Parser {
    
  private final Scanner scanner;
  private Token token;

	public Parser(Scanner scanner) {this.scanner = scanner;}
	
  public void program() throws Exception{

      this.token = this.scanner.nextToken();

      if (this.token.getType() != TokenType.COLON)
        throw new ParserException(
          "Expected COLON, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
      this.token = this.scanner.nextToken();

      if (!(Objects.equals(this.token.getContent(), "DECLARATION")))
        throw new ParserException(
          "Expected RESERVED, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);      this.token = this.scanner.nextToken();

      listaDeclaracoes();

      if (!(Objects.equals(this.token.getContent(), "ALGORITHME")))
        throw new ParserException(
          "Expected RESERVED, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);      this.token = this.scanner.nextToken();

      listaComandos();
  }

    // num
  public void listaDeclaracoes() throws Exception{
      do {
          declaracao();
      } while(this.token.getType() != TokenType.COLON);
      this.token = this.scanner.nextToken();
  }

  private void declaracao() throws Exception {
      if (this.token.getType() != TokenType.IDENTIFIER)
        throw new ParserException(
          "Expected IDENTIFIER, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
      this.token = this.scanner.nextToken();

      if (this.token.getType() != TokenType.COLON)
        throw new ParserException(
          "Expected COLON, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
      tipoVar();
  }

  public void tipoVar() throws Exception{
    this.token = this.scanner.nextToken();
    if (
      !(Objects.equals(this.token.getContent(), "int") || Objects.equals(this.token.getContent(), "float"))
    )
      throw new ParserException(
        "Expected NUMBER, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    this.token = this.scanner.nextToken();
  }

  private void listaComandos() throws Exception{
    comando();
    if (this.token != null){
      if (!Objects.equals(this.token.getContent(), "FIN"))
        listaComandos();
    }
  }

  private void comando() throws Exception {

    if(this.token.getType() != TokenType.RESERVED && this.token.getType() != TokenType.IDENTIFIER) {
      throw new ParserException(
        "Expected RESERVED OR IDENTIFIER, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    }

    if (this.token.getType() == TokenType.IDENTIFIER) {
      this.token = this.scanner.nextToken();
      comandoAtribuicao();
    }

    else if (Objects.equals(this.token.getContent(), "SCANF")) {
      this.token = this.scanner.nextToken();
      comandoEntrada();
    }

    else if (Objects.equals(this.token.getContent(), "PRINTF")) {
      this.token = this.scanner.nextToken();
      comandoSaida();
    }

    else if (Objects.equals(this.token.getContent(), "SI")) {
      this.token = this.scanner.nextToken();
      comandoCondicao();
    }

    else if (Objects.equals(this.token.getContent(), "PENDENT")) {
      this.token = this.scanner.nextToken();
      comandoRepeticao();
    }

    else if (Objects.equals(this.token.getContent(), "DEBUT")) {
      this.token = this.scanner.nextToken();
      subAlgoritmo();
    }
  }

  private void comandoAtribuicao() throws Exception {
    if(this.token.getType() != TokenType.ASSIGN) {
      throw new ParserException(
        "Expected ASSIGN, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    }
      this.token = this.scanner.nextToken();
      expressaoAritmetica();
  }

  public void expressaoAritmetica() throws Exception{
      termoAritmetico();
      expressaoAritmeticaLn();
  }

  public void expressaoAritmeticaLn()  throws Exception{
     if (this.token != null) {
       if (this.token.getContent().equals("+") || this.token.getContent().equals("-")) {
       this.token = this.scanner.nextToken();
       termoAritmetico();
       expressaoAritmeticaLn();
       }
     }
  }

  public void termoAritmetico() throws Exception{
    fatorAritimetrico();
    termoAritmeticoLn();
  }

  public void termoAritmeticoLn() throws Exception{
    if (this.token != null) {
      if (this.token.getContent().equals("*") || this.token.getContent().equals("/")) {
        this.token = this.scanner.nextToken();
        fatorAritimetrico();
        termoAritmeticoLn();
      }
    }
  }

  private void fatorAritimetrico() throws Exception{
    if(this.token.getType() != TokenType.PAR_LEFT){
      if (
        this.token.getType() != TokenType.INTEGER      &&
        this.token.getType() != TokenType.FLOAT        &&
        this.token.getType() != TokenType.IDENTIFIER
      ) {
        throw new ParserException(
          "Expected VALUE OR IDENTIFIER OR PARENTHESES, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);      } else {
        this.token = this.scanner.nextToken();
      }
    } else {
      this.token = this.scanner.nextToken();
      expressaoAritmetica();
      if (this.token.getType() != TokenType.PAR_RIGHT)
        throw new ParserException(
          "Expected RIGHT_PARENTHESES, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
      this.token = this.scanner.nextToken();
    }

  }
  private void comandoEntrada() throws Exception {
    if (this.token.getType() != TokenType.IDENTIFIER)
      throw new ParserException(
        "Expected IDENTIFIER, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    this.token = this.scanner.nextToken();
  }

  private void comandoSaida() throws Exception {
    if (this.token.getType() != TokenType.PAR_LEFT)
      throw new ParserException(
        "Expected LEFT_PARENTHESES, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    this.token = this.scanner.nextToken();

    if (this.token.getType() != TokenType.IDENTIFIER)
      throw new ParserException(
        "Expected IDENTIFIER, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    this.token = this.scanner.nextToken();

    if (this.token.getType() != TokenType.PAR_RIGHT)
      throw new ParserException(
        "Expected RIGHT_PARENTHESES, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    this.token = this.scanner.nextToken();
  }

  private void comandoCondicao() throws Exception {
    expressaoRelacional();

    if (!(Objects.equals(this.token.getContent(), "ALORS"))){
      throw new ParserException(
        "Expected RESERVED, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    } else {
      this.token = this.scanner.nextToken();
      comando();
    }

    if (this.token != null) {
      if (Objects.equals(this.token.getContent(), "SINON")) {
        this.token = this.scanner.nextToken();
        comando();
      }
    }
  }

  private void expressaoRelacional() throws Exception {
    termoRelacional();
    expressaoRelacionalLn();
  }

  private void termoRelacional() throws Exception {
    expressaoAritmetica();

    if (this.token.getType() != TokenType.REL_OP)
      throw new ParserException(
        "Expected RELATIONAL_OPERATOR, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    this.token = this.scanner.nextToken();

    expressaoAritmetica();
  }

  private void expressaoRelacionalLn() throws Exception {
    if (this.token.getType() == TokenType.BOOLEAN ){
      operadorBooleano();
      termoRelacional();
      expressaoRelacionalLn();
    }
  }

  private void operadorBooleano() throws Exception {
    if (!(Objects.equals(this.token.getContent(), "ET") || Objects.equals(this.token.getContent(), "OU")))
      throw new ParserException(
        "Expected BOOLEAN, found [" + this.token.getType() + ":" + this.token.getContent() + "] at "+scanner.current_column +":"+scanner.current_line);
    this.token = this.scanner.nextToken();
  }

  private void comandoRepeticao() throws Exception {
    expressaoRelacional();
    comando();
  }

  private void subAlgoritmo() throws Exception {
    listaComandos();
    this.token = this.scanner.nextToken();
  }

}
