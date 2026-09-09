# FakeStore — Sistema Móvil (Java / Android Studio)

Implementación en Java de las especificaciones de UX/UI recibidas de **Armando Fonseca** (Project Manager) para el equipo **Rafa, Omar, Angel, Chiquilín, Choster y Jairo**.

## Cómo abrirlo en Android Studio

1. Descomprime `FakeStoreApp.zip`.
2. Abre Android Studio → **File > Open...** → selecciona la carpeta `FakeStoreApp/`.
3. Android Studio detectará el proyecto Gradle y comenzará a sincronizar automáticamente.
4. **Nota sobre el Gradle Wrapper**: el proyecto incluye `gradlew`, `gradlew.bat` y `gradle-wrapper.properties` (apuntando a Gradle 8.4), pero el binario `gradle-wrapper.jar` no se pudo generar en este entorno (sin acceso a internet). Al abrir el proyecto, Android Studio detectará que falta y te ofrecerá un aviso tipo *"Gradle wrapper is missing"* con un botón para regenerarlo automáticamente — solo dale clic ("OK" / "Fix Gradle Wrapper") y lo descargará él mismo, ya que tu máquina sí tiene internet.
5. Una vez sincronizado, ejecuta la app con el botón ▶ **Run** sobre un emulador o dispositivo físico (minSdk 26 / Android 8.0+).

## Diseño: pantalla única "Mi Perfil"

Las 3 vistas por rol (Administrador / Auditor / Cliente) están unificadas en **una sola pantalla `ProfileActivity`**, en **Dark Mode** (paleta del spec original), replicando el layout de referencia: avatar circular con inicial, insignia de rol superpuesta, tarjeta de datos con filas icono+label+valor, y botón "Cerrar Sesión" tipo pill con borde rojo.

Lo único que cambia entre roles es la **insignia**:

| Rol | IDs | Ícono | Color de acento |
|---|---|---|---|
| Administrador | 1, 2 | 👑 | `#B388FF` (morado) |
| Auditor | 3 | ⭐ | `#FFD54F` (dorado) |
| Cliente | resto | 🛒 | `#4FC3F7` (celeste) |

> El rol solo determina la insignia mostrada; el dashboard de "todos los usuarios" (Admin) y la vista restringida de solo id/username/email (Auditor) del spec original ya no aplican — fueron reemplazados intencionalmente por esta pantalla común (confirmado por el usuario).

## Estructura del código

| Área | Archivo(s) |
|---|---|
| Ícono de la app (adaptive icon, carrito morado sobre fondo oscuro) | `res/mipmap-anydpi-v26/`, `res/drawable/ic_launcher_*.xml` |
| Paleta Dark Mode + acentos de rol | `res/values/colors.xml` |
| Formas reutilizables (avatar, insignia, tarjeta, botón logout) | `res/drawable/*.xml` |
| Layout de "Mi Perfil" | `res/layout/activity_profile.xml`, `res/layout/item_profile_row.xml` |
| Modelo de datos (sin `password`) | `model/User.java`, `model/LoginRequest.java`, `model/LoginResponse.java` |
| Consumo de `/auth/login` y `/users` | `network/ApiService.java`, `network/ApiClient.java` |
| Pantalla de perfil unificada | `ui/ProfileActivity.java` |
| Login (401 / sin conexión / ruteo por rol) | `ui/LoginActivity.java`, `util/NetworkUtils.java` |
| Logout con limpieza profunda | `session/LogoutHelper.java`, `session/SessionManager.java`, `session/CartLocalStorage.java` |
| Bloqueo de retroceso tras logout | `LoginActivity.onBackPressed()` + `FLAG_ACTIVITY_CLEAR_TASK` |

## Regla de negocio crítica

`User.java` **no define un campo `password`**. Aunque el JSON de `https://fakestoreapi.com/users` lo incluya, Gson lo ignora al no existir un campo mapeado — el dato nunca llega a memoria ni a ningún estado de la app. `LoginRequest.java` sí envía el password (necesario para autenticar), pero no se persiste en ningún lugar tras la respuesta.

## Decisiones tomadas

- **minSdk subido de 24 a 26**: fue necesario para poder usar el adaptive icon (carrito morado sobre fondo oscuro) sin tener que generar además íconos PNG de respaldo para versiones antiguas. Cubre Android 8.0+ (más del 99% de dispositivos activos).
- **Gradle 8.4 + AGP 8.2.2**: versiones estables y compatibles entre sí al momento de esta entrega.

## Pendiente / fuera de alcance

- El catálogo/carrito de compras mencionado originalmente para el Cliente no forma parte de la pantalla "Mi Perfil" unificada (se retiró al unificar el diseño). Si lo necesitas de vuelta, se puede agregar como una pantalla adicional accesible desde un botón en el perfil del Cliente.
