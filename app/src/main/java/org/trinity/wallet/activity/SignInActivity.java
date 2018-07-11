package org.trinity.wallet.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.trinity.util.HexUtil;
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
                signFinish();
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

        // Initialize the logout button event.
        Button signOut = findViewById(R.id.btnSignOut);
        signOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SignInActivity.this).setTitle("SIGN OUT")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing here.
                            }
                        }).show();
            }
        });

        // If there is no wallet, generate one.
        Wallet wallet = getWalletApplication().getWallet();
        if (wallet == null) {
            try {
                mPrivateKeyView.setText(HexUtil.byteArrayToHex(Neoutils.newWallet().getPrivateKey()));
            } catch (Exception ignored) {
            }
        } else {
            findViewById(R.id.signInWelcome).setVisibility(View.GONE);
            findViewById(R.id.signInWelcomeLong).setVisibility(View.GONE);
            findViewById(R.id.signInOwnKey).setVisibility(View.GONE);
            mPrivateKeyView.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid private key, missing fields, etc.), the
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
            signFinish();
        }
    }

    /**
     * Sign out.
     */
    private void logout() {
        // Wallet object persistence.
        getWalletApplication().setWallet(null);
        // Set the login result.
        setResult(0);
        // Go back to the main activity.
        signFinish();
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

    private void signFinish() {
        SignInActivity.this.finish();
    }
}