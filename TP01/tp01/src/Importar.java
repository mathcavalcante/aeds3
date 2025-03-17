import Carros.Carro;
import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import Operações.CRUD;

public class Importar {
    private static final String CSV_FILE_PATH = "carros.csv"; // Nome do seu arquivo CSV
    private static final String BINARY_FILE_PATH = "dados/carros.db"; // Nome do arquivo binário
    private static int nextId = 1;

    // Método para criar uma lista de objetos Carro a partir de um arquivo CSV
    public static List<Carro> createList() {
        List<Carro> listaCarros = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            br.readLine(); // Pula a primeira linha (cabeçalho)
            String linha;
            while ((linha = br.readLine()) != null) {
                Carro carro = processCSVLine(linha);
                if (carro != null) {
                    listaCarros.add(carro);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
        return listaCarros;
    }

    // Método para processar uma linha do CSV e criar um objeto Carro
    private static Carro processCSVLine(String linha) {
        String[] parts = linha.split(",", 2); // Split into name and the rest
        if (parts.length != 2) {
            System.err.println("Linha CSV inválida: " + linha);
            return null;
        }

        String name = parts[0];
        String[] atributos = parts[1].split(",");

        if (atributos.length != 7) {
            System.err.println("Linha CSV inválida: " + linha);
            return null;
        }

        Carro carro = new Carro();
        try {
            carro.setId_carro(nextId++); // Generate ID
            carro.setModelo(name);
            carro.setAno(Integer.parseInt(atributos[0]));
            carro.setPreco(Float.parseFloat(atributos[1]));
            carro.setKilometragem(Integer.parseInt(atributos[2]));
            carro.setTipo_combustivel(atributos[3]);
            carro.setTipo_vendedor(atributos[4]);
            carro.setCambio(atributos[5]);
            carro.setQtd_donos(atributos[6]);
            // utilizei localDate pra obter a data atual e o datatimeformatter para formatar
            // pro padrão que eu quero
            LocalDate dataAtual = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = dataAtual.format(formatter);
            carro.setData_carga(dataFormatada);

        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter um atributo para número: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return carro;
    }

    // Método para criar um arquivo binário a partir de uma lista de objetos Carro
    public static void createByteOutput(List<Carro> lista) throws Exception {
        if (lista == null || lista.isEmpty()) {
            throw new Exception("Lista nula ou vazia, impossível continuar");
        }

        try (RandomAccessFile arq = new RandomAccessFile(BINARY_FILE_PATH, "rw")) {
            if (arq.length() == 0) {
                arq.writeInt(0); // Inicializa o contador de registros se o arquivo estiver vazio
            }
            for (Carro c : lista) {
                CRUD.create(c, arq);
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo binário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
