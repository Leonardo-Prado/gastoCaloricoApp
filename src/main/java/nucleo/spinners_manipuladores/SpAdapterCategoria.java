package nucleo.spinners_manipuladores;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifmg.polardispendium_gastocalorico.R;

import java.util.List;

public class SpAdapterCategoria extends ArrayAdapter<String> {
    private Context context;
    private List<String> strings;

    public SpAdapterCategoria(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.strings = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable String item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_layout,parent,false);
        TextView tvItemSpinner = view.findViewById(R.id.tvCategoriaText);
        ImageView imvImagemCategoria = view.findViewById(R.id.imvImagemCategoria);
        tvItemSpinner.setSingleLine(false);
       // tvItemSpinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvItemSpinner.setTextColor(Color.BLACK);
        tvItemSpinner.setText(strings.get(position));
        Resources resources = getContext().getResources();
        TypedArray typedArray = resources.obtainTypedArray(R.array.imagemListCategorias);
        Drawable drawable = typedArray.getDrawable(position);
        imvImagemCategoria.setImageDrawable(drawable);
        return view;

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_layout,parent,false);
        TextView tvItemSpinner = view.findViewById(R.id.tvCategoriaText);
        ImageView imvImagemCategoria = view.findViewById(R.id.imvImagemCategoria);
        tvItemSpinner.setSingleLine(false);
        // tvItemSpinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvItemSpinner.setTextColor(Color.BLACK);
        tvItemSpinner.setText(strings.get(position));
        Resources resources = getContext().getResources();
        TypedArray typedArray = resources.obtainTypedArray(R.array.imagemListCategorias);
        Drawable drawable = typedArray.getDrawable(position);
        imvImagemCategoria.setImageDrawable(drawable);
        return view;
    }

}


