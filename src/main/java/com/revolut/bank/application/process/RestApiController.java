package com.revolut.bank.application.process;

import com.revolut.bank.application.api.account.CreateAccountRequest;
import com.revolut.bank.application.api.account.CreateAccountResponse;
import com.revolut.bank.application.api.account.GetAccountBalanceResponse;
import com.revolut.bank.application.api.transfer.TransferMoneyRequest;
import com.revolut.bank.application.api.transfer.TransferMoneyResponse;
import com.revolut.bank.application.engine.CommandExecutor;
import com.revolut.bank.application.process.account.balance.GetAccountBalanceCommand;
import com.revolut.bank.application.process.account.create.CreateAccountCommand;
import com.revolut.bank.application.process.transfer.TransferMoneyCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

/**
 * Main controller with API methods
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
@Api(tags = {"Bank API"})
@Path("/api")
@Singleton
public class RestApiController {

    @Inject
    private CommandExecutor executor;

    @Inject
    private CreateAccountCommand createAccountCommand;

    @Inject
    private GetAccountBalanceCommand getAccountBalanceCommand;

    @Inject
    private TransferMoneyCommand transferMoneyCommand;

    @ApiOperation(
            value = "Create account with given balance",
            notes = "Only USD currency is supported",
            response = CreateAccountResponse.class
    )
    @POST
    @Path("/account/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createAccount(@Suspended AsyncResponse asyncResponse,
                              CreateAccountRequest request) {
        executor.execute(createAccountCommand, request, asyncResponse);
    }

    @ApiOperation(
            value = "Return account's balance",
            response = GetAccountBalanceResponse.class
    )
    @GET
    @Path("/account/{uid}/balance")
    @Produces(MediaType.APPLICATION_JSON)
    public void getAccountBalance(@Suspended AsyncResponse asyncResponse,
                                  @PathParam("uid") String uid) {
        executor.execute(getAccountBalanceCommand, uid, asyncResponse);
    }

    @ApiOperation(
            value = "Transfer money between two accounts",
            notes = "Only USD currency is supported",
            response = TransferMoneyResponse.class
    )
    @POST
    @Path("/transfer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void transferMoney(@Suspended AsyncResponse asyncResponse,
                              TransferMoneyRequest request) {
        executor.execute(transferMoneyCommand, request, asyncResponse);
    }

}
