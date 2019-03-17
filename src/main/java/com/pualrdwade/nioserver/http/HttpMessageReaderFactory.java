package com.pualrdwade.nioserver.http;

import com.pualrdwade.nioserver.IMessageReader;
import com.pualrdwade.nioserver.IMessageReaderFactory;

/**
 * @author PualrDwade
 */
public class HttpMessageReaderFactory implements IMessageReaderFactory {

    public HttpMessageReaderFactory() {
    }

    @Override
    public IMessageReader createMessageReader() {
        return new HttpMessageReader();
    }
}
