package com.example.brij.facebooklogin;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static android.util.Base64.encodeToString;

public abstract class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    TextureView txtEmail, txtBirthday;
    ProgressDialog nDialog;
    ImageView imgAvatar;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, intent Data);

    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = callbackManager.factory.create();

        txtBirthday = (TextView) findViewById(R.id.txtBirthday);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        imgAvatar = (ImageView) findViewById(R.id.avatar);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "emails", "user_birthday"));

        loginButton.registerCallback(callbackManager, new facebookCallback<loginResult>());

        if(AccessToken.getCurrentAccessToken() !=null)
        {
            txtEmail.setText(AccessToken.getCurrentAccessToken().getUserId());
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        nDialog = new ProgressDialog(context:mainActivity.this);
        nDialog.setMessage("Retrieving data...");
        nDialog.show();

        String accesstoken = loginResult.getAccessToken().getToken();

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                nDialog.dismiss();
                Log.d(tag:"response", response.toString());
                getData(object);
            }
        });
            Bundle parameters = new Bundle();
            parameters.putString("fields","id,email,birthday");
            request.setParameters(parameters);
            request.executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(facebookException error) {

    }

        });
    private void getData(JSONObject object) {
        try{
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString(name "id")+"/picture?width=250&height=250");

            Picasso.with(this).load(profile_picture.toString()).into(imgAvatar);

            txtEmail.setText(object.getString(name "email"));
            txtBirthday.setText(object.getString(name "birthday"));
        } catch (MalformedURLException e){
            e.printStackTrace();
        }  catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.brij.facebooklogin", PackageManager.GET_SIGNATURES);
            for(signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

            }
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }
}
