package Menu;

import Carros.Carro;
import Operações.CRUD;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Menu {
    private static Scanner scanner = new Scanner(System.in);
    private static int opcao;
    private static final String BINARY_FILE_PATH = "dados/carros.db";

    // método para mostrar o menu no console
    public static void Console() {
        do {
            System.out.println("---------------");
            System.out.println("\nSeja bem vindo ao catálogo de carros! \nDigite uma opção: ");
            System.out.println("\n---------------");
            System.out.println(
                    "\n1: Listar todos os carros \n2: Procurar um carro \n3: Adicionar novo carro \n4: Editar atributos de um carro \n5: Deletar um carro\n6: Ordenação externa \n7: Sair do menu \nSelecionar: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao == 1) {
                listCarro();
            } else if (opcao == 2) {
                searchCarro();
            } else if (opcao == 3) {
                addCarro();
            } else if (opcao == 4) {
                editCarro();
            } else if (opcao == 5) {
                deleteCarro();
            }else if (opcao == 6) {
                ordernacaoExterna();
            }
        } while (opcao != 7);

    }

    // método para listar todos os carros - já implementado na classe crud.
    public static void listCarro() {
        CRUD.list();
    }

    // método que procura o carro de acordo com seu id. para isso, é necessário
    // verificar se o id é válido ou não.
    private static void searchCarro() {
        System.out.println("\nDigite o id do carro: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index >= 1) {
            Carro c = CRUD.read(index);
            if (c == null) {
                System.out.println("\nO id não existe!");
            } else {
                System.out.println(c.toString());
                ;
            }
        }
    }

    // método para editar atributos de um carro
    private static void editCarro() {
        System.out.println("\nDigite o id do carro: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index >= 1) {
            Carro c = CRUD.read(index);
            if (c != null) {
                System.out.println("Carro encontrado: " + c.toString());
                System.out.println("\nQual atributo deseja editar?");
                System.out.println(
                        "1: Modelo \n2: Preço \n3: Kilometragem \n4: Tipo de Combustível \n5: Tipo de Vendedor \n6: Câmbio \n7: Quantidade de Donos \n8: Ano");
                int atributo = scanner.nextInt();
                scanner.nextLine();
                if (atributo == 1) {
                    System.out.println("Novo modelo: ");
                    c.setModelo(scanner.nextLine());
                } else if (atributo == 2) {
                    System.out.println("Novo preço: ");
                    c.setPreco(scanner.nextFloat());
                    scanner.nextLine();
                } else if (atributo == 3) {
                    System.out.println("Nova kilometragem: ");
                    c.setKilometragem(scanner.nextInt());
                    scanner.nextLine();
                } else if (atributo == 4) {
                    System.out.println("Novo tipo de combustível: ");
                    c.setTipo_combustivel(scanner.nextLine());
                } else if (atributo == 5) {
                    System.out.println("Novo tipo de vendedor: ");
                    c.setTipo_vendedor(scanner.nextLine());
                } else if (atributo == 6) {
                    System.out.println("Novo câmbio: ");
                    c.setCambio(scanner.nextLine());
                } else if (atributo == 7) {
                    System.out.println("Nova quantidade de donos: ");
                    c.setQtd_donos(scanner.nextLine());
                } else if (atributo == 8) {
                    System.out.println("Novo ano: ");
                    c.setAno(scanner.nextInt());
                    scanner.nextLine();
                } else {
                    System.out.println("Opção inválida.");
                    return;
                }
                if (CRUD.update(c)) {
                    System.out.println("Carro atualizado com sucesso!");
                } else {
                    System.out.println("Erro ao atualizar o carro!");
                }
            } else {
                System.out.println("\nO ID não existe!");
            }

        } else {
            System.out.println("\nO ID não existe!");
        }
    }

    // método para adicionar um carro -> necessário setar o id como 0, pois não é o
    // usuário que o define. como foi necessária a inserção de vários valores, optei
    // por limpar o buffer depois de cada inserção.
    public static void addCarro() {
        Carro c = new Carro();
        c.setId_carro(0);

        // utilizei localDate pra obter a data atual e o datatimeformatter para formatar
        // pro padrão que eu quero
        LocalDate dataAtual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = dataAtual.format(formatter);
        c.setData_carga(dataFormatada);

        System.out.println("\nDigite o modelo do carro: ");
        c.setModelo(scanner.nextLine());

        System.out.println("\nDigite o ano do carro: ");
        c.setAno(scanner.nextInt());
        scanner.nextLine();

        System.out.println("\nDigite o preço: ");
        c.setPreco(scanner.nextFloat());
        scanner.nextLine();

        System.out.println("\nDigite a kilometragem: ");
        c.setKilometragem(scanner.nextInt());
        scanner.nextLine();

        System.out.println("\nDigite o tipo de combustível (Petrol - CNV - Diesel - Electric - LPG): ");
        c.setTipo_combustivel(scanner.nextLine());

        System.out.println("\nDigite o vendedor do carro (Individual - Dealer - Trustmark Dealer): ");
        c.setTipo_vendedor(scanner.nextLine());

        System.out.println("\nDigite o tipo de câmbio: ");
        c.setCambio(scanner.nextLine());

        System.out.println(
                "\nDigite a quantidade de donos (First Owner - Second Owner - Third Owner - Fourth Owner - Fifth Owner - etc): ");
        c.setQtd_donos(scanner.nextLine());

        List<String> features = new ArrayList<>();
        String addMore;
        do {
            System.out.println("Deseja adicionar uma optional feature? (s/n)");
            addMore = scanner.nextLine();
            if (addMore.equalsIgnoreCase("s")) {
                System.out.println("Digite a optional feature: ");
                features.add(scanner.nextLine());
            }
        } while (addMore.equalsIgnoreCase("s"));
        c.setOptionalFeatures(features);

        try (RandomAccessFile arq = new RandomAccessFile(BINARY_FILE_PATH, "rw")) {
            CRUD.create(c, arq);
        } catch (Exception e) {
            System.err.println("Erro ao criar registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // método para deleção de um objeto carro por meio de seu id.
    public static void deleteCarro() {
        System.out.println("\nDigite o id do carro: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index >= 1) {
            boolean deleted = CRUD.delete(index);
            if (deleted) {
                System.out.println("\nCarro removido com sucesso!");
            } else {
                System.out.println("\nO ID não existe!");
            }

        } else {
            System.out.println("\nO ID não existe!");
        }
    }

    //implementei o algoritmo de ordenação externa na classe CRUD.
    //nesta chamada, o usuário insere o número de caminhos e a quantidade de registros por ordenação. 
    public static void ordernacaoExterna() {
        System.out.println("\n ---- Iniciando ordenação externa ----");

        System.out.println("Digite o número de caminhos (k): ");
        int numCaminhos = scanner.nextInt();
        System.out.println("Digite o número máximo de registros por ordenação em memória: ");
        int maxRegistrosMemoria = scanner.nextInt();

        try {
            CRUD.ordenacaoExterna(numCaminhos, maxRegistrosMemoria);
            System.out.println("\n ---- Ordenação externa concluída ----");
        } catch (IOException e) {
            System.err.println("\nErro durante a ordenação externa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeScanner() {
        scanner.close();
    }

}
