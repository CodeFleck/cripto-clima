package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.Utils.TranslatorUtil;
import br.com.codefleck.criptoclima.enitities.Candle;
import br.com.codefleck.criptoclima.enitities.LatestPrice;
import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.CandleService;
import br.com.codefleck.criptoclima.services.LatestPriceService;
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
    @Autowired
    LatestPriceService latestPriceService;

    @RequestMapping("/")
    String index(Model model) {

        //Grab latest ResultSets from database
        Optional<ResultSet> oneDayResultSetResponse = resultSetService.findLatestResultSetWithOneDayPeriod();
        Optional<ResultSet> twoDayResultSetResponse = resultSetService.findLatestResultSetWithTwoDaysPeriod();
        Optional<ResultSet> threeDayResultSetResponse = resultSetService.findLatestResultSetWithThreeDaysPeriod();
        Optional<ResultSet> fourDayResultSetResponse = resultSetService.findLatestResultSetWithFourDaysPeriod();
        Optional<ResultSet> fiveDayResultSetResponse = resultSetService.findLatestResultSetWithFiveDaysPeriod();
        Optional<ResultSet> sixDayResultSetResponse = resultSetService.findLatestResultSetWithSixDaysPeriod();
        Optional<LatestPrice> latestPriceOptional = latestPriceService.findLastLatestPrice();
        LatestPrice latestPrice = new LatestPrice();
        if (latestPriceOptional.isPresent()){
            latestPrice.setLatestPrice(latestPriceOptional.get().getLatestPrice());
        } else {
            latestPrice.setLatestPrice(0.0);
        }

        double weekDay1Close = 0;
        double weekDay2Close = 0;
        double weekDay3Close = 0;
        double weekDay4Close = 0;
        double weekDay5Close = 0;
        double weekDay6Close = 0;

        //updating prediction for day 1
        if (oneDayResultSetResponse.isPresent()){
            ResultSet latestDailyResultSet = oneDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestDailyResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListByDailyResultSet = resultListByResultSetResponse.get();
                latestDailyResultSet.setResultList(resultListByDailyResultSet);
                for (Result result : resultListByDailyResultSet) {
                    if (result.getPriceCategory() == PriceCategory.CLOSE){
                        weekDay1Close = round(result.getPrediction());
                        model.addAttribute("weekDay1Close", weekDay1Close);
                        model.addAttribute("weekDay1ChangePerc", round(latestPriceService.calculateDailyChangePercentage(latestPrice.getLatestPrice(), weekDay1Close))+"%");
                    }
                }
            }
        } else {
            model.addAttribute("weekDay1Close", "0");
            model.addAttribute("weekDay1ChangePerc", "0");
        }

        //updating prediction for day 2
        if (twoDayResultSetResponse.isPresent()){
            ResultSet latestTwoDaysResultSet = twoDayResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestTwoDaysResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListForTwoDaysResultSet = resultListByResultSetResponse.get();
                latestTwoDaysResultSet.setResultList(resultListForTwoDaysResultSet);
                for (Result result : resultListForTwoDaysResultSet) {
                    if (result.getPriceCategory() == PriceCategory.CLOSE){
                        weekDay2Close = round(result.getPrediction());
                        model.addAttribute("weekDay2Close", weekDay2Close);
                        model.addAttribute("weekDay2ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay1Close, weekDay2Close))+"%");
                    }
                }
            }
        } else {
            model.addAttribute("weekDay2Close", "0");
            model.addAttribute("weekDay2ChangePerc", "0");
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
                        weekDay3Close = round(result.getPrediction());
                        model.addAttribute("weekDay3Close", weekDay3Close);
                        model.addAttribute("weekDay3ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay2Close, weekDay3Close))+"%");
                    }
                }
            }
        } else {
            model.addAttribute("weekDay3Close", "0");
            model.addAttribute("weekDay3ChangePerc", "0");
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
                        weekDay4Close = round(result.getPrediction());
                        model.addAttribute("weekDay4Close", weekDay4Close);
                        model.addAttribute("weekDay4ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay3Close, weekDay4Close))+"%");
                    }
                }
            }
        } else {
            model.addAttribute("weekDay4Close", "0");
            model.addAttribute("weekDay4ChangePerc", "0");
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
                        weekDay5Close = round(result.getPrediction());
                        model.addAttribute("weekDay5Close", weekDay5Close);
                        model.addAttribute("weekDay5ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay4Close, weekDay5Close))+"%");
                    }
                }
            }
        } else {
            model.addAttribute("weekDay5Close", "0");
            model.addAttribute("weekDay5ChangePerc", "0");
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
                        weekDay6Close = round(result.getPrediction());
                        model.addAttribute("weekDay6Close", weekDay6Close);
                        model.addAttribute("weekDay6ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay5Close, weekDay6Close))+"%");
                    }
                }
            }
        } else {
            model.addAttribute("weekDay6Close", "0");
            model.addAttribute("weekDay6ChangePerc", "0");
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
