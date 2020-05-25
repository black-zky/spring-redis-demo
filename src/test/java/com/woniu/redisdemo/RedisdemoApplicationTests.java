package com.woniu.redisdemo;

import com.woniu.dao.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

@SpringBootTest
@ContextConfiguration(value = "classpath:applicationContext.xml")
class RedisdemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void testString(){
        redisTemplate.opsForValue().set("hello","world");
    }

    @Test
    public void testSerialzerObj(){
        User user = new User();
        user.setBirthday(new Date());
        user.setId(1001);
        user.setUsername("zhangsan");
        redisTemplate.opsForValue().set("user"+user.getId(),user);
        List list = new ArrayList<>();
        list.add(user);
        redisTemplate.opsForValue().set("userList",list);
    }

    @Test
    public void testDeserializerObj(){
        User user = (User) redisTemplate.opsForValue().get("user1001");
        System.out.println(user.getUsername()+":"+user.getBirthday());

        List<User> users = (List<User>) redisTemplate.opsForValue().get("userList");
        for (User u : users){
            System.out.println(u.toString());
        }
    }

    @Test
    public void testHash(){
        Map map = new HashMap<>();
        map.put("name", "hello");
        map.put("age", "20");
        redisTemplate.opsForValue().set("myhash", map);

        Map<String,String> result = (Map<String, String>) redisTemplate.opsForValue().get("myhash");
        for (Map.Entry<String, String> entry : result.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    @Test
    public void testList(){
        redisTemplate.delete("myList");
        for(int i = 0; i<10; i++){
            User user  = new User();
            user.setId(i);
            user.setUsername("hello"+i);
            user.setBirthday(new Date());
            redisTemplate.opsForList().rightPush("myList",user);
        }

        List<User> users = redisTemplate.opsForList().range("myList",0,-1);
        for (User u : users){
            System.out.println(u.toString());
        }

        User u = (User) redisTemplate.opsForList().index("myList",2);
        System.out.println(u.toString());
    }

    @Test
    public void testSet(){
        redisTemplate.opsForSet().add("mySet", new String[]{"a","b","c"});
        Set<String> set = redisTemplate.opsForSet().members("mySet");
        for (String s : set) {
            System.out.println(s);
        }

        String s = (String) redisTemplate.opsForSet().pop("mySet");
        System.out.println(s);
    }

    @Test
    public void testZSet(){
        redisTemplate.opsForZSet().add("scores","java", 100d);
        redisTemplate.opsForZSet().add("scores","sql", 80d);
        redisTemplate.opsForZSet().add("scores","html", 90d);

        Set set = new HashSet();
        DefaultTypedTuple ddt = new DefaultTypedTuple("python", 88d);
        set.add(ddt);
        redisTemplate.opsForZSet().add("scores",set);

        Set<String> result = redisTemplate.opsForZSet().reverseRange("scores", 0, -1);
        for(String s : result){
            System.out.println("--->"+s);
        }

        Set<ZSetOperations.TypedTuple> scores = redisTemplate.opsForZSet().rangeWithScores("scores", 0, -1);
        for (ZSetOperations.TypedTuple t: scores){
            System.out.println(t.getValue()+":"+t.getScore());
        }
    }
}
