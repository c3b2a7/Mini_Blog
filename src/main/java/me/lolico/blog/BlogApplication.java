package me.lolico.blog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BlogApplication extends SpringBootServletInitializer implements CommandLineRunner {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BlogApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BlogApplication.class);
    }

    @Override
    public void run(String... args) {
        System.out.println("\n\n\t\t\t\t\t\t" + "  【BLOG FOR LOLICO】\n\n" +
                "\t\tDesigned and built with all the ❤ in the world by lolico.\n\n");
    }
}
