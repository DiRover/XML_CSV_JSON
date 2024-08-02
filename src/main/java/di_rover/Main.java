package di_rover;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);

        writeString(json, "data.json");

        List<Employee> list2 = parseXML("data.xml");

        String json2 = listToJson(list2);

        writeString(json2, "data2.json");

        String json3 = readString("data.json");

        List<Employee> list3 = jsonToList(json3);

        for (Employee employee : list3) {
            System.out.println(employee);
        }
    }

    static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);

            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); //add formatting for json
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        return gson.toJson(list, listType);
    }

    static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        List<Employee> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element employeeElement = (Element) node;
                long id = Long.parseLong(employeeElement.getElementsByTagName("id").item(0).getTextContent());
                String firstName = employeeElement.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = employeeElement.getElementsByTagName("lastName").item(0).getTextContent();
                String country = employeeElement.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(employeeElement.getElementsByTagName("age").item(0).getTextContent());
                Employee newEmployee = new Employee(id, firstName, lastName, country, age);
                list.add(newEmployee);
            }
        }

        return list;
    }

    static String readString(String fileName) {
        JSONParser parser = new JSONParser();

        JSONArray jsonArray = null;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            Object obj = parser.parse(bufferedReader);
            jsonArray = (JSONArray) obj;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    static List<Employee> jsonToList(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONArray jsonArray = (JSONArray) obj;

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        List<Employee> list = new ArrayList<>();

        for (Object elem : jsonArray) {
            Employee employee = gson.fromJson(elem.toString(), Employee.class);
            list.add(employee);
        }

        return list;
    }
}