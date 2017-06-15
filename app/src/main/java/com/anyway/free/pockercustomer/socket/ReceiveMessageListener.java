package com.anyway.free.pockercustomer.socket;

/**
 * Created by john on 2017/1/1.
 */
public interface ReceiveMessageListener {

    void onReceiveMessage(long msg, byte[] data);

}