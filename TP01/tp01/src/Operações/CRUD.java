package Operações;

import Carros.Carro;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CRUD {

    private static final String BINARY_FILE_PATH = "dados/carros.db";
    private static final String TEMP_FILE_PREFIX = "dados/temp_";
    private static final String ORDERED_FILE_PATH = "dados/carros_ordenado.db";
 

    public static boolean create(Carro carro, RandomAccessFile arq) {
        try (RandomAccessFile file = new RandomAccessFile(BINARY_FILE_PATH, "rw")) {
            int lastUsedId = getLastUsedId(file);
            if (carro.getId_carro() == 0) {
                carro.setId_carro(lastUsedId + 1);
            }

            byte[] ba = carro.toByteArray();
            file.seek(file.length());
            file.writeInt(ba.length);
            file.write(ba);

            updateLastUsedId(file, carro.getId_carro());

            return true;
        } catch (IOException e) {
            System.err.println("Erro ao criar registro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Carro read(int id) {
        try (RandomAccessFile arq = new RandomAccessFile(BINARY_FILE_PATH, "r")) {
            int lastUsedId = getLastUsedId(arq);
            if (lastUsedId == 0) {
                return null;
            }
            arq.seek(4);
            while (arq.getFilePointer() < arq.length()) {
                int tam = arq.readInt();
                byte[] ba = new byte[tam];
                arq.read(ba);
                Carro c = new Carro();
                c.fromByteArray(ba);
                if (c.getId_carro() == id && !c.getDeleted()) {
                    return c;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler registro: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean update(Carro carro) {
        try (RandomAccessFile arq = new RandomAccessFile(BINARY_FILE_PATH, "rw")) {
            int lastUsedId = getLastUsedId(arq);
            if (lastUsedId == 0) {
                return false;
            }
            arq.seek(4);
            while (arq.getFilePointer() < arq.length()) {
                long pos = arq.getFilePointer();
                int tam = arq.readInt();
                byte[] ba = new byte[tam];
                arq.read(ba);
                Carro c = new Carro();
                c.fromByteArray(ba);
                if (c.getId_carro() == carro.getId_carro() && !c.getDeleted()) {
                    byte[] newBa = carro.toByteArray();
                    if (newBa.length <= tam) {
                        arq.seek(pos);
                        arq.writeInt(newBa.length);
                        arq.write(newBa);
                    } else {
                        c.setDeleted(true);
                        arq.seek(pos);
                        arq.writeInt(ba.length);
                        arq.write(c.toByteArray());
                        arq.seek(arq.length());
                        arq.writeInt(newBa.length);
                        arq.write(newBa);
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao atualizar registro: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(int id) {
        try (RandomAccessFile arq = new RandomAccessFile(BINARY_FILE_PATH, "rw")) {
            int lastUsedId = getLastUsedId(arq);
            if (lastUsedId == 0) {
                return false;
            }
            arq.seek(4);
            while (arq.getFilePointer() < arq.length()) {
                long pos = arq.getFilePointer();
                int tam = arq.readInt();
                byte[] ba = new byte[tam];
                arq.read(ba);
                Carro c = new Carro();
                c.fromByteArray(ba);
                if (c.getId_carro() == id && !c.getDeleted()) {
                    c.setDeleted(true);
                    arq.seek(pos);
                    arq.writeInt(ba.length);
                    arq.write(c.toByteArray());
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao deletar registro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void list() {
        try (RandomAccessFile arq = new RandomAccessFile(BINARY_FILE_PATH, "r")) {
            int lastUsedId = getLastUsedId(arq);
            
            if (lastUsedId == 0) {
                System.out.println("Não há carros cadastrados!");
                return;
            }
            if(arq.length() <= 4){
                System.out.println("Não há carros cadastrados!");
                return;
            }
            arq.seek(4);
            while (arq.getFilePointer() < arq.length()) {
                try{
                    int tam = arq.readInt();
                    if (tam <= 0 || tam > arq.length() - arq.getFilePointer()) {
                        System.err.println("Registro corrompido ou tamanho inválido. Pulando registro.");
                        arq.seek(arq.getFilePointer() + (tam > 0 ? tam : 0));
                        continue;
                    }
                    
                    byte[] ba = new byte[tam];
                    arq.read(ba);
                    Carro c = new Carro();
                    c.fromByteArray(ba);
                    if (!c.getDeleted()) {
                        System.out.println(c.toString());
                    }
                }catch(EOFException e){
                    System.err.println("Erro ao listar registros: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao listar registros: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static int getLastUsedId(RandomAccessFile arq) throws IOException {
        arq.seek(0);
        if (arq.length() < 4) {
            return 0;
        }
        return arq.readInt();
    }

    private static void updateLastUsedId(RandomAccessFile arq, int id) throws IOException {
        arq.seek(0);
        arq.writeInt(id);
    }
    
    public static void ordenacaoExterna(int numCaminhos, int maxRegistrosMemoria) throws IOException {
        // dividir o arquivo em subarquivos ordenados
        List<String> subArquivos = dividirArquivo(maxRegistrosMemoria);

        // intercalar os subarquivos
        intercalarArquivos(subArquivos, numCaminhos);

        // substituir o arquivo original pelo ordenado
        File originalFile = new File(BINARY_FILE_PATH);
        File orderedFile = new File(ORDERED_FILE_PATH);

        if (originalFile.delete()) {
            if (!orderedFile.renameTo(originalFile)) {
                throw new IOException("Erro ao renomear o arquivo ordenado.");
            }
        } else {
            throw new IOException("Erro ao deletar o arquivo original.");
        }

        // limpar os arquivos temporários
        for (String subArquivo : subArquivos) {
            new File(subArquivo).delete();
        }
    }

    private static List<String> dividirArquivo(int maxRegistrosMemoria) throws IOException {
        List<String> subArquivos = new ArrayList<>();
        List<Carro> registros = new ArrayList<>();
        int subArquivoCount = 0;

        try (RandomAccessFile arq = new RandomAccessFile(BINARY_FILE_PATH, "r")) {
            arq.seek(4); // pula o cabeçalho
            while (arq.getFilePointer() < arq.length()) {
                int tam = arq.readInt();
                byte[] ba = new byte[tam];
                arq.read(ba);
                Carro c = new Carro();
                c.fromByteArray(ba);
                if (!c.getDeleted()) {
                    registros.add(c);
                }

                if (registros.size() == maxRegistrosMemoria) {
                    subArquivos.add(ordenarESalvarSubArquivo(registros, subArquivoCount++));
                    registros.clear();
                }
            }

            if (!registros.isEmpty()) {
                subArquivos.add(ordenarESalvarSubArquivo(registros, subArquivoCount++));
            }
        }
        return subArquivos;
    }

    private static String ordenarESalvarSubArquivo(List<Carro> registros, int subArquivoCount) throws IOException {
        Collections.sort(registros, (c1, c2) -> Integer.compare(c1.getId_carro(), c2.getId_carro()));
        String subArquivoNome = TEMP_FILE_PREFIX + subArquivoCount + ".db";
        try (RandomAccessFile subArquivo = new RandomAccessFile(subArquivoNome, "rw")) {
            subArquivo.writeInt(0); // Inicializa o cabeçalho do subarquivo
            for (Carro c : registros) {
                byte[] ba = c.toByteArray();
                subArquivo.writeInt(ba.length);
                subArquivo.write(ba);
                updateLastUsedId(subArquivo, c.getId_carro());
            }
        }
        return subArquivoNome;
    }
    

    private static void intercalarArquivos(List<String> subArquivos, int numCaminhos) throws IOException {
        if (subArquivos.size() == 1) {
            File subFile = new File(subArquivos.get(0));
            File orderedFile = new File(ORDERED_FILE_PATH);
            if (!subFile.renameTo(orderedFile)) {
                throw new IOException("Erro ao renomear o arquivo ordenado.");
            }
            return;
        }
    
        List<RandomAccessFile> arquivos = new ArrayList<>();
        List<Carro> carros = new ArrayList<>();
        List<Integer> tam = new ArrayList<>();
        List<Long> pos = new ArrayList<>();
    
        for (String subArquivo : subArquivos) {
            RandomAccessFile arq = new RandomAccessFile(subArquivo, "r");
            arquivos.add(arq);
            arq.seek(4); //pula o cabeçalho
            try {
                tam.add(arq.readInt());
                byte[] ba = new byte[tam.get(tam.size() - 1)];
                arq.read(ba);
                Carro c = new Carro();
                c.fromByteArray(ba);
                carros.add(c);
                pos.add(arq.getFilePointer());
            } catch (EOFException e) {
                // se ocorrer EOFException ao ler o primeiro registro, o arquivo está vazio ou corrompido
                // nesse caso, removemos o arquivo da lista e continuamos.
                System.err.println("Arquivo vazio ou corrompido: " + subArquivo);
                arq.close();
                
            }
        }
    
        try (RandomAccessFile arqOrdenado = new RandomAccessFile(ORDERED_FILE_PATH, "rw")) {
            arqOrdenado.writeInt(0);
            while (!carros.isEmpty()) {
                int menorId = Integer.MAX_VALUE;
                int menorIndex = -1;
                for (int i = 0; i < carros.size(); i++) {
                    if (carros.get(i).getId_carro() < menorId) {
                        menorId = carros.get(i).getId_carro();
                        menorIndex = i;
                    }
                }
    
                byte[] ba = carros.get(menorIndex).toByteArray();
                arqOrdenado.writeInt(ba.length);
                arqOrdenado.write(ba);
                updateLastUsedId(arqOrdenado, carros.get(menorIndex).getId_carro());
    
                try {
                    if (arquivos.get(menorIndex).getFilePointer() < arquivos.get(menorIndex).length()) {
                        tam.set(menorIndex, arquivos.get(menorIndex).readInt());
                        byte[] ba2 = new byte[tam.get(menorIndex)];
                        arquivos.get(menorIndex).read(ba2);
                        Carro c = new Carro();
                        c.fromByteArray(ba2);
                        carros.set(menorIndex, c);
                    } else {
                        arquivos.get(menorIndex).close();
                        arquivos.remove(menorIndex);
                        carros.remove(menorIndex);
                        tam.remove(menorIndex);
                    }
                } catch (EOFException e) {
                    // se ocorrer eofexception ao ler o próximo registro, o arquivo acabou
                    // nesse caso, o arquivo é fechado e removido das listas
                    arquivos.get(menorIndex).close();
                    arquivos.remove(menorIndex);
                    carros.remove(menorIndex);
                    tam.remove(menorIndex);
                }
            }
        }
    
        for (RandomAccessFile arq : arquivos) {
            arq.close();
        }
    }

}
