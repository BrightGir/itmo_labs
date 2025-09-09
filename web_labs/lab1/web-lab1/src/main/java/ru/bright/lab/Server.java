package ru.bright.lab;

import com.fastcgi.FCGIInterface;

import java.util.Map;
import java.util.logging.Logger;

public class Server {

    private ResponseService responseService;
    private RequestService requestService;

    public Server() {
        this.requestService = new RequestService();
        this.responseService = new ResponseService();
    }

    public void start() {
        while(true) {
            FCGIInterface fcgi = null;
            fcgi = new FCGIInterface();
            while (fcgi.FCGIaccept() >= 0) {
                try {
                    String method = System.getProperty("REQUEST_METHOD");
                    if (method == null || !method.equalsIgnoreCase("POST")) {
                        logError("Got non-POST method..");
                        System.out.print("Content-Type: text/plain\r\n\r\n");
                        System.out.print("This service only accepts POST requests.");
                        System.out.flush();
                        continue;
                    }
                    long startTime = System.nanoTime();
                    String data = requestService.readData(Integer.parseInt(System.getProperty("CONTENT_LENGTH")));
                    Map<String, String> formData = requestService.parseFormData(data);
                    String xStr = formData.get("x");
                    String yStr = formData.get("y");
                    String rStr = formData.get("r");
                    if (xStr == null || yStr == null || rStr == null) {
                        responseService.sendJsonError("Invalid request");
                        continue;
                    }
                    double x = 0;
                    double y = 0;
                    double r = 0;
                    try {
                        x = Double.parseDouble(xStr);
                        y = Double.parseDouble(yStr);
                        r = Double.parseDouble(rStr);
                    } catch (NumberFormatException e) {
                        responseService.sendJsonError("Invalid request");
                        continue;
                    }
                    long endTime = System.nanoTime();
                    long passedTimeNano = endTime - startTime;
                    responseService.sendJsonResponse(new AreaResponse(
                            xStr, yStr, rStr,
                            isInArena(x, y, r),
                            passedTimeNano));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    logError(e.getMessage());
                }
            }
        }

    }

    private boolean isInArena(double x, double y, double r) {
        if(x == 0) {
            return Math.abs(y) <= r;
        }
        // Прямоугольник
        if (x <= 0 && y >= 0) {
            return (x >= -r) && (y <= r/2);
        }
        // Треугольник
        if (x <= 0 && y <= 0) {
            //выше прямой
            return (y >= -2*x - r);
        }
        // Круг
        if (x >= 0 && y >= 0) {
            return (x*x + y*y <= (r/2)*(r/2));
        }
        return false;
    }

    private void logError(String message) {
        Logger.getGlobal().severe(message);
    }




}
