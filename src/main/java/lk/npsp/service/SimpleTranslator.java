package lk.npsp.service;

import lk.npsp.domain.enumeration.ScreenLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class SimpleTranslator {

    private Map<String, List<String>> dictionary = new HashMap<>();

    public SimpleTranslator(ResourceLocator resourceLocator) throws IOException{
        List<List<String>> dictionaryArray= resourceLocator.locateResource(
            "schedule-screen/dictionary.csv",",");

        for(List<String> dictionaryItem : dictionaryArray){
            dictionary.put(dictionaryItem.get(0),dictionaryItem);
        }
    }

    private String translateWord(String inputString, ScreenLanguage language) {
        if (!dictionary.containsKey(inputString)) {
            return inputString;
        }
        return dictionary.get(inputString).get(language.getValue());
    }

    public String translate(String inputString, ScreenLanguage language) {

       String[] words =inputString.split(" ");

        for (String word : words) {
            inputString = inputString.replaceAll(word, translateWord(word, language));
        }

        return inputString;
    }

    public List<String> translate(List<String> stringList,ScreenLanguage language){
        List<String> translatedList= new ArrayList<>();
        stringList.forEach((word)->translatedList.add(translateWord(word,language)));
        return  translatedList;
    }

}
