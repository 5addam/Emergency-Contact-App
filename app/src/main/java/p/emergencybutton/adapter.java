package p.emergencybutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by mufassirmughal on 7/31/2018.
 */

public class adapter extends RecyclerView.Adapter<adapter.Holder> {

    List<String> names;
    List<String> numbers;
    List<Bitmap> photos;
    List<String> values;
    Context context;

    public adapter(List<String> names, List<String> numbers, List<Bitmap> photos,List<String>values, Context context) {
        this.names = names;
        this.numbers = numbers;
        this.photos = photos;
        this.values = values;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.adapter_view, parent, false);
        return new Holder(view, context);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        holder.name.setText(names.get(position));
        holder.number.setText(numbers.get(position));
        holder.photo.setImageBitmap(photos.get(position));
        if (values.contains(numbers.get(position))) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setTag(position);

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;
        ImageView photo;
        CheckBox checkBox;
        Context context;

        public Holder(View itemView, final Context context) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.txt_name);
            number = (TextView) itemView.findViewById(R.id.txt_number);
            photo = (ImageView) itemView.findViewById(R.id.img_contactPhoto);
            checkBox = (CheckBox) itemView.findViewById(R.id.check);
            this.context = context;

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(checkBox.isChecked()){
                        checkBox.setChecked(true);
                        ((MainActivity)context).values.add(numbers.get(pos));
                    }
                    if(!checkBox.isChecked()){
                        checkBox.setChecked(false);
                        ((MainActivity)context).values.remove(numbers.get(pos));
                    }
                }
            });

        }
    }
}
