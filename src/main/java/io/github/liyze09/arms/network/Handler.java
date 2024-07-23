package io.github.liyze09.arms.network;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler {
    public static final Handler handler = new Handler();

    private Handler() {
    }

    public void accept(@NotNull Socket socket) throws IOException {
        socket.setKeepAlive(true);
        var out = new DataOutputStream(socket.getOutputStream());
        var in = new DataInputStream(socket.getInputStream());
    }


}
