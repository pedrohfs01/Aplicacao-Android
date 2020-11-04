package com.example.bancofernandes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Random;


public class Registro extends AppCompatActivity {

    EditText nomeA, cpfA, ufA,  dtNascA, telefoneA, emailA, senhaA, enderecoA;
    Button btnRegistrar;
    private DatabaseReference mDatabase;
    @Override
    protected void onStart() {
        super.onStart();
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        nomeA = (EditText) findViewById(R.id.txtNome);
        cpfA = (EditText) findViewById(R.id.txtCPF);
        senhaA = (EditText) findViewById(R.id.txtSenha);
        ufA = (EditText) findViewById(R.id.txtUF);
        dtNascA = (EditText) findViewById(R.id.txtDtNasc);
        telefoneA = (EditText) findViewById(R.id.txtTelefone);
        emailA = (EditText) findViewById(R.id.txtEmail);
        enderecoA = (EditText) findViewById(R.id.txtEndereco);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String cpf = cpfA.getText().toString();
                String senha = senhaA.getText().toString();
                String nome = nomeA.getText().toString();
                String uf = ufA.getText().toString();
                String dtNasc = dtNascA.getText().toString();
                String telefone = telefoneA.getText().toString();
                String email = emailA.getText().toString();
                String endereco = enderecoA.getText().toString();
                if (nome.isEmpty()) {
                    nomeA.setError("Campo Nome precisa ser preenchido");
                } else if (cpf.isEmpty()) {
                    cpfA.setError("Campo CPF precisa ser preenchido");
                } else if (uf.isEmpty()) {
                    ufA.setError("Campo UF precisa ser preenchido");
                } else if (dtNasc.isEmpty()) {
                    dtNascA.setError("Campo Data Nascimento precisa ser preenchido");
                } else if (telefone.isEmpty()) {
                    telefoneA.setError("Campo Telefone precisa ser preenchido");
                } else if (endereco.isEmpty()) {
                    enderecoA.setError("Campo Endereço precisa ser preenchido");
                } else if (email.isEmpty()) {
                    emailA.setError("Campo Email precisa ser preenchido");
                } else if (senha.isEmpty()) {
                    senhaA.setError("Campo Senha precisa ser preenchida");
                } else {
                    //Cliente
                    Cliente cliente = new Cliente(cpf, dtNasc, email, endereco, nome, senha, telefone, uf);
                    mDatabase.child("cliente").child(cpf).setValue(cliente).addOnCompleteListener(Registro.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //Conta
                                int agencia = 1001;
                                float saldo = 500, limCredito = 100, limCreditoMax = 100;
                                String numConta = "";
                                Random r = new Random();
                                //NumConta
                                for(int i = 0; i <= 4; i++){
                                    numConta += r.nextInt(9);
                                }

                                Conta conta = new Conta(agencia, cpf, limCredito, limCreditoMax, Integer.parseInt(numConta), saldo);
                                mDatabase.child("conta").child(cpf).setValue(conta).addOnCompleteListener(Registro.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Registrado com sucesso!", Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Erro ao registrar!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Erro ao registrar!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });
    }
}

/*
* int agencia = 1001;
                    float saldo = 500, limCredito = 100, limCreditoMax = 100;
                    String numConta = "";
                    Random r = new Random();
                    //NumConta
                    for(int i = 0; i < 5; i++){
                        numConta += +r.nextInt(9);
                    }
                    //Cliente
                    Cliente cliente = new Cliente(cpf, dtNasc, email, endereco, nome, senha, telefone, uf, agencia, limCredito, limCreditoMax, numConta, saldo);
                    mDatabase.child(cpf).setValue(cliente).addOnCompleteListener(Registro.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Registrado com sucesso!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Erro ao registrar!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
*
*/