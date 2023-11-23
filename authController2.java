import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/api/authentification")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.secret}") // Inject your secret key from properties/configuration
    private String secretKey;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        // Authenticate the user (you can customize this part based on your user data
        // source)
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (userDetails != null
                && bCryptPasswordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
            // Generate a simple token without roles
            Date expirationDate = new Date(System.currentTimeMillis() + (10 * 60 * 1000)); // Token expires in 10
                                                                                           // minutes
            // Date expirationDate = new Date(System.currentTimeMillis() + (10L * 365 * 24 *
            // 60 * 60 * 1000)); // Token expires in 10 years
            String token = Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setExpiration(expirationDate)
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();

            // Return the token in the response
            return ResponseEntity.ok(token);
        } else {
            // Authentication failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validateToken")
    public ResponseEntity<ValidationResponse> validateToken(@RequestParam("token") String token) {
        Claims claims;
        try {
            // Validate the token using the same secret key
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            // Check if the token is expired
            Date expirationDate = claims.getExpiration();
            Date currentDate = new Date();
            if (currentDate.after(expirationDate)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ValidationResponse("Token is expired"));
            }
        } catch (Exception e) {
            // Token validation failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ValidationResponse("Token is invalid"));
        }

        return ResponseEntity.ok(new ValidationResponse("Token is valid"));
    }
}

class AuthRequest {
    private String username;
    private String password;

    // Getters and setters
}

class ValidationResponse {
    private String message;

    public ValidationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
