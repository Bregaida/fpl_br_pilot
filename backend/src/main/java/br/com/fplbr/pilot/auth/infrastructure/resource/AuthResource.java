package br.com.fplbr.pilot.auth.infrastructure.resource;

import br.com.fplbr.pilot.auth.application.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.time.Duration;
import java.util.Map;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(Map<String, Object> body) {
        var out = authService.startRegistration(body);
        return Response.ok(out).build();
    }

    @POST
    @Path("/2fa/verify")
    public Response verify(Map<String, Object> body) {
        String email = (String) body.get("email");
        int code = ((Number) body.get("code")).intValue();
        authService.verifyAndCreate(email, code);
        return Response.noContent().build();
    }

    @POST
    @Path("/login")
    public Response login(Map<String, Object> body) {
        String login = (String) body.get("login");
        String password = (String) body.get("password");
        Integer totp = body.get("totp") == null ? null : ((Number) body.get("totp")).intValue();
        var out = authService.login(login, password, totp);
        if (Boolean.TRUE.equals(out.get("ok"))) {
            String jwt = (String) out.get("jwt");
            NewCookie cookie = new NewCookie("access_token", jwt, "/", null, null, (int) Duration.ofMinutes(10).getSeconds(), true, true);
            return Response.ok(Map.of("ok", true)).cookie(cookie).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity(out).build();
    }

    @POST
    @Path("/login-totp-only")
    public Response loginTotpOnly(Map<String, Object> body) {
        String login = (String) body.get("login");
        int code = ((Number) body.get("code")).intValue();
        var out = authService.loginTotpOnly(login, code);
        if (Boolean.TRUE.equals(out.get("ok"))) {
            String jwt = (String) out.get("resetJwt");
            NewCookie cookie = new NewCookie("reset_token", jwt, "/", null, null, (int) Duration.ofMinutes(15).getSeconds(), true, true);
            return Response.ok(Map.of("ok", true)).cookie(cookie).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity(out).build();
    }

    @POST
    @Path("/forgot")
    public Response forgot(Map<String, Object> body) {
        String login = (String) body.get("login");
        var out = authService.forgot(login);
        return Response.ok(out).build();
    }

    @POST
    @Path("/reset")
    public Response reset(Map<String, Object> body) {
        String tokenOrTemp = (String) body.get("tokenOrTemp");
        String newPassword = (String) body.get("newPassword");
        var out = authService.reset(tokenOrTemp, newPassword);
        return Response.ok(out).build();
    }
}


