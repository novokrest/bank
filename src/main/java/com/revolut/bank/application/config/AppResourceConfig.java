package com.revolut.bank.application.config;

import com.revolut.bank.application.engine.CommandExecutor;
import com.revolut.bank.application.process.RestApiController;
import com.revolut.bank.application.process.RestCommandResponseFilter;
import com.revolut.bank.application.process.RestExceptionHandler;
import com.revolut.bank.application.process.account.balance.GetAccountBalanceCommand;
import com.revolut.bank.application.process.account.create.CreateAccountCommand;
import com.revolut.bank.application.process.transfer.TransferMoneyCommand;
import com.revolut.bank.application.service.account.AccountLocker;
import com.revolut.bank.application.service.account.AccountManager;
import com.revolut.bank.application.service.account.AccountStorage;
import com.revolut.bank.application.service.lock.LocksHolder;
import com.revolut.bank.application.service.transfer.TransferService;
import com.revolut.bank.application.utils.ResourceUtils;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import javax.annotation.Nonnull;
import javax.inject.Singleton;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;

import static java.util.Objects.requireNonNull;

/**
 * JAX-RS resource config for application
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 23.03.2019
 */
public class AppResourceConfig extends ResourceConfig {

    public AppResourceConfig(@Nonnull AppSettings settings) {
        requireNonNull(settings, "settings");

        register(RestApiController.class);
        register(RestCommandResponseFilter.class);
        register(RestExceptionHandler.class);
        register(createBinder(settings));

        registerSwagger(settings);
    }

    @Nonnull
    private static AbstractBinder createBinder(@Nonnull AppSettings settings) {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                CommandExecutor commandExecutor = new CommandExecutor(settings.getCommandThreadsCount());
                bind(commandExecutor).to(CommandExecutor.class).in(Singleton.class);

                AccountStorage accountStorage = new AccountStorage();
                bind(accountStorage).to(AccountStorage.class).in(Singleton.class);

                AccountManager accountManager = new AccountManager(accountStorage,
                        settings.getMinAccountBalance(), settings.getMaxAccountBalance());
                bind(accountManager).to(AccountManager.class).in(Singleton.class);

                bind(CreateAccountCommand.class).to(CreateAccountCommand.class).in(Singleton.class);
                bind(GetAccountBalanceCommand.class).to(GetAccountBalanceCommand.class).in(Singleton.class);
                bind(TransferMoneyCommand.class).to(TransferMoneyCommand.class).in(Singleton.class);
                bind(TransferService.class).to(TransferService.class).in(Singleton.class);
                bind(AccountLocker.class).to(AccountLocker.class).in(Singleton.class);
                bind(LocksHolder.class).to(LocksHolder.class).in(Singleton.class);
            }
        };
    }

    private void registerSwagger(@Nonnull AppSettings settings) {
        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost(String.format("%s:%d", settings.getHost(), settings.getPort()));
        beanConfig.setBasePath(settings.getBasePath());
        beanConfig.setResourcePackage("com.revolut.bank.application.process");
        beanConfig.setScan(true);

        String swaggerHtml = ResourceUtils.resourceToString("swagger/index.html")
                .replace("{{swagger_api_url}}", settings.getApiBaseUrl().toASCIIString() + "/swagger.json");
        registerSwaggerResource("api", swaggerHtml);

        ResourceUtils.listResources("swagger/ui")
                .forEach(name -> registerSwaggerResource(name, ResourceUtils.resourceToString("swagger/ui/" + name)));
    }

    private void registerSwaggerResource(@Nonnull String resourceName, @Nonnull String resourceContent) {
        Resource.Builder resourceBuilder = Resource.builder()
                .path("docs/" + resourceName);
        resourceBuilder
                .addMethod()
                .httpMethod("GET")
                .handledBy(containerRequestContext -> resourceContent);
        registerResources(resourceBuilder.build());
    }

}
