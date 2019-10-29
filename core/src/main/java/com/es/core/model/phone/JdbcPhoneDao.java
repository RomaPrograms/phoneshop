package com.es.core.model.phone;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
public class JdbcPhoneDao implements PhoneDao {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Phone> get(final Long key) {
        PreparedStatementSetter preparedStatementSetterForPhones
                = preparedStatement -> preparedStatement.setLong(1, key);
        Phone phone = (Phone) jdbcTemplate.query("select * from phones where id = ?",
                preparedStatementSetterForPhones,
                new BeanPropertyRowMapper(Phone.class)).get(0);
        PreparedStatementSetter preparedStatementSetterForColors
                = preparedStatement -> preparedStatement.setLong(1, phone.getId());
        List<Color> colors = jdbcTemplate.query(
                "select c.id, c.code from phone2color p2c inner join colors"
                        + " c on p2c.col orId = c.id where p2c.phoneId = ?",
                preparedStatementSetterForColors,
                new BeanPropertyRowMapper(Color.class));
        phone.setColors(new HashSet<>(colors));

        return Optional.of(phone);
    }

    public void save(final Phone phone) {
        try {

            for (Color color : phone.getColors()) {
                jdbcTemplate.update("insert into phone2color (phoneId, colorId)"
                                + " values (?, ?)",
                        (PreparedStatementCallback<Boolean>) preparedStatement -> {
                            preparedStatement.setLong(1, phone.getId());
                            preparedStatement.setLong(2, color.getId());
                            return preparedStatement.execute();
                        });
            }

            jdbcTemplate.update("insert into phones (id, brand, model, price,"
                            + " displaySizeInches, weightGr, lengthMm, widthMm, heightMm,"
                            + " announced, deviceType, os, displayResolution, pixelDensity,"
                            + " displayTechnology, backCameraMegapixels, frontCameraMegapixels,"
                            + " ramGb, internalStorageGb, batteryCapacityMah, talkTimeHours,"
                            + " standByTimeHours, bluetooth, positioning, imageUrl, description)"
                            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    preparedStatement -> {
                        preparedStatement.setString(1, phone.getBrand());
                        preparedStatement.setString(2, phone.getModel());
                        preparedStatement.setBigDecimal(4, phone.getPrice());
                        preparedStatement.setBigDecimal(5, phone.getDisplaySizeInches());
                        preparedStatement.setInt(6, phone.getWeightGr());
                        preparedStatement.setBigDecimal(7, phone.getLengthMm());
                        preparedStatement.setBigDecimal(8, phone.getWidthMm());
                        preparedStatement.setBigDecimal(9, phone.getHeightMm());
                        preparedStatement.setDate(10, new Date(phone.getAnnounced().getTime()));
                        preparedStatement.setString(11, phone.getDeviceType());
                        preparedStatement.setString(12, phone.getOs());
                        preparedStatement.setString(13, phone.getDisplayResolution());
                        preparedStatement.setInt(14, phone.getPixelDensity());
                        preparedStatement.setString(15, phone.getDisplayTechnology());
                        preparedStatement.setBigDecimal(16, phone.getBackCameraMegapixels());
                        preparedStatement.setBigDecimal(17, phone.getFrontCameraMegapixels());
                        preparedStatement.setBigDecimal(18, phone.getRamGb());
                        preparedStatement.setBigDecimal(19, phone.getInternalStorageGb());
                        preparedStatement.setInt(20, phone.getBatteryCapacityMah());
                        preparedStatement.setBigDecimal(21, phone.getTalkTimeHours());
                        preparedStatement.setBigDecimal(22, phone.getStandByTimeHours());
                        preparedStatement.setString(23, phone.getBluetooth());
                        preparedStatement.setString(24, phone.getPositioning());
                        preparedStatement.setString(25, phone.getImageUrl());
                        preparedStatement.setString(26, phone.getDescription());
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Phone> findAll(int offset, int limit) {
        List<Phone> phones = jdbcTemplate.query("select * from phones",
                new BeanPropertyRowMapper(Phone.class));
        for(Phone phone : phones) {
            PreparedStatementSetter preparedStatementSetterForColors
                    = preparedStatement -> preparedStatement.setLong(1, phone.getId());
            List<Color> colors = jdbcTemplate.query(
                    "select c.id, c.code from phone2color p2c inner join colors"
                            + " c on p2c.col orId = c.id where p2c.phoneId = ?",
                    preparedStatementSetterForColors,
                    new BeanPropertyRowMapper(Color.class));
            phone.setColors(new HashSet<>(colors));
        }

        return phones;
    }
}
