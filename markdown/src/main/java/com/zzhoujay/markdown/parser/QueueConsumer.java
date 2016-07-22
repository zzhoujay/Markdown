package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-22.
 */
public interface QueueConsumer {


    void setQueueProvider(QueueProvider provider);

    interface QueueProvider {
        LineQueue getQueue();
    }
}
