package lib.about;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import mobiric.fhbsc.weather.R;

public class AboutActivity extends Activity {

    TextView tvLicences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // version name
        TextView tvVersion = (TextView) findViewById(R.id.tvAboutVersion);
        try {
            String versionName =
                    getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            tvVersion.setText(String.format(getString(R.string.about_version_format), versionName));
        } catch (NameNotFoundException e) {
            tvVersion.setVisibility(View.GONE);
        }

        // // open source licences
        // tvLicences = (TextView) findViewById(R.id.tvLicences);
        // SpannableString content = new SpannableString(tvLicences.getText());
        // content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        // tvLicences.setText(content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // public void onClickLinkToWebsite(View v)
    // {
    // // Flurry.getInstance().logOnClick("www.drinkthisin.com");
    // gotoWeb(R.string.about_rate_it_url);
    // }

    public void onClickLogoGlowworm(View view) {
        // Flurry.getInstance().logOnClick("www.glowworm.mobi");
        gotoWeb(R.string.about_glowworm_url);
    }

    // public void onClickLicences(View view)
    // {
    // // Flurry.getInstance().logOnClick("Open source licences");
    //
    // FragmentTransaction ft = getFragmentManager().beginTransaction();
    // Fragment prev = getFragmentManager().findFragmentByTag("dialog");
    // if (prev != null)
    // {
    // ft.remove(prev);
    // }
    // ft.addToBackStack(null);
    //
    // // Create and show the dialog.
    // DialogFragment newFragment =
    // CreditsDialogFragment.newInstance(R.string.about_licenses,
    // R.layout.dialog_about_licences);
    // newFragment.show(ft, "dialog");
    // }

    private void gotoWeb(int urlResId) {
        Intent browserIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(urlResId)));
        startActivity(browserIntent);
    }

}
