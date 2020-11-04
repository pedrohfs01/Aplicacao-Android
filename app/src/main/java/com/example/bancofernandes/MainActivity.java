package com.example.bancofernandes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText cpfA, senhaA;
    Button btnEntrar;
    String cpfL, senhaL;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cpfA = (EditText) findViewById(R.id.cpf);
        senhaA = (EditText) findViewById(R.id.senha);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);

        inicializarDatabase();
        btnEntrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(senhaA.getText().toString().isEmpty()){
                    senhaA.setError("Campo senha precisa ser preenchida");
                }
                else if(cpfA.getText().toString().isEmpty()){
                    cpfA.setError("Campo CPF precisa ser preenchido");
                }
                else{
                    loginDatabase();
                }

            }

        });

    }

    private void loginDatabase() {
        databaseReference.child("cliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(cpfA.getText().toString()).exists()) {
                        Cliente c = dataSnapshot.child(cpfA.getText().toString()).getValue(Cliente.class);
                        cpfL = c.getCPF();
                        senhaL = c.getSenha();
                        if (cpfL.equals(cpfA.getText().toString()) && senhaL.equals(senhaA.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Logado com sucesso!", Toast.LENGTH_LONG).show();
                            telaPrincipal(cpfL);
                        } else {
                            cpfA.setError("Senha Incorreta");
                        }

                }
                else{
                    cpfA.setError("CPF Incorreto");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializarDatabase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void proximaTela(View v){
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }
    public void telaPrincipal(String cpF){
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.putExtra("cpf", cpF);
        startActivity(intent);
    }
}