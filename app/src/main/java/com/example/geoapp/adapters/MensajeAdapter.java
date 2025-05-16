package com.example.geoapp.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.geoapp.R;
import com.example.geoapp.global;
import java.util.List;
public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {
    private List<com.example.geoapp.model.Mensaje> listaMensajes;
    public MensajeAdapter(List<com.example.geoapp.model.Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }
    public void actualizarMensajes(List<com.example.geoapp.model.Mensaje> nuevos) {
        this.listaMensajes = nuevos;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        String uidActual = global.getInstance().getUid();
        return listaMensajes.get(position).getUid().equals(uidActual) ? 1 : 0;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.message_right : R.layout.message_left;
        View vista = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(vista);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.example.geoapp.model.Mensaje m = listaMensajes.get(position);
        holder.texto.setText(m.getMessage());
    }
    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView texto, nombre;
        ImageView picture;
        LinearLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.message);
            container = itemView.findViewById(R.id.message_container);
        }
    }
}