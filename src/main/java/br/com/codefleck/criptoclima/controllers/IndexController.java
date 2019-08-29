package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.Utils.TranslatorUtil;
import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.TimePeriod;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.ResultService;
import br.com.codefleck.criptoclima.services.ResultSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
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
    @Autowired
    TranslatorUtil translator;

    @RequestMapping("/")
    String index(Model model) {

        Optional<ResultSet> weeklyResultSetResponse = resultSetService.findLatestWeeklyResultSet();
        Optional<ResultSet> dailyResultSetResponse = resultSetService.findLatestDailyResultSet();

        //updating prediction for day 1
        if (dailyResultSetResponse.isPresent()){
            ResultSet latestDailyResultSet = dailyResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestDailyResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListByDailyResultSet = resultListByResultSetResponse.get();
                latestDailyResultSet.setResultList(resultListByDailyResultSet);
                for (Result result : resultListByDailyResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay1High = Math.round(result.getPrediction());
                        model.addAttribute("weekDay1High", weekDay1High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay1Low = Math.round(result.getPrediction());
                        model.addAttribute("weekDay1Low", weekDay1Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay1High", "0");
            model.addAttribute("weekDay1Low", "0");
        }

        //updating prediction for day 6
        if (weeklyResultSetResponse.isPresent()){
            ResultSet latestWeeklyResultSet = weeklyResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestWeeklyResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListByWeeklyResultSet = resultListByResultSetResponse.get();
                latestWeeklyResultSet.setResultList(resultListByWeeklyResultSet);
                for (Result result : resultListByWeeklyResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay6High = Math.round(result.getPrediction());
                        model.addAttribute("weekDay6High", weekDay6High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay6Low = Math.round(result.getPrediction());
                        model.addAttribute("weekDay6Low", weekDay6Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay6High", "0");
            model.addAttribute("weekDay6Low", "0");
        }

        //updating the calendar days of the week
        LocalDate localDate = LocalDate.now();
        model.addAttribute("dayAndMonth", translator.translateMonth(localDate));
        model.addAttribute("weekDayToday", translator.translateDayOfWeek(String.valueOf(localDate.getDayOfWeek()).toLowerCase()));
        model.addAttribute("weekDay1", translator.translateDayOfWeek(String.valueOf(localDate.plusDays(1).getDayOfWeek()).toLowerCase()));
        model.addAttribute("weekDay2", translator.translateDayOfWeek(String.valueOf(localDate.plusDays(2).getDayOfWeek()).toLowerCase()));
        model.addAttribute("weekDay3", translator.translateDayOfWeek(String.valueOf(localDate.plusDays(3).getDayOfWeek()).toLowerCase()));
        model.addAttribute("weekDay4", translator.translateDayOfWeek(String.valueOf(localDate.plusDays(4).getDayOfWeek()).toLowerCase()));
        model.addAttribute("weekDay5", translator.translateDayOfWeek(String.valueOf(localDate.plusDays(5).getDayOfWeek()).toLowerCase()));
        model.addAttribute("weekDay6", translator.translateDayOfWeek(String.valueOf(localDate.plusDays(6).getDayOfWeek()).toLowerCase()));

        return "index";
    }
}
