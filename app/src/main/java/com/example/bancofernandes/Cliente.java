package com.example.bancofernandes;

import com.google.firebase.database.IgnoreExtraProperties;

public class Cliente {
    private String cpf;
    private String nome;
    private String UF;
    private String data;
    private String email;
    private String senha;
    private String telefone;
    private String endereco;
    public Cliente(){

    }

    public Cliente(String cpf, String data, String email, String endereco, String nome, String senha, String telefone, String UF) {
        this.cpf = cpf;
        this.nome = nome;
        this.UF = UF;
        this.data = data;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public String getCPF() {
        return this.cpf;
    }

    public String getNome() {
        return this.nome;
    }

    public String getUF() {return this.UF; }

    public String getData() {return this.data;}

    public String getEmail() {
        return this.email;
    }

    public String getSenha() {
        return this.senha;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public String getEndereco() {
        return this.endereco;
    }

}
