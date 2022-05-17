package com.jony.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private ArrayList<MensagemDados> mensagemDados;
    public Adapter(ArrayList<MensagemDados> arrayList){
        mensagemDados = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        MensagemDados dado = mensagemDados.get(position);
        holder.cabecalho.setText(dado.getCabecalho());
        holder.mensagens.setText(dado.getMensagem());
    }

    @Override
    public int getItemCount() {
        return mensagemDados.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView cabecalho, mensagens;
        public Holder(@NonNull View itemView) {
            super(itemView);
            cabecalho = itemView.findViewById(R.id.tvCabecalho);
            mensagens = itemView.findViewById(R.id.tvMensagemCorpo);
        }
    }
}
