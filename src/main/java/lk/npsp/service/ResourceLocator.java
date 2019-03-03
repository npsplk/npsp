package lk.npsp.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// fetch data from a csv file and return as a List<String>

@Service
public class ResourceLocator {
    public  List<List<String>> locateResource(String classPath, String delimiter) throws IOException {
        List<List<String>> resources= new ArrayList<>();

        File tableHeadersFile = new ClassPathResource(classPath).getFile();
        FileReader tableHeadersFileReader = new FileReader(tableHeadersFile);
        BufferedReader br = new BufferedReader(tableHeadersFileReader);
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(delimiter);
            resources.add(new ArrayList<>(Arrays.asList(values)));
        }
        return resources;
    }

}
