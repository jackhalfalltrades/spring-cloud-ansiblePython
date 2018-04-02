package com.spring.cloud.ansiblePython;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableEncryptableProperties
@EnableConfigurationProperties
@SpringBootApplication
public class AnsiblePythonAPI {

    public static void main(String[] args) {
        SpringApplication.run(AnsiblePythonAPI.class, args);
    }
}
