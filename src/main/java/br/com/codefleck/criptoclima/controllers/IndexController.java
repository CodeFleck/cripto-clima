package br.com.codefleck.criptoclima.controllers;

import br.com.codefleck.criptoclima.Utils.RSSParser;
import br.com.codefleck.criptoclima.Utils.TranslatorUtil;
import br.com.codefleck.criptoclima.enitities.LatestPrice;
import br.com.codefleck.criptoclima.enitities.PriceCategory;
import br.com.codefleck.criptoclima.enitities.results.Result;
import br.com.codefleck.criptoclima.enitities.results.ResultSet;
import br.com.codefleck.criptoclima.services.CandleService;
import br.com.codefleck.criptoclima.services.LatestPriceService;
import br.com.codefleck.criptoclima.services.ResultService;
import br.com.codefleck.criptoclima.services.ResultSetService;
import com.rometools.rome.io.FeedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
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

        RSSParser rssParser = new RSSParser();
        try {
            String cryptoFeed = rssParser.readRssFeed("https://www.google.com/alerts/feeds/12364797074637630541/18029993629479251356");
            model.addAttribute("cryptoNews", cryptoFeed);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }

        //Grab latest ResultSets from database
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
        Optional<ResultSet> predictionsResultSetResponse = resultSetService.findLatestResultSetWithOneDayPeriod();
        if (predictionsResultSetResponse.isPresent()){
            ResultSet latestDailyResultSet = predictionsResultSetResponse.get();
            Optional<List<Result>> resultListByResultSetResponse = resultService.getResultListByResultSetId(latestDailyResultSet.getId());
            if (resultListByResultSetResponse.isPresent()){
                List<Result> resultListByDailyResultSet = resultListByResultSetResponse.get();

                for (Result result : resultListByDailyResultSet) {
                    if (result.getResultsForWeekDay() == 1){
                        if (result.getPriceCategory() == PriceCategory.CLOSE){
                            weekDay1Close = round(result.getPrediction());
                            model.addAttribute("weekDay1Close", weekDay1Close);
                            model.addAttribute("weekDay1ChangePerc", round(latestPriceService.calculateDailyChangePercentage(latestPrice.getLatestPrice(), weekDay1Close))+"%");
                        }
                    }
                    if (result.getResultsForWeekDay() == 2){
                        if (result.getPriceCategory() == PriceCategory.CLOSE){
                            weekDay2Close = round(result.getPrediction());
                            model.addAttribute("weekDay2Close", weekDay2Close);
                            model.addAttribute("weekDay2ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay1Close, weekDay2Close))+"%");
                        }
                    }
                    if (result.getResultsForWeekDay() == 3){
                        if (result.getPriceCategory() == PriceCategory.CLOSE){
                            weekDay3Close = round(result.getPrediction());
                            model.addAttribute("weekDay3Close", weekDay3Close);
                            model.addAttribute("weekDay3ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay2Close, weekDay3Close))+"%");
                        }
                    }
                    if (result.getResultsForWeekDay() == 4){
                        if (result.getPriceCategory() == PriceCategory.CLOSE){
                            weekDay4Close = round(result.getPrediction());
                            model.addAttribute("weekDay4Close", weekDay4Close);
                            model.addAttribute("weekDay4ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay3Close, weekDay4Close))+"%");
                        }
                    }
                    if (result.getResultsForWeekDay() == 5){
                        if (result.getPriceCategory() == PriceCategory.CLOSE){
                            weekDay5Close = round(result.getPrediction());
                            model.addAttribute("weekDay5Close", weekDay5Close);
                            model.addAttribute("weekDay5ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay4Close, weekDay5Close))+"%");
                        }
                    }
                    if (result.getResultsForWeekDay() == 6){
                        if (result.getPriceCategory() == PriceCategory.CLOSE){
                            weekDay6Close = round(result.getPrediction());
                            model.addAttribute("weekDay6Close", weekDay6Close);
                            model.addAttribute("weekDay6ChangePerc", round(latestPriceService.calculateDailyChangePercentage(weekDay5Close, weekDay6Close))+"%");
                        }
                    }
                }
            }
        } else {
            model.addAttribute("weekDay1Close", "0");
            model.addAttribute("weekDay2Close", "0");
            model.addAttribute("weekDay3Close", "0");
            model.addAttribute("weekDay4Close", "0");
            model.addAttribute("weekDay5Close", "0");
            model.addAttribute("weekDay6Close", "0");
            model.addAttribute("weekDay1ChangePerc", "0");
            model.addAttribute("weekDay2ChangePerc", "0");
            model.addAttribute("weekDay3ChangePerc", "0");
            model.addAttribute("weekDay4ChangePerc", "0");
            model.addAttribute("weekDay5ChangePerc", "0");
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
