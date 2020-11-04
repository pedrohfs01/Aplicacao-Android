package com.example.bancofernandes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Menu_Operacoes extends AppCompatActivity {

    Button btnOperacoes, btnMenu, btnTransferencia, btnDepositar, btnSacar;
    EditText valorA;
    String cpfA, auxiliar;
    float valor, saldoA, lim_creditoA, lim_credito_maxA;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__operacoes);
        btnOperacoes = (Button) findViewById(R.id.btnOperacoes);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnTransferencia = (Button) findViewById(R.id.btnTransferencia);
        btnDepositar = (Button) findViewById(R.id.btnDepositar);
        btnSacar = (Button) findViewById(R.id.btnSacar);
        valorA = (EditText) findViewById(R.id.textValor);

        final String cpf;
        Bundle extras = getIntent().getExtras();
        cpf = extras.getString("cpf");
        cpfA = cpf;
        if(cpf == null){
            Toast.makeText(getApplicationContext(), "Você não está logado!", Toast.LENGTH_LONG).show();
            finish();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        btnTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaTransferencia();
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaMenu();
            }
        });
        inicializarDatabase();
        puxarDadosConta(cpfA);
        depositar();
        sacar();

    }

    private void sacar() {
        btnSacar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(valorA.getText().toString() != null) {
                    auxiliar = valorA.getText().toString();
                    valor = Float.parseFloat(auxiliar);
                    float total = saldoA + lim_creditoA;
                    if(valor > total){
                        valorA.setError("Saldo insuficiente!");
                    }
                    else if(valor > saldoA){
                        float aux = valor - saldoA;
                        aux = lim_creditoA - aux;
                        HashMap<String, Object> res = new HashMap<>();
                        res.put("saldo", 0);
                        res.put("lim_credito", aux);
                        firebaseDatabase.getReference().child("conta").child(cpfA).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Menu_Operacoes.this, "Sacado!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Menu_Operacoes.this, "Erro"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        float aux = saldoA - valor;
                        HashMap<String, Object> res = new HashMap<>();
                        res.put("saldo", aux);
                        firebaseDatabase.getReference().child("conta").child(cpfA).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Menu_Operacoes.this, "Sacado!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Menu_Operacoes.this, "Erro"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else{
                    valorA.setError("Preencha este campo para prosseguir.");
                }
            }
        });
    }

    private void depositar() {
        btnDepositar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(valorA.getText().toString() != null) {
                    auxiliar = valorA.getText().toString();
                    valor = Float.parseFloat(auxiliar);
                    float valorRestante = lim_credito_maxA - lim_creditoA;
                    float valorTeste = lim_creditoA + valor;
                    if(lim_creditoA < lim_credito_maxA){
                        if(valorTeste < lim_credito_maxA){
                            HashMap<String, Object> res = new HashMap<>();
                            res.put("lim_credito", valorTeste);
                            firebaseDatabase.getReference().child("conta").child(cpfA).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Menu_Operacoes.this, "Depositado!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Menu_Operacoes.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            float valorD = valor - valorRestante;
                            valorD += saldoA;
                            HashMap<String, Object> res = new HashMap<>();
                            res.put("lim_credito", lim_credito_maxA);
                            res.put("saldo", valorD);
                            firebaseDatabase.getReference().child("conta").child(cpfA).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Menu_Operacoes.this, "Depositado!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Menu_Operacoes.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    else {
                        valor += saldoA;
                        HashMap<String, Object> res = new HashMap<>();
                        res.put("saldo", valor);
                        firebaseDatabase.getReference().child("conta").child(cpfA).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Menu_Operacoes.this, "Depositado!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Menu_Operacoes.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else{
                    valorA.setError("Preencha este campo para prosseguir.");
                }

            }
        });
    }
    private void puxarDadosConta(final String cpf) {
        databaseReference.child("conta").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conta c = snapshot.child(cpf).getValue(Conta.class);
                saldoA = c.getSaldo();
                lim_creditoA = c.getLim_credito();
                lim_credito_maxA = c.getLim_credito_max();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Menu_Operacoes.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("cpf", cpfA);
        startActivity(intent);
    }
    public void telaMenu(){
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.putExtra("cpf", cpfA);
        startActivity(intent);
        finish();
    }
    public void telaTransferencia(){
        Intent intent = new Intent(this, Menu_Transferir.class);
        intent.putExtra("cpf", cpfA);
        startActivity(intent);
        finish();
    }
    private void inicializarDatabase() {
        FirebaseApp.initializeApp(Menu_Operacoes.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}