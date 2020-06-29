package com.cryptext.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.cryptext.app.App;
import com.cryptext.ui.MainActivity;
import com.cryptext.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Asynchronously load an encrypted file.
 *
 * @author Arda Büyükkaya
 */
public class CryptoLoad extends AsyncTask<Void, Integer, DBForNotes> {

    private final String path;
    private String key;
    private Context mContext;
    private ProgressBar spinner; //optional
    private String toastOK; //optional
    private boolean isFileNotFoundException;

    /**
     * Builder pattern with toast and spinner optional.
     *
     * @param path where the database is located
     * @param key  password
     */
    private CryptoLoad(final String path, final String key, Context mContext) {
        super();
        this.path = path;
        this.key = key;
        this.mContext = mContext;
    }

    /**
     * Provide a new instance of CryptoLoad.
     *
     * @author Arda Büyükkaya
     */
    public static class Builder {
        private final String path;
        private final String key;
        private Context mContext;
        private ProgressBar spinner; //null
        private String toastOK; //null

        /**
         * Build the required fields.
         *
         * @param path where the database is located
         * @param key  password
         */
        public Builder(final String path, final String key, Context mContext) {
            this.path = path;
            this.key = key;
            this.mContext = mContext;
        }

        /**
         * Provide a new instance of CryptoLoad.
         *
         * @return instance of CryptoLoad
         */
        public CryptoLoad build() {
            final CryptoLoad result = new CryptoLoad(path, key, mContext);
            result.spinner = spinner;
            result.toastOK = toastOK;
            return result;
        }

        /**
         * Optional builder method for display a toast is there aren't errors.
         *
         * @param toastOK string to display in the toast
         * @return Builder itself
         */
        public Builder toastOK(final String toastOK) {
            this.toastOK = toastOK;
            return this;
        }

        /**
         * Optional builder method for display a spinner until the end of the operations.
         *
         * @param spinner The ProgressBar to display
         * @return Builder itself
         */
        public Builder spinner(final ProgressBar spinner) {
            this.spinner = spinner;
            return this;
        }
    }

    @Override
    protected void onPreExecute() {
        if (spinner != null && spinner.getVisibility() != View.VISIBLE) {
            spinner.setVisibility(View.VISIBLE);
        }
    }
    /**
     *  AES/GCM Cipher with salt
    */
    @Override
    protected DBForNotes doInBackground(final Void... params) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            byte[] salt2 = (byte[]) ois.readObject();
            byte[] byteKey = Crypto.getInstance().deriveKey(key, salt2);
            SecretKeySpec secretKey = new SecretKeySpec(byteKey, "AES");
            //Creation of Cipher objects
            Cipher decrypt = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = (byte[]) ois.readObject();
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            decrypt.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            byte[] original = decrypt.doFinal((byte[]) ois.readObject());//DECRYPT file and read
            ByteArrayInputStream bais = new ByteArrayInputStream(original);
            ObjectInputStream dis = new ObjectInputStream(bais);
            final DBForNotes db = (DBForNotes) dis.readObject();
            if (db == null) {
                throw new InvalidKeyException();//null error !
            }
            return db;
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                isFileNotFoundException = true;
            }
            Log.e("Crypt Text", String.valueOf(e));
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    try {
                        Toast.makeText(this.mContext, R.string.toast_IOError, Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Log.e("Crypt Text", "Error with toast");
                    }
                }
            }
        }
    }

    @Override
    protected void onPostExecute(final DBForNotes db) {
        if (db == null) {
            if (isFileNotFoundException) {
                Toast.makeText(this.mContext, R.string.toast_wrongPath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.mContext, R.string.toast_wrongPassword, Toast.LENGTH_LONG).show();
            }
        } else {
            if (spinner != null) {
                spinner.setVisibility(View.GONE);
            }
            if (toastOK != null) {
                Toast.makeText(this.mContext, toastOK, Toast.LENGTH_LONG).show();
            }
            final Intent intent = new Intent(this.mContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("path", path);
            intent.putExtra("key", key);
            App.setDatabase(db);
            this.mContext.startActivity(intent);
        }
    }

}
