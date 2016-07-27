package ar.uba.fi.splitapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    //Facebook
    CallbackManager fbCallbackManager;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        fbCallbackManager = CallbackManager.Factory.create();
        addLoginButton();

        addLogo();
    }

    private void addLogo() {
        ImageView logo = (ImageView) findViewById(R.id.logo);
        Animation rerotate = AnimationUtils.loadAnimation(this, R.anim.rerotate);

        AnimationTask rotate = new AnimationTask(logo, rerotate); //FIXME: Names

        rotate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void addLoginButton() {
        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends",
                "public_profile", "user_birthday"));
        loginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (profileTracker == null) {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    Log.v("Profile Changed", "LoginActivity::onResume " + profile2.getFirstName());
                    profileTracker.stopTracking();

                    String token = AccessToken.getCurrentAccessToken().getToken();
                    String userId = Profile.getCurrentProfile().getId();
                    ServerHandler.signIn(userId, token,
                            v -> startMainActivity(),
                            v -> Utility.showMessage("Fallo al conectar con el servidor",
                                    Utility.getViewgroup(LoginActivity.this)));
                }
            };
        } else {
            profileTracker.startTracking();
        }
    }

    private void startMainActivity() {
        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    private class AnimationTask extends AsyncTask<Void, Void, Boolean> {

        private ImageView mImageView;
        private Animation mAnimation;
        private int mRotateCount = 0;

        AnimationTask(ImageView view, Animation animation) {
            mImageView = view;
            mAnimation = animation;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return true;
            }
            while (true) {
                runOnUiThread(this::animate);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    return true;
                }
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        }

        private void animate() {

            if (mRotateCount % 2 == 0) {
                mImageView.startAnimation(mAnimation);
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.logo_b));
                mRotateCount++;
            } else {
                mImageView.startAnimation(mAnimation);
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.logo_a));
                mRotateCount++;
            }

        }
    }

}

