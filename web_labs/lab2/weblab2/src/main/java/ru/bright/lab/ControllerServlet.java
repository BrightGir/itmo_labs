package ru.bright.lab;


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
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = "")
public class ControllerServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String xStr = req.getParameter("x");
        String yStr = req.getParameter("y");
        String rStr = req.getParameter("r");
        if(xStr != null && yStr != null && rStr != null) {
            getServletContext().getRequestDispatcher("/checkServlet").forward(req,resp);
        } else {
            HttpSession session = req.getSession();
            List<CheckData> results = (List<CheckData>) session.getAttribute("results");
            if(results == null) {
                results = new ArrayList<>();
            }
            String history = gson.toJson(results);
            req.setAttribute("historyJson", history);
            List<CheckData> reversedList = new ArrayList<>(results);
            Collections.reverse(reversedList);
            req.setAttribute("resultsReversed", reversedList);
            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
        }

    }

}
