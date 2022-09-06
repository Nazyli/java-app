package com.nazyli.javaapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
        out.put("IPPublic", InetAddress.getLocalHost().getHostAddress());
        out.put("Hostname", InetAddress.getLocalHost().getHostName());
        return out;
    }

}
