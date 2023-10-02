package syntax;

import exception.ParserException;
import lexical.Scanner;
import lexical.Token;
import utils.TokenType;

public class Parse {
    
    private Scanner scanner;
	private Token token;
	
	public Parse(Scanner scanner) {
		this.scanner = scanner;
	}
	
    public void programa() throws Exception{
        this.token = this.scanner.nextToken(); 
        if (this.token.getType() != TokenType.DECLARATION && 
        this.token.getType() != TokenType.IDENTIFIER &&
        this.token.getType() != TokenType.RESERVED){
            throw new ParserException("Bad formation of variable declaration");
        }
        listaDeclaracoes();
    }

    public void listaDeclaracoes() throws Exception{
        this.token = this.scanner.nextToken();
        
        declaracoes();
        listaDeclaracoes();
    }

    public void declaracoes() throws Exception {
        this.token = this.scanner.nextToken(); 
        if (this.token.getType() != TokenType.DECLARATION && this.token.getType() != TokenType.IDENTIFIER){
            throw new ParserException("Bad formation of variable declaration");
        }
        tipoVar();
    }

    public void tipoVar() throws Exception{
        this.token = this.scanner.nextToken();
        if (this.token.getType() != TokenType.RESERVED){
            throw new ParserException("Not a type informeded");
        }
    }

    public void expressaoAritmetica() throws Exception{
        this.token = this.scanner.nextToken();
        termoAritmetico();
        expressaoAritmeticaLn();
    }

    public void expressaoAritmeticaLn()  throws Exception{
        this.token = this.scanner.nextToken();
        if (this.token != null) {
            if (this.token.getType() != TokenType.MATH_OP){
                throw new ParserException("Math opperation not define");
            }
            termoAritmetico();
            expressaoAritmeticaLn();
        }
    }

    public void termoAritmetico() throws Exception{
        this.token = this.scanner.nextToken();
        fatorAritimetrico();
        termoAritmeticoLn();
    }

    public void termoAritmeticoLn() throws Exception{
        this.token = this.scanner.nextToken();
        if(this.token != null){
            if (this.token.getType() != TokenType.MATH_OP) {
                throw new ParserException("Math opperation not define");
            }
            termoAritmetico();
            termoAritmeticoLn();
        }
    }

    private void fatorAritimetrico() throws Exception{
        
    }

   
}