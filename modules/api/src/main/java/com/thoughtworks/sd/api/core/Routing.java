package com.thoughtworks.sd.api.core;

import java.net.URI;

public class Routing {
    public static URI service(Service service) {
        return URI.create("/serivces/" + service.getName());
    }
}
