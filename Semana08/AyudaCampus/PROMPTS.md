# Prompts utilizados

Fase 2 · rama `mejora-ia` · Asistente: Gemini en Android Studio.

## Prompt 1 · Tiempo promedio de resolución

**Texto enviado a Gemini:**
> Tengo una app Kotlin con Jetpack Compose, Material 3, MVVM, Room y un AyudaRepository (interface). Agrega en el panel de soporte el tiempo promedio de resolucion de los tickets cerrados (fechaCierre - fecha). La consulta va en TicketDao, la funcion en AyudaRepository y su implementacion, y el valor se expone como StateFlow en PanelViewModel. La View solo lo muestra. No uses XML. Muestrame solo los archivos que cambian.

**Qué generó la IA:** Usé el modo Agent con Gemini 3.1 Pro Preview. En el primer intento falló por cuota (RESOURCE_EXHAUSTED) y luego se quedó trabado más de 15 minutos: editó el DAO, el Repository y el ViewModel, pero nunca modificó `PanelScreen`, así que lo detuve y revertí. En el segundo intento generó todo: la consulta `AVG(fechaCierre - fecha)` filtrando `estado = 'CERRADO'` y `fechaCierre IS NOT NULL` con `Flow<Double?>`, la función en `AyudaRepository` y `AyudaRepositoryRoom`, un `StateFlow<String>` en `PanelViewModel` con el tiempo ya formateado ("2d 3h") y una tarjeta en `PanelScreen`.

**Qué tuve que corregir yo:** El ViewModel devolvía el texto ya formateado, mezclando presentación con lógica. Lo cambié para que el Repository devuelva `Long?` (milisegundos), el ViewModel exponga `StateFlow<Long?>` y la View lo formatee con una función `formatoDuracion` reutilizable (que también usé en el SLA). Agregué un ícono a la tarjeta y el texto "Sin datos" cuando no hay tickets cerrados.

**¿Respeta MVVM?:** Sí. El cálculo está en SQL en el DAO, el Repository expone un `Flow`, el ViewModel un `StateFlow` y la View solo muestra el valor.

## Prompt 2 · Contador de SLA por prioridad

**Texto enviado a Gemini:**
> En la misma app (Compose + Material 3 + MVVM + Room), agrega un contador de SLA a cada ticket: el tiempo restante segun su prioridad (Alta 4 h, Media 24 h, Baja 72 h), contado desde la fecha de creacion. Solo aplica a tickets ABIERTO o EN_PROCESO. El calculo va en el ViewModel (MisTicketsViewModel y BandejaViewModel), que expone la hora actual como StateFlow actualizado cada minuto. La View (TicketCard) solo muestra el texto: verde si queda tiempo, ambar si queda menos del 25 % y rojo si vencio. No toques el DAO ni el Repository. No uses XML. Muestrame solo los archivos que cambian.

**Qué generó la IA:** Agregó en ambos ViewModels un `StateFlow` `ahora` que emite cada minuto y una función `calcularSla` que llama a un helper `calcularSlaHelper`. El helper devuelve una data class `SlaInfo(texto, color)` solo para tickets ABIERTO o EN_PROCESO, con verde, ámbar (< 25 %) y rojo. No tocó el DAO ni el Repository. `TicketCard` recibe `SlaInfo` y muestra el texto en la fila de los chips.

**Qué tuve que corregir yo:**
- El ViewModel importaba `androidx.compose.ui.graphics.Color` y decidía los colores; eso hace que el ViewModel dependa de Compose. Moví la regla a `model/Sla.kt` (Kotlin puro: `restanteMs`, `fraccionRestante`, `vencido`) y ahora el color lo elige `TicketCard` con los colores del tema.
- `TicketCard` importaba `SlaInfo` del paquete `viewmodel`, así que un componente de la View dependía del ViewModel. Ahora usa `model.Sla`.
- Las horas (4/24/72) estaban repetidas en un `when`; usé `Prioridad.horasRespuesta`, que ya existía.
- El formato del tiempo estaba duplicado; reutilicé `formatoDuracion`. Cuando vence, ahora dice "SLA vencido hace X" en lugar de solo "Vencido".

**¿Respeta MVVM?:** La versión de la IA lo respetaba a medias (el cálculo estaba en el ViewModel, pero este dependía de Compose). La versión final sí lo respeta: la regla está en el modelo, el ViewModel expone `ahora` y `sla()`, y la View solo elige el color y muestra el texto.

## Prompt 3 · Búsqueda avanzada en la bandeja

