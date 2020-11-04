package com.example.bancofernandes;

public class Conta {

    private int agencia;
    private String cpf;
    private float lim_credito;
    private float lim_credito_max;
    private int numConta;
    private float saldo;
    public Conta(){

    }
    public Conta(int agencia, String cpf, float lim_credito, float lim_credito_max, int numConta, float saldo) {
        this.agencia = agencia;
        this.cpf = cpf;
        this.lim_credito = lim_credito;
        this.lim_credito_max = lim_credito_max;
        this.numConta = numConta;
        this.saldo = saldo;
    }
    public int getAgencia() {
        return this.agencia;
    }

    public String getCpf() {
        return this.cpf;
    }

    public float getLim_credito() {
        return this.lim_credito;
    }

    public float getLim_credito_max() {
        return this.lim_credito_max;
    }

    public int getNumConta() {
        return this.numConta;
    }

    public float getSaldo() {
        return this.saldo;
    }
}
