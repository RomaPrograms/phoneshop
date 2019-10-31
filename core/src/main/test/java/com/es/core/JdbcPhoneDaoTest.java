package com.es.core;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/context/applicationContext-core.xml")
public class JdbcPhoneDaoTest {

    @Autowired
    private PhoneDao jdbcPhoneDao;

    @Test
    public void testGet() {
        System.out.println("dfghjk");
        Optional<Phone> phone = jdbcPhoneDao.get(1000L);
//
//        if(phone.isPresent()) {
//            System.out.println();
//        }
    }

//    @Test
//    public void testSave() {
//        Phone phone = new Phone();
//        phone.setId(9L);
//        phone.setBrand("Coolest brand");
//        phone.setModel("Coolest model");
//        jdbcPhoneDao.save(phone);
//
//        Optional<Phone> phone1 = jdbcPhoneDao.get(9L);
//        Assert.assertTrue(phone.equals(phone1.get()));
//    }

    @Test
    public void testGetAll() {
        List<Phone> list = jdbcPhoneDao.findAll(1, 1);
        System.out.println("Hello");
    }
}