**Texto enviado a Gemini:**
> En la misma app, mejora la bandeja de Soporte (BandejaViewModel y BandejaScreen) para filtrar al mismo tiempo por estado, prioridad, categoria y texto (titulo, nombre del cliente o numero de ticket). Los filtros viven en el ViewModel en una data class FiltrosBandeja dentro de un MutableStateFlow, y la lista filtrada se expone como StateFlow con combine. Conserva el orden por prioridad que entrega el DAO. La View usa FilterChip de Material 3 y un boton "Limpiar filtros". El ViewModel solo conoce la interface AyudaRepository. No uses XML. Muestrame solo los archivos que cambian.

**Qué generó la IA:** Creó `FiltrosBandeja` dentro de un `MutableStateFlow`, combinó los tickets con los filtros usando `combine` y `filter` (así conserva el orden del DAO), buscó por título, cliente y número (#0005), y en la View usó `FilterChip` para estado, prioridad y categoría, un botón "Limpiar filtros" y un botón para borrar el texto.

**Qué tuve que corregir yo:**
- Usaba `observarCategoriasActivas()`, así que no se podía filtrar por una categoría desactivada aunque tuviera tickets antiguos. Lo cambié a `observarCategorias()`.
- La lógica de "tocar un chip ya marcado lo desmarca" y el cálculo de `hayFiltros` estaban en la View. Los moví al ViewModel (`seleccionarPrioridad`, `seleccionarCategoria` y `FiltrosBandeja.hayFiltros`).
- Agregué el contador "N tickets", los colores de prioridad en sus chips, el ícono de cada categoría y una fila separada para cada tipo de filtro. La búsqueda también incluye la ubicación.

**¿Respeta MVVM?:** Sí, en lo principal: los filtros y la lista filtrada viven en el ViewModel y este solo conoce `AyudaRepository`. La IA dejó algo de lógica en la View, que corregí.

## Prompt 4 · Registro de estudiante

**Texto enviado a Gemini:**
> En la misma app (Compose + Material 3 + MVVM + Room), agrega el registro de estudiante: una pantalla RegistroScreen con nombre, correo, contraseña y confirmación. Las validaciones van en un RegistroViewModel (nombre minimo 3 caracteres, correo valido y UNICO, contraseña minimo 4 y que coincidan). El correo unico se verifica con una consulta en UsuarioDao a traves de AyudaRepository. Al registrarse se crea un usuario con rol CLIENTE y entra directo a Mis tickets. Los errores se muestran junto a cada campo. No uses XML. Muestrame solo los archivos que cambian.

**Qué generó la IA:** Se ejecutó en modo Agent y modificó los archivos directamente. Agregó `buscarPorCorreo` en `UsuarioDao`, `buscarUsuarioPorCorreo` y `registrarUsuario(nombre, correo, clave, rol)` en el repositorio, un `RegistroViewModel` con las validaciones y un error por campo, `RegistroScreen`, la ruta `REGISTRO`, el enlace "Regístrate" en el login y una función `iniciarSesionDirecta` en `SesionViewModel`.

**Qué tuve que corregir yo:**
- `RegistroViewModel` recibía `SesionViewModel` en su constructor: un ViewModel dependía de otro. Lo cambié para que solo conozca el Repository y exponga el usuario registrado; la navegación es la que avisa a la sesión.
- `registrarUsuario` recibía el rol como parámetro, lo que permitiría crear cuentas de Soporte. Lo reemplacé por `registrarEstudiante`, que siempre crea rol CLIENTE.
- No pasaba el correo a minúsculas, así que se podía registrar "Juan@campus.test" aunque ya existiera "juan@campus.test". Ahora el correo se normaliza (también en el login).
- Usaba `android.util.Patterns` en el ViewModel; lo cambié por un `Regex` de Kotlin para no depender de Android.

**¿Respeta MVVM?:** Parcialmente. La consulta del correo único estaba en el DAO y las validaciones en el ViewModel, pero un ViewModel dependía de otro ViewModel. La versión final sí lo respeta.

Además, agregué manualmente (sin IA) el botón para mostrar u ocultar la contraseña y la pantalla "¿Olvidaste tu contraseña?".

## Prompt 5 · Comentarios internos

**Texto enviado a Gemini:**
> En la misma app (Compose + Material 3 + MVVM + Room), agrega comentarios internos: Soporte puede escribir notas internas en un ticket que el estudiante NO ve. Agrega un campo interno: Boolean a la entidad Comentario (sube la version de la base), un parametro interno en agregarComentario del AyudaRepository y su implementacion, y en AtenderViewModel un StateFlow para elegir si el siguiente comentario es nota interna. La regla de visibilidad va en el ViewModel: TicketDetalleViewModel (estudiante) debe filtrar las notas internas. En AtenderTicketScreen agrega un FilterChip "Nota interna" y muestra las notas internas con un estilo distinto. No uses XML. Muestrame solo los archivos que cambian.

**Qué generó la IA:** Gemini (modo Ask) agregó el campo `interno` a `Comentario` y subió la base a la versión 3, el parámetro `interno` con valor por defecto en `agregarComentario`, el filtro de notas internas en `TicketDetalleViewModel`, un `StateFlow` `esNotaInterna` en `AtenderViewModel`, un `FilterChip` "🔒 Nota interna" en `AtenderTicketScreen` y un estilo ámbar en `BurbujaComentario`.

**Qué tuve que corregir yo:** El repositorio guardaba la nota interna sin validar el rol del autor; agregué la regla para que solo Soporte (ADMIN) pueda crearla. También noté que usaba colores fijos en vez de los del tema y que el chip se desactivaba después de cada envío, lo que podía hacer pública una segunda nota sin querer.

**¿Respeta MVVM?:** Sí. La regla de visibilidad está en el ViewModel del estudiante (la pantalla no sabe que existen notas internas), la regla del rol está en el repositorio y la View solo muestra el chip y el estilo distinto.

## Prompt 6 · Animaciones y estados vacíos

**Texto enviado a Gemini:**
> En la misma app (Compose + Material 3 + MVVM), agrega animaciones y estados vacíos SOLO en la capa View (no toques ViewModels, Repository ni DAO): 1) transiciones de entrada y salida entre pantallas en el NavHost de NavAyuda, 2) anima el componente StepperTicket (la línea entre pasos se llena, el check aparece con animación y el paso actual tiene un pulso), 3) mejora el componente EstadoVacio con una ilustración (ícono grande dentro de un círculo con animación suave), un título, una descripción y un botón de acción opcional, y úsalo en Mis tickets, la Bandeja y Notificaciones, 4) anima los ítems de las listas al filtrar con animateItem. No uses XML. Muéstrame solo los archivos que cambian.

