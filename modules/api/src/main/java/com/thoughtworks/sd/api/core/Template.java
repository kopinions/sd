package com.thoughtworks.sd.api.core;

import java.util.Map;

public interface Template {
    String getId();

    Service instantiation(Map<String, Object> params);
}
