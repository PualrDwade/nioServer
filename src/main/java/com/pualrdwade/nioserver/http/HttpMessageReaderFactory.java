package com.pualrdwade.nioserver.http;

import com.pualrdwade.nioserver.IMessageReader;
import com.pualrdwade.nioserver.IMessageReaderFactory;

/**
 * Created by jjenkov on 18-10-2015.
 */
public class HttpMessageReaderFactory implements IMessageReaderFactory {

    public HttpMessageReaderFactory() {
    }

    @Override
    public IMessageReader createMessageReader() {
        return new HttpMessageReader();
    }
}
