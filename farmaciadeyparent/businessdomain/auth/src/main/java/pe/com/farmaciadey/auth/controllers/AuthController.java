package pe.com.farmaciadey.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.farmaciadey.auth.constants.Utils;
import pe.com.farmaciadey.auth.models.UserInfo;
import pe.com.farmaciadey.auth.models.responses.DataResponse;
import pe.com.farmaciadey.auth.models.responses.JwtResponse;
import pe.com.farmaciadey.auth.services.CustomUserDetailsService;
import pe.com.farmaciadey.auth.services.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> usuarioRegistrar(@RequestBody UserInfo request) throws Exception {

        try {
            if (userService.findByUsername(request.getUsername()) != null) {
                return new ResponseEntity<>(new DataResponse("El username " + request.getUsername() + " ya existe"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            request.setRol("USUARIO");
            UserInfo o = userService.save(request);

            if (o == null) {
                return new ResponseEntity<>(new DataResponse("No se pudo registrar el usuario"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(new DataResponse(o, Utils.REQUEST_OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new DataResponse("Error interno. " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> AuthenticateAndGetToken(@RequestBody UserInfo request){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(),
                            request.getPassword()));

            if (authentication.isAuthenticated()) {

                UserInfo user = userService.findByUsername(request.getUsername());

                if (user != null) {
                    JwtResponse jwtResponse = new JwtResponse();
                    jwtResponse.setAccessToken(jwtService.GenerateToken(request.getUsername()));
                    jwtResponse.setUser(user);

                    return (new ResponseEntity<>(new DataResponse(jwtResponse, Utils.REQUEST_OK), HttpStatus.OK));
                }
            }
        } catch (Exception e) {
            return (new ResponseEntity<>(new DataResponse("Credenciales incorrectas", Utils.REQUEST_ERROR),
                    HttpStatus.OK));
        }
        return (new ResponseEntity<>(new DataResponse("Credenciales incorrectas", Utils.REQUEST_ERROR), HttpStatus.OK));
    }

}
