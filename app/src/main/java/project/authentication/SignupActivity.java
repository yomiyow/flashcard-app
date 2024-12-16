package project.authentication;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import project.model.DatabaseHelper;
import project.model.UserModel;

public class SignupActivity extends AppCompatActivity {

    EditText emailEditText, usernameEditText, passwordEditText;
    Button signupBtn, loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initInstanceVariables();
        singUpActionPerformed();
        loginLinkActionPerformed();
    }

    private void initInstanceVariables() {
        emailEditText = findViewById(R.id.email_et);
        usernameEditText = findViewById(R.id.username_et);
        passwordEditText = findViewById(R.id.password_et);
        signupBtn = findViewById(R.id.signup_btn);
        loginLink = findViewById(R.id.login_link);
    }

    private void singUpActionPerformed() {
        signupBtn.setOnClickListener((v) -> {
            try (DatabaseHelper database = new DatabaseHelper(SignupActivity.this)) {
                String email = emailEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                UserModel user = new UserModel(email, username, password);

                boolean exist = database.isUsernameTaken(user);
                if (exist) {
                    Toast.makeText(this, "Username is already taken. Try using a different one.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = database.registerUser(user);
                if (!success) {
                    Toast.makeText(this, "Email already registered. Try using a different one.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Registered Successfully.", Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (SQLiteException e) {
                Toast.makeText(this, "Database error occurred: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginLinkActionPerformed() {
        loginLink.setOnClickListener((v) -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}