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
import java.util.Optional;

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

        Optional<ResultSet> response = resultSetService.findFirstByOrderByIdDesc();
        if (response.isPresent()){
            ResultSet latestResultSet = response.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){

                List<Result> resultListByResultSet = resultListByResultSetResponse.get();
                latestResultSet.setResultList(resultListByResultSet);

                for (Result result : resultListByResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double high = Math.round(result.getPrediction());
                        model.addAttribute("high", high);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double low = Math.round(result.getPrediction());
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
