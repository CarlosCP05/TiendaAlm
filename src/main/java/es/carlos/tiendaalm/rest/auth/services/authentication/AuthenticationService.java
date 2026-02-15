package es.carlos.tiendaalm.rest.auth.services.authentication;

import es.carlos.tiendaalm.rest.auth.dto.JwtAuthResponse;
import es.carlos.tiendaalm.rest.auth.dto.UserSignInRequest;
import es.carlos.tiendaalm.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
  JwtAuthResponse signUp(UserSignUpRequest request);

  JwtAuthResponse signIn(UserSignInRequest request);
}
