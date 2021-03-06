package data;

/**
 *
 * @author emil
 */
public class Datapoint {

    private final double powerNoFilter; //Watt, no Filter
    private final double power;         //Watt, with DIN70020

    private final double torque;        //Nm
    // -> if schlepp: from engPower
    // -> if BMP? ok: from normPower
    // ->       else: from wheelPower

    private final double omega;         //For Schlepp-Calculating

    /**
     * Calculation of Power and Torque incl Schlepp-Power if norm -> Calculation
     * DIN70020 omega = engOmega -> torque = engTorque omega = wheelOmega ->
     * torque = wheelTorque
     *
     * @param wheelPower
     * @param schleppPower
     * @param omega
     */
    public Datapoint(double wheelPower, double schleppPower, double omega) {
        this.powerNoFilter = wheelPower + Math.abs(schleppPower);

        if (Environment.getInstance().isNormEnable()) {
            double press = Environment.getInstance().getAirPress() / 100;  //hPa
            double temp = Environment.getInstance().getEnvTempC() + 273.15; //K
            this.power = ((1013.0 / press) * Math.sqrt(temp / 293.15)) * powerNoFilter;
        } else {
            this.power = powerNoFilter;
        }
        this.omega = omega;
        this.torque = power / omega;
    }

    public Datapoint(double power, double torque, double omega, boolean after) {
        if (after) {
            this.power = power;
            this.powerNoFilter = power;
            this.torque = torque;
            this.omega = omega;
        } else {
            this.powerNoFilter = power;

            if (Environment.getInstance().isNormEnable()) {
                double press = Environment.getInstance().getAirPress() / 100;  //hPa
                double temp = Environment.getInstance().getEnvTempC() + 273.15; //K
                this.power = ((1013.0 / press) * Math.sqrt(temp / 293.15)) * powerNoFilter;
            } else {
                this.power = powerNoFilter;
            }
            this.omega = omega;
            this.torque = power / omega;
        }
    }

    /**
     * Calculation of Power and Torque excl Schlepp-Power if norm -> Calculation
     * DIN70020 omega = engOmega -> torque = engTorque omega = wheelOmega ->
     * torque = wheelTorque
     *
     * @param wheelPower
     * @param omega
     */
    public Datapoint(double wheelPower, double omega) {
        this.powerNoFilter = wheelPower;

        if (Environment.getInstance().isNormEnable()) {
            double press = Environment.getInstance().getAirPress() / 100;  //hPa
            double temp = Environment.getInstance().getEnvTempC() + 273.15; //K
            this.power = ((1013.0 / press) * Math.sqrt(temp / 293.15)) * powerNoFilter;
        } else {
            this.power = powerNoFilter;
        }
        this.omega = omega;
        this.torque = power / omega;
    }

    public double getPower() {
        return power;
    }

    public double getTorque() {
        return torque;
    }

    public double getOmega() {
        return omega;
    }

}
