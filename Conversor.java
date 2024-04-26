import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class Conversor {

    private static final String API_KEY = "af14f0738d021192f5b28d05"; 

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nBem-vindo ao Conversor de Moeda!");
                System.out.println("Escolha a moeda de origem:");
                System.out.println("1. USD - Dolar Americano");
                System.out.println("2. EUR - Euro");
                System.out.println("3. GBP - Libra Esterlina");
                System.out.println("4. JPY - Iene Japones");
                System.out.println("5. AUD - Dolar Australiano");
                System.out.println("6. BRL - Real Brasileiro");
                System.out.println("0. Sair");
                System.out.print("Digite o numero da moeda de origem: ");

                int opcao = scanner.nextInt();
                if (opcao == 0) {
                    System.out.println("Saindo...");
                    break;
                }

                String moedaOrigem = getMoeda(opcao);

                System.out.println("Escolha a moeda de destino:");
                System.out.println("1. USD - Dolar Americano");
                System.out.println("2. EUR - Euro");
                System.out.println("3. GBP - Libra Esterlina");
                System.out.println("4. JPY - Iene Japones");
                System.out.println("5. AUD - Dolar Australiano");
                System.out.println("6. BRL - Real Brasileiro");
                System.out.println("0. Sair");
                System.out.print("Digite o numero da moeda de destino: ");
                opcao = scanner.nextInt();
                if (opcao == 0) {
                    System.out.println("Saindo...");
                    break;
                }

                String moedaDestino = getMoeda(opcao);
                System.out.print("Digite a quantidade a ser convertida: ");
                double quantidade = scanner.nextDouble();
                double taxaDeCambio = getTaxaDeCambio(moedaOrigem, moedaDestino);
                double valorConvertido = quantidade * taxaDeCambio;
                System.out.printf("%.2f %s equivalem a %.2f %s\n", quantidade, moedaOrigem, valorConvertido, moedaDestino);
            }
        } catch (IOException e) {
            System.out.println("Erro ao realizar a conversão: " + e.getMessage());
        }
    }

    private static String getMoeda(int opcao) {
        switch (opcao) {
            case 1:
                return "USD";
            case 2:
                return "EUR";
            case 3:
                return "GBP";
            case 4:
                return "JPY";
            case 5:
                return "AUD";
            case 6:
                return "BRL";
            default:
                throw new IllegalArgumentException("Opcao invalida.");
        }
    }

    private static double getTaxaDeCambio(String moedaOrigem, String moedaDestino) throws IOException {
        URI uri = URI.create("https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + moedaOrigem);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (conn.getResponseCode() != 200) {
            throw new IOException("Falha ao obter resposta da API. Codigo de resposta: " + conn.getResponseCode());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String jsonResponse = response.toString();
            int startIndex = jsonResponse.indexOf(moedaDestino);
            int endIndex = jsonResponse.indexOf(",", startIndex);
            String taxaStr = jsonResponse.substring(startIndex + 5, endIndex);
            return Double.parseDouble(taxaStr);
        } finally {
            conn.disconnect();
        }
    }
}
