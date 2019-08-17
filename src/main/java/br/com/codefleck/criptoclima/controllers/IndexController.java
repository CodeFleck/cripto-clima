package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.ResultSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Homepage controller.
 */
@Controller
public class IndexController {

    @Autowired
    ResultSetService resultSetService;

    @RequestMapping("/")
    String index(Model model){

        ResultSet latestResults = resultSetService.findFirstByOrderByIdDesc();
        double result = Math.round(latestResults.getResultList().get(0).getPrediction());
        model.addAttribute("result", result);
        return "index";
    }

}
