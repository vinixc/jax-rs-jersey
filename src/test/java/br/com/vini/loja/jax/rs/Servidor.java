package br.com.vini.loja.jax.rs;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Servidor {

	public static void main(String[] args) throws IOException {
		HttpServer server = inicializaServidor();
		System.out.println("Servidor rodando");
		System.in.read();
		server.stop();
		
	}

	static HttpServer inicializaServidor() {
		URI uri = URI.create("http://localhost:8080/");
		ResourceConfig config = new ResourceConfig().packages("br.com.vini.loja.jax.rs");
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
		return server;
	}

}
