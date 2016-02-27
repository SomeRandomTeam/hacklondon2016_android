package com.somrandomteam.hacklondon2016;

import android.app.Application;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by szekelyszilv on 27/02/16.
 */
public class HackApplication extends Application {
    private static Properties secrets;

    @Override
    public void onCreate() {
        super.onCreate();

        loadSecrets(this);
    }

    public static String getSecret(String key) {
        return secrets.getProperty(key);
    }

    private static void loadSecrets(HackApplication app) {
        try {
            InputStream secretsInput = app.getAssets().open("secrets.properties");
            secrets = new Properties();
            secrets.load(secretsInput);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load secrets", e);
        }
    }
}
