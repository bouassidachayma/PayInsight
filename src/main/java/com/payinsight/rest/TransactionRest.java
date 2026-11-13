package com.payinsight.rest;

import com.payinsight.data.PaymentDataService;
import com.payinsight.data.Transaction;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionRest {

    private final PaymentDataService paymentDataService =
            new PaymentDataService();

    // 1. Retrieve a transaction by ID
    @GET
    @Path("/{id}")
    public Response getTransaction(@PathParam("id") String id) {

        try {
            Transaction transaction =
                    paymentDataService.getTransaction(id);

            if (transaction == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Transaction not found\"}")
                        .build();
            }

            return Response.ok(transaction).build();

        } catch (IllegalStateException e) {
            return Response.serverError()
                    .entity("{\"error\":\"Unable to retrieve transaction\"}")
                    .build();
        }
    }

    // 2. Retrieve all approved transactions
    @GET
    @Path("/approved")
    public Response getApprovedTransactions() {

        try {
            Transaction[] transactions =
                    paymentDataService.getApprovedTransactions();

            return Response.ok(transactions).build();

        } catch (IllegalStateException e) {
            return Response.serverError()
                    .entity("{\"error\":\"Unable to retrieve transactions\"}")
                    .build();
        }
    }

    // 3. Retrieve transactions above a minimum amount
    @GET
    @Path("/above")
    public Response getTransactionsAboveAmount(@QueryParam("amount") double amount) {

        if (amount < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Amount cannot be negative\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        try {
            Transaction[] transactions =
                    paymentDataService.getTransactionsAboveAmount(amount);

            return Response.ok(transactions).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid amount\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (IllegalStateException e) {
            return Response.serverError()
                    .entity("{\"error\":\"Unable to retrieve transactions\"}")
                    .build();
        }
    }

    // 4. Retrieve the total number of transactions
    @GET
    @Path("/count")
    public Response getTransactionCount() {

        try {
            int count = paymentDataService.getTransactionCount();

            return Response.ok("{\"transactionCount\":" + count + "}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (IllegalStateException e) {
            return Response.serverError()
                    .entity("{\"error\":\"Unable to count transactions\"}")
                    .build();
        }
    }
}