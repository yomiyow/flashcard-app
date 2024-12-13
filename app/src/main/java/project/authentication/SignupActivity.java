package project.authentication;

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

    EditText email, username, password;
    Button signupBtn;

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

        email = findViewById(R.id.email_et);
        username = findViewById(R.id.username_et);
        password = findViewById(R.id.password_et);
        signupBtn = findViewById(R.id.signup_btn);

        singUpActionPerformed();
    }

    private void singUpActionPerformed() {
        signupBtn.setOnClickListener((v) -> {
            try (DatabaseHelper database = new DatabaseHelper(SignupActivity.this)) {
                UserModel user = new UserModel(
                        email.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString()
                );

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
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "An error occurred: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}