# 👟 ShoesUnicor - Tienda Virtual de Zapatos

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-blue.svg)
![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

**Sistema de gestión de tienda virtual de zapatos desarrollado para la Universidad de Córdoba**

[Características](#-características) • [Instalación](#-instalación) • [Uso](#-uso) • [Estructura](#-estructura-del-proyecto) • [Tecnologías](#-tecnologías-utilizadas)

</div>

---

## 📋 Descripción

**ShoesUnicor** es una aplicación de escritorio desarrollada en Java con JavaFX que simula una tienda virtual de zapatos. El sistema permite a los usuarios navegar por un catálogo de productos, agregar artículos al carrito, gestionar una lista de deseos, realizar pedidos y hacer seguimiento de sus compras. Los administradores pueden gestionar productos, usuarios y actualizar el estado de los pedidos.

---

## ✨ Características

### 👤 Funcionalidades para Usuarios

- **🔐 Autenticación**
  - Registro de nuevos usuarios
  - Inicio de sesión seguro con hash de contraseñas
  - Gestión de sesión de usuario

- **🛍️ Catálogo de Productos**
  - Visualización de productos con imágenes
  - Búsqueda de productos por nombre o descripción
  - Filtrado por categoría
  - Vista de detalles del producto con:
    - Imagen ampliada
    - Descripción completa
    - Selector de talla (36-45)
    - Selector de cantidad
    - Precio y stock disponible

- **🛒 Carrito de Compras**
  - Agregar productos al carrito
  - Modificar cantidades
  - Eliminar productos
  - Visualización del total
  - Checkout con formulario completo de dirección:
    - Calle y número
    - Apartamento/Piso (opcional)
    - Ciudad
    - Código Postal
    - Notas de entrega (opcional)

- **❤️ Lista de Deseos (Wishlist)**
  - Agregar productos a favoritos
  - Ver lista de productos favoritos
  - Eliminar de favoritos

- **📦 Historial de Pedidos**
  - Visualización de todos los pedidos realizados
  - Filtros avanzados:
    - Por número de pedido
    - Por estado del pedido
    - Por rango de fechas
  - Vista detallada de cada pedido:
    - Productos incluidos con cantidades
    - Precios individuales
    - Dirección de envío completa
    - Fecha del pedido
    - Fecha estimada de entrega
    - Estado actual del pedido

### 👨‍💼 Funcionalidades para Administradores

- **📦 Gestión de Productos**
  - Agregar nuevos productos
  - Editar productos existentes
  - Actualizar stock y precios
  - Activar/desactivar productos
  - Búsqueda y filtrado avanzado
  - Estadísticas de productos (total, activos, stock bajo)

- **🛒 Gestión de Pedidos**
  - Visualización de todos los pedidos
  - Actualización de estado en tiempo real:
    - Pendiente
    - Procesando
    - Enviado
    - Entregado
    - Cancelado
  - Estados mostrados en español

- **👥 Gestión de Usuarios**
  - Visualización de todos los usuarios registrados
  - Información de roles (Admin/Usuario)

---

## 🚀 Instalación

### Requisitos Previos

- **Java Development Kit (JDK) 21** o superior
- **Apache Maven 3.8+**
- **Apache NetBeans IDE** (recomendado) o cualquier IDE compatible con Java

### Pasos de Instalación

1. **Clonar o descargar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd ShoesUnicor
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación**
   
   **Opción A: Desde Maven**
   ```bash
   mvn javafx:run
   ```
   
   **Opción B: Desde NetBeans**
   - Abrir el proyecto en NetBeans
   - Click derecho en el proyecto → `Run`
   
   **Opción C: Ejecutar JAR**
   ```bash
   java -jar target/ShoesUnicor-1.0-SNAPSHOT.jar
   ```

---

## 💻 Uso

### Primera Ejecución

Al ejecutar la aplicación por primera vez, se crearán automáticamente:

- **Usuario Administrador:**
  - Username: `admin`
  - Password: `admin123`

- **Usuarios de Prueba:**
  - Username: `Victor19`, Password: `123456`

- **Productos de Ejemplo:**
  - 6 productos Air Jordan 1 en diferentes colores

### Flujo de Uso

1. **Iniciar Sesión**
   - Usa las credenciales del administrador o crea una cuenta nueva

2. **Navegar el Catálogo**
   - Explora los productos disponibles
   - Haz clic en cualquier producto para ver detalles completos
   - Agrega productos al carrito o a tu lista de deseos

3. **Realizar una Compra**
   - Agrega productos al carrito
   - Ve al carrito y haz clic en "Finalizar Compra"
   - Completa el formulario de dirección de envío
   - Confirma el pedido

4. **Seguimiento de Pedidos**
   - Ve a "Mis Pedidos" para ver tu historial
   - Usa los filtros para encontrar pedidos específicos
   - Haz clic en un pedido para ver todos los detalles

5. **Panel de Administración** (solo para admins)
   - Accede al panel desde el menú
   - Gestiona productos, pedidos y usuarios
   - Actualiza estados de pedidos en tiempo real

---

## 📁 Estructura del Proyecto

```
ShoesUnicor/
│
├── src/main/java/com/mycompany/shoesunicor/
│   ├── controller/          # Controladores de lógica de negocio
│   │   ├── AuthController.java
│   │   ├── OrderController.java
│   │   ├── ProductController.java
│   │   └── UserController.java
│   │
│   ├── model/              # Modelos de datos
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── OrderStatus.java
│   │   ├── Product.java
│   │   ├── User.java
│   │   └── UserRole.java
│   │
│   ├── util/               # Utilidades
│   │   ├── AnimationUtil.java
│   │   ├── CurrencyFormatter.java
│   │   ├── DataInitializer.java
│   │   ├── JsonDatabase.java
│   │   ├── LocalDateTimeAdapter.java
│   │   ├── PasswordUtil.java
│   │   └── Session.java
│   │
│   ├── view/               # Vistas de la interfaz
│   │   ├── AdminView.java
│   │   ├── CartView.java
│   │   ├── LoginView.java
│   │   ├── MainView.java
│   │   ├── OrderHistoryView.java
│   │   ├── ProductDetailView.java
│   │   ├── ProductsView.java
│   │   ├── RegisterView.java
│   │   └── WishlistView.java
│   │
│   ├── Launcher.java       # Punto de entrada
│   └── Main.java           # Aplicación principal
│
├── src/main/resources/
│   ├── css/
│   │   └── styles.css      # Estilos CSS
│   └── data/               # Base de datos JSON
│       ├── orders.json
│       ├── products.json
│       └── users.json
│
├── images/                 # Imágenes de productos
├── Documents/              # Documentación del proyecto
├── UML/                    # Diagramas UML
├── manual de usuario/      # Manual de usuario
├── pom.xml                 # Configuración Maven
└── README.md              # Este archivo
```

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 21 | Lenguaje de programación |
| **JavaFX** | 21.0.2 | Framework para interfaz gráfica |
| **Maven** | 3.8+ | Gestión de dependencias y build |
| **Gson** | 2.10.1 | Serialización/deserialización JSON |
| **Apache NetBeans** | - | IDE de desarrollo |

### Dependencias Principales

```xml
- javafx-controls: Interfaz de usuario
- javafx-fxml: Soporte FXML
- javafx-graphics: Renderizado gráfico
- javafx-base: Componentes base
- gson: Manejo de JSON
```

---

## 📊 Arquitectura

El proyecto sigue una arquitectura **MVC (Modelo-Vista-Controlador)**:

- **Modelo**: Clases en `model/` que representan las entidades del sistema
- **Vista**: Clases en `view/` que manejan la interfaz de usuario
- **Controlador**: Clases en `controller/` que contienen la lógica de negocio

### Persistencia de Datos

Los datos se almacenan en archivos **JSON** ubicados en `src/main/resources/data/`:
- `users.json`: Usuarios registrados
- `products.json`: Catálogo de productos
- `orders.json`: Historial de pedidos

El sistema utiliza **carga dinámica** de datos, leyendo y escribiendo los archivos JSON en tiempo de ejecución.

---

## 🔐 Seguridad

- **Contraseñas**: Se almacenan con hash SHA-256 (no en texto plano)
- **Sesiones**: Gestión mediante patrón Singleton
- **Validaciones**: Validación de campos en formularios
- **Roles**: Sistema de roles (ADMIN/USER) para control de acceso

---

## 📝 Estados de Pedidos

| Estado | Descripción |
|--------|-------------|
| **Pendiente** | El pedido fue creado pero aún no se procesa |
| **Procesando** | El administrador está preparando el envío |
| **Enviado** | El pedido está en camino |
| **Entregado** | Ha llegado a su destino |
| **Cancelado** | El pedido fue cancelado |

---

## 🎨 Características de la Interfaz

- **Diseño Moderno**: Interfaz limpia y profesional
- **Animaciones**: Transiciones suaves para mejor UX
- **Responsive**: Adaptable a diferentes tamaños de ventana
- **Temas**: Estilos CSS personalizados
- **Iconos**: Uso de emojis para mejor visualización

---

## 🐛 Solución de Problemas

### Error: "Failed to delete target"
```bash
# Eliminar manualmente la carpeta target
rm -rf target  # Linux/Mac
rmdir /s /q target  # Windows
```

### Error: "Module not found"
- Verificar que `module-info.java` esté correctamente configurado
- Asegurarse de que todas las dependencias estén en el `pom.xml`

### La aplicación no inicia
- Verificar que Java 21 esté instalado: `java -version`
- Verificar que Maven esté instalado: `mvn -version`
- Limpiar y recompilar: `mvn clean compile`

---

## 📚 Documentación Adicional

- **Manual de Usuario**: Disponible en `manual de usuario/`
- **Diagramas UML**: Disponibles en `UML/`
- **Documentación Técnica**: Disponible en `Documents/`

---

## 👥 Autores

- **Victor Negrete** - Desarrollo y diseño
  - Universidad de Córdoba

---

## 📄 Licencia

Este proyecto fue desarrollado como parte de un proyecto académico para la Universidad de Córdoba.

---

## 🙏 Agradecimientos

- Universidad de Córdoba por el apoyo académico
- Comunidad de JavaFX por la documentación y recursos
- Todos los contribuidores y testers del proyecto

---

## 📞 Contacto

Para preguntas o sugerencias sobre el proyecto, contactar al desarrollador.

---

<div align="center">

**Desarrollado con ❤️ para la Universidad de Córdoba**

⭐ Si este proyecto te fue útil, ¡dale una estrella!

</div>

