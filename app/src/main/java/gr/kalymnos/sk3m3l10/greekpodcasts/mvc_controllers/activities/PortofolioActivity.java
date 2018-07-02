package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.PortofolioViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.PortofolioViewMvcImpl;

public class PortofolioActivity extends AppCompatActivity {

    private PortofolioViewMvc viewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewMvc = new PortofolioViewMvcImpl(LayoutInflater.from(this), null, this.getSupportFragmentManager());
        setSupportActionBar(viewMvc.getToolbar());
        setContentView(viewMvc.getRootView());
    }


}
