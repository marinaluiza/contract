package br.com.marinaluiza.contract.controllers;

import br.com.marinaluiza.contract.services.ContractTransformationService;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
        public JSONObject get(HttpServletRequest request) {
            ClassLoader classLoader = getClass().getClassLoader();
            String path = classLoader.getResource("meatSaleContract.txt").getFile();
            try (Stream<String> lines = Files.lines(Paths.get(path))) {
                String content = lines.collect(Collectors.joining(System.lineSeparator()));
                return service.createDiagram(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

}
