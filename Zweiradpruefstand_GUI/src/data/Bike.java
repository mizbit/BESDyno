package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author emil
 */
public class Bike {

    private static Bike instance = null;
    
    private String vehicleName;

    private boolean twoStroke;
    private boolean automatic;

    private boolean measRpm;
    private boolean schleppEnable;

    private final List<Datapoint> rpms = new LinkedList<>();

    private final Date date = Calendar.getInstance().getTime();
    private final DateFormat df = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    private String timePoint = null;

    public static Bike getInstance() {
        if (instance == null) {
            instance = new Bike();
        }
        return instance;
    }

    private Bike() {
    }

    //Getter
    public String getVehicleName() {
        return vehicleName;
    }

    public boolean isTwoStroke() {
        return twoStroke;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public List<Datapoint> getDatalist() {
        return rpms;
    }

    public boolean isMeasRpm() {
        return measRpm;
    }

    public boolean isSchleppEnable() {
        return schleppEnable;
    }

    //Setter
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public void setTwoStroke(boolean twoStroke) {
        this.twoStroke = twoStroke;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public void setMeasRpm(boolean measRpm) {
        this.measRpm = measRpm;
    }

    public void setSchleppEnable(boolean schleppEnable) {
        this.schleppEnable = schleppEnable;
    }

    //LinkedList-Methods
    public int size() {
        return rpms.size();
    }

    public Datapoint get(int index) {
        return rpms.get(index);
    }

    public Datapoint set(int index, Datapoint element) {
        return rpms.set(index, element);
    }

    public boolean add(Datapoint e) {
        return rpms.add(e);
    }

    @Override
    public String toString() {
        return rpms.toString();
    }

    //Writout
    private void writeList(BufferedWriter w) throws IOException {
        //Time, RPM, WSS
        for (Datapoint d : rpms) {
            d.writeLine(w);
            w.newLine();
        }
    }

    private void writeHeader(BufferedWriter w) throws IOException {
        w.write("BES-Data");
        timePoint = df.format(date);
        w.write(timePoint);
        w.newLine();
        w.write(vehicleName);
        w.newLine();
        w.write(String.format("%b", twoStroke));
        w.newLine();
        w.write(String.format("%b", automatic));
        w.newLine();
        w.write(String.format("%b", measRpm));
        w.newLine();
        w.write(String.format("%b", schleppEnable));
        w.newLine();
    }

    public void writeFile(BufferedWriter w) throws IOException, IllegalArgumentException {
        writeHeader(w);
        w.newLine();
        writeList(w);
        w.newLine();
    }

    //Read
    public void readFile(BufferedReader r) throws IOException, Exception {
        rpms.clear();

        String line = r.readLine().trim();
        if (!line.contains("BES-Data")) {
            throw new Exception("Not supported file");
        }

        timePoint = r.readLine().trim();
        vehicleName = r.readLine().trim();
        twoStroke = new Scanner(r.readLine().trim()).nextBoolean();
        automatic = new Scanner(r.readLine().trim()).nextBoolean();
        measRpm = new Scanner(r.readLine().trim()).nextBoolean();
        schleppEnable = new Scanner(r.readLine().trim()).nextBoolean();

        while (r.ready()) {
            line = r.readLine().trim();
            if (line.contains("#") || line.isEmpty()) {
                continue;
            }

//            String s[] = line.split("\t");
//            double time = new Scanner(s[0]).nextDouble();
//            double engRpm = new Scanner(s[1]).nextDouble();
//            double wheelRpm = new Scanner(s[2]).nextDouble();
//            add(new Datapoint(wheelRpm, engRpm, time));
            String s[] = line.split("\t");
            int time = Integer.parseInt(s[0]);
            int engRpm = Integer.parseInt(s[1]);
            int wheelRpm = Integer.parseInt(s[2]);
            add(new Datapoint(engRpm, wheelRpm, time));
        }
    }

}
