package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyReaderUtil {
    public String readFileData(String properties) {
        Properties prop = new Properties();
        //File file = new File("src/test/resources/properties/QA.properties");
        File file = new File("Resources/properties/QA.properties");
        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            prop.load(fileInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        properties = prop.getProperty(properties);
        return properties;
    }
}
