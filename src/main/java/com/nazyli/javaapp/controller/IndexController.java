package com.nazyli.javaapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.system.SystemProperties;
import org.springframework.core.SpringVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@RestController
@RequestMapping("/api")
public class IndexController {

    @GetMapping
    public Map<String, Object> checkHostname(HttpServletRequest request) throws UnknownHostException {

        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        String ipRequest = "";
        String ipServer = "";
        if (xForwardedForHeader == null) {
            ipRequest = request.getRemoteAddr();
        } else {
            ipRequest = new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ipServer = socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            ipServer = "failed get ip";
        }

        Map<String, Object> out = new HashMap<>();

        out.put("IPRequest", ipRequest);
        out.put("IPPrivate", ipServer);
        out.put("Hostname", InetAddress.getLocalHost().getHostName());
        out.put("Time", new Date().toString());
        return out;
    }
    @GetMapping("/version")
    public Map<String, Object> checkVersion(HttpServletRequest request) {
        Map<String, Object> out = new HashMap<>();
        out.put("app-version", "0.0.5");
        out.put("spring-version", SpringVersion.getVersion());
        out.put("jdk-version", SystemProperties.get("java.version"));
        out.put("java-version", JavaVersion.getJavaVersion().toString());
        out.put("time", new Date().toString());
        return out;
    }

}
