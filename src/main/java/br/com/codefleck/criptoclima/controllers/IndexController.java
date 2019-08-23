package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.ResultService;
import br.com.codefleck.criptoclima.services.ResultSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Homepage controller.
 */
@Controller
public class IndexController {

    @Autowired
    ResultSetService resultSetService;
    @Autowired
    ResultService resultService;

    @RequestMapping("/")
    String index(Model model) {

        ResultSet latestResultSet = resultSetService.findFirstByOrderByIdDesc();

        if (latestResultSet.getId() != null) {
            List<Result> resultListByResultSet = resultService.getResultListByResultSetId(latestResultSet.getId());
            latestResultSet.setResultList(resultListByResultSet);
            if (resultListByResultSet != null && resultListByResultSet.size() > 0) {
                for (Result result : resultListByResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH) {
                        double high = Math.round(latestResultSet.getResultList().get(3).getPrediction());
                        model.addAttribute("high", high);
                    } else if (result.getPriceCategory() == PriceCategory.LOW) {
                        double low = Math.round(latestResultSet.getResultList().get(2).getPrediction());
                        model.addAttribute("low", low);
                    }
                }
            }
        } else {
            model.addAttribute("high", "0");
            model.addAttribute("low", "0");
        }
        return "index";
    }
}
