import java.util.*;
import Carros.Carro;
import Menu.Menu;

public class Main {
    public static void main(String[] args) {
        List<Carro> listCarros = Importar.createList();

        try {
            Importar.createByteOutput(listCarros);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n ----- Iniciando programa ----- \n");
        Menu.Console();
        System.out.println("\n ----- Fechando programa ----- \n");
        Menu.closeScanner();
    }
}
