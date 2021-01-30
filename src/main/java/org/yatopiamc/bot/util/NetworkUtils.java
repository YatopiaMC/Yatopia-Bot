package org.yatopiamc.bot.util;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NetworkUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);
    public static final CloseableHttpAsyncClient httpAsyncClient;
    public static final PoolingAsyncClientConnectionManager connectionManager;

    static {
        connectionManager = new PoolingAsyncClientConnectionManager();
        connectionManager.setMaxTotal(16);
        connectionManager.setDefaultMaxPerRoute(8);
        httpAsyncClient = HttpAsyncClients.custom()
                .useSystemProperties()
                .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
                .setKeepAliveStrategy((response, context) -> {
                    BasicHeaderElementIterator it = new BasicHeaderElementIterator(
                            response.headerIterator("connection"));
                    while (it.hasNext()) {
                        HeaderElement he = it.next();
                        String param = he.getName();
                        String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase("timeout")) {
                            return TimeValue.ofSeconds(Long.parseLong(value));
                        }
                    }
                    return TimeValue.ofSeconds(60);
                })
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(10, TimeUnit.SECONDS)
                        .setResponseTimeout(10, TimeUnit.SECONDS)
                        .setConnectionRequestTimeout(10, TimeUnit.SECONDS)
                        .build()
                )
                .setConnectionManager(connectionManager)
                .setIOReactorConfig(IOReactorConfig.custom()
                        .setSoKeepAlive(true)
                        .setSoReuseAddress(true)
                        .setTcpNoDelay(true)
                        .setSoTimeout(10, TimeUnit.SECONDS)
                        .build()
                )
                .build();
        httpAsyncClient.start();
    }

    public static void shutdown() {
        httpAsyncClient.close(CloseMode.GRACEFUL);
        connectionManager.close(CloseMode.GRACEFUL);
        try {
            httpAsyncClient.awaitShutdown(TimeValue.MAX_VALUE);
        } catch (InterruptedException ignored) {
        }
    }

    public static CompletableFuture<SimpleHttpResponse> execute(SimpleHttpRequest request) {
        try {
            LOGGER.info("Queuing request for " + request.getUri());
        } catch (URISyntaxException ignored) {
        }
        CompletableFuture<SimpleHttpResponse> future = new CompletableFuture<>();
        try {
            NetworkUtils.httpAsyncClient.execute(request, new FutureCallback<SimpleHttpResponse>() {
                @Override
                public void completed(SimpleHttpResponse result) {
                    future.complete(result);
                }

                @Override
                public void failed(Exception ex) {
                    future.completeExceptionally(ex);
                }

                @Override
                public void cancelled() {
                    future.completeExceptionally(new CancellationException());
                }
            });
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
        return future;
    }

}
