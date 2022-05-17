package com.jony.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.debugger.ReflectionDebuggerFactory;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mAtualiza;
    private Adapter mAdaptador;
    private ArrayList<MensagemDados> mConteudo = new ArrayList<>();
    private AbstractXMPPConnection mConexao;
    public static final String TAG = MainActivity.class.getSimpleName();
    private EditText etEnviaMensagem;
    private Button btEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAtualiza = findViewById(R.id.rvItem);
        mAdaptador = new Adapter (mConteudo);
        etEnviaMensagem = findViewById(R.id.etEnviaMensagem);
        btEnviar = findViewById(R.id.btEnviar);

        LinearLayoutManager gerencia = new LinearLayoutManager(this);
        DividerItemDecoration decoracao = new DividerItemDecoration(this, gerencia.getOrientation());

        mAtualiza.addItemDecoration(decoracao);
        mAtualiza.setLayoutManager(gerencia);
        mAtualiza.setAdapter(mAdaptador);

        setConexao();

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enviaMensagem = etEnviaMensagem.getText().toString();
                if(enviaMensagem.length() > 0){

                    enviar(enviaMensagem,"admin@10.25.93.6");
                    etEnviaMensagem.setText("");

                }
            }
        });
    }

    private void enviar(String mensagem, String id) {
        EntityBareJid jid = null;
        try {
            jid = JidCreate.entityBareFrom(id);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        if(mConexao != null) {
            ChatManager gerenciadorChat = ChatManager.getInstanceFor(mConexao);
            Chat chat = gerenciadorChat.chatWith(jid);
            Message novaMensagem = new Message();
            novaMensagem.setBody(mensagem);
            try {
                chat.send(novaMensagem);
                MensagemDados conteudo = new MensagemDados("User1", mensagem);
                mConteudo.add(conteudo);
                mAdaptador = new Adapter(mConteudo);
                mAtualiza.setAdapter(mAdaptador);

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void setConexao (){
        // Create the configuration for this new connection

        new Thread(){
            @Override
            public void run() {

                InetAddress addr = null;
                try {
                    addr = InetAddress.getByName("10.25.93.6");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                HostnameVerifier verifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return false;
                    }
                };
                DomainBareJid serviceName = null;
                try {
                    serviceName = JidCreate.domainBareFrom("10.25.93.6");
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()

                        .setUsernameAndPassword("user1", "12345")
                        .setPort(5222)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceName)
                        .setHostnameVerifier(verifier)
                        .setHostAddress(addr)
                        .setDebuggerFactory(ReflectionDebuggerFactory.INSTANCE)
                        .build();
                mConexao = new XMPPTCPConnection(config);

                try {
                    mConexao.connect();
                    mConexao.login();

                    // Envia e Recebe mensagens
                    if(mConexao.isAuthenticated() && mConexao.isConnected()){
                        // Assume we've created an XMPPConnection name "connection".

                        Log.e(TAG,"Exe: Autorização concluída e conectada com sucesso!");
                        ChatManager chatManager = ChatManager.getInstanceFor(mConexao);
                        chatManager.addIncomingListener(new IncomingChatMessageListener() {
                            @Override
                            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                                Log.e(TAG,"Nova mensagem de " + from + ": " + message.getBody());

                                MensagemDados dado = new MensagemDados(from.toString(), message.getBody().toString());
                                mConteudo.add(dado);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdaptador = new Adapter(mConteudo);
                                        mAtualiza.setAdapter(mAdaptador);
                                    }
                                });
                            }
                        });
                    }
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}