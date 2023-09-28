# Registro_de_Polizas_Pepin

#OBSERVACION : 
Debe crear la base de datos Mysql con el comando Create Database SeguroPepin ;
De ser necesario debe cambiar la contraseña de base de datos en el application.properties, actualmente se encuentra por defecto root

Documentación del Proyecto: Registro de Seguros de Vehículos
1.	Introducción
•	Funciones implementadas hasta el momento:
-Registrarse como nuevo usuario
-Iniciar sesión como usuario o administrador
-Cuenta que permanece conectada durante la sesión
-Registrar múltiples vehículos por usuario
-Solicitar una póliza por vehículo
-Eliminar un vehículo y su póliza ingresando el número de vehículo
-Función de administración: función de búsqueda para obtener detalles del usuario en forma de tabla

2.	Diseño de la Base de Datos
•	Motor de Base de Datos: MySql
•	Tablas: Las tablas se generan automáticamente al momento de correr la aplicación por primera vez, porque estoy utilizando el dialecto de  Hibernate :
spring.jpa.properties.hibernate.format_sql=true

3.	Arquitectura del Sistema
•	Patrones de Diseño: Patrones de diseño utilizados en la aplicación fue el modelo MVC (Model-View-Controller) 

4.	Desarrollo del Frontend
•	Tecnologías Utilizadas: HTML, Boostrap, JavaScript

5.	Desarrollo del Backend
•	Tecnologías Utilizadas: Las tecnologías de backend utilizadas Java, Spring Boot, servicio REST.
•	Arquitectura del Backend: La estructura y la lógica de backend, está conformada por controladores, servicios y repositorios.
•	Implementación del Servicio REST: Se utilizaron servicios Rest y Endpoints para la comunicación entre el backend y el frontend

6.	Seguridad
•	Autenticación y Autorización: Se utilizó Spring Security 
