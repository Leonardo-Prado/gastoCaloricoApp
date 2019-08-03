package nucleo.outros_manipuladores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.entidades_do_nucleo.GastoEnergetico;
import nucleo.entidades_do_nucleo.Peso;
import nucleo.entidades_do_nucleo.Usuario;

class Sincronizador {
    DBGeneric dbGeneric;
    Context context;
    List<AtividadesRealizadas> atividadesRealizadas;
    List<GastoEnergetico> gastoEnergeticos;
    List<Usuario> usuarios;
    List<Peso> pesos;
    List<List<String>> returnDB;

    public Sincronizador(Context context) {
        this.context = context;
        dbGeneric = new DBGeneric(context);
    }

    public boolean sincronizar(int id){
        try {
            returnDB = dbGeneric.buscar("Usuarios",new String[]{"FirebaseUId"},"_id = ?",new  String[]{Integer.toString(id)});
            if(returnDB.size()>0) {
                String fbId = returnDB.get(0).get(0);
                returnDB = dbGeneric.buscar("AtividadesRealizadas", new String[]{"_id", "_idAtividade", "Data", "HoraInicio", "HoraFim", "_idUsuario"}, "sinc = ? AND _idUsuario = ?", new String[]{"0",Integer.toString(id)});
                atividadesRealizadas = new mapToAtividadesRealizadas(returnDB).getAtividadesRealizadas();
                returnDB = dbGeneric.buscar("Usuarios", new String[]{"_id", "Altura", "MassaCorporal", "GastoMedio","Nome","Email","DataCriacao","PesoMinimo"}, "sinc = ? AND _id = ?", new String[]{"0",Integer.toString(id)});
                usuarios = new mapToUsuarios(returnDB).getUsuarios();
                returnDB = dbGeneric.buscar("Peso", new String[]{"_id", "Data", "Peso", "_idUsuario"}, "sinc = ? AND _idUsuario = ?", new String[]{"0",Integer.toString(id)});
                pesos = new mapToPesos(returnDB).getPesos();
                returnDB = dbGeneric.buscar("GastoEnergetico", new String[]{"_id", "Data", "GastoCalorico", "_idUsuario"}, "sinc = ? AND _idUsuario = ?", new String[]{"0",Integer.toString(id)});
                gastoEnergeticos = new mapToGastoEnergetico(returnDB).getGastoEnergeticos();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference reference = db.getReference();
                reference.child("Usuarios").child(fbId).setValue(usuarios);
                reference.child("Usuarios").child(fbId).child("AtividadesRealizadas").setValue(atividadesRealizadas);
                reference.child("Usuarios").child(fbId).child("Peso").setValue(pesos);
                reference.child("Usuarios").child(fbId).child("GastoEnergetico").setValue(gastoEnergeticos);
                return true;
            }else
                return false;
        } catch (Exception e){
            Log.e("Erro Sinc:",e.getMessage());
            return false;
        }
    }


}

class mapToAtividadesRealizadas{
    List<AtividadesRealizadas> atividadesRealizadas = new ArrayList<>();
    List<List<String>> stringsAtividadesRealizadas;


    public mapToAtividadesRealizadas(List<List<String>> stringsAtividadesRealizadas) {
        this.stringsAtividadesRealizadas = stringsAtividadesRealizadas;
        mapear(this.atividadesRealizadas,this.stringsAtividadesRealizadas);
    }

    private void mapear(List<AtividadesRealizadas> atividadesRealizadas, List<List<String>> stringsAtividadesRealizadas) {
        for (List<String> l:stringsAtividadesRealizadas) {
            AtividadesRealizadas at = new  AtividadesRealizadas();
            at.setId(Integer.parseInt(l.get(0)));
            at.setIdAtividade(Integer.parseInt(l.get(1)));
            at.setDia(Long.parseLong(l.get(2)));
            at.setHoraInicio(Long.parseLong(l.get(3)));
            at.setHoraFim(Long.parseLong(l.get(4)));
            at.setIdUsuario(Integer.parseInt(l.get(5)));
            atividadesRealizadas.add(at);

        }
    }

    public List<AtividadesRealizadas> getAtividadesRealizadas() {
        return atividadesRealizadas;
    }
}

class mapToUsuarios{


    List<Usuario> usuarios = new ArrayList<>();
    List<List<String>> stringsUsuarios;


    public mapToUsuarios(List<List<String>> stringsUsuarios) {
        this.stringsUsuarios = stringsUsuarios;
        mapear(this.usuarios,this.stringsUsuarios);
    }

    private void mapear(List<Usuario> usuarios, List<List<String>> stringsUsuarios) {
        for (List<String> l:stringsUsuarios) {
            Usuario us = new Usuario();
            us.setId(Integer.parseInt(l.get(0)));
            us.setAltura(Double.parseDouble(l.get(1)));
            us.setMassaCorporal(Double.parseDouble(l.get(2)));
            us.setGastoMedio(Double.parseDouble(l.get(3)));
            us.setNome(l.get(4));
            us.setEmail(l.get(5));
            us.setDataCriacao(Long.parseLong(l.get(6)));
            us.setPesoMinimo(Double.parseDouble(l.get(7)));
            usuarios.add(us);

        }
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

}

class mapToPesos {
    private List<Peso> pesos = new ArrayList<>();
    List<List<String>> stringsPesos;

    public mapToPesos(List<List<String>> stringsPesos) {
        this.stringsPesos = stringsPesos;
        mapear(getPesos(),this.stringsPesos);
    }


    private void mapear(List<Peso> pesos, List<List<String>> stringsPesos) {
        for (List<String> l:stringsPesos) {
            Peso ps = new Peso();
            ps.setId(Integer.parseInt(l.get(0)));
            ps.setData( Long.parseLong(l.get(1)));
            ps.setMassaCorporal(Double.parseDouble(l.get(2)));
            ps.setIdUsuario(Integer.parseInt(l.get(3)));
            pesos.add(ps);
        }
    }

    public List<Peso> getPesos() {
        return pesos;
    }

    public void setPesos(List<Peso> pesos) {
        this.pesos = pesos;
    }
}

class mapToGastoEnergetico{
    private List<GastoEnergetico> gastoEnergeticos = new ArrayList<>();
    List<List<String>> stringGastoEnergetico;

    public mapToGastoEnergetico(List<List<String>> stringGastoEnergetico) {
        this.stringGastoEnergetico = stringGastoEnergetico;
        mapear(gastoEnergeticos,this.stringGastoEnergetico);
    }

    private void mapear(List<GastoEnergetico> gastoEnergeticos, List<List<String>> stringGastoEnergetico) {
        for (List<String> l:stringGastoEnergetico) {
            GastoEnergetico ge = new GastoEnergetico();
            ge.setId(Integer.parseInt(l.get(0)));
            ge.setData(Long.parseLong(l.get(1)));
            ge.setCalorias(Double.parseDouble(l.get(2)));
            ge.setIdUsuario(Integer.parseInt(l.get(3)));
            gastoEnergeticos.add(ge);
        }
    }

    public List<GastoEnergetico> getGastoEnergeticos() {
        return gastoEnergeticos;
    }

    public void setGastoEnergeticos(List<GastoEnergetico> gastoEnergeticos) {
        this.gastoEnergeticos = gastoEnergeticos;
    }
}