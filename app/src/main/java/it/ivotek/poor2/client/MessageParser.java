package it.ivotek.poor2.client;


/**
 * Implementa il parsing dei messaggi ricevuti dal robot.
 * [26|143|8|49.64|-288|-420|16028|-4608|-173|152|19|21|8.28]
 * La sequenza dei dati è:
 * Sensore Ultrasuoni Sinistro (3 cifre solo positivi da 0 a 400)
 * Sensore Ultrasuoni Centro (3 cifre solo positivi da 0 a 400)
 * Sensore Ultrasuoni Destro (3 cifre solo positivi da 0 a 400)
 * Bussola in gradi (5 cifre divise da un punto da 0.00 a 360.00)
 * Accelerazione asse X (I dati sono positivi e negativi, 5 cifre anche con segno negativo)
 * Accelerazione asse Y (I dati sono positivi e negativi, 5 cifre anche con segno negativo)
 * Accelerazione asse Z (I dati sono positivi e negativi, 5 cifre anche con segno negativo)
 * Inclinazione asse X (I dati sono positivi e negativi, 5 cifre anche con segno negativo)
 * Inclinazione asse Y (I dati sono positivi e negativi, 5 cifre anche con segno negativo)
 * Inclinazione asse Z (I dati sono positivi e negativi, 5 cifre anche con segno negativo)
 * Umidità (I dati sono positivi, 2 cifre)
 * Temperatura (I dati sono positivi, 2 cifre)
 * Alimentazione (I dati sono positivi, 3 0 4 cifre divise da un punto esempio 8.20 oppure 12.10)
 */
public class MessageParser implements RobotSensorsData {

    private static final String MARKER_START = "[";
    private static final String MARKER_END = "]";
    private static final String SEPARATOR = "\\|";

    private final StringBuilder mData = new StringBuilder();

    // campi parsati dall'ultimo messaggio
    private Integer mUltrasoundLeft;
    private Integer mUltrasoundCenter;
    private Integer mUltrasoundRight;
    private Float mCompass;
    private Integer mAccelerationX;
    private Integer mAccelerationY;
    private Integer mAccelerationZ;
    private Integer mTiltX;
    private Integer mTiltY;
    private Integer mTiltZ;
    private Integer mHumidity;
    private Integer mTemperature;
    private Float mPowerIn;

    /**
     * Fornisce dati al parser.
     * @param message dati in arrivo dal robot, qualunque essi siana.
     * @return true se e' giunta la fine di un messaggio ed e' stato parsato
     * correttamente. In tal caso i vari getters restituiranno qualcosa
     */
    public boolean feedData(String message) {
        // appendi subito i dati entranti
        mData.append(message);

        int start = mData.lastIndexOf(MARKER_START);
        int end = -1;
        if (start >= 0) {
            // avevamo iniziato dei dati, aspettiamoci la fine
            end = mData.indexOf(MARKER_END, start);
        }

        if (start >= 0 && end >= 0) {
            reset();
            if (parse(mData.substring(start+1, end))) {
                mData.delete(0, mData.length());
                return true;
            }
        }

        return false;
    }

    private void reset() {
        mUltrasoundLeft = null;
        mUltrasoundCenter = null;
        mUltrasoundRight = null;
        mCompass = null;
        mAccelerationX = null;
        mAccelerationY = null;
        mAccelerationZ = null;
        mTiltX = null;
        mTiltY = null;
        mTiltZ = null;
        mHumidity = null;
        mTemperature = null;
        mPowerIn = null;
    }

    private boolean parse(String data) {
        String[] values = data.split(SEPARATOR);

        if (values.length > 0)
            mUltrasoundLeft = parseInt(values[0]);
        if (values.length > 1)
            mUltrasoundCenter = parseInt(values[1]);
        if (values.length > 2)
            mUltrasoundRight = parseInt(values[2]);
        if (values.length > 3)
            mCompass = parseFloat(values[3]);
        if (values.length > 4)
            mAccelerationX = parseInt(values[4]);
        if (values.length > 5)
            mAccelerationY = parseInt(values[5]);
        if (values.length > 6)
            mAccelerationZ = parseInt(values[6]);
        if (values.length > 7)
            mTiltX = parseInt(values[7]);
        if (values.length > 8)
            mTiltY = parseInt(values[8]);
        if (values.length > 9)
            mTiltZ = parseInt(values[9]);
        if (values.length > 10)
            mHumidity = parseInt(values[10]);
        if (values.length > 11)
            mTemperature = parseInt(values[11]);
        if (values.length > 12)
            mPowerIn = parseFloat(values[12]);

        return true;
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    public Integer getUltrasoundLeft() {
        return mUltrasoundLeft;
    }

    @Override
    public Integer getUltrasoundCenter() {
        return mUltrasoundCenter;
    }

    @Override
    public Integer getUltrasoundRight() {
        return mUltrasoundRight;
    }

    @Override
    public Float getCompass() {
        return mCompass;
    }

    @Override
    public Integer getAccelerationX() {
        return mAccelerationX;
    }

    @Override
    public Integer getAccelerationY() {
        return mAccelerationY;
    }

    @Override
    public Integer getAccelerationZ() {
        return mAccelerationZ;
    }

    @Override
    public Integer getTiltX() {
        return mTiltX;
    }

    @Override
    public Integer getTiltY() {
        return mTiltY;
    }

    @Override
    public Integer getTiltZ() {
        return mTiltZ;
    }

    @Override
    public Integer getHumidity() {
        return mHumidity;
    }

    @Override
    public Integer getTemperature() {
        return mTemperature;
    }

    @Override
    public Float getPowerIn() {
        return mPowerIn;
    }

}
