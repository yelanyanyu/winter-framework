package com.yelanyanyu;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class SnakeyamlTest {
    @Test
    public void t1() {
        Yaml yaml = new Yaml();
        Object load = yaml.load(this.getClass().getClassLoader().getResourceAsStream("test.yml"));
        System.out.println(load);
    }
}
