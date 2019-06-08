package ssssuper.ghy;

import dao.impl.ConnectionFactoryImpl;
import model.ExcelGenerator;
import utils.HierarchyComparator;
import utils.NameComparator;
import utils.TimeConsume;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        TimeConsume timeConsume = new TimeConsume();
        List<String[]> key = new LinkedList<>();
        String[] weekdays = {"周一", "周二", "周三", "周四", "周五"};
        String[] dayShifts = {"上午1、2节", "上午3、4节", "下午6、7节", "下午8、9节"};

        try {
            Connection conn = new ConnectionFactoryImpl().getConnection();
            PreparedStatement prepareStatement = conn.prepareStatement("select * from origin_data");
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                String person = resultSet.getString(1);
                for (String day : weekdays) {
                    String state = resultSet.getString(day);

                    for (int j = 0; j < dayShifts.length; j++) {
                        int a;
                        if (j <= 1) {
                            a = 2 * j + 1;
                        } else {
                            a = 2 * j + 2;
                        }

                        String s1 = "第" + a + "节";
                        String s2 = "第" + (a + 1) + "节";
                        if (state.contains(s1) && state.contains(s2)) {
                            key.add(new String[]{person, day + dayShifts[j]});
                        }
                    }
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        timeConsume.printTimeConsumed();
    }
}
