package com.pualrdwade.nioserver;

/**
 * @author PualrDwade
 * @apiNote Message的处理者接口
 */
public interface IMessageProcessor {

    /**
     * 处理message
     *
     * @param request
     * @param writeProxy
     */
    public void process(Message request, WriteProxy writeProxy);

}
