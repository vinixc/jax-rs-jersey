package br.com.vini.loja.jax.rs;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
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
	private WebTarget target;

	@Before
	public void before() {
		server = Servidor.inicializaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		
		this.client = ClientBuilder.newClient(config);
		this.target = client.target("http://localhost:8080");
	}
	
	@After
	public void mataServidor() {
		server.stop();
	}
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
		System.out.println(carrinho);
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
        Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        String location = response.getHeaderString("Location");
        Carrinho carrinhoCarregado = client.target(location).request().get(Carrinho.class);
        Assert.assertEquals("Tablet", carrinhoCarregado.getProdutos().get(0).getNome());
	}
	
	@Test
	public void testaPostParaProjetos() {
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
