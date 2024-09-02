package br.com.kjscripts.tabelafipe.main;

import br.com.kjscripts.tabelafipe.model.Data;
import br.com.kjscripts.tabelafipe.model.Model;
import br.com.kjscripts.tabelafipe.model.Vehicle;
import br.com.kjscripts.tabelafipe.service.ConsumeApi;  // Class for consuming data from the API
import br.com.kjscripts.tabelafipe.service.ConvertData;  // Class for converting data (optional, depending on your needs)

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;  // Class for user input
import java.util.stream.Collectors;

/**
 * Main class for the application that interacts with the Tabela FIPE API.
 */
public class Main {

    private final ConsumeApi consumeApi = new ConsumeApi();  // Instance of ConsumeApi for making API calls
    private final ConvertData convertData = new ConvertData();  // Instance of ConvertData for data conversion (optional)
    private final Scanner reading = new Scanner(System.in);  // Scanner for user input

    private final String CLIENT_URL = "https://parallelum.com.br/fipe/api/v1/";  // Base URL for the Tabela FIPE API

    private String url = "";  // Variable to store the constructed URL based on user selection

    /**
     * Enum representing the different vehicle types supported by the API.
     */
    enum VehicleType {
        CARROS("carros/marcas"),  // Path for car brands
        MOTOS("motos/marcas"),    // Path for motorcycle brands
        CAMINHOES("caminhoes/marcas");  // Path for truck brands

        private final String path;

        VehicleType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    /**
     * Main method that starts the application and prompts the user for vehicle selection.
     */
    public void getData() {
        int vehicleChoice = 0;  // Variable to store the user's vehicle choice
        boolean validInput = false;  // Flag to indicate if the input is valid
        var json = "";  // Variable to store the JSON response

        while (!validInput) {  // Loop until valid input is received
            System.out.println("Digite um número para iniciar a busca pelo tipo de veículo desejado: " +
                    "\n1 = Carros" +
                    "\n2 = Motos" +
                    "\n3 = Caminhões");

            try {
                vehicleChoice = reading.nextInt();  // Get user's vehicle choice as an integer
                reading.nextLine();  // Consume the newline character
                validInput = true;  // Set the flag to true as the input is valid
            } catch (Exception e) {  // Catch any input exceptions
                System.out.println("Entrada inválida. Por favor, digite um número.");  // Print error message
                reading.next();  // Consume the invalid input
            }
        }

        VehicleType vehicleType = getVehicleType(vehicleChoice);  // Map the user's choice to the corresponding VehicleType
        if (vehicleType != null) {  // Check if the choice is valid
            url = CLIENT_URL + vehicleType.getPath();  // Construct the URL based on the user's vehicle choice
            json = consumeApi.getData(url);  // Get data from the API

            var brands = convertData.getListData(json, Data.class);  // Convert the JSON response into a list of Data objects

            System.out.println("Marcas: ");
            brands.stream()
                    .sorted(Comparator.comparing(Data::code))  // Sort brands by code
                    .forEach(System.out::println);  // Print each brand
        } else {
            System.out.println("Opção inválida, digite um número entre 1 e 3");  // Print error message if the choice is invalid
        }

        System.out.println("Digite o código da marca para pesquisar");  // Prompt the user to enter a brand code
        var brandCode = reading.nextLine();  // Get the brand code from the user

        url += "/" + brandCode + "/modelos";  // Append the brand code to the URL to get models

        System.out.println(url);  // Print the constructed URL

        json = consumeApi.getData(url);  // Get data from the API for the selected brand
        var modelList = convertData.getData(json, Model.class);  // Convert the JSON response into a Model object

        System.out.println("Modelos: ");
        modelList.models().stream()
                .sorted(Comparator.comparing(Data::code))  // Sort models by code
                .forEach(System.out::println);  // Print each model

        System.out.println("Digite o nome de um veículo para buscar");  // Prompt the user to enter a vehicle name
        var vehicleName = reading.nextLine();  // Get the vehicle name from the user

        List<Data> filteredVehicles = modelList.models().stream()
                .filter(v -> v.name().toLowerCase().contains(vehicleName.toLowerCase()))  // Filter vehicles by name
                .collect(Collectors.toList());  // Collect the filtered vehicles into a list

        System.out.println("Filtrados");
        filteredVehicles.forEach(System.out::println);  // Print the filtered vehicles

        System.out.println("Digite o código do modelo para buscar seus dados detalhados");  // Prompt the user to enter a model code
        var modelCode = reading.nextLine();  // Get the model code from the user

        url += "/" + modelCode + "/anos";  // Append the model code to the URL to get years
        json = consumeApi.getData(url);  // Get data from the API for the selected model

        List<Data> years = convertData.getListData(json, Data.class);  // Convert the JSON response into a list of years

        List<Vehicle> vehicles = new ArrayList<Vehicle>();  // List to store vehicle details

        for (int i = 0; i < years.size(); i++) {  // Iterate through the list of years
            var urlYears = url + "/" + years.get(i).code();  // Construct the URL for each year
            json = consumeApi.getData(urlYears);  // Get data from the API for each year
            Vehicle vehicle = convertData.getData(json, Vehicle.class);  // Convert the JSON response into a Vehicle object
            vehicles.add(vehicle);  // Add the vehicle to the list
        }

        System.out.println("Veículos filtrados: ");
        vehicles.forEach(System.out::println);  // Print the vehicle details
    }

    /**
     * Method that maps user choice to the corresponding VehicleType enum value.
     *
     * @param vehicleChoice Integer representing the user's chosen vehicle type (1, 2, or 3)
     * @return VehicleType The corresponding VehicleType enum value or null if invalid choice
     */
    private VehicleType getVehicleType(int vehicleChoice) {
        switch (vehicleChoice) {
            case 1:
                return VehicleType.CARROS;  // Return VehicleType.CARROS for choice 1
            case 2:
                return VehicleType.MOTOS;  // Return VehicleType.MOTOS for choice 2
            case 3:
                return VehicleType.CAMINHOES;  // Return VehicleType.CAMINHOES for choice 3
            default:
                return null;  // Return null for any other choice
        }
    }
}