package com.pualrdwade.nioserver;

/**
 * @author PualrDwade
 * @apiNote Message的处理者接口
 */
public interface IMessageProcessor {

    public void process(Message request, WriteProxy writeProxy);

}
