package database;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import nucleo.entidades_do_nucleo.Usuario;

public class FirebaseAuth {
    private static Firebase firebase;
    private static Usuario usuario;

    public static Firebase getFirebase() {
        try {
            if( firebase == null ){
                firebase = new Firebase("https://polardispendium-gastocalorico.firebaseio.com/");
            }
        }catch (Exception e){
            Log.e("falha ao criar firebase",e.getMessage());
        }
        return( firebase );
    }
    public static Usuario getUser(Firebase firebase, final String email, String senha){
        setUsuario(null);
        try{
            firebase.authWithPassword(email, senha, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    if(authData!=null){
                        setUsuario(new Usuario());
                        getUsuario().setFirebaseToken(authData.getToken());
                        getUsuario().setFirebaseUId(authData.getUid());
                        getUsuario().setNome(email.substring(0,email.indexOf("@")));
                        getUsuario().setEmail(email);
                        getUsuario().setMetodo(Usuario.FIREBASE);
                    }
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                }
            });

        }catch (Exception e){
            Log.e("falha auth firebase",e.getMessage());
        }
        return getUsuario();
    }
    public static boolean saveUser(){
        return false;
    }
    public static boolean getLoggedState(Firebase firebase){
        try{
            return (firebase.getAuth()!=null)?true:false;
        }catch (Exception e){
            Log.e("falha verificar logado",e.getMessage());
            return false;
        }

    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        FirebaseAuth.usuario = usuario;
    }
}
