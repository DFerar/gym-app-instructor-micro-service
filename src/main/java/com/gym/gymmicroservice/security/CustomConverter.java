package com.gym.gymmicroservice.security;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    /**
     * Converts a Jwt token into an UsernamePasswordAuthenticationToken.
     *
     * @param token - the Jwt token to convert.
     * @return AbstractAuthenticationToken - the converted token.
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt token) {
        return new UsernamePasswordAuthenticationToken(
            token.getClaims().get("sub"),
            null,
            new ArrayList<>()
        );
    }
}
