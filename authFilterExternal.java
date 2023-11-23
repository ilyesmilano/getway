import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SAPUserFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Define IP address criteria for SAP users
        String clientIpAddress = request.getRemoteAddress().getHostString();
        boolean isSAPUser = isSAPUser(clientIpAddress);

        if (isSAPUser) {
            // Call a different token validation API for SAP users
            boolean isTokenValid = validateSAPUserToken(request);

            if (!isTokenValid) {
                // Handle invalid token (e.g., return an error response)
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }

        // Continue with the request chain for non-SAP users or SAP users with valid
        // tokens
        return chain.filter(exchange);
    }

    // private boolean isSAPUser(String ipAddress) {
    // // Define your logic to check if the IP address belongs to an SAP user
    // // This could be based on a predefined list of SAP IP addresses
    // // Return true for SAP users and false for others
    // // Example: return ipAddress.equals("SAP_IP_ADDRESS");
    // return false;
    // }

    private boolean isSAPUser(String ipAddress) {
        // Check if the IP address starts with "10.124"
        return ipAddress.startsWith("10.124");
    }

    private boolean validateSAPUserToken(ServerHttpRequest request) {
        // Call the SAP user token validation API here
        // Perform token validation and return true if the token is valid; otherwise,
        // return false
        // You can use WebClient to make the API call
        // Example:
        // WebClient webClient =
        // WebClient.create("http://sap-token-validation-api-url");
        // boolean isTokenValid =
        // webClient.get().uri("/validateToken").retrieve().toEntity(String.class).block()...
        return false;
    }
}
