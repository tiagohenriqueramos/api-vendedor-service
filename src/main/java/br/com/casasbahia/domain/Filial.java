package br.com.casasbahia.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class Filial {

    private String id;
    private String nome;
    private String cnpj;
    private String cidade;
    private String uf;
    private String tipo;
    private boolean ativo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataCadastro;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataUpdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDate getDataUpdate() {
        return dataUpdate;
    }

    public void setDataUpdate(LocalDate dataUpdate) {
        this.dataUpdate = dataUpdate;
    }
}
