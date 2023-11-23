import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ExternalUserFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Define IP address criteria for External users
        String clientIpAddress = request.getRemoteAddress().getHostString();
        boolean isExternalUser = isExternalUser(clientIpAddress);

        if (isExternalUser) {
            // Call a different token validation API for External users
            boolean isTokenValid = validateExternalUserToken(request);

            if (!isTokenValid) {
                // Handle invalid token (e.g., return an error response)
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }

        // Continue with the request chain for non-External users or External users with
        // valid tokens
        return chain.filter(exchange);
    }

    private boolean isExternalUser(String ipAddress) {
        // Check if the IP address starts with "10.124"
        return ipAddress.startsWith("10.124");
    }

    private boolean validateExternalUserToken(ServerHttpRequest request) {
        // Call the External user token validation API here
        // Perform token validation and return true if the token is valid; otherwise,
        // return false
        // You can use WebClient to make the API call
        // Example:
        // WebClient webClient =
        // WebClient.create("http://External-token-validation-api-url");
        // boolean isTokenValid =
        // webClient.get().uri("/validateToken").retrieve().toEntity(String.class).block()...
        return false; // Replace with your token validation logic
    }
}
