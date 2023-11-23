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
        // Authenticate the user (you can customize this part based on your user data source)
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (userDetails != null && bCryptPasswordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
            // Generate a simple token without roles
            Date expirationDate = new Date(System.currentTimeMillis() + (10 * 60 * 1000)); // Token expires in 10 minutes
            // Date expirationDate = new Date(System.currentTimeMillis() + (10L * 365 * 24 * 60 * 60 * 1000)); // Token expires in 10 years
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
}
