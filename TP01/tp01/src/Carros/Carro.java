package Carros;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Carro { // Na base de dados que eu escolhi não tinha atributos de data no modelo dia/mes/ano, então optei por incluir o atributo de data da carga.
    protected int id_carro;
    protected String modelo;
    protected int ano;
    protected float preco;
    protected int kilometragem;
    protected String tipo_combustivel;
    protected String tipo_vendedor;
    protected String cambio;
    protected String qtd_donos;
    protected String data_carga;
    protected boolean deleted;
    protected List<String> optionalFeatures;


   //getters e setters
    public int getAno() {
        return ano;
    }
    public void setAno(int ano) {
        this.ano = ano;
    }

    public float getPreco() {
        return preco;
    }
    public void setPreco(float preco) {
        this.preco = preco;
    }

    public String getCambio() {
        return cambio;
    }
    public void setCambio(String cambio) {
        this.cambio = cambio;
    }

    public String getData_carga() {
        return data_carga;
    }
    public void setData_carga(String data_carga) {
        this.data_carga = data_carga;
    }
    
    public int getId_carro() {
        return id_carro;
    }
    public void setId_carro(int id_carro) {
        this.id_carro = id_carro;
    }

    public int getKilometragem() {
        return kilometragem;
    }
    public void setKilometragem(int kilometragem) {
        this.kilometragem = kilometragem;
    }

    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getQtd_donos() {
        return qtd_donos;
    }
    public void setQtd_donos(String qtd_donos) {
        this.qtd_donos = qtd_donos;
    }   

    public String getTipo_combustivel() {
        return tipo_combustivel;
    }
    public void setTipo_combustivel(String tipo_combustivel) {
        this.tipo_combustivel = tipo_combustivel;
    }

    public String getTipo_vendedor() {
        return tipo_vendedor;
    }
    public void setTipo_vendedor(String tipo_vendedor) {
        this.tipo_vendedor = tipo_vendedor;
    }

    public List<String> getOptionalFeatures() {
        return optionalFeatures;
    }
    public void setOptionalFeatures(List<String> optionalFeatures) {
        this.optionalFeatures = optionalFeatures;
    }
    public boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    //construtores
    public Carro(int id_carro, String modelo, int ano, int preco, int kilometragem, String tipo_combustivel, String tipo_vendedor, String cambio, String qtd_donos, String data_carga, List<String> optionalFeatures, boolean deleted){
        this.id_carro = id_carro;
        this.modelo = modelo;
        this.preco = preco;
        this.kilometragem = kilometragem;
        this.tipo_combustivel = tipo_combustivel;
        this.tipo_vendedor = tipo_vendedor;
        this.cambio = cambio;
        this.qtd_donos = qtd_donos;
        this.data_carga = data_carga;
        this.optionalFeatures = optionalFeatures;
        this.deleted = deleted;
    }
    public Carro(){
        this.optionalFeatures = new ArrayList<>();
    }
	
    //formatação do objeto para mostrar no console
    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "\nID: " + id_carro +
               "\nModelo: " + modelo +
               "\nPreço: R$ " + df.format(preco) + 
               "\nKilometragem: " + kilometragem +
               "\nTipo de Combustível: " + tipo_combustivel +
               "\nTipo de Vendedor: " + tipo_vendedor +
               "\nCâmbio: " + cambio +
               "\nQuantidade de Donos: " + qtd_donos +
               "\nData de Carga: " + data_carga +
               "\n";
    }

    //descrever o objeto livro por meio de um vetor de bytes
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id_carro);
        dos.writeUTF(modelo);
        dos.writeInt(ano);
        dos.writeFloat(preco);
        dos.writeInt(kilometragem);
        dos.writeUTF(tipo_combustivel);
        dos.writeUTF(tipo_vendedor);
        dos.writeUTF(cambio);
        dos.writeUTF(qtd_donos);
        dos.writeUTF(data_carga);

        if (optionalFeatures == null) {
            dos.writeInt(0); // Write 0 if the list is null
        } else {
            dos.writeInt(optionalFeatures.size()); // Write the size of the list
            for (String feature : optionalFeatures) {
                dos.writeUTF(feature);
            }
        }
        dos.writeBoolean(deleted);

        return baos.toByteArray();
    }


    

    //ler do arquivo um vetor de bytes e carregar no objeto.
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id_carro = dis.readInt();
        modelo = dis.readUTF();
        ano = dis.readInt();
        preco = dis.readFloat();
        kilometragem = dis.readInt();
        tipo_combustivel = dis.readUTF();
        tipo_vendedor = dis.readUTF();
        cambio = dis.readUTF();
        qtd_donos = dis.readUTF();
        data_carga = dis.readUTF();

        // Read the list of optional features
        int listSize = dis.readInt();
        optionalFeatures = new ArrayList<>(); // Initialize the list
        for (int i = 0; i < listSize; i++) {
            optionalFeatures.add(dis.readUTF());
        }
        deleted = dis.readBoolean();
    }

    

    


}





