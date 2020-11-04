package com.example.bancofernandes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipal extends AppCompatActivity {

    TextView saldo, agencia, numero, nome, limite, limiteDisponivel;
    Button btnSair, btnCancelar;
    Button btnOperacoes, btnMenu, btnTransferencia;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String cpfA, nomeA;
    int agenciaA, numContaA;
    float saldoA, lim_creditoA, lim_credito_maxA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        saldo = (TextView) findViewById(R.id.textSaldo);
        agencia = (TextView) findViewById(R.id.textAgencia);
        numero = (TextView) findViewById(R.id.textNumero);
        nome = (TextView) findViewById(R.id.textNome);
        limite = (TextView) findViewById(R.id.textLimite);
        limiteDisponivel = (TextView) findViewById(R.id.textLimiteDisponivel);


        btnOperacoes = (Button) findViewById(R.id.btnOperacoes);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnTransferencia = (Button) findViewById(R.id.btnTransferencia);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);

        btnSair = (Button) findViewById(R.id.btnSair);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Saindo da conta...", Toast.LENGTH_LONG).show();
                finish();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        inicializarDatabase();


        final String cpf;

        Bundle extras = getIntent().getExtras();
        cpf = extras.getString("cpf");
        cpfA = cpf;
        puxarDadosCliente(cpf);
        puxarDadosConta(cpf);
        if(cpf == null){
            Toast.makeText(getApplicationContext(), "Você não está logado!", Toast.LENGTH_LONG).show();
            finish();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        btnOperacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaOperacoes();
            }
        });
        btnTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaTransferencia();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("cliente").child(cpfA).removeValue();
                databaseReference.child("conta").child(cpfA).removeValue();
                finish();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
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

    private void puxarDadosConta(final String cpf) {
        databaseReference.child("conta").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Conta c = snapshot.child(cpf).getValue(Conta.class);
                agenciaA = c.getAgencia();
                numContaA = c.getNumConta();
                saldoA = c.getSaldo();
                lim_creditoA = c.getLim_credito();
                lim_credito_maxA = c.getLim_credito_max();

                agencia.setText("Agência: "+agenciaA);
                numero.setText("N° Conta: "+numContaA);
                saldo.setText("Saldo: R$"+saldoA);
                limiteDisponivel.setText("Limite Disponível: "+lim_creditoA);
                limite.setText("Limite: "+lim_credito_maxA);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPrincipal.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void puxarDadosCliente(final String cpf) {
        databaseReference.child("cliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cliente c = snapshot.child(cpf).getValue(Cliente.class);
                nomeA = c.getNome();
                nome.setText("Nome Completo: "+nomeA);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPrincipal.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void telaOperacoes(){
        Intent intent = new Intent(this, Menu_Operacoes.class);
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
        FirebaseApp.initializeApp(MenuPrincipal.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}