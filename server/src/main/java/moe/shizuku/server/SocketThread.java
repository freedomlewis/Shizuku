package moe.shizuku.server;

import android.os.Handler;
import android.os.Process;
import android.os.RemoteException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

import moe.shizuku.server.util.ServerLog;

/**
 * Created by rikka on 2017/9/23.
 */

public class SocketThread implements Runnable {

    private final Handler mHandler;
    private final ServerSocket mServerSocket;

    private final UUID mToken;

    //private final RequestHandler mRequestHandler;

    SocketThread(Handler handler, ServerSocket serverSocket, UUID token) {
        mHandler = handler;
        mServerSocket = serverSocket;
        mToken = token;
        //mRequestHandler = new RequestHandler(this);
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        for (; ; ) {
            try {
                Socket socket = mServerSocket.accept();
                //boolean stop = !mRequestHandler.handle(socket, sToken);
                RequestHandler.handle(socket, mToken);
                socket.close();

                /*if (stop) {
                    break;
                }*/
            } catch (IOException e) {
                if (SocketException.class.equals(e.getClass()) && "Socket closed".equals(e.getMessage())) {
                    ServerLog.i("server socket is closed");
                    break;
                }
                ServerLog.w("cannot accept", e);
            /*} catch (RemoteException e) {
                ServerLog.w("remote error", e);*/
            } catch (Exception e) {
                ServerLog.w("error", e);
            }
        }
        mHandler.sendEmptyMessage(Server.MESSAGE_EXIT);
        try {
            mServerSocket.close();
        } catch (IOException ignored) {
        }
    }
}
