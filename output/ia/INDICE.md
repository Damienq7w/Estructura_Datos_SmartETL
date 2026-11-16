# Índice de evidencias de uso de IA

Los datos del proyecto son públicos y están agrupados por condado. No contienen información personal.

## Asistentes utilizados durante el desarrollo

ChatGPT se utilizó para orientar la implementación del módulo de bitácora,
proponer código y pruebas, y preparar las plantillas de prompts.
Las pruebas de Java se ejecutaron localmente en el proyecto.

## Ejecuciones con IA

Los prompts enviados se guardan en `prompts/` y las respuestas recibidas en
`respuestas/`, dentro de esta misma carpeta.

| Fecha y hora    | Modelo | Tarea                                   | Prompt enviado                       | Respuesta recibida                                 | Corrección y motivo                                                                                                                                  |
| --------------- | ------ | --------------------------------------- | ------------------------------------ | -------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2026-11-16 9:30 | Gemini | Reporte y flujograma del ETL por etapas | [Prompt](prompts/etl-por-etapas.txt) | [Respuesta original](respuestas/etl-por-etapas.md) | Cifras y transiciones verificadas. Los 2 casos con advertencias y los 2 condados reparados ya están incluidos en sus respectivos conteos de válidos. |

Agrega una fila después de cada ejecución con IA. Si la respuesta se valida
sin cambios, registra «Sin correcciones».
