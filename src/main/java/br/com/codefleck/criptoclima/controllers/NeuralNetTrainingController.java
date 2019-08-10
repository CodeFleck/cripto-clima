package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.services.NeuralNetTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/neural-net")
public class NeuralNetTrainingController {

    @Autowired
    NeuralNetTrainingService trainingService;

    @GetMapping
    String neuralNet(Model model){
        model.addAttribute("breadcrumbs", "redes neurais");
        return "neuralnettraining";
    }

    @PostMapping("/init")
    String initTraining(@ModelAttribute("epochs") int epochs){

        System.out.println("Starting training for neural nets. This may take a while...");

        ExecutorService executor = Executors.newFixedThreadPool(5); //picking a max of 5 threads at random, change this value as needed
        executor.submit(() -> {
            trainingService.trainNeuralNet("BTC", epochs, "all");
        });

        return "redirect:/neural-net";
    }
}
