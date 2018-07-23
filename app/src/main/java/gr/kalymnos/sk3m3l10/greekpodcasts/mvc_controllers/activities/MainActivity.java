package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

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

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, PortofolioActivity.class));
            } else {
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
