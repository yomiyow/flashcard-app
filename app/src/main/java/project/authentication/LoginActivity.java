package project.authentication;

import android.content.Context;
import android.content.Intent;
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

import project.main.HomeActivity;
import project.model.DatabaseHelper;
import project.model.UserModel;
import project.utils.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    private EditText emailEdiText, passwordEditText;
    private Button signupLink, loginBtn;
    private CheckBox showPasswordBtn;

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
        showPasswordActionPerformed();
    }


    private void initInstanceVariables() {
        context = LoginActivity.this;
        emailEdiText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        signupLink = findViewById(R.id.signup_link);
        loginBtn = findViewById(R.id.login_btn);
        showPasswordBtn = findViewById(R.id.show_password_cb);
    }

    private void loginActionPerformed() {
        loginBtn.setOnClickListener((v) -> {
            try (DatabaseHelper database = new DatabaseHelper(context)) {
                String email = emailEdiText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                UserModel user = new UserModel(email, password);
                boolean accountExist = database.loginUser(user);
                if (!accountExist) {
                    ToastUtil.showToast(context, "Account Not Found!");
                    return;
                }

                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
            } catch (IllegalArgumentException e) {
                ToastUtil.showToast(context, e.getMessage());
            }
        });
    }

    private void signupLinkActionPerformed() {
        signupLink.setOnClickListener((v) -> {
            Intent intent = new Intent(context, SignupActivity.class);
            startActivity(intent);
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