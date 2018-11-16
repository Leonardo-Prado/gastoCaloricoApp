package objetos_auxiliares;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class DialogConstrutor extends AlertDialog.Builder {
    private String titulo;
    private String menssagem;
    private AlertDialog dialog;
    private String positiveButtonTexto;

    public DialogConstrutor(@NonNull Context context, String titulo, String menssagem, String positiveButtonTexto) {
        super(context);
        setTitulo(titulo);
        setMenssagem(menssagem);
        this.positiveButtonTexto = positiveButtonTexto;
        this.setPositiveButton();
        this.setDialog(this.create());
        getDialog().show();


    }

    public DialogConstrutor(@NonNull Context context, String titulo, String menssagem) {
        super(context);
        setTitulo(titulo);
        setMenssagem(menssagem);
        setDialog(this.create());
        getDialog().show();

    }

    public DialogConstrutor(@NonNull Context context) {
        super(context);
        setDialog(this.create());
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        setTitle(titulo);
        this.titulo = titulo;
    }

    public String getMenssagem() {
        return menssagem;
    }

    public void setMenssagem(String menssagem) {
        this.setMessage(menssagem);
        this.menssagem = menssagem;
    }

    public String getPositiveButtonTexto() {
        return positiveButtonTexto;
    }

    public void setPositiveButtonTexto(String positiveButtonTexto) {
        this.positiveButtonTexto = positiveButtonTexto;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    private void setPositiveButton() {
        super.setPositiveButton(getPositiveButtonTexto(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
    }
}
