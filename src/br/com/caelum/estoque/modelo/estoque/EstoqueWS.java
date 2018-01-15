package br.com.caelum.estoque.modelo.estoque;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import br.com.caelum.estoque.modelo.item.Filtro;
import br.com.caelum.estoque.modelo.item.Filtros;
import br.com.caelum.estoque.modelo.item.Item;
import br.com.caelum.estoque.modelo.item.ItemDao;
import br.com.caelum.estoque.modelo.item.ItemValidador;
import br.com.caelum.estoque.modelo.usuario.AutorizacaoException;
import br.com.caelum.estoque.modelo.usuario.TokenDao;
import br.com.caelum.estoque.modelo.usuario.TokenUsuario;

@WebService
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public class EstoqueWS {

    private ItemDao dao = new ItemDao(); 

    @WebMethod(operationName="TodosOsItens")
    @WebResult(name="itens")
    @RequestWrapper(localName="listaItens")
    @ResponseWrapper(localName="itens")
    public List<Item> getItens(@WebParam(name="filtros") Filtros filtros) {
        System.out.println("Chamando getItens()");
        List<Filtro> lista = filtros.getLista();
        List<Item> itensResultado = dao.todosItens(lista);
        return itensResultado;
    }
    
    @WebMethod(action="CadastrarItem", operationName="CadastrarItem")  
    @WebResult(name="item")
    public Item cadastrarItem(
    		@WebParam(name="tokenUsuario", header = true) TokenUsuario token, 
    		@WebParam(name="item") Item item) 
    				throws AutorizacaoException {
    	System.out.println("cadastrando item " + item + " token " + token);
    	
    	boolean valido = new TokenDao().ehValido(token);
    	if(!valido) {
    		throw new AutorizacaoException("Autorzação falhou");
    	}
    	
    	new ItemValidador(item).validate();
    	
    	this.dao.cadastrar(item);
    	
    	return item;
    }

    @WebMethod(exclude=true)
    public void outroMetodo() { 
        //nao vai fazer parte do WSDL
    }
}