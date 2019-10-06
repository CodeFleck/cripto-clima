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
@RequestMapping("/config")
public class ConfigController {


    private NeuralNetTrainingService trainingService;

    @GetMapping
    String neuralNet(Model model){
        model.addAttribute("breadcrumbs", "config");
        return "config";
    }

    @PostMapping("/init-training-daily")
    String initTrainingDaily(@ModelAttribute("epochs") int epochs){

        System.out.println("Starting training for daily neural net. This may take a while...");

        //picking a max of 5 threads at random, pretty sure dl4j creates it's own pool
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            trainingService.trainNeuralNet(epochs, "all");
        });

        return "redirect:/config";
    }

    //setters
    @Autowired
    public void setTrainingService(NeuralNetTrainingService trainingService){
        this.trainingService = trainingService;
    }

}
