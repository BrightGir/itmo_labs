package ru.bright.weblab3.db;

import lombok.Getter;
import lombok.Setter;
import ru.bright.weblab3.entity.CheckData;

import javax.ejb.Stateless;


@Getter
@Setter
public class ResultDAO extends DefaultDAO<CheckData, Long> {

    public ResultDAO() {
        super(CheckData.class);
    }
}