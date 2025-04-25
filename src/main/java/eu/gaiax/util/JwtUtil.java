package eu.gaiax.util;

import com.google.gson.Gson;

import java.util.Base64;

public class JwtUtil {

    public static <T> T readTokenIntoClass(String bearerToken, Class<T> clazz) {
        String token = bearerToken.split("\\s+")[1];
        String tokenBody = token.split("\\.")[1];

        Base64.Decoder decoder = Base64.getDecoder();
        String jsonStr = new String(decoder.decode(tokenBody));
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, clazz);
    }

}
