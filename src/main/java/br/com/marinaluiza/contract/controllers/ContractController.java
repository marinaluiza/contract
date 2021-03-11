package br.com.marinaluiza.contract.controllers;

import br.com.marinaluiza.contract.model.Contract;
import br.com.marinaluiza.contract.services.ContractTransformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/contract")
public class ContractController {

        ContractTransformationService service;

        public ContractController(ContractTransformationService service) {
            this.service = service;
        }

        @PostMapping("/")
        public ResponseEntity<String> get(HttpServletRequest request) {
            ClassLoader classLoader = getClass().getClassLoader();
            String path = classLoader.getResource("Simple_Meat_Sample.txt").getFile();
            try (Stream<String> lines = Files.lines(Paths.get(path))) {
                String content = lines.collect(Collectors.joining(System.lineSeparator()));
                service.createDiagram(content);
                System.out.println(content);
                return ResponseEntity.ok("Hello World!");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

}
