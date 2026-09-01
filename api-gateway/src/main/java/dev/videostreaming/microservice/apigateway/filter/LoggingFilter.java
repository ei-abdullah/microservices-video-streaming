package dev.videostreaming.microservice.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String method = request.getMethod().name();
        String path = request.getPath().value();
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        long startTime = System.currentTimeMillis();

        log.info("→ {} {} from {}", method, path, ip);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    long duration = System.currentTimeMillis() - startTime;

                    log.info("← {} {} → {} ({}ms)", method, path, status, duration);
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
