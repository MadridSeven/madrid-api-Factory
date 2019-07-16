package madrid.apiFactory.core.filter;

import java.io.IOException;


public interface EatuulFilter {
    void run() throws IOException;

    int filterOrder();

    String filterType();
}
