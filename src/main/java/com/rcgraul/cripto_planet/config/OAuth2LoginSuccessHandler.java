package com.rcgraul.cripto_planet.config;

import com.rcgraul.cripto_planet.enums.OauthClientId;
import com.rcgraul.cripto_planet.enums.UserRole;
import com.rcgraul.cripto_planet.models.Role;
import com.rcgraul.cripto_planet.models.TwoFactorAuth;
import com.rcgraul.cripto_planet.models.User;
import com.rcgraul.cripto_planet.repositories.RoleRepository;
import com.rcgraul.cripto_planet.security.jwt.JwtUtils;
import com.rcgraul.cripto_planet.security.services.UserDetailsImpl;
import com.rcgraul.cripto_planet.services.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    // Email del usuario autenticado (extraído de los atributos de OAuth2)
    String email;

    // Nombre del usuario autenticado (extraído de los atributos de OAuth2)
    String realName;

    String username;

    // Clave utilizada para identificar al usuario de manera única en el sistema (depende del proveedor OAuth2)
    String idAttributeKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        // Castear la autentificacion al tipo OAuth2AuthenticationToken para manejar OAuth2 específicamente
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;

        try {
            Enum.valueOf(OauthClientId.class, oAuth2Token.getAuthorizedClientRegistrationId().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Client not supported");
        }


        // Obtener los datos del usuario autenticado desde el principal de OAuth2
        DefaultOAuth2User principal = (DefaultOAuth2User) oAuth2Token.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        email = attributes.get("email") != null ? attributes.get("email").toString() : "";
        realName = attributes.get("name") != null ? attributes.get("name").toString() : "";

        OauthClientId signUpMethod = OauthClientId.GITHUB;

        if (oAuth2Token.getAuthorizedClientRegistrationId().equals("google")) {
            idAttributeKey = "sub";
            signUpMethod = OauthClientId.GOOGLE;
        } else if (oAuth2Token.getAuthorizedClientRegistrationId().equals("github")) {
            idAttributeKey = "id";
            username = attributes.getOrDefault("login", "").toString();
        }

        //TODO: agregarle firebase

        System.out.println("HELLO OAUTH: " + email + " : " + realName + " : " + username);

        Optional<User> userDb = userService.findByUsername(username);

        if (userDb.isPresent()) {

            // aqui es para cuando el user existe pero no tiene el mismo signUpMethod, osea que el usuario se registro con un proveedor diferente
            boolean isSameProvider = !userDb.get().getSignUpMethod().name().equals(oAuth2Token.getAuthorizedClientRegistrationId().toUpperCase());
            if (isSameProvider) {
                String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/account-exists")
                        .build()
                        .toUriString();

                response.sendRedirect(redirectUrl);
            }

            // ? Crear un DefaultOAuth2User con los roles del usuario desde la base de datos
            // ? Es un modelo puro que encapsula información del usuario sin más lógica asociada
            DefaultOAuth2User oAuth2User = new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority(userDb.get().getRole().getRoleName().name())),
                    attributes,
                    idAttributeKey
            );

            // Configurar el contexto de seguridad con el usuario autenticado
            Authentication securityAuth = new OAuth2AuthenticationToken(
                    oAuth2User,
                    List.of(new SimpleGrantedAuthority(userDb.get().getRole().getRoleName().name())),
                    idAttributeKey);

            // ? Actualiza el contexto de seguridad con el token
            SecurityContextHolder.getContext().setAuthentication(securityAuth);

        } else {
            User newUser = new User();

            Optional<Role> userRole = roleRepository.findByRoleName(UserRole.ROLE_COSTUMER);

            if (userRole.isPresent()) {
                newUser.setRole(userRole.get());
            } else {
                throw new RuntimeException("Role not found");
            }

            String firstName = splitName(realName).get("firstName");
            String lastName = splitName(realName).get("lastName");

            newUser.setUsername(username);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);

            newUser.setOAuth(true);

            newUser.setSignUpMethod(signUpMethod);
            userService.createUser(newUser);

            // Crear un DefaultOAuth2User para el nuevo usuario
            DefaultOAuth2User securityUser = new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())), // Roles
                    attributes, // Atributos obtenidos del proveedor
                    idAttributeKey // Clave de identificación
            );

            // Configurar el contexto de seguridad con el nuevo usuario autenticado
            Authentication securityAuth = new OAuth2AuthenticationToken(
                    securityUser,
                    List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())), // Roles
                    oAuth2Token.getAuthorizedClientRegistrationId() // Proveedor
            );

            SecurityContextHolder.getContext().setAuthentication(securityAuth);
        }


        this.setAlwaysUseDefaultTargetUrl(true);

        // Obtener nuevamente el usuario autenticado y sus atributos (para generar el JWT)
        principal = (DefaultOAuth2User) oAuth2Token.getPrincipal();
        attributes = principal.getAttributes(); // Atributos del usuario

        Set<SimpleGrantedAuthority> authorities = new HashSet<>(principal.getAuthorities().stream() // Roles y permisos del usuario
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList()));

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName().name()));

        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEAbled(user.getTwoFactorAuth().isEAbled());
        twoFactorAuth.setSentTo(user.getTwoFactorAuth().getSentTo());

        // Crear una instancia de UserDetailsImpl con los datos del usuario
        UserDetailsImpl userDetails = new UserDetailsImpl(
                null, // ID no relevante aquí
                username, // Nombre de usuario
                email, // Email
                null, // Contraseña no necesaria (es OAuth2)
                signUpMethod,
                twoFactorAuth,
                authorities
        );

        String jwtToken = jwtUtils.generateTokenFromEmail(userDetails);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/login")
                .queryParam("token", jwtToken)
                .build().toUriString();

        // Establecer la URL de redirección como la URL objetivo
        this.setDefaultTargetUrl(targetUrl);

        // Llamar al método padre para completar el flujo de redirección
        super.onAuthenticationSuccess(request, response, authentication);
    }


    public Map<String, String> splitName(String fullName) {
        // Dividir la cadena en palabras
        String[] parts = fullName.trim().split(" ");

        // Crear un mapa para almacenar el resultado
        Map<String, String> nameMap = new HashMap<>();

        // Manejar casos especiales: si tiene 2 o menos palabras
        if (parts.length <= 2) {
            nameMap.put("firstName", parts[0]);
            nameMap.put("lastName", parts.length > 1 ? parts[1] : "");
            return nameMap;
        }

        // Los primeros elementos (hasta el penúltimo) son el firstName
        String firstName = String.join(" ", java.util.Arrays.copyOfRange(parts, 0, parts.length - 2));

        // Los últimos dos elementos son el lastName
        String lastName = String.join(" ", java.util.Arrays.copyOfRange(parts, parts.length - 2, parts.length));

        // Agregar al mapa
        nameMap.put("firstName", firstName);
        nameMap.put("lastName", lastName);

        return nameMap;
    }
}
