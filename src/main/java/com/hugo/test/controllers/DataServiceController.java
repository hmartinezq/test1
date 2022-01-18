package com.hugo.test.controllers;

/**
 * Clase base de Co
 * , que implementa la interface de servicio
 * @author Rodrigo R.
 * @version 2020-11-26
 */
public class DataServiceController implements DataServiceInterface {
    // Definición del objeto de respuesta
    DataServiceResponse dataServiceResponse = null;
    // Definición del nombre del servicio
    static final String serviceNameV1 = serviceBase + serviceVersion1;
}