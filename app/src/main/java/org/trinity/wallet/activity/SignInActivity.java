package org.trinity.wallet.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.trinity.wallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import neoutils.Neoutils;
import neoutils.Wallet;

/**
 * A login screen that offers login via private key.
 */
public class SignInActivity extends BaseActivity {
    // UI references.
    @BindView(R.id.signInPrivateKey)
    public EditText mPrivateKeyView;

    // Wallet.
    private Wallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        // Initialize the back button event.
        Button back = findViewById(R.id.btnBackLogin);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signInFinish();
            }
        });

        // Set up the login form.
        mPrivateKeyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // Initialize the login button event.
        Button signIn = findViewById(R.id.btnSignIn);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mPrivateKeyView.setError(null);

        // Store values at the time of the login attempt.
        String privateKey = mPrivateKeyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid privateKey, if the user entered one.
        if (!TextUtils.isEmpty(privateKey) && !isPrivateKeyValid(privateKey)) {
            mPrivateKeyView.setError(getString(R.string.error_invalid_private_key));
            focusView = mPrivateKeyView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Wallet object persistence.
            getWalletApplication().setWallet(wallet);
            // Set the login result.
            setResult(0);
            // Go back to the main activity.
            signInFinish();
        }
    }

    private boolean isPrivateKeyValid(String privateKey) {
        try {
            // Verify the private key and instantiate the wallet model.
            wallet = Neoutils.generateFromPrivateKey(privateKey);
        } catch (Exception e) {
            new Exception("User input: Invalid NEO private key.").printStackTrace();
            return false;
        }
        return true;
    }

    private void signInFinish() {
        SignInActivity.this.finish();
    }
}
