package database;

import com.firebase.client.Firebase;

public class FirebaseAuth {
    private static Firebase firebase;

    public static Firebase getFirebase() {
        if( firebase == null ){
            firebase = new Firebase("https://polardispendium-gastocalorico.firebaseio.com/");
        }
        return( firebase );
    }
}
