package com.hochoy.javabase.nio;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPReactor implements Runnable {


    private final ServerSocketChannel ssc;
    private final Selector selector;

    public TCPReactor(int port) throws Exception {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();

        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(),port);
        ssc.socket().bind(address);
        ssc.configureBlocking(false);

        //向selector注册该channel
        SelectionKey register = ssc.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("-->Start serverSocket.register!");

        register.attach(new Acceptor(ssc, selector));
        System.out.println("-->attach(new Acceptor()!");

    }

    @Override
    public void run() {

        while (!Thread.interrupted()) {
            System.out.println("waiting for new event on port : " + ssc.socket().getLocalPort());
            try {
                if (selector.select() == 0) {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();

            //Selector如果发现channel有OP_ACCEPT或READ事件发生，下列遍历就会进行。
            while (it.hasNext()) {

                SelectionKey next = it.next();
                //来一个事件 第一次触发一个accepter线程
                //以后触发SocketReadHandler
                dispatch(next);
//                it.remove();
            }
            selectionKeys.clear();


        }

    }

    void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        if (r != null) {
            r.run();
        }
    }
}

class Acceptor implements Runnable {
    private final ServerSocketChannel ssc;
    private final Selector selector;

    public Acceptor(ServerSocketChannel ssc, Selector selector) {
        this.ssc = ssc;
        this.selector = selector;
    }

    @Override
    public void run() {

        try {
            SocketChannel sc = ssc.accept();
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + "is connected");
            if (sc != null) {
                sc.configureBlocking(false);
                SelectionKey sk = sc.register(selector, SelectionKey.OP_READ);
                selector.wakeup();
                sk.attach(new TCPHandler(selector, sc));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


class TCPHandler implements Runnable {

    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(Integer.MAX_VALUE);
    ByteBuffer output = ByteBuffer.allocate(Integer.MAX_VALUE);
    static final int READING = 0, SENDING = 1;
    int state = READING;


    public TCPHandler(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        //设置为非阻塞模式
        c.configureBlocking(false);
        //此处的0，表示不关注任何时间
        sk = socket.register(sel, 0);
        //将SelectionKey绑定为本Handler 下一步有事件触发时，将调用本类的run方法
        sk.attach(this);
        //将SelectionKey标记为可读，以便读取，不可关注可写事件
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }

    boolean inputIsComplete() {
        return false;
    }

    boolean outputIsComplete() {
        return false;
    }

    //这里可以通过线程池处理数据
    void process() {

    }


    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) { /* ... */ }

    }


    void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            process();
            state = SENDING;
            // Normally also do first write now
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        socket.write(output);
        if (outputIsComplete()) {
            //
            sk.cancel();
        }
    }

}

class Main {
    public static void main(String[] args) {
        try {
            TCPReactor reactor = new TCPReactor(1333);
            reactor.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
