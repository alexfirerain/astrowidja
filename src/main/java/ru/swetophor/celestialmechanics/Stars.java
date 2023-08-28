package ru.swetophor.celestialmechanics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class Stars {
    public static void main(String[] args) {
        double magnitude = 6.0; // Видимая звездная величина

        System.out.println(getStarsOfMagnitude(magnitude));

    }

    private static String getStarsOfMagnitude(double magnitude) {
        String apiResponse = null;
        try {
            HttpURLConnection connection = getHttpURLConnection(magnitude);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                apiResponse = getApiResponse(connection);
            } else {
                System.out.println("Ошибка при получении данных от Simbad. Код ответа: " + responseCode);
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("Ошибка получения данных: " + e.getLocalizedMessage());
        }
        return apiResponse;
    }

    private static HttpURLConnection getHttpURLConnection(double magnitude) throws URISyntaxException, IOException {
        int limit = 1000;
        String apiUrl = ("https://api.simbad.u-strasbg.fr/simbad/sim-id?output.format=ASCII&output.max=%d&output.params=flux(V)&fluxfilter(V)<%s")
                    .formatted(limit, String.valueOf(magnitude));
        URL url = new URI(apiUrl).toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private static String getApiResponse(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
