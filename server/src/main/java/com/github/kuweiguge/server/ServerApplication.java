package com.github.kuweiguge.server;

import com.github.kuweiguge.server.netty.NodesServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(ServerApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
		NodesServer nodesServer = new NodesServer();
		nodesServer.startNettyServer(11111);
		// nodesServer.startNettyServer(11112);
	}

}