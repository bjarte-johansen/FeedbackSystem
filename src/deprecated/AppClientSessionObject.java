package root.controllers;

import jakarta.servlet.http.HttpServletRequest;
import root.app.AppConfig;

import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated
class AppClientSessionObject {
    Map<String, Boolean> attributes = new LinkedHashMap<>();
}
