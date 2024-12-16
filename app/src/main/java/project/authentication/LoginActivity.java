package project.authentication;

import android.content.Context;
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

    private Context context;
    private EditText emailEdiText, passwordEditText;
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

        initInstanceVariables();
        loginActionPerformed();
        signupLinkActionPerformed();
    }


    private void initInstanceVariables() {
        context = LoginActivity.this;
        emailEdiText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        signupLink = findViewById(R.id.signup_link);
        loginBtn = findViewById(R.id.login_btn);
    }

    private void loginActionPerformed() {
        loginBtn.setOnClickListener((v) -> {
            try (DatabaseHelper database = new DatabaseHelper(context)) {
                String email = emailEdiText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                UserModel user = new UserModel(email, password);

                boolean accountExist = database.loginUser(user);
                if (!accountExist) {
                    Toast.makeText(this, "Account not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signupLinkActionPerformed() {
        signupLink.setOnClickListener((v) -> {
            Intent intent = new Intent(context, SignupActivity.class);
            startActivity(intent);
        });
    }
}