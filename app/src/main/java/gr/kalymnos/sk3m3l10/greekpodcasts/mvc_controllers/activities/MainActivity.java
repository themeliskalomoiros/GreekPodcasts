package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.main_screen.MainViewMvcImpl;

public class MainActivity extends AppCompatActivity implements MainViewMvc.OnActionCreatePodcastClickListener {

    private static final int RC_SIGN_IN = 123;

    private MainViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViewMvc();

        //  TODO:   Shouldn't do something here with checking savedInstanceState and fragments? Check it!!!
        this.setContentView(this.viewMvc.getRootView());
    }

    @Override
    public void onActionCreatePodcastClick() {
        //  Kick off FirebaseUI sign in flow
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //  Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }else{
                //  Sign in failed. If response is null the user canceled the
                //  sign-in flow using the back button. Otherwise check response.getError().getErrorCode()
                //  and handle the error
            }
        }
    }

    private void initializeViewMvc() {
        this.viewMvc = new MainViewMvcImpl(LayoutInflater.from(this), null, this.getSupportFragmentManager());
        this.viewMvc.setOnCreatePodcastClickListener(this);
        this.setSupportActionBar(this.viewMvc.getToolbar());
    }
}
