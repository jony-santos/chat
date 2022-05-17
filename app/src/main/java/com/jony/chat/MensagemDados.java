package com.jony.chat;

public class MensagemDados {
    private String cabecalho,mensagem;
    public MensagemDados(String cabecalhoAux, String mensagemAux) {
        cabecalho = cabecalhoAux;
        mensagem = mensagemAux;
    }
    public String getCabecalho(){
        return cabecalho;
    }
    public String getMensagem(){
        return mensagem;
    }
}
