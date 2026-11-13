package com.payinsight.rest;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class RestApplication extends ResourceConfig {

    public RestApplication() {

        register(TransactionRest.class);

    }
}