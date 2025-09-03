package pe.com.farmaciadey.auth.models.responses;

import pe.com.farmaciadey.auth.models.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {

    private String accessToken;

    private UserInfo user;
}
