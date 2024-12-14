package project.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import project.main.HomeActivity;
import project.model.DatabaseHelper;
import project.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button signupLink, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email_et);
        password = findViewById(R.id.password_et);
        signupLink = findViewById(R.id.signup_link);
        loginBtn = findViewById(R.id.login_btn);

        loginActionPerformed();
        signupLinkActionPerformed();
    }


    private void loginActionPerformed() {
        loginBtn.setOnClickListener((v) -> {
            try (DatabaseHelper database = new DatabaseHelper(LoginActivity.this)) {
                UserModel user = new UserModel(
                        email.getText().toString().trim(),
                        password.getText().toString().trim()
                );

                boolean accountExist = database.loginUser(user);
                if (!accountExist) {
                    Toast.makeText(this, "Account not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signupLinkActionPerformed() {
        signupLink.setOnClickListener((v) -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}