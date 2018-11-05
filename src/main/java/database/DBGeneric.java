package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBGeneric {
    private SQLiteDatabase db;
    private Context context;
    public  DBGeneric(Context context){
        this.context = context;
        DBHelp dbHelp = new DBHelp(context);
        db = dbHelp.getWritableDatabase();
    }
    public int inserir(ContentValues values,String tabela)throws SQLException{
        int i = -1;
        try {

            i =(int) db.insert(tabela,null,values);

        }catch (SQLException e)
        {
            Log.e("Erro ao inserir",e.getMessage());
        }
        return i;
    }
    public void inserir( ContentValues values,String tabela,Context context){
        try {
            db.insert(tabela,null,values);
        }catch (SQLException e)
        {
            Log.e( "Erro ao inserir: ",e.getMessage());
        }
    }
    public  void  atualizar(String tabela,ContentValues values,String condicao,String[] argumentos ){
        try {
            db.update(tabela,values,condicao,argumentos);
        }catch (SQLException e)
        {
            e.getMessage();
        }

    }


    public  void  deletar(String tabela,String condicao,String[] argumentos){
        try {
            db.delete(tabela,condicao,argumentos);
        }catch (SQLException e)
        {
            Log.e("falha ao deletar",e.getMessage());
        }

    }

    public List<List<String>> buscar(String tabela,String [] campos){
        List<List<String>> lista = new ArrayList<>();
        Cursor cursor = db.query(tabela,campos,null,null,null,null,"_id ASC");

        if (cursor.getCount()>0)
        {
            int fim = cursor.getCount();
            int i = 0;
            int j = cursor.getColumnCount();
            cursor.moveToFirst();
            while(i<fim)
            {
                List<String> strings = new ArrayList<>();
                int k = 0;
                while(k<j)
                {
                    switch (cursor.getType(k)) {
                        case 1:
                            strings.add(Integer.toString(cursor.getInt(k)));
                            break;
                        case 2:
                            strings.add(Double.toString(cursor.getFloat(k)));
                            break;
                        case 3:
                            strings.add(cursor.getString(k));
                            break;
                    }
                    k++;
                }
                cursor.moveToNext();
                lista.add(strings);
                i++;
            }
        }
        return (lista);
    }


    public List<List<String>> buscar(String tabela,String [] campos,String selection,String [] argumento) {
        List<List<String>> lista = new ArrayList<>();
        try {
            Cursor cursor = db.query(tabela, campos, selection, argumento, null, null, "_id ASC");

            if (cursor.getCount() > 0) {
                int fim = cursor.getCount();
                int i = 0;
                int j = cursor.getColumnCount();
                cursor.moveToFirst();
                while (i < fim) {
                    List<String> strings = new ArrayList<>();
                    int k = 0;
                    while (k < j) {
                        switch (cursor.getType(k)) {
                            case 1:
                                strings.add(Integer.toString(cursor.getInt(k)));
                                break;
                            case 2:
                                strings.add(Double.toString(cursor.getFloat(k)));
                                break;
                            case 3:
                                strings.add(cursor.getString(k));
                                break;
                        }
                        k++;
                    }
                    cursor.moveToNext();
                    lista.add(strings);
                    i++;
                }
            }

        } catch (SQLException e) {
            Log.e("buscar db", "Erro ao buscar no banco");
        } catch (Exception e) {
            Log.e("buscar db", "Erro ao buscar no banco");
        }
        return (lista);
    }

    public List<List<String>> buscar(String tabela,String [] campos,String selection,String [] argumento,String orderBy) {
        List<List<String>> lista = new ArrayList<>();
        try {
            Cursor cursor = db.query(tabela, campos, selection, argumento, null, null, orderBy);

            if (cursor.getCount() > 0) {
                int fim = cursor.getCount();
                int i = 0;
                int j = cursor.getColumnCount();
                cursor.moveToFirst();
                while (i < fim) {
                    List<String> strings = new ArrayList<>();
                    int k = 0;
                    while (k < j) {
                        switch (cursor.getType(k)) {
                            case 1:
                                strings.add(Long.toString(cursor.getLong(k)));
                                break;
                            case 2:
                                strings.add(Double.toString(cursor.getFloat(k)));
                                break;
                            case 3:
                                strings.add(cursor.getString(k));
                                break;
                        }
                        k++;
                    }
                    cursor.moveToNext();
                    lista.add(strings);
                    i++;
                }
            }

        } catch (SQLException e) {
            Log.e("buscar db", "Erro ao buscar no banco");
        } catch (Exception e) {
            Log.e("buscar db", "Erro ao buscar no banco");
        }
        return (lista);
    }
}