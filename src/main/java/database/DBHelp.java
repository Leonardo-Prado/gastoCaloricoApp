package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import static android.provider.Contacts.SettingsColumns.KEY;

public class DBHelp extends SQLiteOpenHelper {

    public static final String DBNOME = "GastoCaloricoDB";
    public static final int VERSAO = 2;


    public DBHelp(Context context) {
        super(context,DBNOME, null, VERSAO);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       try {
           sqLiteDatabase.execSQL("CREATE TABLE Categorias(_id INTEGER PRIMARY KEY AUTOINCREMENT,Categoria TEXT NOT NULL,Descricao TEXT)");
           sqLiteDatabase.execSQL("CREATE TABLE AtividadesFisicas(_id INTEGER PRIMARY KEY AUTOINCREMENT,MET REAL NOT NULL,_idCategoria INTEGER NOT NULL,Atividades TEXT NOT NULL ,FOREIGN KEY(_idCategoria) REFERENCES Categorias(_id))");
           sqLiteDatabase.execSQL("CREATE TABLE AtividadesRealizadas (_id INTEGER PRIMARY KEY AUTOINCREMENT, _idAtividade INTEGER NOT NULL, Data INTEGER NOT NULL, HoraInicio INTEGER NOT NULL, HoraFim INTEGER NOT NULL,_idUsuario INTEGER NOT NULL,FOREIGN KEY(_idAtividade) REFERENCES AtividadesFisicas(_id),FOREIGN KEY(_idUsuario) REFERENCES Usuarios(_id))");
           sqLiteDatabase.execSQL("CREATE TABLE Usuarios (_id INTEGER PRIMARY KEY AUTOINCREMENT,Nome TEXT NOT NULL,Idade INTEGER,Sexo INTEGER,MassaCorporal REAL NOT NULL)");
           sqLiteDatabase.execSQL("CREATE TABLE GastoEnergetico (_id INTEGER PRIMARY KEY AUTOINCREMENT,Data INTEGER NOT NULL,GastoCalorico REAL NOT NULL,_idUsuario INTEGER NOT NULL,FOREIGN KEY(_idUsuario) REFERENCES Usuarios(_id))");
           sqLiteDatabase.execSQL("CREATE TABLE Peso (_id INTEGER PRIMARY KEY AUTOINCREMENT,Data INTEGER NOT NULL,Peso REAL NOT NULL,_idUsuario INTEGER NOT NULL,FOREIGN KEY(_idUsuario) REFERENCES Usuarios(_id))");
       }catch (SQLException e)
       {
           Log.e("Erro ao criar tabela",e.getMessage().toString());
       }
       }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
       try{
             sqLiteDatabase.execSQL("drop table Categorias");
             sqLiteDatabase.execSQL("drop table AtividadesFisicas");
             sqLiteDatabase.execSQL("drop table AtividadesRealizadas");
             sqLiteDatabase.execSQL("drop table Usuarios");
             sqLiteDatabase.execSQL("drop table GastoEnergetico");
             onCreate(sqLiteDatabase);
       }catch (SQLException e)
       {
           Log.e("Erro ao dropar tabela",e.getMessage().toString());
       }

    }
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try{
            sqLiteDatabase.execSQL("drop table Categorias");
            sqLiteDatabase.execSQL("drop table AtividadesFisicas");
            sqLiteDatabase.execSQL("drop table AtividadesRealizadas");
            sqLiteDatabase.execSQL("drop table Usuarios");
            sqLiteDatabase.execSQL("drop table GastoEnergetico");
            onCreate(sqLiteDatabase);
        }catch (SQLException e)
        {
            Log.e("Erro ao dropar tabela",e.getMessage().toString());
        }

    }


}
