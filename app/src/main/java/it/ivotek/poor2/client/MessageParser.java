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
    private int mUltrasoundLeft;
    private int mUltrasoundCenter;
    private int mUltrasoundRight;
    private float mCompass;
    private int mAccelerationX;
    private int mAccelerationY;
    private int mAccelerationZ;
    private int mTiltX;
    private int mTiltY;
    private int mTiltZ;
    private int mHumidity;
    private int mTemperature;
    private float mPowerIn;

    /**
     * Fornisce dati al parser.
     * @param message dati in arrivo dal robot, qualunque essi siana.
     * @return true se e' giunta la fine di un messaggio ed e' stato parsato
     * correttamente. In tal caso i vari getters restituiranno qualcosa
     */
    public boolean feedData(String message) {
        // appendi subito i dati entranti
        mData.append(message);

        int start = mData.indexOf(MARKER_START);
        int end = -1;
        if (start >= 0) {
            // avevamo iniziato dei dati, aspettiamoci la fine
            end = mData.indexOf(MARKER_END, start);
        }

        if (start >= 0 && end >= 0) {
            if (parse(mData.substring(start+1, end))) {
                mData.delete(0, mData.length());
                return true;
            }
        }

        return false;
    }

    private boolean parse(String data) {
        String[] values = data.split(SEPARATOR);
        if (values.length < 13)
            // TODO errore oppure andiamo avanti lo stesso con quello che abbiamo?
            return false;

        mUltrasoundLeft = parseInt(values[0], -1);
        mUltrasoundCenter = parseInt(values[1], -1);
        mUltrasoundRight = parseInt(values[2], -1);
        mCompass = parseFloat(values[3], -1);
        mAccelerationX = parseInt(values[4], -1);
        mAccelerationY = parseInt(values[5], -1);
        mAccelerationZ = parseInt(values[6], -1);
        mTiltX = parseInt(values[7], -1);
        mTiltY = parseInt(values[8], -1);
        mTiltZ = parseInt(values[9], -1);
        mHumidity = parseInt(values[10], -1);
        mTemperature = parseInt(values[11], -1);
        mPowerIn = parseFloat(values[12], -1);

        return true;
    }

    private int parseInt(String value, int error) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ignored) {
            return error;
        }
    }

    private float parseFloat(String value, float error) {
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException ignored) {
            return error;
        }
    }

    @Override
    public int getUltrasoundLeft() {
        return mUltrasoundLeft;
    }

    @Override
    public int getUltrasoundCenter() {
        return mUltrasoundCenter;
    }

    @Override
    public int getUltrasoundRight() {
        return mUltrasoundRight;
    }

    @Override
    public float getCompass() {
        return mCompass;
    }

    @Override
    public int getAccelerationX() {
        return mAccelerationX;
    }

    @Override
    public int getAccelerationY() {
        return mAccelerationY;
    }

    @Override
    public int getAccelerationZ() {
        return mAccelerationZ;
    }

    @Override
    public int getTiltX() {
        return mTiltX;
    }

    @Override
    public int getTiltY() {
        return mTiltY;
    }

    @Override
    public int getTiltZ() {
        return mTiltZ;
    }

    @Override
    public int getHumidity() {
        return mHumidity;
    }

    @Override
    public int getTemperature() {
        return mTemperature;
    }

    @Override
    public float getPowerIn() {
        return mPowerIn;
    }

}
