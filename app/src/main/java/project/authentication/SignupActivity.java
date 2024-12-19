package project.authentication;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import project.model.DatabaseHelper;
import project.model.UserModel;
import project.utils.ToastUtil;

public class SignupActivity extends AppCompatActivity {

    private Context context;
    private EditText emailEditText, usernameEditText, passwordEditText;
    private Button signupBtn, loginLink;
    private CheckBox showPasswordBtn;

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
        showPasswordActionPerformed();
    }

    private void initInstanceVariables() {
        context = SignupActivity.this;
        emailEditText = findViewById(R.id.email_et);
        usernameEditText = findViewById(R.id.username_et);
        passwordEditText = findViewById(R.id.password_et);
        signupBtn = findViewById(R.id.signup_btn);
        loginLink = findViewById(R.id.login_link);
        showPasswordBtn = findViewById(R.id.show_password_cb);
    }

    private void clearUserInput() {
        emailEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    private void returnToLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    private void singUpActionPerformed() {
        signupBtn.setOnClickListener((v) -> {
            try (DatabaseHelper database = new DatabaseHelper(context)) {
                String email = emailEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                UserModel user = new UserModel(email, username, password);

                boolean exist = database.isUsernameTaken(user);
                if (exist) {
                    ToastUtil.showToast(context, "Username is already taken. Try using a different one.");
                    return;
                }

                boolean success = database.registerUser(user);
                if (!success) {
                    ToastUtil.showToast(context, "Email already registered. Try using a different one.");
                    return;
                }

                ToastUtil.showToast(context, "Registered Successfully.");
                clearUserInput();
                returnToLoginActivity();
            } catch (IllegalArgumentException | SQLiteException e) {
                ToastUtil.showToast(context, e.getMessage());
            }
        });
    }

    private void loginLinkActionPerformed() {
        loginLink.setOnClickListener((v) -> {
            returnToLoginActivity();
        });
    }

    private void showPasswordActionPerformed() {
        showPasswordBtn.setOnClickListener((v) -> {
            if (showPasswordBtn.isChecked()) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordEditText.setTypeface(Typeface.DEFAULT);
            }
        });
    }
}