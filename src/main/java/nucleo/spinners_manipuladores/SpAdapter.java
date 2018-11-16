package nucleo.spinners_manipuladores;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ifmg.polardispendium_gastocalorico.R;

import java.util.ArrayList;
import java.util.List;

public class SpAdapter extends ArrayAdapter<String> {
    Context context;
    List<String> strings;

    public SpAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
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

        TextView tvItemSpinner = (TextView) super.getView(position, convertView, parent);
        tvItemSpinner.setSingleLine(false);
        tvItemSpinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvItemSpinner.setTextColor(Color.BLACK);
        tvItemSpinner.setText(strings.get(position));
        return tvItemSpinner;

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tvItemSpinner = (TextView) super.getDropDownView(position, convertView, parent);
        tvItemSpinner.setSingleLine(false);
        tvItemSpinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        tvItemSpinner.setTextColor(Color.BLACK);
        tvItemSpinner.setText(strings.get(position));
        return tvItemSpinner;
    }
}