**Qué generó la IA:** Gemini (modo Ask) agregó transiciones de deslizar y desvanecer en el `NavHost`, animó el `StepperTicket` (línea que se llena con retraso por paso, check con `scaleIn` y `spring`, halo con pulso en el paso actual), rediseñó `EstadoVacio` con un ícono dentro de un círculo que late, título, descripción y botón opcional, lo usó con mensajes distintos en Mis tickets, la Bandeja y Notificaciones, y envolvió las tarjetas de las listas con `animateItem()`. Respetó el pedido de no tocar ViewModels, Repository ni DAO.

**Qué tuve que corregir yo:** No resolvió que el texto del SLA quedaba tapado por el botón "+" en Mis tickets; modifiqué `TicketCard` para mostrar el SLA en su propia línea. También noté que el botón "Limpiar filtros" de Mis tickets llama a dos funciones del ViewModel desde la View; sería más limpio una sola función en el ViewModel.

**¿Respeta MVVM?:** Sí. Todos los cambios son de la capa View. Las animaciones reaccionan al estado que ya exponen los ViewModels; por ejemplo, el stepper se anima solo cuando cambia el `StateFlow` del ticket.

Además, agregué manualmente (sin IA) las notificaciones: campanita con contador y notificaciones de Android.

## Reflexión (5 líneas)
- ¿Qué mejoró la IA y qué no?
La IA acertó al ubicar cada pieza en su capa (consulta en el DAO, Flow en el Repository, StateFlow en el ViewModel) y usó bien combine y stateIn, pero no reutilizó lo que ya existía (Prioridad.horasRespuesta, el formateo del tiempo).

- ¿Qué error cometió?
Su error más serio fue meter Color de Compose en el ViewModel y hacer que TicketCard dependa del paquete viewmodel, lo que acopla las capas; además filtró solo categorías activas en la bandeja, rompiendo una regla de negocio.

- ¿Qué aprendí sobre MVVM, Room y reglas de negocio?
Aprendí en:
MVVM: que un código funcione no significa que respete la arquitectura. Una forma rápida de revisarlo son los imports: si un ViewModel importa algo de androidx.compose.ui, o un componente de la View importa del paquete viewmodel, hay acoplamiento.

Room: los cálculos sobre muchos registros (promedio, conteos, orden por prioridad) conviene hacerlos en SQL, en el DAO, y exponerlos como Flow para que la pantalla se actualice sola.

Reglas de negocio: deben vivir fuera de la View (en el modelo o en el ViewModel), en Kotlin puro, para poder reutilizarlas y probarlas. Por ejemplo, calcularSla sirve igual para Mis tickets y la Bandeja. Además, la IA no conoce las reglas del negocio si no se las das: no sabía que las categorías inactivas pueden tener tickets.