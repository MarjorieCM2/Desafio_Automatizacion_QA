# OpenCart Automation - Selenium Test Suite

## Descripción
Este es un test automatizado de OpenCart utilizando Selenium WebDriver con TestNG. El código realiza pruebas de extremo a extremo sobre la funcionalidad del carrito de compras, incluyendo:

- Búsqueda y agregado de productos al carrito.
- Visualización del carrito y validación de los productos.
- Inicio de sesión y registro de usuario.
- Proceso de compra completo (Checkout).
- Verificación del historial de pedidos.

## Tecnologías utilizadas
- Java (JDK 17)
- Selenium WebDriver
- TestNG
- WebDriverManager
- Apache Commons IO (para capturas de pantalla)

## Estructura del código

```plaintext
src/test/java/org/example
 ┣ OpenCartTest.java      # Código principal del test automatizado
 ┣ resources
 ┃ ┗ credentials.properties  # Archivo con las credenciales de login
screenshots              # Directorio donde se guardan las capturas de pantalla
```

## Pre-requisitos

Antes de ejecutar el test, asegúrate de tener:
1. JDK 17 o superior instalado.
2. Maven configurado para gestionar las dependencias.
3. Chrome y WebDriverManager instalados.

## Configuración

1. Clona el repositorio o copia el código en tu proyecto.

2. Añade las dependencias en tu archivo `pom.xml` (si usas Maven):

```xml
<dependencies>
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.12.1</version>
    </dependency>
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.4.1</version>
    </dependency>
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.8.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.11.0</version>
    </dependency>
</dependencies>
```

## Configura las credenciales en credentials.properties
```
email=usuario@example.com
password=12345678
```

## Ejecutar test
```
mvn test
```
## Casos de prueba

### Test 1: Agregar productos al carrito
- Búsqueda y adición de productos (`iPod Classic` e `iMac`).
- Captura de pantalla después de agregar productos.

### Test 2: Ver carrito y validar productos
- Abrir el carrito y verificar productos agregados.
- Manejo de `StaleElementReferenceException` si la página recarga la tabla.

### Test 3: Inicio de sesión y registro
- Inicio de sesión con credenciales almacenadas.
- Registro si el usuario no está registrado.

### Test 4: Proceso de compra
- Llenado del formulario de dirección.
- Selección del país y estado con `Select` de Selenium.
- Validación del método de envío (`Flat Shipping Rate - $5.00`).
- Verificación del total del pedido antes del pago.

### Test 5: Verificación del historial de pedidos
- Confirmación del pedido.
- Verificación del estado del pedido en `"Pending"`.


## Capturas de pantalla:
``` plaintext
screenshots
┣ Add_iPod_Classic_YYYYMMDD_HHMMSS.png
┣ Add_iMac_YYYYMMDD_HHMMSS.png
┣ Cart_Page_Loaded_YYYYMMDD_HHMMSS.png
┣ Cart_Validation_YYYYMMDD_HHMMSS.png
┣ Order_Status_YYYYMMDD_HHMMSS.png
┗ ...
```

## Ejemplo de ejecución
```
[INFO] Running OpenCartTest
Producto 'iMac' encontrado en el carrito. (No logrado)
Producto 'iPod Classic' encontrado en el carrito.
Validación de productos en carrito completada.
Validación del método de envío completada: Flat Shipping Rate - $5.00
Validación del estado del pedido: Pending (No logrado)
Test completado con éxito.
```

## Autor

**Nombre:** Marjorie Cespedes  
**Fecha:** Marzo 2025

