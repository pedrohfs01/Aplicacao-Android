package com.example.bancofernandes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
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

public class Menu_Transferir extends AppCompatActivity {

    Button btnOperacoes, btnMenu, btnTransferencia, btnTransferir;

    EditText agenciaR, numeroR, cpfR, valorR;

    String cpfA;
    String cpfRem;
    int numero, numeroRem;
    int agencia, agenciaRem;
    float valor, saldoA, lim_creditoA, lim_credito_maxA,saldoR, lim_creditoR, lim_credito_maxR;;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__transferir);
        btnOperacoes = (Button) findViewById(R.id.btnOperacoes);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnTransferencia = (Button) findViewById(R.id.btnTransferencia);

        btnTransferir = (Button) findViewById(R.id.btnTransferir);
        agenciaR = (EditText) findViewById(R.id.textAgencia);
        numeroR = (EditText) findViewById(R.id.textNumero);
        cpfR = (EditText) findViewById(R.id.textCPF);
        valorR = (EditText) findViewById(R.id.textValor);


        final String cpf;
        Bundle extras = getIntent().getExtras();
        cpf = extras.getString("cpf");
        if(cpf == null){
            Toast.makeText(getApplicationContext(), "Você não está logado!", Toast.LENGTH_LONG).show();
            finish();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        cpfA = cpf;

        cpfRem = cpfR.getText().toString();

        btnOperacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaOperacoes();
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaMenu();
            }
        });
        valorR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cpfRem = cpfR.getText().toString();
                databaseReference.child("conta").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(cpfRem).exists()) {
                            Conta rem = snapshot.child(cpfRem).getValue(Conta.class);
                            agencia = rem.getAgencia();
                            numero = rem.getNumConta();
                            saldoR = rem.getSaldo();
                            lim_credito_maxR = rem.getLim_credito_max();
                            lim_creditoR = rem.getLim_credito();
                        }
                        else{
                            Toast.makeText(Menu_Transferir.this, "Dados Incorretos", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inicializarDatabase();
        puxarDadosConta(cpfA);
        transferir();
    }

    private void transferir() {
        btnTransferir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cpfRem = cpfR.getText().toString();
                databaseReference.child("conta").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(cpfRem).exists()) {
                            Conta rem = snapshot.child(cpfRem).getValue(Conta.class);
                            agencia = rem.getAgencia();
                            numero = rem.getNumConta();
                            saldoR = rem.getSaldo();
                            lim_credito_maxR = rem.getLim_credito_max();
                            lim_creditoR = rem.getLim_credito();
                        }
                        else{
                            Toast.makeText(Menu_Transferir.this, "Dados Incorretos", Toast.LENGTH_SHORT).show();
                            telaMenu();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Menu_Transferir.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                if(agenciaR.getText().toString() == null || numeroR.getText().toString() == null
                || cpfR.getText().toString() == null || valorR.getText().toString() == null){
                    Toast.makeText(Menu_Transferir.this, "Preencha os campos corretamente.", Toast.LENGTH_SHORT).show();
                }
                else {
                    float total = saldoA + lim_creditoA;
                    float valorRem = Float.parseFloat(valorR.getText().toString());
                    if (total < valorRem) {
                        Toast.makeText(Menu_Transferir.this, "Saldo Insuficiente", Toast.LENGTH_SHORT).show();
                    } else {
                        valor = Float.parseFloat(valorR.getText().toString());

                        if (valor > saldoA) {
                            float aux = valor - saldoA;
                            aux = lim_creditoA - aux;
                            HashMap<String, Object> res = new HashMap<>();
                            res.put("saldo", 0);
                            res.put("lim_credito", aux);
                            firebaseDatabase.getReference().child("conta").child(cpfA).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Menu_Transferir.this, "Efetuado!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Menu_Transferir.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            float aux = saldoA - valor;
                            HashMap<String, Object> res = new HashMap<>();
                            res.put("saldo", aux);
                            firebaseDatabase.getReference().child("conta").child(cpfA).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Menu_Transferir.this, "Efetuado!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Menu_Transferir.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        valor = Float.parseFloat(valorR.getText().toString());

                        float valorRestante = lim_credito_maxR - lim_creditoR;
                        float valorTeste = lim_creditoR + valor;
                        if (lim_creditoR < lim_credito_maxR) {

                            if (valorTeste < lim_credito_maxR) {
                                HashMap<String, Object> res = new HashMap<>();
                                res.put("lim_credito", valorTeste);
                                firebaseDatabase.getReference().child("conta").child(cpfRem).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Menu_Transferir.this, "Efetuado!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Menu_Transferir.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                float valorD = valor - valorRestante;
                                valorD += saldoR;
                                HashMap<String, Object> res = new HashMap<>();
                                res.put("lim_credito", lim_credito_maxR);
                                res.put("saldo", valorD);
                                firebaseDatabase.getReference().child("conta").child(cpfRem).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Menu_Transferir.this, "Efetuado!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Menu_Transferir.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            float aux = valor += saldoR;
                            HashMap<String, Object> res = new HashMap<>();
                            res.put("saldo", aux);
                            firebaseDatabase.getReference().child("conta").child(cpfRem).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Menu_Transferir.this, "Efetuado!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Menu_Transferir.this, "Erro" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
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
    public void telaOperacoes(){
        Intent intent = new Intent(this, Menu_Operacoes.class);
        intent.putExtra("cpf", cpfA);
        startActivity(intent);
        finish();
    }
    public void telaMenu(){
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.putExtra("cpf", cpfA);
        startActivity(intent);
        finish();
    }
    private void puxarDadosConta(final String cpf) {
        cpfRem = cpfR.getText().toString();
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
                Toast.makeText(Menu_Transferir.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void inicializarDatabase() {
        FirebaseApp.initializeApp(Menu_Transferir.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}