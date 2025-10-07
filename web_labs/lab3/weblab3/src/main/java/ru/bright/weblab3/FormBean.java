package ru.bright.weblab3;

import lombok.Getter;
import lombok.Setter;
import ru.bright.weblab3.entity.CheckData;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

//@Named
//@RequestScoped
@Setter
@Getter
public class FormBean {

    public FormBean() {

    }

    // @Inject
    private ResultHistoryBean resultHistoryBean;

    private Double x;
    private Double y;
    private Double r;
    private Long clientTimeStamp;
    private String clientTimeZone;

    public String submit() {
        if (x == null || y == null || r == null) {
            return null;
        }
        long startTime = System.nanoTime();
        boolean hit = checkHit(x, y, r);
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e6;

        CheckData result = new CheckData();
        result.setX(x);
        result.setY(y);
        result.setR(r);
        result.setHit(hit);
        result.setExecutionTime(executionTime);
        result.setClientTimestamp(clientTimeStamp);
        result.setClientTimeZone(clientTimeZone);

        resultHistoryBean.addResult(result);

        x = null;
        y = null;
       // r = null;
        return null;
    }


    private boolean checkHit(Double x, Double y, Double r) {
        double ellipseA = 7;
        double ellipseB = 3;
        double xNormalized = x/(r/4);
        double yNormalized = y/(r/4);
        if(!((xNormalized*xNormalized)/(ellipseA*ellipseA) + (yNormalized*yNormalized)/(ellipseB*ellipseB) <= 1)) {
            return false;
        }

        if(xNormalized >= -4 && xNormalized <= 4) {
            if(!(yNormalized >= fBatmanBottom(xNormalized))) {
                return false;
            }
        }
        if((xNormalized >= -3 && xNormalized <= -1) || (xNormalized >= 1 && xNormalized <= 3)) {
            if(!(yNormalized <= fBatmanWing(xNormalized))) {
                return false;
            }
        }
        if((xNormalized >= -1 && xNormalized <= -0.75) || (xNormalized >= 0.75 && xNormalized <= 1)) {
            if(!(yNormalized <= fHeadSide(xNormalized))) {
                return false;
            }
        }
        if((xNormalized >= -0.75 && xNormalized <= -0.5) || (xNormalized >= 0.5 && xNormalized <= 0.75)) {
            if(!(yNormalized <= fEarSide(xNormalized))) {
                return false;
            }
        }
        if((xNormalized >= -0.5 && xNormalized <= 0.5)) {
            if(!(yNormalized <= 2.25)) {
                return false;
            }
        }
        return true;
    }

    private double fBatmanBottom(double x) {
        return Math.abs(x/2.0) - (3.0*Math.sqrt(33.0)-7.0)/112.0 * x*x - 3.0 +
                Math.sqrt(1.0 - Math.pow(Math.abs(Math.abs(x)-2.0)-1.0, 2));
    }

    private double fBatmanWing(double x) {
        if(x == -1 || x == 1) {
            return 1;
        }
        return (6.0*Math.sqrt(10.0))/7.0 +
                (1.5 - 0.5*Math.abs(x)) * Math.signum(Math.abs(x) - 1.0) -
                (6.0*Math.sqrt(10.0))/14.0 * Math.sqrt(4.0 - Math.pow(Math.abs(x) - 1.0, 2));
    }

    private double fHeadSide(double x) {
        if(x >= 0) {
            return -8*x + 9;
        } else {
            return 8*x+9;
        }
    }

    private double fEarSide(double x) {
        if(x >= 0) {
            return 3*x+0.75;
        } else {
            return -3*x+0.75;
        }
    }

}
