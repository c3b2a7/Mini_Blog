package me.lolico.blog.config;

import me.lolico.blog.security.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lolico li
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        List<Parameter> parameters = getParameters();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .globalOperationParameters(parameters)
                .select()
                .apis(RequestHandlerSelectors.basePackage("me.lolico.blog.web"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Mini_Blog API")
                .description("Designed and built with all the ❤ in the world by <a target=\"_blank\" href=\"https://lolico.me\">lolico.</a>")
                .contact(new Contact("Lolico Li", "https://lolico.me", "534619360@qq.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .version("1.0")
                .build();
    }

    private List<Parameter> getParameters() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name(Constants.AUTHORITIES_CLAIMS_NAME)
                .description("令牌")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build()
        );
        return parameters;
    }

}
