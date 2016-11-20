package it.ivotek.poor2.client;


/**
 * Interfaccia per le classi che forniscono dati dai sensori del robot.
 */
public interface RobotSensorsData {

    /** Sensore Ultrasuoni Sinistro (3 cifre solo positivi da 0 a 400) */
    Integer getUltrasoundLeft();

    /** Sensore Ultrasuoni Centro (3 cifre solo positivi da 0 a 400) */
    Integer getUltrasoundCenter();

    /** Sensore Ultrasuoni Destro (3 cifre solo positivi da 0 a 400) */
    Integer getUltrasoundRight();

    /** Bussola in gradi (5 cifre divise da un punto da 0.00 a 360.00) */
    Float getCompass();

    /** Accelerazione asse X (I dati sono positivi e negativi, 5 cifre anche con segno negativo) */
    Integer getAccelerationX();

    /** Accelerazione asse Y (I dati sono positivi e negativi, 5 cifre anche con segno negativo) */
    Integer getAccelerationY();

    /** Accelerazione asse Z (I dati sono positivi e negativi, 5 cifre anche con segno negativo) */
    Integer getAccelerationZ();

    /** Inclinazione asse X (I dati sono positivi e negativi, 5 cifre anche con segno negativo) */
    Integer getTiltX();

    /** Inclinazione asse Y (I dati sono positivi e negativi, 5 cifre anche con segno negativo) */
    Integer getTiltY();

    /** Inclinazione asse Z (I dati sono positivi e negativi, 5 cifre anche con segno negativo) */
    Integer getTiltZ();

    /** Umidità (I dati sono positivi, 2 cifre) */
    Integer getHumidity();

    /** Temperatura (I dati sono positivi, 2 cifre) */
    Integer getTemperature();

    /** Alimentazione (I dati sono positivi, 3 0 4 cifre divise da un punto esempio 8.20 oppure 12.10) */
    Float getPowerIn();

    /** Sensore ottico sinistro (0-1) */
    Integer getOpticalLeft();

    /** Sensore ottico centrale sinistro (0-1) */
    Integer getOpticalCenterLeft();

    /** Sensore ottico centrale destro (0-1) */
    Integer getOpticalCenterRight();

    /** Sensore ottico destro (0-1) */
    Integer getOpticalRight();

    /** Fotoresistenza sinistra ([0, 5] V) */
    Float getPhotoresistorLeft();

    /** Fotoresistenza centrale ([0, 5] V) */
    Float getPhotoresistorCenter();

    /** Fotoresistenza destra ([0, 5] V) */
    Float getPhotoresistorRight();

}
