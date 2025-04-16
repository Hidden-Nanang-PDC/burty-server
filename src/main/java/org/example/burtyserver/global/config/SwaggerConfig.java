package org.example.burtyserver.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Burty Server API")
                        .version("1.0.0")
                        .description("버티 서버 API 문서"))
                .paths(new Paths());
        openAPI.getPaths().addPathItem("/oauth2/authorize/{provider}", new PathItem()
                .get(new Operation()
                        .addParametersItem(new Parameter()
                                .name("provider")
                                .in("path")
                                .required(true)
                                .schema(new Schema<String>().type("string").example("kakao"))
                                .description("소셜 로그인 제공자(kakao, google, naver)"))
                        .summary("소셜 로그인 요청")
                        .description("소셜 로그인 인증 페이지로 리다이렉트합니다.")
                        .tags(List.of("소셜 로그인"))));
        openAPI.getPaths().addPathItem("/login/oauth2/code/{provider}", new PathItem()
                .get(new Operation()
                        .addParametersItem(new Parameter()
                                .name("provider")
                                .in("path")
                                .required(true)
                                .schema(new Schema<String>().type("string").example("kakao"))
                                .description("소셜 로그인 제공자(kakao, google, naver)"))
                        .summary("소셜 로그인 콜백")
                        .description("소셜 로그인 인증 후 콜백 처리를 합니다.")
                        .tags(List.of("소셜 로그인"))));
        return openAPI;
    }
}
