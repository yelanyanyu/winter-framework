package com.yelanyanyu.transaction;

import com.yelanyanyu.annotation.Autowired;
import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.bean.Monster;
import com.yelanyanyu.jdbc.JdbcTemplate;
import com.yelanyanyu.jdbc.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@Slf4j
@Transactional
public class MonsterService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Monster getMonsterById(int id) {
        return jdbcTemplate.queryForObject("select * from monster where id = ?", Monster.class, id);
    }

    public Number count() {
        return jdbcTemplate.queryForNumber("select count(*) from monster");
    }

    public int addMonster() {
        return jdbcTemplate.update(
                """
                        INSERT INTO Monster (age, birthday, email, gender, name, salary)
                        VALUES (230, '1791-08-05 00:00:00', 'frankenstein@monstermail.com', '1', 'Frankenstein_1', 2000.00)
                        """
        );
    }

    public int update(Monster monster) {
//        int a = 8 / 0;
        return jdbcTemplate.update("update monster set name = ? where id = ?", monster.getName(), monster.getId());
    }

    public void doAll() {
        addMonster();
        log.info("count: {}", count());
        update(new Monster(1, "test_name_2"));
        log.info("monster: {}", getMonsterById(1));
    }

}
