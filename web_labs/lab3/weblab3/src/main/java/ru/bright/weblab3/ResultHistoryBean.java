package ru.bright.weblab3;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import ru.bright.weblab3.db.ResultDAO;
import ru.bright.weblab3.entity.CheckData;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PostLoad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//@Named
//@ApplicationScoped
@Getter
@Setter
public class ResultHistoryBean {


    private ResultHistoryBean instance;
    private static ResultHistoryBean instanceSingle;
    private List<CheckData> results;
    private final static Gson gson = new Gson();
    //    @Inject
    private ResultDAO resultDAO;
    private static volatile boolean initialized = false;

    public ResultHistoryBean() {
        results = new CopyOnWriteArrayList<>();
        // :)
        if (instanceSingle != null) {
            this.instance = instanceSingle;
            this.results = instanceSingle.getResults();
            this.resultDAO = instanceSingle.getResultDAO();
        } else {
            this.instance = this;
            instanceSingle = this;
        }
    }


    public void addResult(CheckData result) {
        try {
            resultDAO.save(result);
            results.add(result);
        } catch (Exception e) {
            System.err.println("Error while result saving: " + e.getMessage());
        }
    }

    public void deleteResult(CheckData result) {
        try {
            resultDAO.delete(result);
            results.remove(result);
        } catch (Exception e) {
            System.err.println("Error while result removing: " + e.getMessage());
        }
    }

    public List<CheckData> getResults() {
        List<CheckData> reversed = new ArrayList<>(results);
        Collections.reverse(reversed);
        return reversed;
    }

    public String getResultsAsJson() {
        return results.isEmpty() ? "[]" : gson.toJson(results);
    }

    @PostConstruct
    public void init() {
        if(initialized) {
            return;
        }
        System.out.println("ResultHistory loading...");
        try {
            List<CheckData> resultsFromDb = resultDAO.findAll();
            results.addAll(resultsFromDb);
            System.out.println("Loaded " + results.size() + " objects.");
            instanceSingle = this;
        } catch (Exception e) {
            System.err.println("Get exception " + e.getMessage());
        }
        initialized = true;
    }


  //  public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
  //      init();
  //  }


}
