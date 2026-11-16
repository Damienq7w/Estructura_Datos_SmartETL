package model;

/**
 * Motivos por los que el Transform rechaza un registro.
 * Estan todos en un solo lugar para poder contarlos por tipo
 * con un arreglo de enteros (una casilla por tipo, usando ordinal()).
 */
public enum TipoError {
    COLUMNAS_INCOMPLETAS,   // la fila trae menos campos que la cabecera
    CONDADO_DESCONOCIDO,    // sin FIPS y el condado dice "Unknown"
    AREA_SIN_FIPS,          // sin FIPS pero con nombre (New York City, Kansas City, Joplin)
    VALOR_NO_NUMERICO,      // casos o muertes no son un numero
    VALOR_NEGATIVO,         // casos o muertes menores que cero
    FECHA_INVALIDA,         // la fecha no tiene la forma AAAA-MM-DD
    NO_ES_CONDADO,          // fila del catalogo que es un pais o un estado
    DUPLICADO,              // el mismo registro ya se habia cargado
    AUTO_VECINDAD           // un condado que aparece como vecino de si mismo
}
