package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class MarvelServiceApplication {

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        SpringApplication.run(MarvelServiceApplication.class, args);
    }

}
