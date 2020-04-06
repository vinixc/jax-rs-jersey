package br.com.vini.loja.jax.rs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.vini.loja.jax.rs.modelo.Carrinho;
import br.com.vini.loja.jax.rs.modelo.Produto;
import br.com.vini.loja.jax.rs.modelo.Projeto;
import junit.framework.Assert;

public class ClienteTest {
	
	private HttpServer server;
	private Client client;

	@Before
	public void before() {
		server = Servidor.inicializaServidor();
	}
	
	@After
	public void mataServidor() {
		server.stop();
	}
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		
		client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		System.out.println(conteudo);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
		
	}
	
	@Test
    public void testaQueBuscarUmProjetoTrazOProjetoEsperado() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080");
        String conteudo = target.path("/projetos/1").request().get(String.class);
        Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
        Assert.assertEquals("Minha loja", projeto.getNome());
    }
	
	@Test
	public void testaPostParaCarrinhos() {
		Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080");
        
        Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        String xml = carrinho.toXml();
        
        Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);

        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        String location = response.getHeaderString("Location");
        String conteudo = client.target(location).request().get(String.class);
        Assert.assertTrue(conteudo.contains("Tablet"));
	}
	
	@Test
	public void testaPostParaProjetos() {
		client = ClientBuilder.newClient();
		 WebTarget target = client.target("http://localhost:8080");
		 
		 Projeto projeto = new Projeto(2l, "jogos", 2020);
		 String xml = projeto.toXML();
		 
		 Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		 
		 Response response = target.path("/projetos").request().post(entity);
		 Assert.assertEquals(201, response.getStatus());
		 String location = response.getHeaderString("Location");
	     String conteudo = client.target(location).request().get(String.class);
	     Assert.assertTrue(conteudo.contains("jogos"));
	}

}
