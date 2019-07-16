package madrid.apiFactory.core.http;


import madrid.apiFactory.core.filter.EatuulFilter;
import madrid.apiFactory.core.filter.post.SendResponseFilter;
import madrid.apiFactory.core.filter.pre.RequestWrapperFilter;
import madrid.apiFactory.core.filter.route.RoutingFilter;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class EatRunner {

    private ConcurrentHashMap<String, List<EatuulFilter>> hashFiltersByType = new ConcurrentHashMap<String, List<EatuulFilter>>() {{
        put("pre", new ArrayList<EatuulFilter>() {
            {
                add(new RequestWrapperFilter());
            }
        });
        put("route", new ArrayList<EatuulFilter>() {
            {
                add(new RoutingFilter());
            }
        });
        put("post", new ArrayList<EatuulFilter>() {
            {
                add(new SendResponseFilter());
            }
        });

    }};


    public void init(HttpServletRequest request, HttpServletResponse response) {
//        Boolean license = LicenseValidate.get().validate();
//        if (license) {
            RequestContext ctx = RequestContext.getCurrentContext();
            ctx.setRequest(request);
            ctx.setResponse(response);
//        }else {
//            throw new RuntimeException("License过期，请联系系统管理员！");
//        }
    }


    public void preRoute() throws Throwable {
        runFilters("pre");
    }

    public void route() throws Throwable {
        runFilters("route");
    }

    public void postRoute() throws Throwable {
        runFilters("post");
    }

    public void runFilters(String sType) throws Throwable {
        List<EatuulFilter> list = this.hashFiltersByType.get(sType);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                EatuulFilter zuuFilter = list.get(i);
                zuuFilter.run();
            }
        }
    }
}
