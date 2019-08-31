package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.Utils.TranslatorUtil;
import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.CandleService;
import br.com.codefleck.criptoclima.services.ResultService;
import br.com.codefleck.criptoclima.services.ResultSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.ZoneId;
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
    @Autowired
    CandleService candleService;

    @RequestMapping("/")
    String index(Model model) {

        //Grab latest ResultSets from database
        Optional<ResultSet> oneDayResultSetResponse = resultSetService.findLatestResultSetWithOneDayPeriod();
        Optional<ResultSet> twoDayResultSetResponse = resultSetService.findLatestResultSetWithTwoDaysPeriod();
        Optional<ResultSet> threeDayResultSetResponse = resultSetService.findLatestResultSetWithThreeDaysPeriod();
        Optional<ResultSet> fourDayResultSetResponse = resultSetService.findLatestResultSetWithFourDaysPeriod();
        Optional<ResultSet> fiveDayResultSetResponse = resultSetService.findLatestResultSetWithFiveDaysPeriod();
        Optional<ResultSet> sixDayResultSetResponse = resultSetService.findLatestResultSetWithSixDaysPeriod();

        //updating latest price
        Optional<Candle> latestCandle = candleService.findLastCandle();
        if (latestCandle.isPresent()){
            Candle candle = latestCandle.get();
            model.addAttribute("latestPrice", round(candle.getClose()));
        } else {
            model.addAttribute("latestPrice", "0");
        }

        //updating prediction for day 1
        if (oneDayResultSetResponse.isPresent()){
            ResultSet latestDailyResultSet = oneDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestDailyResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListByDailyResultSet = resultListByResultSetResponse.get();
                latestDailyResultSet.setResultList(resultListByDailyResultSet);
                for (Result result : resultListByDailyResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay1High = round(result.getPrediction());
                        model.addAttribute("weekDay1High", weekDay1High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay1Low = round(result.getPrediction());
                        model.addAttribute("weekDay1Low", weekDay1Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay1High", "0");
            model.addAttribute("weekDay1Low", "0");
        }

        //updating prediction for day 2
        if (twoDayResultSetResponse.isPresent()){
            ResultSet latestTwoDaysResultSet = twoDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestTwoDaysResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListForTwoDaysResultSet = resultListByResultSetResponse.get();
                latestTwoDaysResultSet.setResultList(resultListForTwoDaysResultSet);
                for (Result result : resultListForTwoDaysResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay2High = round(result.getPrediction());
                        model.addAttribute("weekDay2High", weekDay2High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay2Low = round(result.getPrediction());
                        model.addAttribute("weekDay2Low", weekDay2Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay2High", "0");
            model.addAttribute("weekDay2Low", "0");
        }

        //updating prediction for day 3
        if (threeDayResultSetResponse.isPresent()){
            ResultSet latestThreeDaysResultSet = threeDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestThreeDaysResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListForThreeDaysResultSet = resultListByResultSetResponse.get();
                latestThreeDaysResultSet.setResultList(resultListForThreeDaysResultSet);
                for (Result result : resultListForThreeDaysResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay3High = round(result.getPrediction());
                        model.addAttribute("weekDay3High", weekDay3High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay3Low = round(result.getPrediction());
                        model.addAttribute("weekDay3Low", weekDay3Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay3High", "0");
            model.addAttribute("weekDay3Low", "0");
        }

        //updating prediction for day 4
        if (fourDayResultSetResponse.isPresent()){
            ResultSet latestFourDaysResultSet = fourDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestFourDaysResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListForFourDaysResultSet = resultListByResultSetResponse.get();
                latestFourDaysResultSet.setResultList(resultListForFourDaysResultSet);
                for (Result result : resultListForFourDaysResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay4High = round(result.getPrediction());
                        model.addAttribute("weekDay4High", weekDay4High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay4Low = round(result.getPrediction());
                        model.addAttribute("weekDay4Low", weekDay4Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay4High", "0");
            model.addAttribute("weekDay4Low", "0");
        }

        //updating prediction for day 5
        if (fiveDayResultSetResponse.isPresent()){
            ResultSet latestFiveDaysResultSet = fiveDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestFiveDaysResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListForFiveDaysResultSet = resultListByResultSetResponse.get();
                latestFiveDaysResultSet.setResultList(resultListForFiveDaysResultSet);
                for (Result result : resultListForFiveDaysResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay5High = round(result.getPrediction());
                        model.addAttribute("weekDay5High", weekDay5High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay5Low = round(result.getPrediction());
                        model.addAttribute("weekDay5Low", weekDay5Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay5High", "0");
            model.addAttribute("weekDay5Low", "0");
        }

        //updating prediction for day 6
        if (sixDayResultSetResponse.isPresent()){
            ResultSet latestSixDaysResultSet = sixDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestSixDaysResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListForSixDaysResultSet = resultListByResultSetResponse.get();
                latestSixDaysResultSet.setResultList(resultListForSixDaysResultSet);
                for (Result result : resultListForSixDaysResultSet) {
                    if (result.getPriceCategory() == PriceCategory.HIGH){
                        double weekDay6High = round(result.getPrediction());
                        model.addAttribute("weekDay6High", weekDay6High);
                    } else if (result.getPriceCategory() == PriceCategory.LOW){
                        double weekDay6Low = round(result.getPrediction());
                        model.addAttribute("weekDay6Low", weekDay6Low);
                    }
                }
            }
        } else {
            model.addAttribute("weekDay6High", "0");
            model.addAttribute("weekDay6Low", "0");
        }

        //updating the calendar days of the week
        LocalDate localDate = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

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

    private double round(double n){
        return Math.round(n * 100d) / 100d;
    }
}
