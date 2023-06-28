package com.example.opticalcharacterrecognitionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    EditText name;
    EditText email;
    EditText password;
    EditText conPassword;
    UserDBHandler helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            name = findViewById(R.id.name);
            email = findViewById(R.id.email);
            password = findViewById(R.id.signupPassword);
            conPassword = findViewById(R.id.conPassword);
            helper = new UserDBHandler(this, "userDB", null, 1);
        }catch (Exception e){
            Log.e("myTag", "" + e);
        }
    }

    public void signUp(View v){

        String uname = name.getText().toString(), e = email.getText().toString(), pass = password.getText().toString(), conPass = conPassword.getText().toString();

        if(uname.equals("") || e.equals("") || pass.equals("") || conPass.equals(""))
            return;

        if(!isEmailValid(e)){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show();
            return;
        }

        if (helper.isUserPresent(e)) {
            Toast.makeText(this, "Entered Credentials already exists", Toast.LENGTH_LONG).show();

        } else if (isPasswordValid(pass, conPass)) {

            if (helper.addUser(new User(uname, e, pass))) {
                Bundle bundle = new Bundle();
                bundle.putString("Username", e);

                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SignupActivity.this, Home.class);
                intent.putExtra("data", bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error occurred during registration, Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isPasswordValid(String pass, String repass){

        String msg;
        Pattern lowerCase = Pattern.compile("^.*[a-z].*$");
        Pattern upperCase = Pattern.compile("^.*[A-Z].*$");
        Pattern number = Pattern.compile("^.*[0-9].*$");
        //Pattern specialCharacter = Pattern.compile("^.*[^a-zA-Z0-9].*$");
        if(pass.equals("") || repass.equals(""))
            msg = "Enter password and Confirm it";
        else if(pass.length() < 8)
            msg = "Password must at least be 8 characters long";
        else if(! lowerCase.matcher(pass).matches())
            msg = "Password must at least have one lowercase letter";
        else if(! upperCase.matcher(pass).matches())
            msg = "Password must at least have one uppercase letter";
        else if(! number.matcher(pass).matches())
            msg = "Password must at least have one digit";
        else if(! pass.equals(repass))
            msg = "Both passwords must be same";
        else
            return true;

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        return false;
    }


    private boolean isEmailValid(String email){
        Pattern regex = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        return regex.matcher(email).matches();
    }
}
