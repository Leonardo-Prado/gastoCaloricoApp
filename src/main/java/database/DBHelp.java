package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelp extends SQLiteOpenHelper {

    public static final String DBNOME = "GastoCaloricoDB";
    public static final int VERSAO = 5;
    public DBHelp(Context context) {
        super(context,DBNOME, null, VERSAO);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
        }catch (Exception e){
            Log.e("erro pegar db",e.getMessage());
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       try {
           sqLiteDatabase.execSQL("CREATE TABLE Categorias(_id INTEGER PRIMARY KEY AUTOINCREMENT,Categoria TEXT NOT NULL,Descricao TEXT)");
           sqLiteDatabase.execSQL("CREATE TABLE AtividadesFisicas(_id INTEGER PRIMARY KEY AUTOINCREMENT,MET REAL NOT NULL,_idCategoria INTEGER NOT NULL,Atividades TEXT NOT NULL ,FOREIGN KEY(_idCategoria) REFERENCES Categorias(_id))");
           sqLiteDatabase.execSQL("CREATE TABLE AtividadesRealizadas (_id INTEGER PRIMARY KEY AUTOINCREMENT, _idAtividade INTEGER NOT NULL, Data INTEGER NOT NULL, HoraInicio INTEGER NOT NULL, HoraFim INTEGER NOT NULL,sinc INTEGER NOT NULL DEFAULT 0,_idUsuario INTEGER NOT NULL,FOREIGN KEY(_idAtividade) REFERENCES AtividadesFisicas(_id),FOREIGN KEY(_idUsuario) REFERENCES Usuarios(_id))");
           sqLiteDatabase.execSQL("CREATE TABLE Usuarios (_id INTEGER PRIMARY KEY AUTOINCREMENT,Nome TEXT NOT NULL,Email TEXT NOT NULL,Senha TEXT NOT NULL,Idade INTEGER,Sexo INTEGER,MassaCorporal REAL,Altura REAL,Logado INTEGER NOT NULL,Metodo INTEGER NOT NULL,sinc INTEGER NOT NULL DEFAULT 0,FirebaseToken TEXT,GoogleToken TEXT,FacebookToken TEXT,FirebaseUId TEXT,GoogleUId TEXT,FacebookUId TEXT,DataCriacao INTEGER,PesoMinimo REAL DEFAULT 1,PesoMaximo REAL DEFAULT 2,GastoMinimo REAL DEFAULT 1,GastoMaximo REAL DEFAULT 2,GastoMedio REAL DEFAULT 1)");
           sqLiteDatabase.execSQL("CREATE TABLE GastoEnergetico (_id INTEGER PRIMARY KEY AUTOINCREMENT,Data INTEGER NOT NULL,GastoCalorico REAL NOT NULL,_idUsuario INTEGER NOT NULL,sinc INTEGER NOT NULL DEFAULT 0,FOREIGN KEY(_idUsuario) REFERENCES Usuarios(_id))");
           sqLiteDatabase.execSQL("CREATE TABLE Peso (_id INTEGER PRIMARY KEY AUTOINCREMENT,Data INTEGER NOT NULL,Peso REAL NOT NULL,_idUsuario INTEGER NOT NULL,sinc INTEGER NOT NULL DEFAULT 0,Inicial INTEGER,FOREIGN KEY(_idUsuario) REFERENCES Usuarios(_id))");
           sqLiteDatabase.execSQL("CREATE TABLE Preferencias(_id INTEGER PRIMARY KEY AUTOINCREMENT, _idUsuario INTEGER NOT NULL, RolamentoSens INTEGER, EscondeGraficoTempo INTEGER, GraficoPontosSize INTEGER, GraficoTextoSize INTEGER, FOREIGN KEY(_idUsuario) REFERENCES Usuarios(_id))");

       }catch (SQLException e)
       {
           Log.e("Erro ao criar tabela", e.getMessage());
       }
       }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
       try{
           if (i<3){
               sqLiteDatabase.execSQL("drop table Categorias");
               sqLiteDatabase.execSQL("drop table AtividadesFisicas");
               sqLiteDatabase.execSQL("CREATE TABLE Categorias(_id INTEGER PRIMARY KEY AUTOINCREMENT,Categoria TEXT NOT NULL,Descricao TEXT)");
               sqLiteDatabase.execSQL("CREATE TABLE AtividadesFisicas(_id INTEGER PRIMARY KEY AUTOINCREMENT,MET REAL NOT NULL,_idCategoria INTEGER NOT NULL,Atividades TEXT NOT NULL ,FOREIGN KEY(_idCategoria) REFERENCES Categorias(_id))");
           }
           if(i<=3&&i1>=4){
               sqLiteDatabase.execSQL("ALTER TABLE Usuarios ADD COLUMN PesoMinimo REAL DEFAULT 1");
               sqLiteDatabase.execSQL("ALTER TABLE Usuarios ADD COLUMN PesoMaximo REAL DEFAULT 2");
               sqLiteDatabase.execSQL("ALTER TABLE Usuarios ADD COLUMN GastoMinimo REAL DEFAULT 1");
               sqLiteDatabase.execSQL("ALTER TABLE Usuarios ADD COLUMN GastoMaximo REAL DEFAULT 2");
               sqLiteDatabase.execSQL("ALTER TABLE Usuarios ADD COLUMN GastoMedio REAL DEFAULT 1");
           }
           if(i<=4&&i1>=5){
               sqLiteDatabase.execSQL("ALTER TABLE Usuarios ADD COLUMN sinc INTEGER DEFAULT 0");
               sqLiteDatabase.execSQL("ALTER TABLE AtividadesRealizadas ADD COLUMN sinc INTEGER DEFAULT 0");
               sqLiteDatabase.execSQL("ALTER TABLE GastoEnergetico ADD COLUMN sinc INTEGER DEFAULT 0");
               sqLiteDatabase.execSQL("ALTER TABLE Peso ADD COLUMN sinc INTEGER DEFAULT 0");
           }


       }catch (SQLException e) {
           Log.e("Erro ao atualizar db", e.getMessage());
       }

    }
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try{
            sqLiteDatabase.execSQL("drop table Categorias");
            sqLiteDatabase.execSQL("drop table AtividadesFisicas");
            sqLiteDatabase.execSQL("drop table AtividadesRealizadas");
            sqLiteDatabase.execSQL("drop table Usuarios");
            sqLiteDatabase.execSQL("drop table GastoEnergetico");
            try{
                sqLiteDatabase.execSQL("drop table Peso");
            }catch (SQLException e) {
                Log.e("Erro ao dropar tabela", e.getMessage());
            }
            onCreate(sqLiteDatabase);
        }catch (SQLException e)
        {
            Log.e("Erro ao dropar tabela", e.getMessage());
        }

    }


}
