package ssssuper;

public interface UnitScheduleIndicator {

    void iter(String person, String shift, Integer state);

    double[] getIndex();

    double[] getIdeal();

    void clear();
}
