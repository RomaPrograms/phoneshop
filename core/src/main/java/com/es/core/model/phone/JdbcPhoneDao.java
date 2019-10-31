package com.es.core.model.phone;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class JdbcPhoneDao implements PhoneDao {
    @Resource
    private JdbcTemplate jdbcTemplate;

    private final static String QUERY_FOR_GETTING_ALL_PHONES
            = "select p.id, p.brand, p.model, p.price, p.displaySizeInches,"
            + " p.weightGr, p.lengthMm, p.widthMm, p.heightMm, p.announced,"
            + " p.deviceType, p.os, p.displayResolution, p.pixelDensity,"
            + " p.displayTechnology, p.backCameraMegapixels, p.frontCameraMegapixels,"
            + " p.ramGb, p.internalStorageGb, p.batteryCapacityMah, p.talkTimeHours,"
            + " p.standByTimeHours, p.bluetooth, p.positioning, p.imageUrl,"
            + " p.description, c.id, c.code from phones p left join phone2color p2c"
            + " inner join colors c on p2c.colorId = c.id";

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
                        preparedStatement.setString(2, phone.getBrand());
                        preparedStatement.setString(3, phone.getModel());
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
            System.out.println(e.getMessage()); // что лучше здесь делать когда мы ловим exeption.
        }
    }

    public List<Phone> findAll(int offset, int limit) {
//        System.out.println("Does it works");
//        List<Phone> phones = jdbcTemplate.query("select * from phones",
//                new BeanPropertyRowMapper(Phone.class));
//        for(Phone phone : phones) {
//            PreparedStatementSetter preparedStatementSetterForColors
//                    = preparedStatement -> preparedStatement.setLong(1, phone.getId());
//            List<Color> colors = jdbcTemplate.query(
//                    "select c.id, c.code from phone2color p2c inner join colors"
//                            + " c on p2c.colorId = c.id where p2c.phoneId = ?",
//                    preparedStatementSetterForColors,
//                    new BeanPropertyRowMapper(Color.class));
//            phone.setColors(new HashSet<>(colors));
//        }

//
//        ResultSetExtractor<List<Phone>> phone1
//                = new ResultSetExtractor<List<Phone>>() {
//            @Override
//            public List<Phone> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
//                List<Phone> list = new ArrayList<>();
//                while(resultSet.next()) {
//                    Phone phone1 = new Phone();
//                    phone1.setBrand(resultSet.getString(2));
//                    phone1.setModel(resultSet.getString(3));
//                    phone1.setPrice(resultSet.getBigDecimal(4));
//                    phone1.setDisplaySizeInches(resultSet.getBigDecimal(5));
//                    phone1.setWeightGr(resultSet.getInt(6));
//                    phone1.setLengthMm(resultSet.getBigDecimal(7));
//                    phone1.setWidthMm(resultSet.getBigDecimal(8));
//                    phone1.setHeightMm(resultSet.getBigDecimal(9));
//                    phone1.setAnnounced(resultSet.getDate(10));
//                    phone1.setDeviceType(resultSet.getString(11));
//                    phone1.setOs(resultSet.getString(12));
//                    phone1.setDisplayResolution(resultSet.getString(13));
//                    phone1.setPixelDensity(resultSet.getInt(14));
//                    phone1.setDisplayTechnology(resultSet.getString(15));
//                    phone1.setBackCameraMegapixels(resultSet.getBigDecimal(16));
//                    phone1.setFrontCameraMegapixels(resultSet.getBigDecimal(17));
//                    phone1.setRamGb(resultSet.getBigDecimal(18));
//                    phone1.setInternalStorageGb(resultSet.getBigDecimal(19));
//                    phone1.setBatteryCapacityMah(resultSet.getInt(20));
//                    phone1.setTalkTimeHours(resultSet.getBigDecimal(21));
//                    phone1.setStandByTimeHours(resultSet.getBigDecimal(22));
//                    phone1.setBluetooth(resultSet.getString(23));
//                    phone1.setPositioning(resultSet.getString(24));
//                    phone1.setImageUrl(resultSet.getString(25));
//                    phone1.setDescription(resultSet.getString(26));
//                    list.add(phone1);
//                }
//                return list;
//            }
//        };
//
//        System.out.println("dfsdfdssdf");
        ResultSetExtractor<Map<Long, Phone>> extractorForGettingAllPhones
                = resultSet -> {
                    Map<Long, Phone> map = new HashMap<>();
//            System.out.println("hy");
                    while(resultSet.next()) {
//                        System.out.println(resultSet.getLong(1));
                        Phone existedPhone = map.get(resultSet.getLong(1));
                        if (existedPhone != null) {
                            Color color = new Color();
                            color.setId(resultSet.getLong(27));
                            color.setCode(resultSet.getString(28));
                            existedPhone.getColors().add(color);
                        } else {
//                            System.out.println(resultSet.getLong(1));
                            Phone phone = new Phone();
                            phone.setColors(new HashSet<>());
                            phone.setBrand(resultSet.getString(2));
                            phone.setModel(resultSet.getString(3));
                            phone.setPrice(resultSet.getBigDecimal(4));
                            phone.setDisplaySizeInches(resultSet.getBigDecimal(5));
                            phone.setWeightGr(resultSet.getInt(6));
                            phone.setLengthMm(resultSet.getBigDecimal(7));
                            phone.setWidthMm(resultSet.getBigDecimal(8));
                            phone.setHeightMm(resultSet.getBigDecimal(9));
                            phone.setAnnounced(resultSet.getDate(10));
                            phone.setDeviceType(resultSet.getString(11));
                            phone.setOs(resultSet.getString(12));
                            phone.setDisplayResolution(resultSet.getString(13));
                            phone.setPixelDensity(resultSet.getInt(14));
                            phone.setDisplayTechnology(resultSet.getString(15));
                            phone.setBackCameraMegapixels(resultSet.getBigDecimal(16));
                            phone.setFrontCameraMegapixels(resultSet.getBigDecimal(17));
                            phone.setRamGb(resultSet.getBigDecimal(18));
                            phone.setInternalStorageGb(resultSet.getBigDecimal(19));
                            phone.setBatteryCapacityMah(resultSet.getInt(20));
                            phone.setTalkTimeHours(resultSet.getBigDecimal(21));
                            phone.setStandByTimeHours(resultSet.getBigDecimal(22));
                            phone.setBluetooth(resultSet.getString(23));
                            phone.setPositioning(resultSet.getString(24));
                            phone.setImageUrl(resultSet.getString(25));
                            phone.setDescription(resultSet.getString(26));
                            map.put(resultSet.getLong(1), phone);
                        }
                    }
                    return map;
                };

        Map<Long, Phone> map = jdbcTemplate.query(QUERY_FOR_GETTING_ALL_PHONES,
                extractorForGettingAllPhones);

//        System.out.println(map.size());

        return new ArrayList<>(map.values());
//        return jdbcTemplate.query(QUERY_FOR_GETTING_ALL_PHONES,
//                phone1);
    }
}
