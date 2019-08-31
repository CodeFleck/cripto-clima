package br.com.codefleck.criptoclima.Utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@Component
public class TranslatorUtil {

    public String translateDayOfWeek(String word){
        switch (word) {
            case "monday": return "Segunda";
            case "tuesday": return "Terça";
            case "wednesday": return "Quarta";
            case "thursday": return "Quinta";
            case "friday": return "Sexta";
            case "saturday": return "Sábado";
            case "sunday": return "Domingo";
            default: throw new NoSuchElementException();
        }
    }

    public String translateMonth(LocalDate localDate){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");
        String dayAndMonth = localDate.format(formatter);

        String translatedMonth = "";

        switch (dayAndMonth.substring(3,6)) {
            case "Jan": translatedMonth = "Jan"; break;
            case "Feb": translatedMonth = "Feb"; break;
            case "Mar": translatedMonth = "Mar"; break;
            case "Apr": translatedMonth = "Abr"; break;
            case "May": translatedMonth = "Mai"; break;
            case "Jun": translatedMonth = "Jun"; break;
            case "Jul": translatedMonth = "Jul"; break;
            case "Aug": translatedMonth = "Ago"; break;
            case "Sep": translatedMonth = "Set"; break;
            case "Oct": translatedMonth = "Out"; break;
            case "Nov": translatedMonth = "Nov"; break;
            case "Dec": translatedMonth = "Dez"; break;
            default: translatedMonth = dayAndMonth.substring(3,6);
        }
        return dayAndMonth.substring(0,2).concat(" ").concat(translatedMonth);
    }
}
