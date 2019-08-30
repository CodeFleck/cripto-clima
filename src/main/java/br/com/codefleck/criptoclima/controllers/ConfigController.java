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
            trainingService.trainNeuralNet("BTC", epochs, "all", "daily");
        });

        return "redirect:/config";
    }

    @PostMapping("/init-training-two-days")
    String initTrainingTwoDays(@ModelAttribute("epochs") int epochs){

        System.out.println("Starting training for two days neural net. This may take a while...");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            trainingService.trainNeuralNet("BTC", epochs, "all", "twoDays");
        });

        return "redirect:/config";
    }

    @PostMapping("/init-training-three-days")
    String initTrainingThreeDays(@ModelAttribute("epochs") int epochs){

        System.out.println("Starting training for two days neural net. This may take a while...");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            trainingService.trainNeuralNet("BTC", epochs, "all", "threeDays");
        });

        return "redirect:/config";
    }

    @PostMapping("/init-training-four-days")
    String initTrainingFourDays(@ModelAttribute("epochs") int epochs){

        System.out.println("Starting training for two days neural net. This may take a while...");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            trainingService.trainNeuralNet("BTC", epochs, "all", "fourDays");
        });

        return "redirect:/config";
    }

    @PostMapping("/init-training-five-days")
    String initTrainingFiveDays(@ModelAttribute("epochs") int epochs){

        System.out.println("Starting training for two days neural net. This may take a while...");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            trainingService.trainNeuralNet("BTC", epochs, "all", "fiveDays");
        });

        return "redirect:/config";
    }

    @PostMapping("/init-training-weekly")
    String initTrainingWeekly(@ModelAttribute("epochs") int epochs){

        System.out.println("Starting training for weekly neural net. This may take a while...");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            trainingService.trainNeuralNet("BTC", epochs, "all", "weekly");
        });

        return "redirect:/config";
    }


    //setters
    @Autowired
    public void setTrainingService(NeuralNetTrainingService trainingService){
        this.trainingService = trainingService;
    }

}
