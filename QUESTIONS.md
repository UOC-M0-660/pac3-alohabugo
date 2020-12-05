# PARTE TEORICA

### Lifecycle

#### Explica el ciclo de vida de una Activity.
Una aplicación en Android está formada por un conjunto de elementos básicos de interacción con el usuario, conocidos como actividades (activities). Además de varias actividades, una app también puede contener servicios. 
Cuando un usuario navega por la app, sale de ella y vuelve a entrar, las instancias de Activity, pasan por diferentes estados de su ciclo de vida. La clase Activity proporciona una serie de devoluciones de llamada que permiten a la actividad saber que cambió un estado, es decir, que el sistema está creando, deteniendo o reanudando una actividad, o finalizando el proceso en el que se encuentra.

##### ¿Por qué vinculamos las tareas de red a los componentes UI de la aplicación?
Una vez se ha recibido respuesta a una petición de red, en el caso de que haya sido exitosa, obtendremos los datos solicitados en dicha petición, por lo que necesitamos mostrarlos de algún modo al usuario, es por ello que los vinculamos a los componentes UI que previamente habrán sido creados en nuestra app.

##### ¿Qué pasaría si intentamos actualizar la recyclerview con nuevos streams después de que el usuario haya cerrado la aplicación?
En ese caso la actualización de la recyclerview lanzaría una excepción, ya que el usuario ha cerrado la aplicación y la activity se encontraría en un estado en el que está finalizando “isFinishing”. Dicha situación puede resolverse mediante scopes. Si hacemos uso de lifecycleScope, al cerrar la aplicación se cancelaría la corutina, evitando así dicha excepción.

##### Describe brevemente los principales estados del ciclo de vida de una Activity.
**Constructor**: La actividad, al ser una clase, tiene su propio constructor, que se ejecuta cada vez que se crea un nuevo objeto (una nueva versión de la actividad). Es importante tener en cuenta que hasta que no finaliza la ejecución de este método, la actividad no está realmente creada.

**onCreate**: Se ejecuta en cuanto se crea la actividad, después del constructor. Es el lugar idóneo para inicializar la actividad: crear las vistas que contendrá, cargar los datos que se vayan a visualizar, etc.
Este método recibe un objeto de tipo Bundle. Si no es la primera vez que se crea esta actividad, es decir, si la actividad ya ha existido antes pero fue destruida, el objeto Bundle contendrá lo necesario para restaurar su estado anterior de forma que el usuario se encuentre la actividad tal y como se la espera.

**onStart**: Este método se ejecuta cuando el sistema tiene intención de hacer visible la actividad. Se suele utilizar para crear aquellos procesos cuyo objetivo es actualizar la interfaz de usuario: animaciones, temporizadores, localización GPS etc.

**onResume**: Este método se ejecuta justo antes de que la actividad sea completamente visible y el usuario pueda empezar a interactuar con ella. Es el sitio ideal para iniciar animaciones, acceder a recursos que se tienen que utilizar de forma exclusiva (como la cámara), etc. De esta forma no utilizamos los recursos más valiosos hasta que realmente van a ser necesarios.

**onPause**: Se ejecuta cuando el usuario deja de poder interactuar con la actividad actual, porque va a ser reemplazada por otra (la anterior o una que se superponga). Es, por lo tanto, el sitio ideal para detener todo lo que se ha activado en onResume y liberar los recursos de uso exclusivo (dado que podría necesitarlos la nueva actividad).

**onStop**: Se llama a este método cuando la actividad ha sido ocultada totalmente por otra que ya está interactuando con el usuario. Aquí se suelen destruir los procesos creados en el método onStart, para liberar recursos y permitir que la actividad que está interactuando actualmente con el usuario pueda ir lo más fluida posible.

**onRestart**: Este método se ejecuta cuando una actividad había dejado de ser completamente visible pero tiene que mostrarse de nuevo (sin que haya sido destruida entre tanto, claro). Su uso no es tan habitual como el del resto de métodos.

**onDestroy**: El sistema ejecuta este método cuando ya no tiene intención de reutilizar más el objeto que contiene la actividad, ya sea porque se ha eliminado la actividad desde el propio código o porque el sistema está tratando de liberar memoria. Aquí ya no se puede hacer otra cosa que destruir los objetos, hilos y demás que hayamos creado en onCreate, para no dejar basura atrás.

**finalize**: Este método es el destructor de la clase. Se ejecuta cuando el recolector de basura va a eliminar el objeto.


---

### Paginación 

#### Explica el uso de paginación en la API de Twitch.
Por defecto, al obtener la primera petición de streams sólo se devuelven los primeros 20, ya que la API de Twitch utiliza paginación. Es por ello que se necesita ir haciendo nuevas peticiones a medida que el usuario va haciendo scroll en la recyclerview de la app.
El uso de la paginación nos permite ir solicitando datos poco a poco conforme sean necesarios en lugar de obtener su totalidad, de esta manera se optimiza el uso de la red.

##### ¿Qué ventajas ofrece la paginación a la aplicación?
Además de optimizar el uso de la red, comentado en el punto anterior, mediante la paginación se reducen los resultados de cada consulta, de esta manera se obtiene un tamaño de datos que se puede manejar, pudiéndolos mostrar al usuario más rápidamente.

##### ¿Qué problemas puede tener la aplicación si no se utiliza paginación?
Los problemas encontrados serían los inconvenientes relacionados con el punto anterior, es decir, podrían no obtenerse los beneficios de rendimiento deseados, ya que tendríamos que esperar a obtener respuestas de datos totales en lugar de parciales. Eso conlleva a descargar una gran cantidad de datos que no podríamos mostrar todos a la vez en nuestra recyclerview, puesto que las filas de visualización de las mismas están limitadas al tamaño del dispositivo, haciendo dicha app mucho menos ligera.

##### Lista algunos ejemplos de aplicaciones que usan paginación.
Algunas aplicaciones que usan paginación son: twitter, Facebook, pinterest, instagram, … Pero no sólo en redes sociales, algunas páginas de ecommerce como la de nike.
