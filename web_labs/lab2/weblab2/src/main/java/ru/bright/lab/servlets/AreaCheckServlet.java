package ru.bright.lab.servlets;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ru.bright.lab.data.CheckData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/checkServlet")
public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            long startTime = System.nanoTime();
            String xStr = req.getParameter("x");
            String yStr = req.getParameter("y");
            String rStr = req.getParameter("r");
            double x = Double.parseDouble(xStr.replace(",", "."));
            double y = Double.parseDouble(yStr.replace(",", "."));
            double r = Double.parseDouble(rStr.replace(",", "."));
            if (!isValid(x, y, r)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректные параметры");
                return;
            }
            boolean isHit = checkHit(x, y, r);
            long executionTime = System.nanoTime() - startTime;
            CheckData result = new CheckData(x, y, r, isHit, executionTime);
            HttpSession session = req.getSession();
            List<CheckData> results = (List<CheckData>) session.getAttribute("results");
            if (results == null) {
                results = new ArrayList<>();
            }
            results.add(result);
            session.setAttribute("results", results);
            req.setAttribute("result", result);
            getServletContext().getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (NumberFormatException | NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректные или недостающие параметры");
        }
    }


    private boolean checkHit(double x, double y, double r) {
        if(x == 0) return y <= r && y >= -r/2;
        if(y == 0) return x >= -r && x <= r/2;
        if(x > 0 && y > 0) return x*x + y*y <= r*r/4;
        if(x < 0 && y > 0) return x >= -r/2 && y <= r;
        if(x < 0 && y < 0) return y >= -(0.5)*x - r/2; // k = 1/2, y >= kx
        return false;
    }


    private boolean isValid(double x, double y, double r) {
        return (x >= -5 && x <= 5) &&
                (y >= -4 && y <= 4) &&
                (r >= 1 && r <= 3);
    }
}
